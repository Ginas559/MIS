package vn.iotstar.coolenglish.service;

import java.util.List;

import vn.iotstar.coolenglish.builder.ScheduleBuilder;
import vn.iotstar.coolenglish.dao.impl.ScheduleDAO;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Schedule;
import vn.iotstar.coolenglish.entity.Session;

public class ScheduleService {

    private final ScheduleDAO scheduleDAO = new ScheduleDAO();
    private final ScheduleConflictService conflictService = new ScheduleConflictService();

    public Schedule createSchedule(String scheduleID, java.util.Date createDate, String description,
                                   List<Session> sessions, EnglishClass linkedClass) {
        ScheduleBuilder builder = new ScheduleBuilder()
                .withScheduleID(scheduleID)
                .withCreateDate(createDate)
                .withDescription(description)
                .withConflictChecker(conflictService);

        if (linkedClass != null) {
            builder.withEnglishClass(linkedClass);
        }

        if (sessions != null) {
            for (Session s : sessions) {
                builder.addSession(s);
            }
        }

        return builder.build();
    }

    public void saveSchedule(Schedule schedule) {
        scheduleDAO.insert(schedule);
    }

    public Schedule createAndSaveSchedule(String scheduleID, java.util.Date createDate, String description,
                                          List<Session> sessions, EnglishClass linkedClass) {
        Schedule schedule = createSchedule(scheduleID, createDate, description, sessions, linkedClass);
        saveSchedule(schedule);
        return schedule;
    }

    public Schedule findSchedule(String scheduleID) {
        return scheduleDAO.findWithSessionsByScheduleID(scheduleID);
    }

    public List<Schedule> findAllSchedules() {
        return scheduleDAO.findAllWithEnglishClass();
    }

    public void updateSchedule(Schedule schedule) {
        scheduleDAO.update(schedule);
    }

    public void deleteSchedule(String scheduleID) {
        scheduleDAO.delete(scheduleID, Schedule.class);
    }
}
