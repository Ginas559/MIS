package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Notification;

public class NotificationDAO extends AbstractDAO<Notification> {

    @Override
    protected void validateEntity(Notification entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Notification entity is required.");
        }
        if (entity.getNotificationID() == null || entity.getNotificationID().isBlank()) {
            throw new IllegalArgumentException("Notification ID is required.");
        }
        if (entity.getRelatedID() == null || entity.getRelatedID().isBlank()) {
            throw new IllegalArgumentException("Notification relatedID is required.");
        }
        if (entity.getContent() == null || entity.getContent().isBlank()) {
            throw new IllegalArgumentException("Notification content is required.");
        }
    }

    public List<Notification> findByRelatedID(String relatedID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Notification> query = em.createQuery(
                    "SELECT n FROM Notification n WHERE n.relatedID = :relatedID ORDER BY n.sendDate DESC",
                    Notification.class);
            query.setParameter("relatedID", relatedID);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
