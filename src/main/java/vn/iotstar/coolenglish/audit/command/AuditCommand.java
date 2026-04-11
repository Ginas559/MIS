package vn.iotstar.coolenglish.audit.command;

public interface AuditCommand {

    void execute();

    void undo();
}
