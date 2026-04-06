package vn.iotstar.coolenglish.web;

import java.io.Serializable;

/**
 * Stores registration input in session before OTP verification.
 */
public class PendingSignupData implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String email;
    private final String password;
    private final String role;

    public PendingSignupData(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}

