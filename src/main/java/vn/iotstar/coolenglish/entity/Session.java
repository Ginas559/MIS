package vn.iotstar.coolenglish.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import vn.iotstar.coolenglish.audit.listener.AuditEntityListener;
import vn.iotstar.coolenglish.enums.SessionStatus;

@Entity
@Table(name = "Sessions")
@EntityListeners(AuditEntityListener.class)
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "sessionID", length = 50)
    private String sessionID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleID", referencedColumnName = "scheduleID", nullable = false)
    private Schedule schedule;

    @Column(name = "sessionName", length = 150)
    private String sessionName;

    @Column(name = "durationHours")
    private Double durationHours;

    @Temporal(TemporalType.DATE)
    @Column(name = "sessionDate")
    private Date sessionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private SessionStatus status;

    public Session() {
        this.status = SessionStatus.SCHEDULED;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Double getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Double durationHours) {
        this.durationHours = durationHours;
    }

    public Date getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(Date sessionDate) {
        this.sessionDate = sessionDate;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}

