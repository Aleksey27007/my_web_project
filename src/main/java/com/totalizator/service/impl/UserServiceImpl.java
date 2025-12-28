package com.totalizator.service.impl;

import com.totalizator.dao.UserDao;
import com.totalizator.model.Role;
import com.totalizator.model.User;
import com.totalizator.service.UserService;
import com.totalizator.service.factory.DaoFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger();
    private final UserDao userDao;

    /**
     * Constructor.
     */
    public UserServiceImpl() {
        this.userDao = DaoFactory.getInstance().getUserDao();
    }

    @Override
    public Optional<User> findById(Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return Optional.empty();
        }
        return userDao.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return Optional.empty();
        }
        return userDao.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User register(User user) {
        validateUser(user);
        
        // Check if username already exists
        if (userDao.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Check if email already exists
        if (userDao.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Hash password
        user.setPassword(hashPassword(user.getPassword()));
        
        // Set default role if not set (CLIENT)
        if (user.getRole() == null) {
            Role clientRole = new Role(3, "CLIENT", "Клиент");
            user.setRole(clientRole);
        }
        
        // Set default balance if not set
        if (user.getBalance() == null) {
            user.setBalance(BigDecimal.ZERO);
        }
        
        // Set user as active by default
        user.setActive(true);
        
        logger.info("Registering new user: {}", user.getUsername());
        return userDao.save(user);
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return Optional.empty();
        }
        
        Optional<User> userOptional = userDao.findByUsername(username);
        if (userOptional.isPresent()) {
            logger.info("Optional object has user: {}", username);
            User user = userOptional.get();
            String hashedPassword = hashPassword(password);
            if (hashedPassword.equals(user.getPassword()) && user.isActive()) {
                logger.info("User authenticated: {}", username);
                return Optional.of(user);
            }
            logger.info("Optional cant return user: {}", username);
        }
        
        logger.warn("Authentication failed for username: {}", username);
        return Optional.empty();
    }

    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getId() <= 0) {
            return false;
        }
        
        // If password is being updated, hash it
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // Check if password is already hashed (simple check)
            if (user.getPassword().length() < 60) {
                user.setPassword(hashPassword(user.getPassword()));
            }
        } else {
            // Keep existing password
            Optional<User> existingUser = userDao.findById(user.getId());
            existingUser.ifPresent(value -> user.setPassword(value.getPassword()));
        }
        
        return userDao.update(user);
    }

    @Override
    public boolean deleteUser(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return userDao.deleteById(id);
    }

    /**
     * Validates user data.
     * 
     * @param user user to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        if (StringUtils.isBlank(user.getEmail())) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    /**
     * Validates email format.
     * 
     * @param email email to validate
     * @return true if email is valid
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Hashes password using SHA-256.
     * Note: In production, use BCrypt or similar.
     * 
     * @param password plain text password
     * @return hashed password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error hashing password", e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}

