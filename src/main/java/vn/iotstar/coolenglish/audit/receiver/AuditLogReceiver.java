package vn.iotstar.coolenglish.audit.receiver;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import vn.iotstar.coolenglish.audit.context.AuditActor;
import vn.iotstar.coolenglish.audit.context.AuditActorContext;
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

    public void appendUndoEntry(Long targetAuditId) {
        if (targetAuditId == null) {
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            AuditLog target = em.find(AuditLog.class, targetAuditId);
            AuditActor actor = AuditActorContext.getCurrentActor();

            AuditLog reversal = new AuditLog();
            reversal.setAction("AUDIT_UNDO");
            reversal.setEntityName("AuditLog");
            reversal.setEntityId(String.valueOf(targetAuditId));
            reversal.setChangedAt(LocalDateTime.now());

            if (actor != null) {
                reversal.setActorId(actor.userId());
                reversal.setActorUsername(actor.username());
                reversal.setActorRole(actor.role());
            } else {
                reversal.setActorUsername("SYSTEM");
                reversal.setActorRole("SYSTEM");
            }

            if (target != null) {
                reversal.setDetails("Undo reference auditId=" + targetAuditId
                        + ", action=" + target.getAction()
                        + ", entity=" + target.getEntityName()
                        + ", entityId=" + target.getEntityId());
            } else {
                reversal.setDetails("Undo reference auditId=" + targetAuditId + " (target not found)");
            }

            em.persist(reversal);
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
