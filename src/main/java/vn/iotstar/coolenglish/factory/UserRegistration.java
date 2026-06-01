package vn.iotstar.coolenglish.factory;

import vn.iotstar.coolenglish.entity.Person;

public abstract class UserRegistration {

    public final Person registerUser(String name, String email) {
        Person person = createPerson();
        person.setFullName(name);
        person.setEmail(email);
        initializeSpecificInfo(person);
        saveToDatabase(person);
        return person;
    }

    protected abstract Person createPerson();

    protected abstract void initializeSpecificInfo(Person person);

    private void saveToDatabase(Person person) {
        System.out.println("Saving " + person.getFullName() + " to CoolEnglish Database...");
    }
}

