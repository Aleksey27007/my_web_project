package com.totalizator.dao.impl;

import com.totalizator.dao.BookmakerDao;
import com.totalizator.util.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BookmakerDaoImpl implements BookmakerDao {
    private static final String UPDATE_COMPETITION_BET_TYPE = "INSERT INTO competition_bet_types (competition_id, bet_type_id, multiplier) " +
            "VALUES (?, (SELECT id FROM bet_types WHERE name = ?), ?) " +
            "ON DUPLICATE KEY UPDATE multiplier = ?";

    private final ConnectionPool connectionPool;


    public BookmakerDaoImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public boolean updateOddsCompetition(int competitionId, BigDecimal winMultiplier, BigDecimal drawMultiplier, BigDecimal lossMultiplier,
                                         BigDecimal exactScoreMultiplier, BigDecimal totalOverMultiplier,
                                         BigDecimal totalUnderMultiplier) {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_COMPETITION_BET_TYPE)) {

            nameForMethod(competitionId, winMultiplier, drawMultiplier, lossMultiplier,
                    exactScoreMultiplier, totalOverMultiplier, totalUnderMultiplier, statement);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private static void nameForMethod(int competitionId, BigDecimal winMultiplier, BigDecimal drawMultiplier, BigDecimal lossMultiplier,
                                     BigDecimal exactScoreMultiplier, BigDecimal totalOverMultiplier, BigDecimal totalUnderMultiplier,
                                     PreparedStatement statement) throws SQLException {
        statement.setInt(1, competitionId);
        statement.setString(2, "WIN");
        statement.setBigDecimal(3, winMultiplier);
        statement.setBigDecimal(4, winMultiplier);
        statement.executeUpdate();

        statement.setInt(1, competitionId);
        statement.setString(2, "DRAW");
        statement.setBigDecimal(3, drawMultiplier);
        statement.setBigDecimal(4, drawMultiplier);
        statement.executeUpdate();

        statement.setInt(1, competitionId);
        statement.setString(2, "LOSS");
        statement.setBigDecimal(3, lossMultiplier);
        statement.setBigDecimal(4, lossMultiplier);
        statement.executeUpdate();

        statement.setInt(1, competitionId);
        statement.setString(2, "EXACT_SCORE");
        statement.setBigDecimal(3, exactScoreMultiplier);
        statement.setBigDecimal(4, exactScoreMultiplier);
        statement.executeUpdate();

        statement.setInt(1, competitionId);
        statement.setString(2, "TOTAL_OVER");
        statement.setBigDecimal(3, totalOverMultiplier);
        statement.setBigDecimal(4, totalOverMultiplier);
        statement.executeUpdate();

        statement.setInt(1, competitionId);
        statement.setString(2, "TOTAL_UNDER");
        statement.setBigDecimal(3, totalUnderMultiplier);
        statement.setBigDecimal(4, totalUnderMultiplier);
        statement.executeUpdate();
    }
}
