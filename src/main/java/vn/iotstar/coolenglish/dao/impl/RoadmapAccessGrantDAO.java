package vn.iotstar.coolenglish.dao.impl;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Roadmap;
import vn.iotstar.coolenglish.entity.RoadmapAccessGrant;

public class RoadmapAccessGrantDAO extends AbstractDAO<RoadmapAccessGrant> {

    @Override
    protected void validateEntity(RoadmapAccessGrant entity) {
        if (entity == null) {
            throw new IllegalArgumentException("RoadmapAccessGrant is required.");
        }
        if (entity.getRoadmap() == null || entity.getRoadmap().getId() == null) {
            throw new IllegalArgumentException("roadmap is required.");
        }
        if (entity.getUserId() == null) {
            throw new IllegalArgumentException("userId is required.");
        }
        if (entity.getGrantedAt() == null) {
            entity.setGrantedAt(LocalDateTime.now());
        }
    }

    public boolean hasActiveGrant(String roadmapCode, Long userId) {
        if (roadmapCode == null || roadmapCode.isBlank() || userId == null) {
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(g.id) FROM RoadmapAccessGrant g "
                            + "WHERE g.active = true AND g.userId = :userId "
                            + "AND g.roadmap.active = true AND g.roadmap.roadmapCode = :roadmapCode",
                    Long.class);
            query.setParameter("roadmapCode", roadmapCode);
            query.setParameter("userId", userId);
            Long total = query.getSingleResult();
            return total != null && total > 0;
        } finally {
            em.close();
        }
    }

    public List<Long> findActiveRoadmapIdsByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT g.roadmap.id FROM RoadmapAccessGrant g "
                            + "WHERE g.active = true AND g.userId = :userId",
                    Long.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void replaceActiveGrants(Long userId, List<Long> roadmapIds, String grantedBy) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required.");
        }

        List<Long> normalizedRoadmapIds = roadmapIds == null ? List.of() : roadmapIds;
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            em.createQuery("UPDATE RoadmapAccessGrant g SET g.active = false WHERE g.userId = :userId")
                    .setParameter("userId", userId)
                    .executeUpdate();

            for (Long roadmapId : normalizedRoadmapIds) {
                if (roadmapId == null) {
                    continue;
                }

                RoadmapAccessGrant existing = findGrant(em, roadmapId, userId);
                if (existing != null) {
                    existing.setActive(true);
                    existing.setGrantedBy(grantedBy);
                    existing.setGrantedAt(LocalDateTime.now());
                    em.merge(existing);
                    continue;
                }

                RoadmapAccessGrant grant = new RoadmapAccessGrant();
                grant.setRoadmap(em.getReference(Roadmap.class, roadmapId));
                grant.setUserId(userId);
                grant.setGrantedBy(grantedBy);
                grant.setGrantedAt(LocalDateTime.now());
                grant.setActive(true);
                em.persist(grant);
            }

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

    private RoadmapAccessGrant findGrant(EntityManager em, Long roadmapId, Long userId) {
        TypedQuery<RoadmapAccessGrant> query = em.createQuery(
                "SELECT g FROM RoadmapAccessGrant g WHERE g.roadmap.id = :roadmapId AND g.userId = :userId",
                RoadmapAccessGrant.class);
        query.setParameter("roadmapId", roadmapId);
        query.setParameter("userId", userId);
        List<RoadmapAccessGrant> results = query.setMaxResults(1).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
