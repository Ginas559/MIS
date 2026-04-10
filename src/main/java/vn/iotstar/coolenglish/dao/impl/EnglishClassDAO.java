package vn.iotstar.coolenglish.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.enums.ClassStatus;

public class EnglishClassDAO extends AbstractDAO<EnglishClass> {

    @Override
    protected void validateEntity(EnglishClass entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Class entity is required.");
        }
    }

    public EnglishClass findByClassID(String classID) {
        return findById(classID, EnglishClass.class);
    }

    public boolean existsByClassID(String classID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(c) FROM EnglishClass c WHERE c.classID = :classID", Long.class);
            query.setParameter("classID", classID);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public EnglishClass findFirstOpenClassByCourseID(String courseID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<EnglishClass> query = em.createQuery(
                    "SELECT c FROM EnglishClass c WHERE c.courseID = :courseID AND c.status = :status ORDER BY c.classID",
                    EnglishClass.class);
            query.setParameter("courseID", courseID);
            query.setParameter("status", ClassStatus.OPEN);
            query.setMaxResults(1);
            java.util.List<EnglishClass> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
}

