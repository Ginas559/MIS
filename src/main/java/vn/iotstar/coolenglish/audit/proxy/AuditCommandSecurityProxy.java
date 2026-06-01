package vn.iotstar.coolenglish.audit.proxy;

import vn.iotstar.coolenglish.audit.command.AuditCommand;
import vn.iotstar.coolenglish.audit.context.AuditActor;
import vn.iotstar.coolenglish.audit.context.AuditActorContext;

public class AuditCommandSecurityProxy implements AuditCommand {

    private final AuditCommand realCommand;
    private final AuditActor actor;

    public AuditCommandSecurityProxy(AuditCommand realCommand, AuditActor actor) {
        this.realCommand = realCommand;
        this.actor = actor;
    }

    @Override
    public void execute() {
        checkExecutePermission();
        realCommand.execute();
    }

    @Override
    public void undo() {
        checkUndoPermission();
        realCommand.undo();
    }

    private void checkExecutePermission() {
        // Allow all roles to execute audit logging so every actor can be traced.
    }

    private void checkUndoPermission() {
        AuditActor effectiveActor = AuditActorContext.getCurrentActor();
        if (effectiveActor == null) {
            effectiveActor = actor;
        }

        if (effectiveActor == null || effectiveActor.role() == null) {
            return;
        }

        String role = effectiveActor.role();
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new SecurityException("Ban khong co quyen undo audit log.");
        }
    }
}
