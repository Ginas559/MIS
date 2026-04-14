package vn.iotstar.coolenglish.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.dao.impl.RoadmapDAO;
import vn.iotstar.coolenglish.entity.AcademicContentEntity;
import vn.iotstar.coolenglish.entity.Lesson;
import vn.iotstar.coolenglish.entity.Module;
import vn.iotstar.coolenglish.entity.Roadmap;

public class RoadmapManagementService {

    private final RoadmapDAO roadmapDAO;

    public RoadmapManagementService() {
        this(new RoadmapDAO());
    }

    public RoadmapManagementService(RoadmapDAO roadmapDAO) {
        this.roadmapDAO = roadmapDAO;
    }

    public List<Roadmap> findAllRoadmaps() {
        return roadmapDAO.findAll(Roadmap.class);
    }

    public void createRoadmap(String roadmapCode, String title, String description) {
        String normalizedCode = requireText(roadmapCode, "roadmapCode").toUpperCase();
        String normalizedTitle = requireText(title, "title");

        if (roadmapDAO.existsByCodeIgnoreCase(normalizedCode)) {
            throw new IllegalArgumentException("Roadmap code da ton tai. Vui long dung ma khac.");
        }

        runInTransaction(em -> {
            Module rootModule = new Module(normalizedTitle + " - Root", description);
            em.persist(rootModule);

            Roadmap roadmap = new Roadmap();
            roadmap.setRoadmapCode(normalizedCode);
            roadmap.setTitle(normalizedTitle);
            roadmap.setDescription(description);
            roadmap.setRootModule(rootModule);
            roadmap.setActive(true);
            em.persist(roadmap);
        });
    }

    public void updateRoadmap(Long roadmapId, String title, String description, boolean active) {
        Roadmap roadmap = roadmapDAO.findById(roadmapId, Roadmap.class);
        if (roadmap == null) {
            throw new IllegalArgumentException("Roadmap not found.");
        }

        roadmap.setTitle(requireText(title, "title"));
        roadmap.setDescription(description);
        roadmap.setActive(active);
        roadmapDAO.update(roadmap);
    }

    public void addModule(Long parentModuleId, String title, String description) {
        runInTransaction(em -> {
            Module parentModule = em.find(Module.class, parentModuleId);
            if (parentModule == null) {
                throw new IllegalArgumentException("Parent module not found.");
            }

            Module module = new Module(requireText(title, "title"), description);
            parentModule.add(module);
            em.persist(module);
        });
    }

    public void addLesson(Long parentModuleId, String title, String description, String lessonType, String resourceUrl) {
        runInTransaction(em -> {
            Module parentModule = em.find(Module.class, parentModuleId);
            if (parentModule == null) {
                throw new IllegalArgumentException("Parent module not found.");
            }

            Lesson lesson = new Lesson(requireText(title, "title"), description, trim(lessonType), trim(resourceUrl));
            parentModule.add(lesson);
            em.persist(lesson);
        });
    }

    public void updateContent(Long contentId, String title, String description, String lessonType, String resourceUrl) {
        runInTransaction(em -> {
            AcademicContentEntity content = em.find(AcademicContentEntity.class, contentId);
            if (content == null) {
                throw new IllegalArgumentException("Content not found.");
            }

            content.setTitle(requireText(title, "title"));
            content.setDescription(trim(description));

            if (content instanceof Lesson lesson) {
                lesson.setLessonType(trim(lessonType));
                lesson.setResourceUrl(trim(resourceUrl));
            }
        });
    }

    public void deleteContent(Long contentId) {
        runInTransaction(em -> {
            AcademicContentEntity content = em.find(AcademicContentEntity.class, contentId);
            if (content == null) {
                throw new IllegalArgumentException("Content not found.");
            }

            if (content instanceof Module module && !module.getChildren().isEmpty()) {
                throw new IllegalArgumentException("Khong the xoa module khi van con bai hoc/chang con ben trong.");
            }

            if (content instanceof Module) {
                long referencedRoadmaps = roadmapDAO.countByRootModuleId(contentId);
                if (referencedRoadmaps > 0) {
                    throw new IllegalArgumentException(
                            "Khong the xoa module dang la root module cua roadmap. Vui long xu ly roadmap lien quan truoc.");
                }
            }

            Module parent = content.getParentModule();
            if (parent != null) {
                parent.remove(content);
            }
            em.remove(content);
        });
    }

    public void deleteRoadmap(Long roadmapId) {
        runInTransaction(em -> {
            Roadmap roadmap = em.find(Roadmap.class, roadmapId);
            if (roadmap == null) {
                throw new IllegalArgumentException("Roadmap not found.");
            }

            Module rootModule = em.find(Module.class, roadmap.getRootModule().getId());
            if (rootModule != null && !rootModule.getChildren().isEmpty()) {
                throw new IllegalArgumentException("Khong the xoa roadmap khi root module van con noi dung.");
            }

            em.remove(roadmap);
            if (rootModule != null) {
                long referencedByOtherRoadmaps = roadmapDAO.countByRootModuleIdExcludingRoadmap(rootModule.getId(), roadmapId);
                if (referencedByOtherRoadmaps > 0) {
                    throw new IllegalArgumentException(
                            "Root module dang duoc roadmap khac su dung. Khong the xoa module goc.");
                }
                em.remove(rootModule);
            }
        });
    }

    public List<ContentNodeView> findRoadmapContentNodes(Long roadmapId) {
        if (roadmapId == null) {
            return List.of();
        }

        Roadmap roadmap = roadmapDAO.findById(roadmapId, Roadmap.class);
        if (roadmap == null || roadmap.getRootModule() == null || roadmap.getRootModule().getId() == null) {
            return List.of();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            Query query = em.createNativeQuery(
                    "WITH content_tree AS ("
                            + " SELECT ac.id, ac.title, ac.content_type, ac.parent_id, CAST(0 AS INT) AS depth"
                            + " FROM AcademicContents ac WHERE ac.id = :rootId"
                            + " UNION ALL"
                            + " SELECT child.id, child.title, child.content_type, child.parent_id, ct.depth + 1"
                            + " FROM AcademicContents child"
                            + " INNER JOIN content_tree ct ON child.parent_id = ct.id"
                            + ")"
                            + " SELECT id, title, content_type, parent_id, depth"
                            + " FROM content_tree"
                            + " ORDER BY depth, id");
            query.setParameter("rootId", roadmap.getRootModule().getId());

            @SuppressWarnings("unchecked")
            List<Object[]> rows = query.getResultList();
            List<ContentNodeView> nodes = new ArrayList<>();
            for (Object[] row : rows) {
                Long id = ((Number) row[0]).longValue();
                String title = row[1] != null ? row[1].toString() : "";
                String type = row[2] != null ? row[2].toString() : "";
                Long parentId = row[3] != null ? ((Number) row[3]).longValue() : null;
                int depth = ((Number) row[4]).intValue();
                nodes.add(new ContentNodeView(id, title, type, parentId, depth));
            }
            return nodes;
        } finally {
            em.close();
        }
    }

    private void runInTransaction(EntityAction action) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            action.execute(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private String requireText(String value, String fieldName) {
        String trimmed = trim(value);
        if (trimmed == null || trimmed.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return trimmed;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    @FunctionalInterface
    private interface EntityAction {
        void execute(EntityManager em);
    }

    public static final class ContentNodeView {
        private final Long id;
        private final String title;
        private final String type;
        private final Long parentId;
        private final int depth;

        public ContentNodeView(Long id, String title, String type, Long parentId, int depth) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.parentId = parentId;
            this.depth = depth;
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public Long getParentId() {
            return parentId;
        }

        public int getDepth() {
            return depth;
        }

        public boolean isModule() {
            return "MODULE".equalsIgnoreCase(type);
        }
    }
}

