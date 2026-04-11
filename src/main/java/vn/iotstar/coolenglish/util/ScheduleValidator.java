package vn.iotstar.coolenglish.util;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Schedule;
import vn.iotstar.coolenglish.entity.Session;
import vn.iotstar.coolenglish.entity.Room;
import vn.iotstar.coolenglish.entity.Teacher;

public final class ScheduleValidator {

    private ScheduleValidator() {
    }

    public static void validateNoConflicts(Schedule schedule) {
        if (schedule == null) {
            return;
        }

        List<Session> sessions = schedule.getSessions();
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        // Basic field validation
        for (Session s : sessions) {
            if (s.getSessionDate() == null) {
                throw new IllegalArgumentException("Session date is required for session: " + s.getSessionID());
            }
            if (s.getStartTime() == null || s.getEndTime() == null) {
                throw new IllegalArgumentException("Session startTime and endTime are required for session: " + s.getSessionID());
            }
            if (!s.getStartTime().isBefore(s.getEndTime())) {
                throw new IllegalArgumentException("Session startTime must be before endTime for session: " + s.getSessionID());
            }
        }

        // 1) Check duplicates/overlaps inside the same schedule
        for (int i = 0; i < sessions.size(); i++) {
            Session a = sessions.get(i);
            for (int j = i + 1; j < sessions.size(); j++) {
                Session b = sessions.get(j);
                if (datesEqual(a.getSessionDate(), b.getSessionDate())) {
                    if (timesOverlap(a.getStartTime(), a.getEndTime(), b.getStartTime(), b.getEndTime())) {
                        Room ra = a.getRoom();
                        Room rb = b.getRoom();
                        if (ra != null && rb != null && ra.getRoomID() != null && ra.getRoomID().equals(rb.getRoomID())) {
                            throw new IllegalStateException("Conflict inside schedule: same room " + ra.getRoomID() + " overlapping on " + a.getSessionDate());
                        }
                        Teacher ta = a.getTeacher();
                        Teacher tb = b.getTeacher();
                        if (ta != null && tb != null && ta.getTeacherID() != null && ta.getTeacherID().equals(tb.getTeacherID())) {
                            throw new IllegalStateException("Conflict inside schedule: same teacher " + ta.getTeacherID() + " overlapping on " + a.getSessionDate());
                        }
                    }
                }
            }
        }

        // 2) Check conflicts against existing sessions in DB
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String sid = schedule.getScheduleID();
            for (Session s : sessions) {
                Date date = s.getSessionDate();
                LocalTime start = s.getStartTime();
                LocalTime end = s.getEndTime();

                // Room conflicts
                if (s.getRoom() != null && s.getRoom().getRoomID() != null) {
                    String jpql = "SELECT se FROM Session se WHERE se.sessionDate = :date AND se.room.roomID = :roomId";
                    if (sid != null) {
                        jpql += " AND (se.schedule IS NULL OR se.schedule.scheduleID <> :sid)";
                    }
                    TypedQuery<Session> q = em.createQuery(jpql, Session.class);
                    q.setParameter("date", date);
                    q.setParameter("roomId", s.getRoom().getRoomID());
                    if (sid != null) {
                        q.setParameter("sid", sid);
                    }
                    List<Session> found = q.getResultList();
                    for (Session ex : found) {
                        if (ex.getStartTime() == null || ex.getEndTime() == null) {
                            throw new IllegalStateException("Room already booked on " + date + " in room " + s.getRoom().getRoomID());
                        }
                        if (timesOverlap(start, end, ex.getStartTime(), ex.getEndTime())) {
                            throw new IllegalStateException("Room already booked on " + date + " at overlapping time in room " + s.getRoom().getRoomID());
                        }
                    }
                }

                // Teacher conflicts
                if (s.getTeacher() != null && s.getTeacher().getTeacherID() != null) {
                    String jpql = "SELECT se FROM Session se WHERE se.sessionDate = :date AND se.teacher.teacherID = :teacherId";
                    if (sid != null) {
                        jpql += " AND (se.schedule IS NULL OR se.schedule.scheduleID <> :sid)";
                    }
                    TypedQuery<Session> q = em.createQuery(jpql, Session.class);
                    q.setParameter("date", date);
                    q.setParameter("teacherId", s.getTeacher().getTeacherID());
                    if (sid != null) {
                        q.setParameter("sid", sid);
                    }
                    List<Session> found = q.getResultList();
                    for (Session ex : found) {
                        if (ex.getStartTime() == null || ex.getEndTime() == null) {
                            throw new IllegalStateException("Teacher already assigned on " + date + " for teacher " + s.getTeacher().getTeacherID());
                        }
                        if (timesOverlap(start, end, ex.getStartTime(), ex.getEndTime())) {
                            throw new IllegalStateException("Teacher already assigned on " + date + " at overlapping time for teacher " + s.getTeacher().getTeacherID());
                        }
                    }
                }
            }
        } finally {
            em.close();
        }
    }

    private static boolean datesEqual(Date a, Date b) {
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private static boolean timesOverlap(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
        if (aStart == null || aEnd == null || bStart == null || bEnd == null) return false;
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }
}
