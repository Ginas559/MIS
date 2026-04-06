package vn.iotstar.coolenglish.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STAFF")
public class Staff extends Person {

    @Column(name = "staff_id", length = 50)
    private String staffID;

    @Column(name = "position", length = 100)
    private String position;

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public void showSpecificInfo() {
        System.out.println("Role: Staff - Position: " + position);
    }
}

