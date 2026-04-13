package vn.iotstar.coolenglish.audit.command;

import vn.iotstar.coolenglish.audit.receiver.AuditLogReceiver;
import vn.iotstar.coolenglish.entity.AuditLog;

public class PersistAuditLogCommand implements AuditCommand {

    private final AuditLogReceiver receiver;
    private final AuditLog auditLog;
    private Long persistedAuditId;

    public PersistAuditLogCommand(AuditLogReceiver receiver, AuditLog auditLog) {
        this.receiver = receiver;
        this.auditLog = auditLog;
    }

    @Override
    public void execute() {
        persistedAuditId = receiver.save(auditLog);
    }

    @Override
    public void undo() {
        if (persistedAuditId != null) {
            receiver.appendUndoEntry(persistedAuditId);
        }
    }
}
