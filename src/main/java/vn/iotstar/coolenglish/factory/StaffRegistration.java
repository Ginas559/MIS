package vn.iotstar.coolenglish.factory;

import vn.iotstar.coolenglish.entity.Person;
import vn.iotstar.coolenglish.entity.Staff;

public class StaffRegistration extends UserRegistration {

    @Override
    protected Person createPerson() {
        return new Staff();
    }

    @Override
    protected void initializeSpecificInfo(Person person) {
        Staff staff = (Staff) person;
        staff.setStaffID("STF-" + System.currentTimeMillis());
        staff.setPosition("Staff");
    }
}

