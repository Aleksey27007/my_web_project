package com.totalizator.dao;

import com.totalizator.model.Bet;

import java.util.List;

/**
 * DAO interface for Bet entity.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public interface BetDao extends Dao<Bet, Integer> {
    
    /**
     * Finds bets by user id.
     * 
     * @param userId user identifier
     * @return list of user's bets
     */
    List<Bet> findByUserId(Integer userId);
    
    /**
     * Finds bets by competition id.
     * 
     * @param competitionId competition identifier
     * @return list of bets for competition
     */
    List<Bet> findByCompetitionId(Integer competitionId);
}

