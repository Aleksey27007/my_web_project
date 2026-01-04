package com.totalizator.dao;

import com.totalizator.model.Bet;

import java.util.List;


public interface BetDao extends Dao<Bet, Integer> {
    List<Bet> findByUserId(Integer userId);
    List<Bet> findByCompetitionId(Integer competitionId);
}

