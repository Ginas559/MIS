package vn.iotstar.coolenglish.dao;

import java.util.List;

public interface IGenericDAO<T> {
    void insert(T entity);

    void update(T entity);

    void delete(Object id, Class<T> clazz);

    T findById(Object id, Class<T> clazz);

    List<T> findAll(Class<T> clazz);
}

