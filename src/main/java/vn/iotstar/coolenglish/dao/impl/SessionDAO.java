package vn.iotstar.coolenglish.dao.impl;

import vn.iotstar.coolenglish.entity.Session;

public class SessionDAO extends AbstractDAO<Session> {

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
