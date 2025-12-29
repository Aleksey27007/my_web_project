package com.totalizator.service;

import com.totalizator.model.Competition;

import java.util.List;
import java.util.Optional;


public interface CompetitionService {
    
    
    Optional<Competition> findById(Integer id);
    
    
    List<Competition> findAll();
    
    
    List<Competition> findByStatus(String status);
    
    
    Competition createCompetition(Competition competition);
    
    
    boolean updateCompetition(Competition competition);
    
    
    boolean deleteCompetition(Integer id);
    
    
    boolean generateRandomResult(Integer competitionId);
}

