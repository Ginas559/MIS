package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.enums.ClassStatus;
import vn.iotstar.coolenglish.enums.TeacherStatus;

public class EnglishClassDAO extends AbstractDAO<EnglishClass> {

    @Override
    protected void validateEntity(EnglishClass entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Class entity is required.");
        }

        if (entity.getClassName() == null || entity.getClassName().isBlank()) {
            throw new IllegalStateException("Tên lớp học không được để trống!");
        }

        if (entity.getCurrentEnrollment() != null && entity.getCurrentEnrollment() < 0) {
            throw new IllegalStateException("Số học viên hiện tại không hợp lệ!");
        }

        if (entity.getStartDate() == null) {
            throw new IllegalStateException("Ngày bắt đầu là bắt buộc!");
        }

        if (entity.getEndDate() == null) {
            throw new IllegalStateException("Ngày kết thúc là bắt buộc!");
        }

        if (entity.getMaxStudent() == null || entity.getMaxStudent() <= 0) {
            throw new IllegalStateException("Sức chứa tối đa phải lớn hơn 0!");
        }

        if (entity.getStartDate() != null && entity.getEndDate() != null
                && entity.getEndDate().isBefore(entity.getStartDate())) {
            throw new IllegalStateException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu!");
        }

        // Kiểm tra lớp học phải có giáo viên (bắt buộc)
        if (entity.getTeacher() == null) {
            throw new IllegalStateException("Lớp học thiếu giáo viên phụ trách!");
        }

        // Kiểm tra giáo viên không ở trạng thái INACTIVE
        if (entity.getTeacher().getStatus() == TeacherStatus.INACTIVE) {
            throw new IllegalStateException("Không thể gán giáo viên đang ngừng hoạt động!");
        }
    }

    public EnglishClass findByClassID(String classID) {
        if (classID == null || classID.isBlank()) {
            return null;
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<EnglishClass> query = em.createQuery(
                    "SELECT DISTINCT c FROM EnglishClass c LEFT JOIN FETCH c.teacher WHERE c.classID = :classID",
                    EnglishClass.class);
            query.setParameter("classID", classID);
            List<EnglishClass> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public List<EnglishClass> findAllWithDetails() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<EnglishClass> query = em.createQuery(
                    "SELECT DISTINCT c FROM EnglishClass c "
                            + "LEFT JOIN FETCH c.teacher "
                            + "LEFT JOIN FETCH c.course "
                            + "ORDER BY c.classID",
                    EnglishClass.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public EnglishClass findByClassIDWithDetails(String classID) {
        if (classID == null || classID.isBlank()) {
            return null;
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<EnglishClass> query = em.createQuery(
                    "SELECT DISTINCT c FROM EnglishClass c "
                            + "LEFT JOIN FETCH c.teacher "
                            + "LEFT JOIN FETCH c.course "
                            + "LEFT JOIN FETCH c.schedule "
                            + "WHERE c.classID = :classID",
                    EnglishClass.class);
            query.setParameter("classID", classID);
            List<EnglishClass> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public EnglishClass findByClassIdWithSchedule(String classID) {
        if (classID == null || classID.isBlank()) {
            return null;
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<EnglishClass> query = em.createQuery(
                    "SELECT DISTINCT c FROM EnglishClass c "
                            + "LEFT JOIN FETCH c.teacher "
                            + "LEFT JOIN FETCH c.schedule WHERE c.classID = :classID",
                    EnglishClass.class);
            query.setParameter("classID", classID);
            List<EnglishClass> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /** Lớp + khóa + lịch + buổi học (dùng khi hóa đơn chưa gắn enrollment). */
    public EnglishClass findByClassIdWithInvoiceRelations(String classID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<EnglishClass> query = em.createQuery(
                    "SELECT DISTINCT c FROM EnglishClass c "
                            + "LEFT JOIN FETCH c.course "
                            + "LEFT JOIN FETCH c.schedule sch "
                            + "LEFT JOIN FETCH sch.sessions sess "
                            + "LEFT JOIN FETCH sess.room "
                            + "LEFT JOIN FETCH c.teacher "
                            + "WHERE c.classID = :classID",
                    EnglishClass.class);
            query.setParameter("classID", classID);
            List<EnglishClass> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /** Lớp + khóa + lịch + toàn bộ buổi học (dùng trang điểm danh). */
    public EnglishClass findByClassIdWithScheduleSessions(String classID) {
        if (classID == null || classID.isBlank()) {
            return null;
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<EnglishClass> query = em.createQuery(
                    "SELECT DISTINCT c FROM EnglishClass c "
                            + "LEFT JOIN FETCH c.course "
                            + "LEFT JOIN FETCH c.schedule s "
                            + "LEFT JOIN FETCH s.sessions "
                            + "WHERE c.classID = :classID",
                    EnglishClass.class);
            query.setParameter("classID", classID);
            List<EnglishClass> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    /** Các lớp do giáo viên phụ trách, kèm lịch và buổi học. */
    public List<EnglishClass> findByTeacherIdWithScheduleSessions(Long teacherId) {
        if (teacherId == null) {
            return List.of();
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<EnglishClass> query = em.createQuery(
                    "SELECT DISTINCT c FROM EnglishClass c "
                            + "LEFT JOIN FETCH c.course "
                            + "LEFT JOIN FETCH c.schedule s "
                            + "LEFT JOIN FETCH s.sessions "
                            + "LEFT JOIN FETCH c.teacher "
                            + "WHERE c.teacher.id = :tid",
                    EnglishClass.class);
            query.setParameter("tid", teacherId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Lớp chưa có lịch, hoặc đang gắn đúng lịch {@code editingScheduleID} (dùng form sửa lịch).
     */
    public List<EnglishClass> findAssignableForSchedule(String editingScheduleID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (editingScheduleID == null || editingScheduleID.isBlank()) {
                TypedQuery<EnglishClass> query = em.createQuery(
                        "SELECT c FROM EnglishClass c WHERE c.schedule IS NULL ORDER BY c.classID",
                        EnglishClass.class);
                return query.getResultList();
            }
            TypedQuery<EnglishClass> query = em.createQuery(
                    "SELECT c FROM EnglishClass c WHERE c.schedule IS NULL OR c.schedule.scheduleID = :sid ORDER BY c.classID",
                    EnglishClass.class);
            query.setParameter("sid", editingScheduleID);
            return query.getResultList();
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
                    "SELECT DISTINCT c FROM EnglishClass c LEFT JOIN FETCH c.teacher "
                            + "WHERE c.courseID = :courseID AND c.status = :status ORDER BY c.classID",
                    EnglishClass.class);
            query.setParameter("courseID", courseID);
            query.setParameter("status", ClassStatus.OPEN);
            query.setMaxResults(1);
            List<EnglishClass> results = query.getResultList();
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
                            + "LEFT JOIN FETCH c.teacher "
                            + "WHERE c.teacher.id = :teacherID "
                            + "ORDER BY c.className",
                    EnglishClass.class);
            query.setParameter("teacherID", teacherID);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

