package com.totalizator.dao.impl;

import com.totalizator.dao.CompetitionDao;
import com.totalizator.model.Competition;
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
 * Implementation of CompetitionDao using JDBC with PreparedStatement for SQL injection protection.
 * Uses try-with-resources for automatic resource management.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public class CompetitionDaoImpl implements CompetitionDao {
    private static final Logger logger = LogManager.getLogger();
    private final ConnectionPool connectionPool;

    private static final String FIND_BY_ID = "SELECT id, title, description, sport_type, start_date, " +
            "end_date, status, result, team1, team2, score1, score2, created_at, updated_at " +
            "FROM competitions WHERE id = ?";
    
    private static final String FIND_ALL = "SELECT id, title, description, sport_type, start_date, " +
            "end_date, status, result, team1, team2, score1, score2, created_at, updated_at " +
            "FROM competitions ORDER BY start_date DESC";
    
    private static final String FIND_BY_STATUS = "SELECT id, title, description, sport_type, start_date, " +
            "end_date, status, result, team1, team2, score1, score2, created_at, updated_at " +
            "FROM competitions WHERE status = ? ORDER BY start_date DESC";
    
    private static final String INSERT = "INSERT INTO competitions (title, description, sport_type, " +
            "start_date, end_date, status, result, team1, team2, score1, score2) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE = "UPDATE competitions SET title = ?, description = ?, " +
            "sport_type = ?, start_date = ?, end_date = ?, status = ?, result = ?, " +
            "team1 = ?, team2 = ?, score1 = ?, score2 = ? WHERE id = ?";
    
    private static final String DELETE = "DELETE FROM competitions WHERE id = ?";

    /**
     * Constructor.
     * 
     * @param connectionPool connection pool instance
     */
    public CompetitionDaoImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Competition> findById(Integer id) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToCompetition(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding competition by id: {}", id, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return Optional.empty();
    }

    @Override
    public List<Competition> findAll() {
        List<Competition> competitions = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            if (connection == null) {
                logger.error("Failed to get database connection in findAll()");
                throw new RuntimeException("Failed to get database connection");
            }
            
            logger.info("Executing query: {}", FIND_ALL);
            try (PreparedStatement statement = connection.prepareStatement(FIND_ALL);
                 ResultSet resultSet = statement.executeQuery()) {
                int count = 0;
                while (resultSet.next()) {
                    try {
                        Competition competition = mapResultSetToCompetition(resultSet);
                        competitions.add(competition);
                        count++;
                        logger.debug("Mapped competition: id={}, title={}", competition.getId(), competition.getTitle());
                    } catch (Exception e) {
                        logger.error("Error mapping competition from ResultSet at row {}", count + 1, e);
                        // Continue processing other rows
                    }
                }
                logger.info("Found {} competitions in database, successfully mapped {}", count, competitions.size());
            }
        } catch (SQLException e) {
            logger.error("SQL error finding all competitions", e);
            throw new RuntimeException("Database error while loading competitions", e);
        } catch (Exception e) {
            logger.error("Unexpected error in findAll()", e);
            throw new RuntimeException("Unexpected error while loading competitions", e);
        } finally {
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
        logger.info("Returning {} competitions", competitions.size());
        return competitions;
    }

    @Override
    public List<Competition> findByStatus(String status) {
        List<Competition> competitions = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_STATUS)) {
            statement.setString(1, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    competitions.add(mapResultSetToCompetition(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding competitions by status: {}", status, e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return competitions;
    }

    @Override
    public Competition save(Competition competition) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            changeCompetitionToStatement(competition, statement);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating competition failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    competition.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating competition failed, no ID obtained.");
                }
            }
            
            logger.info("Competition saved with id: {}", competition.getId());
            return competition;
        } catch (SQLException e) {
            logger.error("Error saving competition", e);
            throw new RuntimeException("Error saving competition", e);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public boolean update(Competition competition) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            changeCompetitionToStatement(competition, statement);

            statement.setInt(12, competition.getId());
            
            int affectedRows = statement.executeUpdate();
            logger.info("Competition updated: {} rows affected", affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating competition", e);
            return false;
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    private void changeCompetitionToStatement(Competition competition, PreparedStatement statement) throws SQLException {
        statement.setString(1, competition.getTitle());
        if (competition.getDescription() != null) {
            statement.setString(2, competition.getDescription());
        } else {
            statement.setNull(2, java.sql.Types.VARCHAR);
        }
        statement.setString(3, competition.getSportType());
        statement.setTimestamp(4, Timestamp.valueOf(competition.getStartDate()));

        if (competition.getEndDate() != null) {
            statement.setTimestamp(5, Timestamp.valueOf(competition.getEndDate()));
        } else {
            statement.setNull(5, java.sql.Types.TIMESTAMP);
        }

        statement.setString(6, competition.getStatus() != null ? competition.getStatus().name() : "SCHEDULED");
        if (competition.getResult() != null) {
            statement.setString(7, competition.getResult());
        } else {
            statement.setNull(7, java.sql.Types.VARCHAR);
        }
        statement.setString(8, competition.getTeam1());
        statement.setString(9, competition.getTeam2());

        if (competition.getScore1() != null) {
            statement.setInt(10, competition.getScore1());
        } else {
            statement.setNull(10, java.sql.Types.INTEGER);
        }

        if (competition.getScore2() != null) {
            statement.setInt(11, competition.getScore2());
        } else {
            statement.setNull(11, java.sql.Types.INTEGER);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            logger.info("Competition deleted: {} rows affected", affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting competition", e);
            return false;
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    /**
     * Maps ResultSet row to Competition object.
     * 
     * @param resultSet ResultSet containing competition data
     * @return Competition object
     * @throws SQLException if mapping fails
     */
    private Competition mapResultSetToCompetition(ResultSet resultSet) throws SQLException {
        try {
            Competition competition = new Competition();
            competition.setId(resultSet.getInt("id"));
            
            String title = resultSet.getString("title");
            if (title == null) {
                logger.warn("Competition with id {} has null title", competition.getId());
                title = "Untitled Competition";
            }
            competition.setTitle(title);
            
            competition.setDescription(resultSet.getString("description"));
            competition.setSportType(resultSet.getString("sport_type"));
            
            Timestamp startDate = resultSet.getTimestamp("start_date");
            if (startDate == null) {
                logger.error("Competition with id {} has null start_date, which is required", competition.getId());
                throw new SQLException("start_date cannot be null for competition id: " + competition.getId());
            }
            competition.setStartDate(startDate.toLocalDateTime());
            
            Timestamp endDate = resultSet.getTimestamp("end_date");
            if (endDate != null) {
                competition.setEndDate(endDate.toLocalDateTime());
            }
            
            String statusStr = resultSet.getString("status");
            if (statusStr != null && !statusStr.isEmpty()) {
                try {
                    competition.setStatus(Competition.CompetitionStatus.valueOf(statusStr));
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid status '{}' for competition id {}, defaulting to SCHEDULED", statusStr, competition.getId());
                    competition.setStatus(Competition.CompetitionStatus.SCHEDULED);
                }
            } else {
                competition.setStatus(Competition.CompetitionStatus.SCHEDULED);
            }
            
            competition.setResult(resultSet.getString("result"));
            
            String team1 = resultSet.getString("team1");
            if (team1 == null) {
                logger.warn("Competition with id {} has null team1", competition.getId());
                team1 = "Team 1";
            }
            competition.setTeam1(team1);
            
            String team2 = resultSet.getString("team2");
            if (team2 == null) {
                logger.warn("Competition with id {} has null team2", competition.getId());
                team2 = "Team 2";
            }
            competition.setTeam2(team2);
            
            int score1 = resultSet.getInt("score1");
            if (!resultSet.wasNull()) {
                competition.setScore1(score1);
            }
            
            int score2 = resultSet.getInt("score2");
            if (!resultSet.wasNull()) {
                competition.setScore2(score2);
            }
            
            Timestamp createdAt = resultSet.getTimestamp("created_at");
            if (createdAt != null) {
                competition.setCreatedAt(createdAt.toLocalDateTime());
            }
            
            Timestamp updatedAt = resultSet.getTimestamp("updated_at");
            if (updatedAt != null) {
                competition.setUpdatedAt(updatedAt.toLocalDateTime());
            }
            
            return competition;
        } catch (SQLException e) {
            logger.error("Error mapping ResultSet to Competition", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error mapping ResultSet to Competition", e);
            throw new SQLException("Error mapping competition: " + e.getMessage(), e);
        }
    }
}
