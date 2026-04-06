package vn.iotstar.coolenglish.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Rooms")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "roomID", length = 50)
    private String roomID;
    private String roomName;
    private Integer capacity;
    private String location;
    private String status;

    public Room() {
    }

    public Room(String roomID, String roomName, Integer capacity, String location, String status) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.capacity = capacity;
        this.location = location;
        this.status = status;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

