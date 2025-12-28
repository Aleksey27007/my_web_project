package com.totalizator.dao.impl;

import com.totalizator.dao.Dao;
import com.totalizator.model.BetType;
import com.totalizator.util.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of DAO for BetType entity using JDBC with PreparedStatement.
 * Uses try-with-resources for automatic resource management.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public class BetTypeDaoImpl implements Dao<BetType, Integer> {
    private static final Logger logger = LogManager.getLogger();
    private final ConnectionPool connectionPool;

    private static final String FIND_BY_ID = "SELECT id, name, description, multiplier FROM bet_types WHERE id = ?";
    private static final String FIND_ALL = "SELECT id, name, description, multiplier FROM bet_types ORDER BY id";
    private static final String INSERT = "INSERT INTO bet_types (name, description, multiplier) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE bet_types SET name = ?, description = ?, multiplier = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM bet_types WHERE id = ?";

    /**
     * Constructor.
     * 
     * @param connectionPool connection pool instance
     */
    public BetTypeDaoImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<BetType> findById(Integer id) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToBetType(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding bet type by id: {}", id, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public List<BetType> findAll() {
        List<BetType> betTypes = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                betTypes.add(mapResultSetToBetType(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error finding all bet types", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return betTypes;
    }

    @Override
    public BetType save(BetType betType) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, betType.getName());
            statement.setString(2, betType.getDescription());
            statement.setBigDecimal(3, betType.getMultiplier());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating bet type failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    betType.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating bet type failed, no ID obtained.");
                }
            }
            
            logger.info("BetType saved with id: {}", betType.getId());
            return betType;
        } catch (SQLException e) {
            logger.error("Error saving bet type", e);
            throw new RuntimeException("Error saving bet type", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public boolean update(BetType betType) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, betType.getName());
            statement.setString(2, betType.getDescription());
            statement.setBigDecimal(3, betType.getMultiplier());
            statement.setInt(4, betType.getId());
            
            int affectedRows = statement.executeUpdate();
            logger.info("BetType updated: {} rows affected", affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating bet type", e);
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
            logger.info("BetType deleted: {} rows affected", affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting bet type", e);
            return false;
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    /**
     * Maps ResultSet row to BetType object.
     * 
     * @param resultSet ResultSet containing bet type data
     * @return BetType object
     * @throws SQLException if mapping fails
     */
    private BetType mapResultSetToBetType(ResultSet resultSet) throws SQLException {
        BetType betType = new BetType();
        betType.setId(resultSet.getInt("id"));
        betType.setName(resultSet.getString("name"));
        betType.setDescription(resultSet.getString("description"));
        betType.setMultiplier(resultSet.getBigDecimal("multiplier"));
        return betType;
    }
}
