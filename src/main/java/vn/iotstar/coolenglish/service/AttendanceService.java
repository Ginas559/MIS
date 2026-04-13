package vn.iotstar.coolenglish.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import vn.iotstar.coolenglish.dao.impl.AttendanceDAO;
import vn.iotstar.coolenglish.entity.Attendance;
import vn.iotstar.coolenglish.entity.Enrollment;
import vn.iotstar.coolenglish.entity.Session;
import vn.iotstar.coolenglish.enums.AttendanceStatus;

public class AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    public AttendanceService(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO;
    }

    public void markAttendance(Enrollment enrollment, Session session, boolean isPresent, String note) {
        Attendance existing = attendanceDAO.findByEnrollmentIdAndSessionId(enrollment.getId(), session.getSessionID());
        Date checkin = new Date();
        AttendanceStatus st = isPresent ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT;
        String noteVal = note != null ? note : "";

        if (existing != null) {
            existing.setStatus(st);
            existing.setCheckinTime(checkin);
            if (note != null) {
                existing.setNote(note);
            }
            attendanceDAO.update(existing);
        } else {
            String id = UUID.randomUUID().toString();
            attendanceDAO.insert(new Attendance(id, enrollment, session, checkin, st, noteVal));
        }
    }

    public void updateAttendance(String attendanceID, AttendanceStatus status, String note) {
        Attendance attendance = attendanceDAO.findById(attendanceID, Attendance.class);
        if (attendance != null) {
            attendance.setStatus(status);
            attendance.setNote(note);
            attendanceDAO.update(attendance);
        } else {
            throw new IllegalArgumentException("Không tìm thấy điểm danh với ID: " + attendanceID);
        }
    }

    public Attendance getAttendanceById(String attendanceID) {
        return attendanceDAO.findById(attendanceID, Attendance.class);
    }
    
    public Attendance findByEnrollmentIdAndSessionId(Long enrollmentId, String sessionId) {
        return attendanceDAO.findByEnrollmentIdAndSessionId(enrollmentId, sessionId);
    }

    public List<Attendance> getAllAttendances() {
        return attendanceDAO.findAll(Attendance.class);
    }

    public List<Attendance> getAttendancesBySession(Session session) {
        List<Attendance> all = attendanceDAO.findAll(Attendance.class);
        return all.stream().filter(a -> a.getSession().equals(session)).toList();
    }

    public List<Attendance> getAttendancesByEnrollment(Enrollment enrollment) {
        List<Attendance> all = attendanceDAO.findAll(Attendance.class);
        return all.stream().filter(a -> a.getEnrollment().equals(enrollment)).toList();
    }
}
