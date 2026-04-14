package vn.iotstar.coolenglish.entity;

import java.io.Serializable;
import java.io.Serial;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import vn.iotstar.coolenglish.audit.listener.AuditEntityListener;
import vn.iotstar.coolenglish.enums.UserRole;

@Entity
@Table(name = "user_account")
@EntityListeners(AuditEntityListener.class)
public class UserAccount implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userID;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "related_id")
    private Long relatedID;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public UserAccount() {
    }

    public UserAccount(String email, String username, String password, UserRole role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }


    public UserAccount(String email, String username, String password, UserRole role, boolean active) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Long getRelatedID() {
        return relatedID;
    }

    public void setRelatedID(Long relatedID) {
        this.relatedID = relatedID;
    }

    public boolean login(String inputPassword) {
        return password != null && password.equals(inputPassword);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public void changePassword(String currentPassword, String newPassword) {
        if (!login(currentPassword)) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password is required.");
        }
        this.password = newPassword;
    }
}

