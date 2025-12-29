package com.totalizator.dao;

import com.totalizator.model.Competition;

import java.util.List;


public interface CompetitionDao extends Dao<Competition, Integer> {
    
    
    List<Competition> findByStatus(String status);
}

