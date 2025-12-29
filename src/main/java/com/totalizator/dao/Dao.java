package com.totalizator.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, K> {
    Optional<T> findById(K id);
    List<T> findAll();
    T save(T entity);
    boolean update(T entity);
    boolean deleteById(K id);
}

