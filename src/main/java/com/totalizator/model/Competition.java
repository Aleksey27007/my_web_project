package com.totalizator.model;

import java.time.LocalDateTime;

/**
 * Entity class representing a sports competition.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public class Competition {
    private int id;
    private String title;
    private String description;
    private String sportType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CompetitionStatus status;
    private String result;
    private String team1;
    private String team2;
    private Integer score1;
    private Integer score2;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Enumeration for competition status.
     */
    public enum CompetitionStatus {
        SCHEDULED, IN_PROGRESS, FINISHED, CANCELLED
    }

    /**
     * Default constructor.
     */
    public Competition() {
    }

    /**
     * Constructor with parameters.
     * 
     * @param id          competition identifier
     * @param title       competition title
     * @param sportType   type of sport
     * @param startDate   start date and time
     * @param team1       first team name
     * @param team2       second team name
     */
    public Competition(int id, String title, String sportType, LocalDateTime startDate, 
                       String team1, String team2) {
        this.id = id;
        this.title = title;
        this.sportType = sportType;
        this.startDate = startDate;
        this.team1 = team1;
        this.team2 = team2;
        this.status = CompetitionStatus.SCHEDULED;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public CompetitionStatus getStatus() {
        return status;
    }

    public void setStatus(CompetitionStatus status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public Integer getScore1() {
        return score1;
    }

    public void setScore1(Integer score1) {
        this.score1 = score1;
    }

    public Integer getScore2() {
        return score2;
    }

    public void setScore2(Integer score2) {
        this.score2 = score2;
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
        return "Competition{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", sportType='" + sportType + '\'' +
                ", startDate=" + startDate +
                ", status=" + status +
                ", team1='" + team1 + '\'' +
                ", team2='" + team2 + '\'' +
                '}';
    }
}

