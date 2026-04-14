package vn.iotstar.coolenglish.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.iotstar.coolenglish.dao.impl.NotificationDAO;

public class ResultSubject {

    private static final ResultSubject INSTANCE = new ResultSubject();
    private static final ExecutorService NOTIFICATION_EXECUTOR = Executors.newFixedThreadPool(2);
    private final List<ResultObserver> observers = new ArrayList<>();

    private ResultSubject() {
        registerObserver(new NotificationObserver(new NotificationDAO()));
    }

    public static ResultSubject getInstance() {
        return INSTANCE;
    }

    public synchronized void registerObserver(ResultObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public synchronized void removeObserver(ResultObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(vn.iotstar.coolenglish.entity.ExamResult result) {
        if (result == null) {
            return;
        }

        List<ResultObserver> snapshot;
        synchronized (this) {
            snapshot = new ArrayList<>(observers);
        }

        for (ResultObserver observer : snapshot) {
            NOTIFICATION_EXECUTOR.submit(() -> observer.update(result));
        }
    }
}
