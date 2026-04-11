package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.UserAccount;

public class UserAccountDAO extends AbstractDAO<UserAccount> {

    @Override
    protected void validateEntity(UserAccount entity) {
        if (entity == null) {
            throw new IllegalArgumentException("UserAccount entity is required.");
        }
    }


    public UserAccount checkLogin(String email, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT ua FROM UserAccount ua WHERE ua.email = :email AND ua.password = :password";
            TypedQuery<UserAccount> query = em.createQuery(jpql, UserAccount.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            
            java.util.List<UserAccount> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }


    public boolean isEmailExists(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(ua) FROM UserAccount ua WHERE ua.email = :email";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }


    public UserAccount findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT ua FROM UserAccount ua WHERE ua.email = :email";
            TypedQuery<UserAccount> query = em.createQuery(jpql, UserAccount.class);
            query.setParameter("email", email);
            
            java.util.List<UserAccount> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public List<UserAccount> findLearnersForRoadmapGrant() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT ua FROM UserAccount ua "
                    + "WHERE ua.relatedID IS NOT NULL "
                    + "AND (ua.role = :studentRole OR ua.role = :teacherRole) "
                    + "ORDER BY ua.role, ua.username";
            TypedQuery<UserAccount> query = em.createQuery(jpql, UserAccount.class);
            query.setParameter("studentRole", vn.iotstar.coolenglish.enums.UserRole.STUDENT);
            query.setParameter("teacherRole", vn.iotstar.coolenglish.enums.UserRole.TEACHER);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

