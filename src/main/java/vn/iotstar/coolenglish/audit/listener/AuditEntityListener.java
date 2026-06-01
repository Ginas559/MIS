package vn.iotstar.coolenglish.audit.listener;

import java.time.LocalDateTime;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;
import vn.iotstar.coolenglish.audit.command.AuditCommand;
import vn.iotstar.coolenglish.audit.command.AuditCommandInvoker;
import vn.iotstar.coolenglish.audit.command.PersistAuditLogCommand;
import vn.iotstar.coolenglish.audit.context.AuditActor;
import vn.iotstar.coolenglish.audit.context.AuditActorContext;
import vn.iotstar.coolenglish.audit.proxy.AuditCommandSecurityProxy;
import vn.iotstar.coolenglish.audit.receiver.AuditLogReceiver;
import vn.iotstar.coolenglish.audit.util.AuditSnapshotBuilder;
import vn.iotstar.coolenglish.entity.AuditLog;

public class AuditEntityListener {

    @PostPersist
    public void onPostPersist(Object entity) {
        record("INSERT", entity);
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        record("UPDATE", entity);
    }

    @PreRemove
    public void onPreRemove(Object entity) {
        record("DELETE", entity);
    }

    private void record(String action, Object entity) {
        if (entity instanceof AuditLog) {
            return;
        }

        AuditActor actor = AuditActorContext.getCurrentActor();

        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityName(entity.getClass().getSimpleName());
        auditLog.setEntityId(AuditSnapshotBuilder.resolveEntityId(entity));
        auditLog.setChangedAt(LocalDateTime.now());
        auditLog.setDetails(AuditSnapshotBuilder.buildDetails(entity));

        if (actor != null) {
            auditLog.setActorId(actor.userId());
            auditLog.setActorUsername(actor.username());
            auditLog.setActorRole(actor.role());
        } else {
            auditLog.setActorUsername("SYSTEM");
            auditLog.setActorRole("SYSTEM");
        }
        AuditCommand command = new PersistAuditLogCommand(new AuditLogReceiver(), auditLog);
        AuditCommand securedCommand = new AuditCommandSecurityProxy(command, actor);
        AuditCommandInvoker.getInstance().executeCommand(securedCommand);
    }
}
