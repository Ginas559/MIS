package vn.iotstar.coolenglish.service;

import vn.iotstar.coolenglish.entity.ExamResult;

public interface ResultObserver {
    void update(ExamResult result);
}
