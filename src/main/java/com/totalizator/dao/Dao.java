package com.totalizator.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface for common CRUD operations.
 * 
 * @param <T> entity type
 * @param <K> key type
 * @author Totalizator Team
 * @version 1.0
 */
public interface Dao<T, K> {
    
    /**
     * Finds entity by id.
     * 
     * @param id entity identifier
     * @return Optional containing entity if found
     */
    Optional<T> findById(K id);
    
    /**
     * Finds all entities.
     * 
     * @return list of all entities
     */
    List<T> findAll();
    
    /**
     * Saves new entity.
     * 
     * @param entity entity to save
     * @return saved entity with generated id
     */
    T save(T entity);
    
    /**
     * Updates existing entity.
     * 
     * @param entity entity to update
     * @return true if updated successfully
     */
    boolean update(T entity);
    
    /**
     * Deletes entity by id.
     * 
     * @param id entity identifier
     * @return true if deleted successfully
     */
    boolean deleteById(K id);
}

