package com.totalizator.dao;

import com.totalizator.model.User;

import java.util.Optional;

/**
 * DAO interface for User entity.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public interface UserDao extends Dao<User, Integer> {
    
    /**
     * Finds user by username.
     * 
     * @param username username
     * @return Optional containing user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Finds user by email.
     * 
     * @param email email address
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);
}

