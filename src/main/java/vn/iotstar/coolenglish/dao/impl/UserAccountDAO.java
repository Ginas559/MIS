package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

public class UserAccountDAO extends AbstractDAO<UserAccount> {

    @Override
    protected void validateEntity(UserAccount entity) {
        if (entity == null) {
            throw new IllegalArgumentException("UserAccount entity is required.");
        }
    }

    public UserAccount checkLogin(String email, String password) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            String jpql = "SELECT ua FROM UserAccount ua "
                    + "WHERE ua.email = :email AND ua.password = :password AND ua.active = true";
            TypedQuery<UserAccount> query = em.createQuery(jpql, UserAccount.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            List<UserAccount> results = query.getResultList();
            return results.stream().findFirst().orElse(null);
        }
    }

    public boolean isEmailExists(String email) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(ua) FROM UserAccount ua WHERE ua.email = :email",
                    Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        }
    }

    public UserAccount findByEmail(String email) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            String jpql = "SELECT ua FROM UserAccount ua WHERE ua.email = :email";
            TypedQuery<UserAccount> query = em.createQuery(jpql, UserAccount.class);
            query.setParameter("email", email);
            List<UserAccount> results = query.getResultList();
            return results.stream().findFirst().orElse(null);
        }
    }

    public List<UserAccount> findAllWithProfiles() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            String jpql = "SELECT ua FROM UserAccount ua "
                    + "LEFT JOIN Person p ON p.id = ua.relatedID "
                    + "ORDER BY ua.userID DESC";
            return em.createQuery(jpql, UserAccount.class).getResultList();
        }
    }

    public UserAccount findByUserID(Long userID) {
        if (userID == null) {
            return null;
        }
        return findById(userID, UserAccount.class);
    }

    public List<UserAccount> findLearnersForRoadmapGrant() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            String jpql = "SELECT ua FROM UserAccount ua "
                    + "WHERE ua.relatedID IS NOT NULL "
                    + "AND (ua.role = :studentRole OR ua.role = :teacherRole) "
                    + "ORDER BY ua.role, ua.username";
            TypedQuery<UserAccount> query = em.createQuery(jpql, UserAccount.class);
            query.setParameter("studentRole", UserRole.STUDENT);
            query.setParameter("teacherRole", UserRole.TEACHER);
            return query.getResultList();
        }
    }
}

