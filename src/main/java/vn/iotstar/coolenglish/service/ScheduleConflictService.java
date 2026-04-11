package vn.iotstar.coolenglish.service;

import vn.iotstar.coolenglish.builder.ScheduleBuilder.ScheduleConflictChecker;
import vn.iotstar.coolenglish.dao.impl.ScheduleDAO;
import vn.iotstar.coolenglish.entity.Session;

public class ScheduleConflictService implements ScheduleConflictChecker {

    private final ScheduleDAO dao = new ScheduleDAO();

    @Override
    public boolean hasConflict(Session session, String excludeScheduleID) {
        return dao.existsConflictForSession(session, excludeScheduleID);
    }
}
