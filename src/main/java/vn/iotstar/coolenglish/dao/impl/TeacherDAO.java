package vn.iotstar.coolenglish.dao.impl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Teacher;

public class TeacherDAO extends AbstractDAO<Teacher> {

    @Override
    protected void validateEntity(Teacher entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Teacher is required.");
        }
    }

    public Teacher findByTeacherID(String teacherID) {
        return findById(teacherID, Teacher.class);
    }
    
    public Teacher findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Teacher> query = em.createQuery(
                    "SELECT t FROM Teacher t WHERE t.email = :email",
                    Teacher.class);
            query.setParameter("email", email);
            java.util.List<Teacher> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
}
