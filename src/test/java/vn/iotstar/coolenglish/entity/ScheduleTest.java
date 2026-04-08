package vn.iotstar.coolenglish.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.Test;

class ScheduleTest {

    @Test
    void calculateTotalHoursShouldSumAllSessionDurations() {
        Schedule schedule = new Schedule();
        schedule.setScheduleID("SCH_TEST_01");
        schedule.setTotalSessions(2);
        schedule.setDescription("Test schedule");
        schedule.setCreateDate(new Date());

        Session session1 = new Session();
        session1.setSessionID("SES_TEST_01");
        session1.setDurationHours(1.5);

        Session session2 = new Session();
        session2.setSessionID("SES_TEST_02");
        session2.setDurationHours(2.0);

        schedule.setSessions(Arrays.asList(session1, session2));

        assertEquals(3.5, schedule.calculateTotalHours());
        assertNotNull(schedule.getSessions());
        assertEquals(2, schedule.getSessions().size());
    }
}

