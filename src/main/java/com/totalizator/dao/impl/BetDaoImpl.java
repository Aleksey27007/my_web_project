package com.totalizator.dao.impl;

import com.totalizator.dao.BetDao;
import com.totalizator.dao.CompetitionDao;
import com.totalizator.dao.UserDao;
import com.totalizator.model.Bet;
import com.totalizator.model.BetType;
import com.totalizator.model.Competition;
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

/**
 * Implementation of BetDao using JDBC with PreparedStatement for SQL injection protection.
 * Uses try-with-resources for automatic resource management.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public class BetDaoImpl implements BetDao {
    private static final Logger logger = LogManager.getLogger();
    private final ConnectionPool connectionPool;
    private final UserDao userDao;
    private final CompetitionDao competitionDao;

    private static final String FIND_BY_ID = "SELECT b.id, b.user_id, b.competition_id, b.bet_type_id, " +
            "b.amount, b.predicted_value, b.status, b.win_amount, b.created_at, b.updated_at, " +
            "bt.name as bet_type_name, bt.description as bet_type_description, bt.multiplier " +
            "FROM bets b JOIN bet_types bt ON b.bet_type_id = bt.id WHERE b.id = ?";
    
    private static final String FIND_ALL = "SELECT b.id, b.user_id, b.competition_id, b.bet_type_id, " +
            "b.amount, b.predicted_value, b.status, b.win_amount, b.created_at, b.updated_at, " +
            "bt.name as bet_type_name, bt.description as bet_type_description, bt.multiplier " +
            "FROM bets b JOIN bet_types bt ON b.bet_type_id = bt.id ORDER BY b.created_at DESC";
    
    private static final String FIND_BY_USER_ID = "SELECT b.id, b.user_id, b.competition_id, b.bet_type_id, " +
            "b.amount, b.predicted_value, b.status, b.win_amount, b.created_at, b.updated_at, " +
            "bt.name as bet_type_name, bt.description as bet_type_description, bt.multiplier " +
            "FROM bets b JOIN bet_types bt ON b.bet_type_id = bt.id WHERE b.user_id = ? ORDER BY b.created_at DESC";
    
    private static final String FIND_BY_COMPETITION_ID = "SELECT b.id, b.user_id, b.competition_id, b.bet_type_id, " +
            "b.amount, b.predicted_value, b.status, b.win_amount, b.created_at, b.updated_at, " +
            "bt.name as bet_type_name, bt.description as bet_type_description, bt.multiplier " +
            "FROM bets b JOIN bet_types bt ON b.bet_type_id = bt.id WHERE b.competition_id = ? ORDER BY b.created_at DESC";
    
    private static final String INSERT = "INSERT INTO bets (user_id, competition_id, bet_type_id, " +
            "amount, predicted_value, status, win_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE = "UPDATE bets SET user_id = ?, competition_id = ?, bet_type_id = ?, " +
            "amount = ?, predicted_value = ?, status = ?, win_amount = ? WHERE id = ?";
    
    private static final String DELETE = "DELETE FROM bets WHERE id = ?";

    /**
     * Constructor.
     * 
     * @param connectionPool connection pool instance
     * @param userDao user DAO instance
     * @param competitionDao competition DAO instance
     */
    public BetDaoImpl(ConnectionPool connectionPool, UserDao userDao, CompetitionDao competitionDao) {
        this.connectionPool = connectionPool;
        this.userDao = userDao;
        this.competitionDao = competitionDao;
    }

    @Override
    public Optional<Bet> findById(Integer id) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToBet(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding bet by id: {}", id, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public List<Bet> findAll() {
        List<Bet> bets = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                bets.add(mapResultSetToBet(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error finding all bets", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return bets;
    }

    @Override
    public List<Bet> findByUserId(Integer userId) {
        List<Bet> bets = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bets.add(mapResultSetToBet(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding bets by user id: {}", userId, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return bets;
    }

    @Override
    public List<Bet> findByCompetitionId(Integer competitionId) {
        List<Bet> bets = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_COMPETITION_ID)) {
            statement.setInt(1, competitionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bets.add(mapResultSetToBet(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding bets by competition id: {}", competitionId, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return bets;
    }

    @Override
    public Bet save(Bet bet) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            changeBetToStatement(bet, statement);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating bet failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bet.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating bet failed, no ID obtained.");
                }
            }
            
            logger.info("Bet saved with id: {}", bet.getId());
            return bet;
        } catch (SQLException e) {
            logger.error("Error saving bet", e);
            throw new RuntimeException("Error saving bet", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    private void changeBetToStatement(Bet bet, PreparedStatement statement) throws SQLException {
        statement.setInt(1, bet.getUser().getId());
        statement.setInt(2, bet.getCompetition().getId());
        statement.setInt(3, bet.getBetType().getId());
        statement.setBigDecimal(4, bet.getAmount());
        statement.setString(5, bet.getPredictedValue());
        statement.setString(6, bet.getStatus().name());

        if (bet.getWinAmount() != null) {
            statement.setBigDecimal(7, bet.getWinAmount());
        } else {
            statement.setNull(7, java.sql.Types.DECIMAL);
        }
    }

    @Override
    public boolean update(Bet bet) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            changeBetToStatement(bet, statement);

            statement.setInt(8, bet.getId());
            
            int affectedRows = statement.executeUpdate();
            logger.info("Bet updated: {} rows affected", affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating bet", e);
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
            logger.info("Bet deleted: {} rows affected", affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting bet", e);
            return false;
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    /**
     * Maps ResultSet row to Bet object.
     * 
     * @param resultSet ResultSet containing bet data
     * @return Bet object
     * @throws SQLException if mapping fails
     */
    private Bet mapResultSetToBet(ResultSet resultSet) throws SQLException {
        Bet bet = new Bet();
        bet.setId(resultSet.getInt("id"));
        bet.setAmount(resultSet.getBigDecimal("amount"));
        bet.setPredictedValue(resultSet.getString("predicted_value"));
        
        String statusStr = resultSet.getString("status");
        if (statusStr != null) {
            try {
                bet.setStatus(Bet.BetStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                bet.setStatus(Bet.BetStatus.PENDING);
            }
        }
        
        java.math.BigDecimal winAmount = resultSet.getBigDecimal("win_amount");
        if (winAmount != null) {
            bet.setWinAmount(winAmount);
        }
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            bet.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            bet.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // Load related entities
        int userId = resultSet.getInt("user_id");
        Optional<User> user = userDao.findById(userId);
        user.ifPresent(bet::setUser);
        
        int competitionId = resultSet.getInt("competition_id");
        Optional<Competition> competition = competitionDao.findById(competitionId);
        competition.ifPresent(bet::setCompetition);
        
        // Set BetType
        BetType betType = new BetType();
        betType.setId(resultSet.getInt("bet_type_id"));
        betType.setName(resultSet.getString("bet_type_name"));
        betType.setDescription(resultSet.getString("bet_type_description"));
        betType.setMultiplier(resultSet.getBigDecimal("multiplier"));
        bet.setBetType(betType);
        
        return bet;
    }
}
