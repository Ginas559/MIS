package vn.iotstar.coolenglish.builder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Schedule;
import vn.iotstar.coolenglish.entity.Session;

public class ScheduleBuilder {

    private String scheduleID;
    private Date createDate;
    private String description;

    private final List<Session> sessions = new ArrayList<>();

    private ScheduleConflictChecker conflictChecker;

    private EnglishClass englishClass;

    public ScheduleBuilder withScheduleID(String scheduleID) {
        this.scheduleID = scheduleID;
        return this;
    }

    public ScheduleBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public ScheduleBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ScheduleBuilder addSession(Session session) {
        if (session != null) {
            this.sessions.add(session);
        }
        return this;
    }

    public ScheduleBuilder sessions(List<Session> sessions) {
        this.sessions.clear();
        if (sessions != null) {
            this.sessions.addAll(sessions);
        }
        return this;
    }

    public ScheduleBuilder withConflictChecker(ScheduleConflictChecker checker) {
        this.conflictChecker = checker;
        return this;
    }

    public ScheduleBuilder withEnglishClass(EnglishClass englishClass) {
        this.englishClass = englishClass;
        return this;
    }

    public Schedule build() {
        if (createDate == null) {
            throw new IllegalArgumentException("Ngày tạo là bắt buộc");
        }

        if (sessions.isEmpty()) {
            throw new IllegalArgumentException("Lịch học phải có ít nhất một buổi học");
        }

        for (Session s : sessions) {
            if (s.getSessionDate() == null || s.getStartTime() == null || s.getEndTime() == null) {
                throw new IllegalArgumentException("Ngày học và thời gian của buổi học là bắt buộc");
            }
            if (!s.getStartTime().isBefore(s.getEndTime())) {
                throw new IllegalArgumentException("Thời gian bắt đầu phải trước thời gian kết thúc");
            }
        }

        validateSessionDatesWithinClassPeriod();

        validateRoomGapBetweenSessions();

        String id = this.scheduleID != null ? this.scheduleID : "SCH-" + System.currentTimeMillis();

        if (englishClass != null) {
            Schedule existing = englishClass.getSchedule();
            if (existing != null && !existing.getScheduleID().equals(id)) {
                throw new IllegalStateException(
                        "Lớp " + englishClass.getClassID() + " đã gắn lịch khác (" + existing.getScheduleID() + ").");
            }
        }

        if (conflictChecker == null) {
            throw new IllegalStateException("Cần cung cấp bộ kiểm tra xung đột lịch");
        }

        for (Session s : sessions) {
            if (conflictChecker.hasConflict(s, id)) {
                throw new IllegalStateException("Phát hiện xung đột lịch (phòng hoặc giáo viên) vào ngày " + s.getSessionDate());
            }
        }

        Schedule schedule = new Schedule();
        schedule.setScheduleID(id);
        schedule.setCreateDate(createDate);
        schedule.setDescription(description);
        schedule.setEnglishClass(englishClass);
        for (Session s : sessions) {
            s.setSchedule(schedule);
        }
        schedule.setSessions(sessions);
        schedule.setTotalSessions(sessions.size());

        return schedule;
    }

    private void validateSessionDatesWithinClassPeriod() {
        if (englishClass == null) {
            return; 
        }

        if (englishClass.getStartDate() == null || englishClass.getEndDate() == null) {
            return; 
        }

        for (Session session : sessions) {
            if (session.getSessionDate() == null) {
                continue;
            }

            java.time.LocalDate sessionDate = new java.sql.Date(session.getSessionDate().getTime()).toLocalDate();

            if (sessionDate.isBefore(englishClass.getStartDate())) {
                throw new IllegalArgumentException(
                    "Buổi học ngày " + sessionDate + " diễn ra trước ngày bắt đầu lớp (" + englishClass.getStartDate() + ")");
            }

            if (sessionDate.isAfter(englishClass.getEndDate())) {
                throw new IllegalArgumentException(
                    "Buổi học ngày " + sessionDate + " diễn ra sau ngày kết thúc lớp (" + englishClass.getEndDate() + ")");
            }
        }
    }

    private void validateRoomGapBetweenSessions() {
        for (int i = 0; i < sessions.size(); i++) {
            Session first = sessions.get(i);
            for (int j = i + 1; j < sessions.size(); j++) {
                Session second = sessions.get(j);
                if (haveSameDateAndRoom(first, second) && isLessThanFifteenMinutesApart(first, second)) {
                    throw new IllegalArgumentException("Hai buổi học cùng phòng cùng ngày phải cách nhau ít nhất 15 phút");
                }
            }
        }
    }

    private boolean haveSameDateAndRoom(Session first, Session second) {
        if (first.getRoom() == null || second.getRoom() == null) {
            return false;
        }
        if (first.getRoom().getRoomID() == null || second.getRoom().getRoomID() == null) {
            return false;
        }
        return first.getSessionDate().equals(second.getSessionDate())
                && first.getRoom().getRoomID().equals(second.getRoom().getRoomID());
    }

    private boolean isLessThanFifteenMinutesApart(Session first, Session second) {
        LocalTime firstStart = first.getStartTime();
        LocalTime firstEnd = first.getEndTime();
        LocalTime secondStart = second.getStartTime();
        LocalTime secondEnd = second.getEndTime();

        if (firstEnd.isBefore(secondStart) || firstEnd.equals(secondStart)) {
            return firstEnd.plusMinutes(15).isAfter(secondStart);
        }

        if (secondEnd.isBefore(firstStart) || secondEnd.equals(firstStart)) {
            return secondEnd.plusMinutes(15).isAfter(firstStart);
        }

        return true;
    }

    @FunctionalInterface
    public interface ScheduleConflictChecker {
        boolean hasConflict(Session session, String excludeScheduleID);
    }
}
