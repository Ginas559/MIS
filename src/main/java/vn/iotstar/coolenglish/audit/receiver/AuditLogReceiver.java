package vn.iotstar.coolenglish.audit.receiver;

import jakarta.persistence.EntityManager;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.AuditLog;

public class AuditLogReceiver {

    public Long save(AuditLog auditLog) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(auditLog);
            em.getTransaction().commit();
            return auditLog.getAuditId();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void deleteById(Long auditId) {
        if (auditId == null) {
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            AuditLog auditLog = em.find(AuditLog.class, auditId);
            if (auditLog != null) {
                em.remove(auditLog);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }
}
