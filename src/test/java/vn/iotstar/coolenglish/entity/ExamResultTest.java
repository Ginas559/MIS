package vn.iotstar.coolenglish.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ExamResultTest {

    @Test
    void shouldCalculateToeicTwoSkillsTotal() {
        ExamResult result = new ExamResult();
        result.setExamFormat("TOEIC");
        result.setHasListening(true);
        result.setHasReading(true);
        result.setListeningRaw(80);
        result.setReadingRaw(75);

        assertEquals(770d, result.getScore());
        assertNull(result.getGrade());
        assertTrue(result.hasRecordedScore());
        assertEquals(0d, result.getDisplaySpeakingScore());
        assertEquals(0d, result.getDisplayWritingScore());
    }

    @Test
    void shouldCalculateIeltsOverallByAverage() {
        ExamResult result = new ExamResult();
        result.setExamFormat("IELTS");
        result.setHasListening(true);
        result.setHasReading(true);
        result.setHasSpeaking(true);
        result.setHasWriting(true);
        result.setListeningScore(6.5d);
        result.setReadingScore(7d);
        result.setSpeakingScore(6d);
        result.setWritingScore(6.5d);

        assertEquals(6.5d, result.getScore());
        assertNull(result.getGrade());
    }

    @Test
    void shouldNotProduceOverallScoreWhenMissingRequiredSkill() {
        ExamResult result = new ExamResult();
        result.setExamFormat("TOEIC");
        result.setHasListening(true);
        result.setHasReading(true);
        result.setListeningRaw(60);

        assertEquals(300d, result.getScore());
        assertNull(result.getGrade());
        assertTrue(result.hasRecordedScore());
    }

    @Test
    void shouldDefaultOverallScoreToZeroWhenNoSkillScoreEnteredYet() {
        ExamResult result = new ExamResult();
        result.setExamFormat("TOEIC");
        result.setHasListening(true);
        result.setHasReading(true);

        assertEquals(0d, result.getScore());
        assertFalse(result.hasRecordedScore());
    }
}
