package vn.iotstar.coolenglish.service;

import java.util.List;

import vn.iotstar.coolenglish.builder.ScheduleBuilder;
import vn.iotstar.coolenglish.dao.impl.ScheduleDAO;
import vn.iotstar.coolenglish.entity.Schedule;
import vn.iotstar.coolenglish.entity.Session;

public class ScheduleService {

    private final ScheduleDAO scheduleDAO = new ScheduleDAO();
    private final ScheduleConflictService conflictService = new ScheduleConflictService();

    /**
     * Create a new schedule using the Builder pattern with conflict checking.
     *
     * @param scheduleID     unique identifier for schedule
     * @param createDate     date when schedule is created (java.util.Date)
     * @param description    optional description
     * @param sessions       list of sessions to add to schedule
     * @return created Schedule object
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException    if conflict is detected
     */
    public Schedule createSchedule(String scheduleID, java.util.Date createDate, String description,
                                   List<Session> sessions) {
        ScheduleBuilder builder = new ScheduleBuilder()
                .withScheduleID(scheduleID)
                .withCreateDate(createDate)
                .withDescription(description)
                .withConflictChecker(conflictService);

        if (sessions != null) {
            for (Session s : sessions) {
                builder.addSession(s);
            }
        }

        return builder.build();
    }

    /**
     * Save a schedule to the database.
     *
     * @param schedule the schedule to save
     */
    public void saveSchedule(Schedule schedule) {
        scheduleDAO.insert(schedule);
    }

    /**
     * Create and save schedule in one operation.
     *
     * @param scheduleID  unique identifier for schedule
     * @param createDate  date when schedule is created
     * @param description optional description
     * @param sessions    list of sessions
     * @return saved Schedule object
     */
    public Schedule createAndSaveSchedule(String scheduleID, java.util.Date createDate, String description,
                                          List<Session> sessions) {
        Schedule schedule = createSchedule(scheduleID, createDate, description, sessions);
        saveSchedule(schedule);
        return schedule;
    }

    /**
     * Find schedule by ID.
     *
     * @param scheduleID the schedule id
     * @return Schedule or null if not found
     */
    public Schedule findSchedule(String scheduleID) {
        return scheduleDAO.findWithSessionsByScheduleID(scheduleID);
    }

    /**
     * Find all schedules.
     *
     * @return list of all schedules
     */
    public List<Schedule> findAllSchedules() {
        return scheduleDAO.findAll(Schedule.class);
    }

    /**
     * Update an existing schedule.
     *
     * @param schedule the schedule to update
     */
    public void updateSchedule(Schedule schedule) {
        scheduleDAO.update(schedule);
    }

    /**
     * Delete a schedule by ID.
     *
     * @param scheduleID the schedule id
     */
    public void deleteSchedule(String scheduleID) {
        scheduleDAO.delete(scheduleID, Schedule.class);
    }
}
