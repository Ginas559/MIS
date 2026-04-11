package vn.iotstar.coolenglish.dao.impl;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Schedule;
import vn.iotstar.coolenglish.entity.Session;

public class ScheduleDAO extends AbstractDAO<Schedule> {

    @Override
    protected void validateEntity(Schedule entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Lịch học là bắt buộc");
        }
        // final safety: ensure no conflicts exist for any session in this schedule
        if (entity.getSessions() != null) {
            for (Session s : entity.getSessions()) {
                if (existsConflictForSession(s, entity.getScheduleID())) {
                    throw new IllegalStateException("Phát hiện xung đột buổi học " + s.getSessionID());
                }
            }
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
            		"SELECT DISTINCT s FROM Schedule s " +
                            "LEFT JOIN FETCH s.sessions se " +
                            "LEFT JOIN FETCH se.room " +
                            "LEFT JOIN FETCH se.teacher " +
                            "WHERE s.scheduleID = :scheduleID",
                    Schedule.class);
            query.setParameter("scheduleID", scheduleID);
            List<Schedule> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
    

    public boolean existsConflictForSession(Session session) {
        return existsConflictForSession(session, null);
    }

    public boolean existsConflictForSession(Session session, String excludeScheduleID) {
        if (session == null || session.getSessionDate() == null) {
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            LocalTime start = session.getStartTime();
            LocalTime end = session.getEndTime();

            // Check room conflicts
            if (session.getRoom() != null && session.getRoom().getRoomID() != null && start != null && end != null) {
                String jpql = "SELECT COUNT(se) FROM Session se WHERE se.sessionDate = :date AND se.room.roomID = :roomId"
                        + " AND (CAST(se.startTime AS string) < CAST(:endTime AS string) AND CAST(se.endTime AS string) > CAST(:startTime AS string))";
                if (excludeScheduleID != null) {
                    jpql += " AND (se.schedule IS NULL OR se.schedule.scheduleID <> :sid)";
                }
                TypedQuery<Long> q = em.createQuery(jpql, Long.class);
                q.setParameter("date", session.getSessionDate());
                q.setParameter("roomId", session.getRoom().getRoomID());
                q.setParameter("startTime", start.toString());
                q.setParameter("endTime", end.toString());
                if (excludeScheduleID != null) {
                    q.setParameter("sid", excludeScheduleID);
                }
                if (q.getSingleResult() > 0) {
                    return true;
                }
            }

            // Check teacher conflicts
            if (session.getTeacher() != null && session.getTeacher().getTeacherID() != null && start != null
                    && end != null) {
                String jpql = "SELECT COUNT(se) FROM Session se WHERE se.sessionDate = :date AND se.teacher.teacherID = :tid"
                        + " AND (CAST(se.startTime AS string) < CAST(:endTime AS string) AND CAST(se.endTime AS string) > CAST(:startTime AS string))";
                if (excludeScheduleID != null) {
                    jpql += " AND (se.schedule IS NULL OR se.schedule.scheduleID <> :sid)";
                }
                TypedQuery<Long> q = em.createQuery(jpql, Long.class);
                q.setParameter("date", session.getSessionDate());
                q.setParameter("tid", session.getTeacher().getTeacherID());
                q.setParameter("startTime", start.toString());
                q.setParameter("endTime", end.toString());
                if (excludeScheduleID != null) {
                    q.setParameter("sid", excludeScheduleID);
                }
                if (q.getSingleResult() > 0) {
                    return true;
                }
            }

            return false;
        } finally {
            em.close();
        }
    }
}

