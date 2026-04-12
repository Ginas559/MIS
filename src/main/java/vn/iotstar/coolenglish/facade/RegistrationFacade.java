package vn.iotstar.coolenglish.facade;

import jakarta.persistence.EntityManager;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Person;
import vn.iotstar.coolenglish.entity.UserAccount;

public class RegistrationFacade {

    private static RegistrationFacade instance;

    private RegistrationFacade() {
    }

    public static synchronized RegistrationFacade getInstance() {
        if (instance == null) {
            instance = new RegistrationFacade();
        }
        return instance;
    }

    public UserAccount register(Person person, UserAccount account) {
        if (person == null) {
            throw new IllegalArgumentException("Person entity is required.");
        }
        if (account == null) {
            throw new IllegalArgumentException("UserAccount entity is required.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(person);
            em.flush();
            account.setRelatedID(person.getId());
            em.persist(account);
            em.getTransaction().commit();
            return account;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}

