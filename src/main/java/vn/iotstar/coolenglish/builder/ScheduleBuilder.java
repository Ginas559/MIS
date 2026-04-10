package vn.iotstar.coolenglish.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vn.iotstar.coolenglish.entity.Schedule;
import vn.iotstar.coolenglish.entity.Session;

public class ScheduleBuilder {

    private String scheduleID;
    private Date createDate;
    private String description;

    private final List<Session> sessions = new ArrayList<>();

    private ScheduleConflictChecker conflictChecker;

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

        String id = this.scheduleID != null ? this.scheduleID : "SCH-" + System.currentTimeMillis();

        // Conflict checker must be supplied by caller (keeps Builder free of DB internals)
        if (conflictChecker == null) {
            throw new IllegalStateException("Cần cung cấp bộ kiểm tra xung đột lịch");
        }

        // Conflict check for each session (room/teacher/time overlap) - exclude this schedule id
        for (Session s : sessions) {
            if (conflictChecker.hasConflict(s, id)) {
                throw new IllegalStateException("Phát hiện xung đột lịch (phòng hoặc giáo viên) vào ngày " + s.getSessionDate());
            }
        }

        Schedule schedule = new Schedule();
        schedule.setScheduleID(id);
        schedule.setCreateDate(createDate);
        schedule.setDescription(description);
        // attach sessions
        for (Session s : sessions) {
            s.setSchedule(schedule);
        }
        schedule.setSessions(sessions);
        schedule.setTotalSessions(sessions.size());

        return schedule;
    }

    @FunctionalInterface
    public interface ScheduleConflictChecker {
        boolean hasConflict(Session session, String excludeScheduleID);
    }
}
