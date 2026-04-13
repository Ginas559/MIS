package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Attendance;

public class AttendanceDAO extends AbstractDAO<Attendance> {

    public Attendance findByEnrollmentIdAndSessionId(Long enrollmentId, String sessionId) {
        if (enrollmentId == null || sessionId == null || sessionId.isBlank()) {
            return null;
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Attendance> query = em.createQuery(
                    "SELECT a FROM Attendance a WHERE a.enrollment.id = :eid AND a.session.sessionID = :sid",
                    Attendance.class);
            query.setParameter("eid", enrollmentId);
            query.setParameter("sid", sessionId);
            List<Attendance> list = query.getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    protected void validateEntity(Attendance entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Thực thể điểm danh không được null");
        }
        if (entity.getEnrollment() == null) {
            throw new IllegalArgumentException("Đăng ký không được null");
        }
        if (entity.getSession() == null) {
            throw new IllegalArgumentException("Buổi học không được null");
        }
        if (entity.getAttendanceID() == null || entity.getAttendanceID().isBlank()) {
            throw new IllegalArgumentException("Mã điểm danh không được null");
        }
        if (entity.getStatus() == null) {
            throw new IllegalArgumentException("Trạng thái điểm danh không được null");
        }
        if (entity.getCheckinTime() == null) {
            throw new IllegalArgumentException("Thời gian check-in không được null");
        }
    }
}