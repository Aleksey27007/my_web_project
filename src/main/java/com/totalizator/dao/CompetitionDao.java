package com.totalizator.dao;

import com.totalizator.model.Competition;

import java.util.List;

/**
 * DAO interface for Competition entity.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public interface CompetitionDao extends Dao<Competition, Integer> {
    
    /**
     * Finds competitions by status.
     * 
     * @param status competition status
     * @return list of competitions with specified status
     */
    List<Competition> findByStatus(String status);
}

