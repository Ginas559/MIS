package vn.iotstar.coolenglish.dao.impl;

import java.util.HashSet;
import java.util.Set;

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
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(e) FROM Enrollment e WHERE e.englishClass.classID = :classID AND e.student.id = :studentId",
                    Long.class);
            query.setParameter("classID", classID);
            query.setParameter("studentId", studentPersonId);
            return query.getSingleResult() > 0;
        }
    }

    public boolean existsByCourseAndStudentEmail(String courseID, String studentEmail) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(e) FROM Enrollment e "
                            + "WHERE e.englishClass.courseID = :courseID AND e.student.email = :studentEmail",
                    Long.class);
            query.setParameter("courseID", courseID);
            query.setParameter("studentEmail", studentEmail);
            return query.getSingleResult() > 0;
        }
    }

    public Set<String> findEnrolledCourseIdsByStudentEmail(String studentEmail) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<String> query = em.createQuery(
                    "SELECT DISTINCT e.englishClass.courseID FROM Enrollment e WHERE e.student.email = :studentEmail",
                    String.class);
            query.setParameter("studentEmail", studentEmail);
            return new HashSet<>(query.getResultList());
        }
    }
}

