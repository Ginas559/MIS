package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Person;

public class PersonDAO extends AbstractDAO<Person> {

    @Override
    protected void validateEntity(Person entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Person entity is required.");
        }
    }

    public Person findByEmail(String email) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.email = :email", Person.class);
            query.setParameter("email", email);
            List<Person> results = query.getResultList();
            return results.stream().findFirst().orElse(null);
        }
    }
}


