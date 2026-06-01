package vn.iotstar.coolenglish.audit.proxy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.audit.command.AuditCommand;
import vn.iotstar.coolenglish.audit.context.AuditActor;

class AuditCommandSecurityProxyTest {

    @Test
    void shouldAllowExecuteForAllRoles() {
        CounterCommand adminCommand = new CounterCommand();
        CounterCommand staffCommand = new CounterCommand();
        CounterCommand studentCommand = new CounterCommand();

        AuditCommand adminProxy = new AuditCommandSecurityProxy(adminCommand, new AuditActor(1L, "admin", "ADMIN"));
        AuditCommand staffProxy = new AuditCommandSecurityProxy(staffCommand, new AuditActor(2L, "staff", "STAFF"));
        AuditCommand studentProxy = new AuditCommandSecurityProxy(studentCommand, new AuditActor(3L, "student", "STUDENT"));

        assertDoesNotThrow(adminProxy::execute);
        assertDoesNotThrow(staffProxy::execute);
        assertDoesNotThrow(studentProxy::execute);
        assertEquals(1, adminCommand.executeCount);
        assertEquals(1, staffCommand.executeCount);
        assertEquals(1, studentCommand.executeCount);
    }

    @Test
    void shouldAllowUndoForAdminOnly() {
        CounterCommand adminCommand = new CounterCommand();
        CounterCommand staffCommand = new CounterCommand();
        CounterCommand studentCommand = new CounterCommand();

        AuditCommand adminProxy = new AuditCommandSecurityProxy(adminCommand, new AuditActor(1L, "admin", "ADMIN"));
        AuditCommand staffProxy = new AuditCommandSecurityProxy(staffCommand, new AuditActor(2L, "staff", "STAFF"));
        AuditCommand studentProxy = new AuditCommandSecurityProxy(studentCommand, new AuditActor(3L, "student", "STUDENT"));

        assertDoesNotThrow(adminProxy::undo);
        assertEquals(1, adminCommand.undoCount);

        assertThrows(SecurityException.class, staffProxy::undo);
        assertThrows(SecurityException.class, studentProxy::undo);
        assertEquals(0, staffCommand.undoCount);
        assertEquals(0, studentCommand.undoCount);
    }

    private static final class CounterCommand implements AuditCommand {

        private int executeCount;
        private int undoCount;

        @Override
        public void execute() {
            executeCount++;
        }

        @Override
        public void undo() {
            undoCount++;
        }
    }
}
