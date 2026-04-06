package vn.iotstar.coolenglish.dao.impl;

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

    /**
     * Kiểm tra đăng nhập bằng email và password
     * 
     * @param email Email của user
     * @param password Password của user
     * @return UserAccount nếu thành công, null nếu thất bại
     */
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

    /**
     * Kiểm tra email đã tồn tại chưa (để tránh trùng lặp khi đăng ký)
     * 
     * @param email Email cần kiểm tra
     * @return true nếu email đã tồn tại, false nếu chưa
     */
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

    /**
     * Tìm user account theo email
     * 
     * @param email Email cần tìm
     * @return UserAccount nếu tồn tại, null nếu không tồn tại
     */
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
}

