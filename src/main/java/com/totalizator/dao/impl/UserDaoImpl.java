package com.totalizator.dao.impl;

import com.totalizator.dao.UserDao;
import com.totalizator.model.Role;
import com.totalizator.model.User;
import com.totalizator.util.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UserDaoImpl implements UserDao {
    private static final Logger logger = LogManager.getLogger();
    private final ConnectionPool connectionPool;

    private static final String FIND_BY_ID = "SELECT u.id, u.username, u.email, u.password, " +
            "u.first_name, u.last_name, u.role_id, u.balance, u.is_active, u.created_at, u.updated_at, " +
            "r.name as role_name, r.description as role_description " +
            "FROM users u JOIN roles r ON u.role_id = r.id WHERE u.id = ?";
    
    private static final String FIND_ALL = "SELECT u.id, u.username, u.email, u.password, " +
            "u.first_name, u.last_name, u.role_id, u.balance, u.is_active, u.created_at, u.updated_at, " +
            "r.name as role_name, r.description as role_description " +
            "FROM users u JOIN roles r ON u.role_id = r.id ORDER BY u.id";
    
    private static final String FIND_BY_USERNAME = "SELECT u.id, u.username, u.email, u.password, " +
            "u.first_name, u.last_name, u.role_id, u.balance, u.is_active, u.created_at, u.updated_at, " +
            "r.name as role_name, r.description as role_description " +
            "FROM users u JOIN roles r ON u.role_id = r.id WHERE u.username = ?";
    
    private static final String FIND_BY_EMAIL = "SELECT u.id, u.username, u.email, u.password, " +
            "u.first_name, u.last_name, u.role_id, u.balance, u.is_active, u.created_at, u.updated_at, " +
            "r.name as role_name, r.description as role_description " +
            "FROM users u JOIN roles r ON u.role_id = r.id WHERE u.email = ?";
    
    private static final String INSERT = "INSERT INTO users (username, email, password, first_name, " +
            "last_name, role_id, balance, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE = "UPDATE users SET username = ?, email = ?, password = ?, " +
            "first_name = ?, last_name = ?, role_id = ?, balance = ?, is_active = ? WHERE id = ?";
    
    private static final String DELETE = "DELETE FROM users WHERE id = ?";

    
    public UserDaoImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<User> findById(Integer id) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by id: {}", id, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error finding all users", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return users;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username: {}", username, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email: {}", email, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            changeUserToStatement(user, statement);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            
            logger.info("User saved with id: {}", user.getId());
            return user;
        } catch (SQLException e) {
            logger.error("Error saving user", e);
            throw new RuntimeException("Error saving user", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    private void changeUserToStatement(User user, PreparedStatement statement) throws SQLException {
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        statement.setString(4, user.getFirstName());
        statement.setString(5, user.getLastName());
        statement.setInt(6, user.getRole().getId());
        statement.setBigDecimal(7, user.getBalance());
        statement.setBoolean(8, user.isActive());
    }

    @Override
    public boolean update(User user) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            changeUserToStatement(user, statement);
            statement.setInt(9, user.getId());
            
            int affectedRows = statement.executeUpdate();
            logger.info("User updated: {} rows affected", affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating user", e);
            return false;
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            logger.info("User deleted: {} rows affected", affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting user", e);
            return false;
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setBalance(resultSet.getBigDecimal("balance"));
        user.setActive(resultSet.getBoolean("is_active"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        Role role = new Role();
        role.setId(resultSet.getInt("role_id"));
        role.setName(resultSet.getString("role_name"));
        role.setDescription(resultSet.getString("role_description"));
        user.setRole(role);
        
        return user;
    }
}
