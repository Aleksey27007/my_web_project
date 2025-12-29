package com.totalizator.service.impl;

import com.totalizator.dao.CompetitionDao;
import com.totalizator.model.Competition;
import com.totalizator.service.CompetitionService;
import com.totalizator.service.factory.DaoFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;


public class CompetitionServiceImpl implements CompetitionService {
    private static final Logger logger = LogManager.getLogger();
    private final CompetitionDao competitionDao;
    private final Random random = new Random();

    
    public CompetitionServiceImpl() {
        this.competitionDao = DaoFactory.getInstance().getCompetitionDao();
    }

    @Override
    public Optional<Competition> findById(Integer id) {
        if (!com.totalizator.util.ValidationUtils.isValidId(id)) {
            return Optional.empty();
        }
        return competitionDao.findById(id);
    }

    @Override
    public List<Competition> findAll() {
        return competitionDao.findAll();
    }

    @Override
    public List<Competition> findByStatus(String status) {
        if (StringUtils.isBlank(status)) {
            return findAll();
        }
        return competitionDao.findByStatus(status);
    }

    @Override
    public Competition createCompetition(Competition competition) {
        validateCompetition(competition);
        logger.info("Creating competition: {}", competition.getTitle());
        return competitionDao.save(competition);
    }

    @Override
    public boolean updateCompetition(Competition competition) {
        if (competition == null || competition.getId() <= 0) {
            return false;
        }
        validateCompetition(competition);
        return competitionDao.update(competition);
    }

    @Override
    public boolean deleteCompetition(Integer id) {
        if (!com.totalizator.util.ValidationUtils.isValidId(id)) {
            return false;
        }
        logger.info("Deleting competition with id: {}", id);
        return competitionDao.deleteById(id);
    }

    @Override
    public boolean generateRandomResult(Integer competitionId) {
        Optional<Competition> competitionOptional = competitionDao.findById(competitionId);
        if (!competitionOptional.isPresent()) {
            logger.warn("Competition not found for id: {}", competitionId);
            return false;
        }
        
        Competition competition = competitionOptional.get();

        int score1 = random.nextInt(6); // 0-5 goals
        int score2 = random.nextInt(6);
        
        competition.setScore1(score1);
        competition.setScore2(score2);
        competition.setStatus(Competition.CompetitionStatus.FINISHED);
        competition.setEndDate(LocalDateTime.now());

        if (score1 > score2) {
            competition.setResult("WIN_TEAM1");
        } else if (score2 > score1) {
            competition.setResult("WIN_TEAM2");
        } else {
            competition.setResult("DRAW");
        }
        
        logger.info("Generated random result for competition {}: {}-{}", 
                competitionId, score1, score2);
        return competitionDao.update(competition);
    }

    
    private void validateCompetition(Competition competition) {
        if (competition == null) {
            throw new IllegalArgumentException("Competition cannot be null");
        }
        if (StringUtils.isBlank(competition.getTitle())) {
            throw new IllegalArgumentException("Competition title cannot be empty");
        }
        if (StringUtils.isBlank(competition.getTeam1())) {
            throw new IllegalArgumentException("Team1 cannot be empty");
        }
        if (StringUtils.isBlank(competition.getTeam2())) {
            throw new IllegalArgumentException("Team2 cannot be empty");
        }
        if (competition.getStartDate() == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (competition.getStatus() == null) {
            competition.setStatus(Competition.CompetitionStatus.SCHEDULED);
        }
    }
}

