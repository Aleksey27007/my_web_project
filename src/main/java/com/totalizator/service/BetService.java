package com.totalizator.service;

import com.totalizator.model.Bet;

import java.util.List;
import java.util.Optional;


public interface BetService {
    
    
    Optional<Bet> findById(Integer id);
    
    
    List<Bet> findAll();
    
    
    List<Bet> findByUserId(Integer userId);
    
    
    List<Bet> findByCompetitionId(Integer competitionId);
    
    
    Bet placeBet(Bet bet);
    
    
    boolean cancelBet(Integer betId);
    
    
    int processBetsForCompetition(Integer competitionId);
}

