package vn.iotstar.coolenglish.service;

import vn.iotstar.coolenglish.dao.impl.ModuleDAO;
import vn.iotstar.coolenglish.dao.impl.RoadmapAccessGrantDAO;
import vn.iotstar.coolenglish.dao.impl.RoadmapDAO;
import vn.iotstar.coolenglish.entity.AcademicContent;
import vn.iotstar.coolenglish.entity.Lesson;
import vn.iotstar.coolenglish.entity.Module;
import vn.iotstar.coolenglish.entity.Roadmap;
import vn.iotstar.coolenglish.entity.decorator.PremiumContentDecorator;
import vn.iotstar.coolenglish.entity.decorator.WatermarkDecorator;
import vn.iotstar.coolenglish.entity.iterator.CourseNavigator;
import vn.iotstar.coolenglish.enums.UserRole;

public class AcademicContentService {

    private final ModuleDAO moduleDAO;
    private final RoadmapDAO roadmapDAO;
    private final RoadmapAccessGrantDAO roadmapAccessGrantDAO;
    private final AccessPolicyService accessPolicyService;

    public AcademicContentService() {
        this(new ModuleDAO(), new RoadmapDAO(), new RoadmapAccessGrantDAO(), new AccessPolicyService());
    }

    public AcademicContentService(ModuleDAO moduleDAO, RoadmapDAO roadmapDAO, RoadmapAccessGrantDAO roadmapAccessGrantDAO) {
        this(moduleDAO, roadmapDAO, roadmapAccessGrantDAO, new AccessPolicyService());
    }

    public AcademicContentService(ModuleDAO moduleDAO, RoadmapDAO roadmapDAO, RoadmapAccessGrantDAO roadmapAccessGrantDAO,
            AccessPolicyService accessPolicyService) {
        this.moduleDAO = moduleDAO;
        this.roadmapDAO = roadmapDAO;
        this.roadmapAccessGrantDAO = roadmapAccessGrantDAO;
        this.accessPolicyService = accessPolicyService;
    }

    public LearningViewModel buildLearningView(String roadmapCode, Long learnerUserId, String learnerEmail,
            String currentTitle, UserRole role) {
        Roadmap roadmap = roadmapDAO.findByCode(roadmapCode);
        Module rootModule = loadRootModule(roadmap);
        CourseNavigator navigator = new CourseNavigator(rootModule);

        AcademicContent current = navigator.findByTitleOrFirst(currentTitle);
        AcademicContent next = navigator.findNextAfter(current);

        boolean canAccess = accessPolicyService.canAccessRoadmap(role, roadmapCode, learnerUserId,
                this::hasRoadmapGrant);
        AcademicContent decorated = decorateForLearner(current, canAccess, learnerEmail);

        return new LearningViewModel(roadmapCode, roadmap, rootModule, decorated, next, !canAccess);
    }

    AcademicContent decorateForLearner(AcademicContent content, boolean canAccessPremium, String studentEmail) {
        if (content == null) {
            return null;
        }

        AcademicContent premiumProtected = new PremiumContentDecorator(content, canAccessPremium);
        return new WatermarkDecorator(premiumProtected, studentEmail);
    }

    boolean hasRoadmapGrant(String roadmapCode, Long learnerUserId) {
        return roadmapAccessGrantDAO.hasActiveGrant(roadmapCode, learnerUserId);
    }

    private Module loadRootModule(Roadmap roadmap) {
        if (roadmap != null && roadmap.getRootModule() != null) {
            if (roadmap.getRootModule().getId() != null) {
                Module persistedModule = moduleDAO.findByIdWithChildren(roadmap.getRootModule().getId());
                if (persistedModule != null) {
                    return persistedModule;
                }
            }
            return roadmap.getRootModule();
        }

        return buildFallbackModule();
    }

    private Module buildFallbackModule() {
        Module root = new Module("Starter Module", "Noi dung mau cho trang hoc.");
        Lesson lesson1 = new Lesson("Welcome", "Gioi thieu khoa hoc", "READING", "https://coolenglish.local/lessons/welcome");
        Lesson lesson2 = new Lesson("Pronunciation Basics", "Luyen phat am co ban", "VIDEO",
                "https://coolenglish.local/lessons/pronunciation");
        root.add(lesson1);
        root.add(lesson2);
        return root;
    }

    public static final class LearningViewModel {
        private final String roadmapCode;
        private final Roadmap roadmap;
        private final Module rootModule;
        private final AcademicContent currentContent;
        private final AcademicContent nextContent;
        private final boolean premiumLocked;

        public LearningViewModel(String roadmapCode, Roadmap roadmap, Module rootModule, AcademicContent currentContent, AcademicContent nextContent,
                boolean premiumLocked) {
            this.roadmapCode = roadmapCode;
            this.roadmap = roadmap;
            this.rootModule = rootModule;
            this.currentContent = currentContent;
            this.nextContent = nextContent;
            this.premiumLocked = premiumLocked;
        }

        public String getRoadmapCode() {
            return roadmapCode;
        }

        public Roadmap getRoadmap() {
            return roadmap;
        }

        public Module getRootModule() {
            return rootModule;
        }

        public AcademicContent getCurrentContent() {
            return currentContent;
        }

        public AcademicContent getNextContent() {
            return nextContent;
        }

        public boolean isPremiumLocked() {
            return premiumLocked;
        }
    }
}

