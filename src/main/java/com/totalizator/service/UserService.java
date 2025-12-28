package com.totalizator.service;

import com.totalizator.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User operations.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public interface UserService {
    
    /**
     * Finds user by id.
     * 
     * @param id user identifier
     * @return Optional containing user if found
     */
    Optional<User> findById(Integer id);
    
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
    
    /**
     * Finds all users.
     * 
     * @return list of all users
     */
    List<User> findAll();
    
    /**
     * Registers a new user.
     * 
     * @param user user to register
     * @return registered user
     * @throws IllegalArgumentException if user data is invalid
     */
    User register(User user);
    
    /**
     * Authenticates a user.
     * 
     * @param username username
     * @param password password (plain text)
     * @return Optional containing user if authenticated
     */
    Optional<User> authenticate(String username, String password);
    
    /**
     * Updates user information.
     * 
     * @param user user to update
     * @return true if updated successfully
     */
    boolean updateUser(User user);
    
    /**
     * Deletes user by id.
     * 
     * @param id user identifier
     * @return true if deleted successfully
     */
    boolean deleteUser(Integer id);
}

