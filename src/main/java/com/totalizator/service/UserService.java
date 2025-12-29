package com.totalizator.service;

import com.totalizator.model.User;

import java.util.List;
import java.util.Optional;


public interface UserService {
    
    
    Optional<User> findById(Integer id);
    
    
    Optional<User> findByUsername(String username);
    
    
    Optional<User> findByEmail(String email);
    
    
    List<User> findAll();
    
    
    User register(User user);
    
    
    Optional<User> authenticate(String username, String password);
    
    
    boolean updateUser(User user);
    
    
    boolean deleteUser(Integer id);
}

