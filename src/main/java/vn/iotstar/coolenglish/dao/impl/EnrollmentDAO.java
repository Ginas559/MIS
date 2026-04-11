package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

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

    public Enrollment findLatestByClassAndStudent(String classID, Long studentPersonId) {
        if (classID == null || classID.isBlank() || studentPersonId == null) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Enrollment> query = em.createQuery(
                    "SELECT e FROM Enrollment e WHERE e.englishClass.classID = :classID AND e.student.id = :studentId ORDER BY e.enrolledAt DESC",
                    Enrollment.class);
            query.setParameter("classID", classID);
            query.setParameter("studentId", studentPersonId);
            query.setMaxResults(1);
            List<Enrollment> enrollments = query.getResultList();
            return enrollments.isEmpty() ? null : enrollments.get(0);
        } finally {
            em.close();
        }
    }
}
