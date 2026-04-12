package vn.iotstar.coolenglish.builder;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.entity.Room;
import vn.iotstar.coolenglish.entity.Schedule;
import vn.iotstar.coolenglish.entity.Session;
import vn.iotstar.coolenglish.entity.Teacher;

@DisplayName("ScheduleBuilder Tests")
class ScheduleBuilderTest {

    private ScheduleBuilder builder;
    private Date createDate;
    private Session session1;
    private Session session2;
    private Room room1;
    private Teacher teacher1;

    @BeforeEach
    void setUp() {
        builder = new ScheduleBuilder();
        createDate = new Date();
        
        // Create sample entities
        room1 = new Room("ROOM-001", "Classroom A", 30, "Floor 1", "Available");
        teacher1 = new Teacher();
        teacher1.setTeacherID("TCH-001");

        session1 = new Session();
        session1.setSessionID("SESS-001");
        session1.setSessionDate(createDate);
        session1.setStartTime(LocalTime.of(8, 0));
        session1.setEndTime(LocalTime.of(9, 30));
        session1.setRoom(room1);
        session1.setTeacher(teacher1);

        session2 = new Session();
        session2.setSessionID("SESS-002");
        session2.setSessionDate(createDate);
        session2.setStartTime(LocalTime.of(10, 0));
        session2.setEndTime(LocalTime.of(11, 30));
        session2.setRoom(room1);
        session2.setTeacher(teacher1);
    }

    // ==================== Successful Build Tests ====================

    @Test
    @DisplayName("Should build schedule successfully with all required fields")
    void testBuildSuccessWithAllFields() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Schedule schedule = builder
                .withScheduleID("SCH-001")
                .withCreateDate(createDate)
                .withDescription("Morning Classes")
                .addSession(session1)
                .withConflictChecker(noConflictChecker)
                .build();

        assertNotNull(schedule);
        assertEquals("SCH-001", schedule.getScheduleID());
        assertEquals(createDate, schedule.getCreateDate());
        assertEquals("Morning Classes", schedule.getDescription());
        assertEquals(1, schedule.getTotalSessions());
        assertEquals(1, schedule.getSessions().size());
    }

    @Test
    @DisplayName("Should auto-generate scheduleID if not provided")
    void testBuildAutoGeneratesScheduleID() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Schedule schedule = builder
                .withCreateDate(createDate)
                .addSession(session1)
                .withConflictChecker(noConflictChecker)
                .build();

        assertNotNull(schedule);
        assertNotNull(schedule.getScheduleID());
        assertTrue(schedule.getScheduleID().startsWith("SCH-"));
    }

    @Test
    @DisplayName("Should build schedule with multiple sessions")
    void testBuildWithMultipleSessions() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Schedule schedule = builder
                .withScheduleID("SCH-002")
                .withCreateDate(createDate)
                .addSession(session1)
                .addSession(session2)
                .withConflictChecker(noConflictChecker)
                .build();

        assertNotNull(schedule);
        assertEquals(2, schedule.getTotalSessions());
        assertEquals(2, schedule.getSessions().size());
    }

    @Test
    @DisplayName("Should reject same-room sessions that are less than 15 minutes apart")
    void testBuildRejectsSameRoomSessionsTooClose() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Session session3 = new Session();
        session3.setSessionID("SESS-003");
        session3.setSessionDate(createDate);
        session3.setStartTime(LocalTime.of(9, 35));
        session3.setEndTime(LocalTime.of(10, 30));
        session3.setRoom(room1);
        session3.setTeacher(teacher1);

        builder
                .withCreateDate(createDate)
                .addSession(session1)
                .addSession(session3)
                .withConflictChecker(noConflictChecker);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );

        assertTrue(exception.getMessage().contains("15 phút"));
    }

    @Test
    @DisplayName("Should set sessions using sessions() method")
    void testBuildWithSessionsList() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;
        List<Session> sessionList = new ArrayList<>();
        sessionList.add(session1);
        sessionList.add(session2);

        Schedule schedule = builder
                .withScheduleID("SCH-003")
                .withCreateDate(createDate)
                .sessions(sessionList)
                .withConflictChecker(noConflictChecker)
                .build();

        assertNotNull(schedule);
        assertEquals(2, schedule.getTotalSessions());
        assertEquals(session1.getSchedule(), schedule);
        assertEquals(session2.getSchedule(), schedule);
    }

    @Test
    @DisplayName("Should attach sessions to schedule properly")
    void testSessionsAttachedToSchedule() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Schedule schedule = builder
                .withScheduleID("SCH-004")
                .withCreateDate(createDate)
                .addSession(session1)
                .withConflictChecker(noConflictChecker)
                .build();

        assertNotNull(schedule);
        assertEquals(schedule, session1.getSchedule());
    }

    // ==================== Validation Error Tests ====================

    @Test
    @DisplayName("Should throw when createDate is null")
    void testBuildThrowsWhenCreateDateNull() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        builder
                .addSession(session1)
                .withConflictChecker(noConflictChecker);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );
        assertTrue(exception.getMessage().contains("Ngày tạo là bắt buộc"));
    }

    @Test
    @DisplayName("Should throw when no sessions are added")
    void testBuildThrowsWhenNoSessions() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        builder
                .withCreateDate(createDate)
                .withConflictChecker(noConflictChecker);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );
        assertTrue(exception.getMessage().contains("Lịch học phải có ít nhất một buổi học"));
    }

    @Test
    @DisplayName("Should throw when session date is null")
    void testBuildThrowsWhenSessionDateNull() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Session invalidSession = new Session();
        invalidSession.setSessionID("SESS-INVALID");
        invalidSession.setStartTime(LocalTime.of(8, 0));
        invalidSession.setEndTime(LocalTime.of(9, 30));

        builder
                .withCreateDate(createDate)
                .addSession(invalidSession)
                .withConflictChecker(noConflictChecker);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );
        assertTrue(exception.getMessage().contains("Ngày học và thời gian của buổi học là bắt buộc"));
    }

    @Test
    @DisplayName("Should throw when session startTime is null")
    void testBuildThrowsWhenSessionStartTimeNull() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Session invalidSession = new Session();
        invalidSession.setSessionID("SESS-INVALID");
        invalidSession.setSessionDate(createDate);
        invalidSession.setEndTime(LocalTime.of(9, 30));

        builder
                .withCreateDate(createDate)
                .addSession(invalidSession)
                .withConflictChecker(noConflictChecker);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );
        assertTrue(exception.getMessage().contains("Ngày học và thời gian của buổi học là bắt buộc"));
    }

    @Test
    @DisplayName("Should throw when session endTime is null")
    void testBuildThrowsWhenSessionEndTimeNull() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Session invalidSession = new Session();
        invalidSession.setSessionID("SESS-INVALID");
        invalidSession.setSessionDate(createDate);
        invalidSession.setStartTime(LocalTime.of(8, 0));

        builder
                .withCreateDate(createDate)
                .addSession(invalidSession)
                .withConflictChecker(noConflictChecker);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );
        assertTrue(exception.getMessage().contains("Ngày học và thời gian của buổi học là bắt buộc"));
    }

    @Test
    @DisplayName("Should throw when startTime is after endTime")
    void testBuildThrowsWhenStartTimeAfterEndTime() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Session invalidSession = new Session();
        invalidSession.setSessionID("SESS-INVALID");
        invalidSession.setSessionDate(createDate);
        invalidSession.setStartTime(LocalTime.of(10, 0));
        invalidSession.setEndTime(LocalTime.of(8, 0));

        builder
                .withCreateDate(createDate)
                .addSession(invalidSession)
                .withConflictChecker(noConflictChecker);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );
        assertTrue(exception.getMessage().contains("Thời gian bắt đầu phải trước thời gian kết thúc"));
    }

    @Test
    @DisplayName("Should throw when startTime equals endTime")
    void testBuildThrowsWhenStartTimeEqualsEndTime() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Session invalidSession = new Session();
        invalidSession.setSessionID("SESS-INVALID");
        invalidSession.setSessionDate(createDate);
        invalidSession.setStartTime(LocalTime.of(8, 0));
        invalidSession.setEndTime(LocalTime.of(8, 0));

        builder
                .withCreateDate(createDate)
                .addSession(invalidSession)
                .withConflictChecker(noConflictChecker);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> builder.build()
        );
        assertTrue(exception.getMessage().contains("Thời gian bắt đầu phải trước thời gian kết thúc"));
    }

    // ==================== Conflict Checker Tests ====================

    @Test
    @DisplayName("Should throw when conflictChecker is not provided")
    void testBuildThrowsWhenNoConflictChecker() {
        builder
                .withCreateDate(createDate)
                .addSession(session1);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> builder.build()
        );
        assertTrue(exception.getMessage().contains("Cần cung cấp bộ kiểm tra xung đột lịch"));
    }

    @Test
    @DisplayName("Should throw when conflict is detected")
    void testBuildThrowsWhenConflictDetected() {
        ScheduleBuilder.ScheduleConflictChecker conflictChecker = (session, scheduleID) -> true;

        builder
                .withCreateDate(createDate)
                .addSession(session1)
                .withConflictChecker(conflictChecker);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> builder.build()
        );
        assertTrue(exception.getMessage().contains("Phát hiện xung đột lịch"));
    }

    @Test
    @DisplayName("Should pass conflict check when no conflict")
    void testBuildPassesConflictCheck() {
        ScheduleBuilder.ScheduleConflictChecker conflictChecker = (session, scheduleID) -> false;

        Schedule schedule = builder
                .withScheduleID("SCH-005")
                .withCreateDate(createDate)
                .addSession(session1)
                .withConflictChecker(conflictChecker)
                .build();

        assertNotNull(schedule);
        assertEquals(1, schedule.getTotalSessions());
    }

    @Test
    @DisplayName("Should pass conflict checker scheduleID to checker")
    void testConflictCheckerReceivesScheduleID() {
        String scheduleID = "SCH-006";
        final String[] capturedScheduleID = new String[1];

        ScheduleBuilder.ScheduleConflictChecker conflictChecker = (session, sid) -> {
            capturedScheduleID[0] = sid;
            return false;
        };

        builder
                .withScheduleID(scheduleID)
                .withCreateDate(createDate)
                .addSession(session1)
                .withConflictChecker(conflictChecker)
                .build();

        assertEquals(scheduleID, capturedScheduleID[0]);
    }

    // ==================== Fluent API Tests ====================

    @Test
    @DisplayName("Should return builder from all fluent methods")
    void testFluentAPIReturnBuilder() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        assertSame(builder, builder.withScheduleID("SCH-007"));
        assertSame(builder, builder.withCreateDate(createDate));
        assertSame(builder, builder.withDescription("Test"));
        assertSame(builder, builder.addSession(session1));
        assertSame(builder, builder.withConflictChecker(noConflictChecker));
    }

    @Test
    @DisplayName("Should allow resetting sessions with sessions() method")
    void testResetSessionsWithSessionsMethod() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Session session3 = new Session();
        session3.setSessionID("SESS-003");
        session3.setSessionDate(createDate);
        session3.setStartTime(LocalTime.of(13, 0));
        session3.setEndTime(LocalTime.of(14, 30));

        // Add session1 and session2, then reset with only session3
        Schedule schedule = builder
                .addSession(session1)
                .addSession(session2)
                .withCreateDate(createDate)
                .sessions(List.of(session3))  // Reset to only session3
                .withConflictChecker(noConflictChecker)
                .build();

        // Verify only session3 is in the schedule (reset worked)
        assertEquals(1, schedule.getTotalSessions());
        assertEquals(1, schedule.getSessions().size());
        assertEquals(session3, schedule.getSessions().get(0));
    }

    @Test
    @DisplayName("Should handle null session in addSession gracefully")
    void testAddNullSessionIgnored() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        builder
                .addSession(session1)
                .addSession(null)
                .withCreateDate(createDate)
                .withConflictChecker(noConflictChecker);

        Schedule schedule = builder.build();
        assertEquals(1, schedule.getTotalSessions());
    }

    @Test
    @DisplayName("Should handle null description")
    void testNullDescriptionAllowed() {
        ScheduleBuilder.ScheduleConflictChecker noConflictChecker = (session, scheduleID) -> false;

        Schedule schedule = builder
                .withScheduleID("SCH-008")
                .withCreateDate(createDate)
                .addSession(session1)
                .withDescription(null)
                .withConflictChecker(noConflictChecker)
                .build();

        assertNotNull(schedule);
        assertNull(schedule.getDescription());
    }
}
