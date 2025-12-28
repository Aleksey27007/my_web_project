package com.totalizator.service;

import com.totalizator.model.Competition;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Competition operations.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public interface CompetitionService {
    
    /**
     * Finds competition by id.
     * 
     * @param id competition identifier
     * @return Optional containing competition if found
     */
    Optional<Competition> findById(Integer id);
    
    /**
     * Finds all competitions.
     * 
     * @return list of all competitions
     */
    List<Competition> findAll();
    
    /**
     * Finds competitions by status.
     * 
     * @param status competition status
     * @return list of competitions with specified status
     */
    List<Competition> findByStatus(String status);
    
    /**
     * Creates a new competition.
     * 
     * @param competition competition to create
     * @return created competition
     */
    Competition createCompetition(Competition competition);
    
    /**
     * Updates competition.
     * 
     * @param competition competition to update
     * @return true if updated successfully
     */
    boolean updateCompetition(Competition competition);
    
    /**
     * Deletes competition.
     * 
     * @param id competition identifier
     * @return true if deleted successfully
     */
    boolean deleteCompetition(Integer id);
    
    /**
     * Generates random result for a competition.
     * 
     * @param competitionId competition identifier
     * @return true if result was generated successfully
     */
    boolean generateRandomResult(Integer competitionId);
}

