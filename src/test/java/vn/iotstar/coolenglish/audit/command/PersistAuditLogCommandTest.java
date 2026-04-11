package vn.iotstar.coolenglish.audit.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.audit.receiver.AuditLogReceiver;
import vn.iotstar.coolenglish.entity.AuditLog;

class PersistAuditLogCommandTest {

    @Test
    void executeAndUndoShouldDelegateToReceiver() {
        InMemoryAuditLogReceiver receiver = new InMemoryAuditLogReceiver();
        PersistAuditLogCommand command = new PersistAuditLogCommand(receiver, new AuditLog());

        command.execute();
        command.undo();

        assertEquals(1, receiver.saveCallCount);
        assertEquals(1, receiver.deleteCallCount);
        assertEquals(1L, receiver.deletedId);
    }

    @Test
    void invokerShouldStoreHistoryAndUndoLastCommand() {
        AuditCommandInvoker invoker = AuditCommandInvoker.getInstance();
        invoker.clearHistory();

        InMemoryAuditLogReceiver receiver = new InMemoryAuditLogReceiver();
        PersistAuditLogCommand command = new PersistAuditLogCommand(receiver, new AuditLog());

        invoker.executeCommand(command);
        assertEquals(1, invoker.historySize());

        boolean undone = invoker.undoLast();

        assertTrue(undone);
        assertEquals(0, invoker.historySize());
        assertEquals(1, receiver.deleteCallCount);
    }

    @Test
    void invokerShouldKeepHistoryWhenUndoFails() {
        AuditCommandInvoker invoker = AuditCommandInvoker.getInstance();
        invoker.clearHistory();

        AuditCommand failingUndoCommand = new AuditCommand() {
            @Override
            public void execute() {
                // No-op
            }

            @Override
            public void undo() {
                throw new SecurityException("undo blocked");
            }
        };

        invoker.executeCommand(failingUndoCommand);
        assertEquals(1, invoker.historySize());

        assertThrows(SecurityException.class, invoker::undoLast);
        assertEquals(1, invoker.historySize());
    }

    private static final class InMemoryAuditLogReceiver extends AuditLogReceiver {

        private int saveCallCount;
        private int deleteCallCount;
        private Long deletedId;

        @Override
        public Long save(AuditLog auditLog) {
            saveCallCount++;
            return 1L;
        }

        @Override
        public void deleteById(Long auditId) {
            deleteCallCount++;
            deletedId = auditId;
        }
    }
}
