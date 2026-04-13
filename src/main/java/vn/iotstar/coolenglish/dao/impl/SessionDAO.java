package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Session;

public class SessionDAO extends AbstractDAO<Session> {

    public Session findByIdWithSchedule(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return null;
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Session> query = em.createQuery(
                    "SELECT s FROM Session s JOIN FETCH s.schedule WHERE s.sessionID = :id",
                    Session.class);
            query.setParameter("id", sessionId);
            List<Session> list = query.getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    protected void validateEntity(Session entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Buổi học là bắt buộc");
        }
        if (entity.getSessionDate() == null || entity.getStartTime() == null || entity.getEndTime() == null) {
            throw new IllegalArgumentException("Ngày học và thời gian bắt đầu/kết thúc là bắt buộc.");
        }
        if (!entity.getStartTime().isBefore(entity.getEndTime())) {
            throw new IllegalArgumentException("Thời gian bắt đầu phải trước thời gian kết thúc");
        }
    }
}
