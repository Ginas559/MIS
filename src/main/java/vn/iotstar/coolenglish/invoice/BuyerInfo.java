package vn.iotstar.coolenglish.invoice;

import java.io.Serializable;

/** Thông tin người mua / học viên. */
public final class BuyerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String fullName;
    private final String email;
    private final String phone;
    private final String studentCode;

    public BuyerInfo(String fullName, String email, String phone, String studentCode) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.studentCode = studentCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStudentCode() {
        return studentCode;
    }
}
