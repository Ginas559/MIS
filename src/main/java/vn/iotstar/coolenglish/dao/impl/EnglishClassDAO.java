package vn.iotstar.coolenglish.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Teacher;
import vn.iotstar.coolenglish.enums.ClassStatus;
import vn.iotstar.coolenglish.enums.TeacherStatus;

public class EnglishClassDAO extends AbstractDAO<EnglishClass> {

    private final TeacherDAO teacherDAO = new TeacherDAO();

    @Override
    protected void validateEntity(EnglishClass entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Class entity is required.");
        }

        if (entity.getTeacherID() == null) {
            return;
        }

        Teacher teacher = teacherDAO.findById(entity.getTeacherID(), Teacher.class);
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher not found: " + entity.getTeacherID());
        }

        if (teacher.getStatus() == TeacherStatus.INACTIVE) {
            throw new IllegalArgumentException("Cannot assign INACTIVE teacher to class.");
        }

        entity.assignTeacher(teacher);
    }

    public EnglishClass findByClassID(String classID) {
        return findById(classID, EnglishClass.class);
    }

    /** Lớp + khóa + phòng + lịch (dùng khi hóa đơn chưa gắn enrollment). */
    public EnglishClass findByClassIdWithInvoiceRelations(String classID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<EnglishClass> query = em.createQuery(
                    "SELECT DISTINCT c FROM EnglishClass c "
                            + "LEFT JOIN FETCH c.course "
                            + "LEFT JOIN FETCH c.room "
                            + "LEFT JOIN FETCH c.schedule "
                            + "LEFT JOIN FETCH c.teacher "
                            + "WHERE c.classID = :classID",
                    EnglishClass.class);
            query.setParameter("classID", classID);
            java.util.List<EnglishClass> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
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

    public java.util.List<EnglishClass> findByTeacherID(Long teacherID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<EnglishClass> query = em.createQuery(
                    "SELECT c FROM EnglishClass c "
                            + "LEFT JOIN FETCH c.course "
                            + "LEFT JOIN FETCH c.room "
                            + "WHERE c.teacherID = :teacherID "
                            + "ORDER BY c.className",
                    EnglishClass.class);
            query.setParameter("teacherID", teacherID);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

