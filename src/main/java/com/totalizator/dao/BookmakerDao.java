package com.totalizator.dao;

import java.math.BigDecimal;

public interface BookmakerDao {
    boolean updateOddsCompetition(int competitionId, BigDecimal winMultiplier, BigDecimal drawMultiplier, BigDecimal lossMultiplier,
                                  BigDecimal exactScoreMultiplier, BigDecimal totalOverMultiplier,
                                  BigDecimal totalUnderMultiplier);
}
