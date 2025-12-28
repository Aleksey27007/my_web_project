package com.totalizator.service.impl;

import com.totalizator.dao.BetDao;
import com.totalizator.dao.Dao;
import com.totalizator.model.Bet;
import com.totalizator.model.BetType;
import com.totalizator.model.Competition;
import com.totalizator.model.User;
import com.totalizator.service.BetService;
import com.totalizator.service.CompetitionService;
import com.totalizator.service.UserService;
import com.totalizator.service.factory.DaoFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of BetService.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public class BetServiceImpl implements BetService {
    private static final Logger logger = LogManager.getLogger();
    private final BetDao betDao;
    private final UserService userService;
    private final CompetitionService competitionService;
    private final Dao<BetType, Integer> betTypeDao;

    /**
     * Constructor.
     */
    public BetServiceImpl() {
        this.betDao = DaoFactory.getInstance().getBetDao();
        this.userService = new UserServiceImpl();
        this.competitionService = new CompetitionServiceImpl();
        this.betTypeDao = DaoFactory.getInstance().getBetTypeDao();
    }

    @Override
    public Optional<Bet> findById(Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return betDao.findById(id);
    }

    @Override
    public List<Bet> findAll() {
        return betDao.findAll();
    }

    @Override
    public List<Bet> findByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            return Arrays.asList();
        }
        return betDao.findByUserId(userId);
    }

    @Override
    public List<Bet> findByCompetitionId(Integer competitionId) {
        if (competitionId == null || competitionId <= 0) {
            return Arrays.asList();
        }
        return betDao.findByCompetitionId(competitionId);
    }

    @Override
    public Bet placeBet(Bet bet) {
        validateBet(bet);
        
        // Check user balance
        User user = bet.getUser();
        Optional<User> userOptional = userService.findById(user.getId());
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User actualUser = userOptional.get();
        if (actualUser.getBalance().compareTo(bet.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        
        // Check competition exists and is scheduled
        Optional<Competition> competitionOptional = competitionService.findById(bet.getCompetition().getId());
        if (competitionOptional.isEmpty()) {
            throw new IllegalArgumentException("Competition not found");
        }
        
        Competition competition = competitionOptional.get();
        if (competition.getStatus() != Competition.CompetitionStatus.SCHEDULED) {
            throw new IllegalArgumentException("Competition is not available for betting");
        }
        
        // Deduct bet amount from user balance
        BigDecimal newBalance = actualUser.getBalance().subtract(bet.getAmount());
        actualUser.setBalance(newBalance);
        userService.updateUser(actualUser);
        
        // Set bet status
        bet.setStatus(Bet.BetStatus.PENDING);
        bet.setUser(actualUser);
        
        logger.info("Placing bet: user={}, competition={}, amount={}", 
                actualUser.getUsername(), competition.getTitle(), bet.getAmount());
        return betDao.save(bet);
    }

    @Override
    public boolean cancelBet(Integer betId) {
        Optional<Bet> betOptional = betDao.findById(betId);
        if (!betOptional.isPresent()) {
            return false;
        }
        
        Bet bet = betOptional.get();
        if (bet.getStatus() != Bet.BetStatus.PENDING) {
            logger.warn("Cannot cancel bet {} with status {}", betId, bet.getStatus());
            return false;
        }
        
        // Check if competition is still scheduled
        Competition competition = bet.getCompetition();
        if (competition.getStatus() != Competition.CompetitionStatus.SCHEDULED) {
            logger.warn("Cannot cancel bet {} - competition already started/finished", betId);
            return false;
        }
        
        // Return money to user
        User user = bet.getUser();
        BigDecimal newBalance = user.getBalance().add(bet.getAmount());
        user.setBalance(newBalance);
        userService.updateUser(user);
        
        // Update bet status
        bet.setStatus(Bet.BetStatus.CANCELLED);
        boolean updated = betDao.update(bet);
        
        logger.info("Bet {} cancelled, amount {} returned to user {}", 
                betId, bet.getAmount(), user.getUsername());
        return updated;
    }

    @Override
    public int processBetsForCompetition(Integer competitionId) {
        Optional<Competition> competitionOptional = competitionService.findById(competitionId);
        if (!competitionOptional.isPresent()) {
            logger.warn("Competition not found: {}", competitionId);
            return 0;
        }
        
        Competition competition = competitionOptional.get();
        if (competition.getStatus() != Competition.CompetitionStatus.FINISHED) {
            logger.warn("Competition {} is not finished", competitionId);
            return 0;
        }
        
        List<Bet> bets = betDao.findByCompetitionId(competitionId);
        int processedCount = 0;
        
        for (Bet bet : bets) {
            if (bet.getStatus() == Bet.BetStatus.PENDING) {
                boolean won = checkBetWin(bet, competition);
                bet.setStatus(won ? Bet.BetStatus.WON : Bet.BetStatus.LOST);
                
                if (won) {
                    // Calculate win amount
                    BigDecimal winAmount = bet.getAmount().multiply(bet.getBetType().getMultiplier());
                    bet.setWinAmount(winAmount);
                    
                    // Add winnings to user balance
                    User user = bet.getUser();
                    BigDecimal newBalance = user.getBalance().add(winAmount);
                    user.setBalance(newBalance);
                    userService.updateUser(user);
                    
                    logger.info("Bet {} won, user {} receives {}", bet.getId(), user.getUsername(), winAmount);
                }
                
                betDao.update(bet);
                processedCount++;
            }
        }
        
        logger.info("Processed {} bets for competition {}", processedCount, competitionId);
        return processedCount;
    }

    /**
     * Checks if a bet won based on competition result.
     * 
     * @param bet bet to check
     * @param competition competition with result
     * @return true if bet won
     */
    private boolean checkBetWin(Bet bet, Competition competition) {
        String betTypeName = bet.getBetType().getName();
        String predictedValue = bet.getPredictedValue();
        String result = competition.getResult();
        Integer score1 = competition.getScore1();
        Integer score2 = competition.getScore2();
        
        if (score1 == null || score2 == null) {
            return false;
        }
        
        switch (betTypeName) {
            case "WIN":
                return (result.equals("WIN_TEAM1") && predictedValue.equals("TEAM1")) ||
                       (result.equals("WIN_TEAM2") && predictedValue.equals("TEAM2"));
            case "DRAW":
                return result.equals("DRAW") && predictedValue.equals("DRAW");
            case "LOSS":
                return (result.equals("WIN_TEAM2") && predictedValue.equals("TEAM1")) ||
                       (result.equals("WIN_TEAM1") && predictedValue.equals("TEAM2"));
            case "EXACT_SCORE":
                String expectedScore = score1 + ":" + score2;
                return expectedScore.equals(predictedValue);
            default:
                return false;
        }
    }

    /**
     * Validates bet data.
     * 
     * @param bet bet to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateBet(Bet bet) {
        if (bet == null) {
            throw new IllegalArgumentException("Bet cannot be null");
        }
        if (bet.getUser() == null || bet.getUser().getId() <= 0) {
            throw new IllegalArgumentException("Invalid user");
        }
        if (bet.getCompetition() == null || bet.getCompetition().getId() <= 0) {
            throw new IllegalArgumentException("Invalid competition");
        }
        if (bet.getBetType() == null || bet.getBetType().getId() <= 0) {
            throw new IllegalArgumentException("Invalid bet type");
        }
        if (bet.getAmount() == null || bet.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bet amount must be positive");
        }
        if (bet.getPredictedValue() == null || bet.getPredictedValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Predicted value cannot be empty");
        }
    }
}

