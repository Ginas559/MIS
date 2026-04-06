package vn.iotstar.coolenglish.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Enrollment;

public class EnrollmentDAO extends AbstractDAO<Enrollment> {

    @Override
    protected void validateEntity(Enrollment entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Enrollment entity is required.");
        }
    }

    public boolean existsByClassAndStudent(String classID, Long studentPersonId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(e) FROM Enrollment e WHERE e.englishClass.classID = :classID AND e.student.id = :studentId",
                    Long.class);
            query.setParameter("classID", classID);
            query.setParameter("studentId", studentPersonId);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}

