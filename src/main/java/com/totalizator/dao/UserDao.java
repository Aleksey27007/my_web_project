package com.totalizator.dao;

import com.totalizator.model.User;

import java.util.Optional;


public interface UserDao extends Dao<User, Integer> {
    
    
    Optional<User> findByUsername(String username);
    
    
    Optional<User> findByEmail(String email);
}

