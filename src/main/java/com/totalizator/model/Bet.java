package com.totalizator.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing a bet made by a user.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public class Bet {
    private int id;
    private User user;
    private Competition competition;
    private BetType betType;
    private BigDecimal amount;
    private String predictedValue;
    private BetStatus status;
    private BigDecimal winAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Enumeration for bet status.
     */
    public enum BetStatus {
        PENDING, WON, LOST, CANCELLED
    }

    /**
     * Default constructor.
     */
    public Bet() {
    }

    /**
     * Constructor with parameters.
     * 
     * @param id            bet identifier
     * @param user          user who made the bet
     * @param competition   competition the bet is on
     * @param betType       type of bet
     * @param amount        bet amount
     * @param predictedValue predicted outcome
     */
    public Bet(int id, User user, Competition competition, BetType betType, 
               BigDecimal amount, String predictedValue) {
        this.id = id;
        this.user = user;
        this.competition = competition;
        this.betType = betType;
        this.amount = amount;
        this.predictedValue = predictedValue;
        this.status = BetStatus.PENDING;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public BetType getBetType() {
        return betType;
    }

    public void setBetType(BetType betType) {
        this.betType = betType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPredictedValue() {
        return predictedValue;
    }

    public void setPredictedValue(String predictedValue) {
        this.predictedValue = predictedValue;
    }

    public BetStatus getStatus() {
        return status;
    }

    public void setStatus(BetStatus status) {
        this.status = status;
    }

    public BigDecimal getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(BigDecimal winAmount) {
        this.winAmount = winAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Bet{" +
                "id=" + id +
                ", user=" + user +
                ", competition=" + competition +
                ", betType=" + betType +
                ", amount=" + amount +
                ", predictedValue='" + predictedValue + '\'' +
                ", status=" + status +
                ", winAmount=" + winAmount +
                '}';
    }
}

