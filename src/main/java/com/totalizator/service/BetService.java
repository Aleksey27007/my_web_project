package com.totalizator.service;

import com.totalizator.model.Bet;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Bet operations.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public interface BetService {
    
    /**
     * Finds bet by id.
     * 
     * @param id bet identifier
     * @return Optional containing bet if found
     */
    Optional<Bet> findById(Integer id);
    
    /**
     * Finds all bets.
     * 
     * @return list of all bets
     */
    List<Bet> findAll();
    
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
    
    /**
     * Places a new bet.
     * 
     * @param bet bet to place
     * @return created bet
     */
    Bet placeBet(Bet bet);
    
    /**
     * Cancels a bet.
     * 
     * @param betId bet identifier
     * @return true if cancelled successfully
     */
    boolean cancelBet(Integer betId);
    
    /**
     * Processes bets for a finished competition.
     * 
     * @param competitionId competition identifier
     * @return number of processed bets
     */
    int processBetsForCompetition(Integer competitionId);
}

