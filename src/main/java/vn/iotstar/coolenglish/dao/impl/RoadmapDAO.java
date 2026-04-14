package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Roadmap;

public class RoadmapDAO extends AbstractDAO<Roadmap> {

    @Override
    protected void validateEntity(Roadmap entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Roadmap is required.");
        }
        if (entity.getRoadmapCode() == null || entity.getRoadmapCode().isBlank()) {
            throw new IllegalArgumentException("roadmapCode is required.");
        }
        if (entity.getTitle() == null || entity.getTitle().isBlank()) {
            throw new IllegalArgumentException("title is required.");
        }
        if (entity.getRootModule() == null || entity.getRootModule().getId() == null) {
            throw new IllegalArgumentException("rootModule is required.");
        }
    }

    public Roadmap findByCode(String roadmapCode) {
        if (roadmapCode == null || roadmapCode.isBlank()) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Roadmap> query = em.createQuery(
                    "SELECT r FROM Roadmap r JOIN FETCH r.rootModule WHERE r.roadmapCode = :roadmapCode AND r.active = true",
                    Roadmap.class);
            query.setParameter("roadmapCode", roadmapCode);
            List<Roadmap> results = query.setMaxResults(1).getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public List<Roadmap> findAllActive() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Roadmap> query = em.createQuery(
                    "SELECT r FROM Roadmap r WHERE r.active = true ORDER BY r.id",
                    Roadmap.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existsByCodeIgnoreCase(String roadmapCode) {
        if (roadmapCode == null || roadmapCode.isBlank()) {
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r.id) FROM Roadmap r WHERE UPPER(r.roadmapCode) = :roadmapCode",
                    Long.class);
            query.setParameter("roadmapCode", roadmapCode.trim().toUpperCase());
            Long total = query.getSingleResult();
            return total != null && total > 0;
        } finally {
            em.close();
        }
    }

    public long countByRootModuleId(Long rootModuleId) {
        if (rootModuleId == null) {
            return 0L;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r.id) FROM Roadmap r WHERE r.rootModule.id = :rootModuleId",
                    Long.class);
            query.setParameter("rootModuleId", rootModuleId);
            Long total = query.getSingleResult();
            return total == null ? 0L : total;
        } finally {
            em.close();
        }
    }

    public long countByRootModuleIdExcludingRoadmap(Long rootModuleId, Long roadmapId) {
        if (rootModuleId == null) {
            return 0L;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r.id) FROM Roadmap r "
                            + "WHERE r.rootModule.id = :rootModuleId "
                            + "AND (:roadmapId IS NULL OR r.id <> :roadmapId)",
                    Long.class);
            query.setParameter("rootModuleId", rootModuleId);
            query.setParameter("roadmapId", roadmapId);
            Long total = query.getSingleResult();
            return total == null ? 0L : total;
        } finally {
            em.close();
        }
    }
}


