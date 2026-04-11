package vn.iotstar.coolenglish.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import vn.iotstar.coolenglish.audit.listener.AuditEntityListener;

@Entity
@Table(name = "Schedules")
@EntityListeners(AuditEntityListener.class)
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "scheduleID", length = 50)
    private String scheduleID;

    @Column(name = "totalSessions")
    private Integer totalSessions;

    @Column(name = "description", length = 255)
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "createDate")
    private Date createDate;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sessionDate ASC, sessionID ASC")
    private List<Session> sessions = new ArrayList<>();

    public Schedule() {
    }

    public String getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(String scheduleID) {
        this.scheduleID = scheduleID;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions == null ? new ArrayList<>() : sessions;
        for (Session session : this.sessions) {
            if (session != null) {
                session.setSchedule(this);
            }
        }
    }

    public Double calculateTotalHours() {
        double totalHours = 0.0;
        if (sessions != null) {
            for (Session session : sessions) {
                if (session != null && session.getDurationHours() != null) {
                    totalHours += session.getDurationHours();
                }
            }
        }
        return totalHours;
    }
}


