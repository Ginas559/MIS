package vn.iotstar.coolenglish.audit.command;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class AuditCommandInvoker {

    private static final AuditCommandInvoker INSTANCE = new AuditCommandInvoker();
    private final Deque<AuditCommand> history = new ConcurrentLinkedDeque<>();

    private AuditCommandInvoker() {
    }

    public static AuditCommandInvoker getInstance() {
        return INSTANCE;
    }

    public void executeCommand(AuditCommand command) {
        command.execute();
        history.push(command);
    }

    public boolean undoLast() {
        AuditCommand command = history.peek();
        if (command == null) {
            return false;
        }
        command.undo();
        history.poll();
        return true;
    }

    public int historySize() {
        return history.size();
    }

    public void clearHistory() {
        history.clear();
    }
}
