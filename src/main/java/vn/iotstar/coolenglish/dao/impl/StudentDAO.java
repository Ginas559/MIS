package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Student;

public class StudentDAO extends AbstractDAO<Student> {

    @Override
    protected void validateEntity(Student entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Student entity is required.");
        }
    }

    public Student findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Student> query = em.createQuery("SELECT s FROM Student s WHERE s.email = :email", Student.class);
            query.setParameter("email", email);
            List<Student> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
}

