package vn.iotstar.coolenglish.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.dao.IGenericDAO;

public abstract class AbstractDAO<T> implements IGenericDAO<T> {

    // Template hook: subclasses must define entity-level validation.
    protected abstract void validateEntity(T entity);

    @Override
    public final void insert(T entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            validateEntity(entity);
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public final void update(T entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            validateEntity(entity);
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public final void delete(Object id, Class<T> clazz) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.getReference(clazz, id);
            em.remove(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public final T findById(Object id, Class<T> clazz) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(clazz, id);
        } finally {
            em.close();
        }
    }

    @Override
    public final List<T> findAll(Class<T> clazz) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT obj FROM " + clazz.getSimpleName() + " obj";
            TypedQuery<T> query = em.createQuery(jpql, clazz);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

