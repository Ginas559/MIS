package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Schedule;

public class ScheduleDAO extends AbstractDAO<Schedule> {

    @Override
    protected void validateEntity(Schedule entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Schedule entity is required.");
        }
    }

    public Schedule findByScheduleID(String scheduleID) {
        return findById(scheduleID, Schedule.class);
    }

    public boolean existsByScheduleID(String scheduleID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(s) FROM Schedule s WHERE s.scheduleID = :scheduleID", Long.class);
            query.setParameter("scheduleID", scheduleID);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public Schedule findWithSessionsByScheduleID(String scheduleID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Schedule> query = em.createQuery(
                    "SELECT DISTINCT s FROM Schedule s LEFT JOIN FETCH s.sessions WHERE s.scheduleID = :scheduleID",
                    Schedule.class);
            query.setParameter("scheduleID", scheduleID);
            List<Schedule> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
}

