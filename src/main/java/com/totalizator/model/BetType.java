package com.totalizator.model;

import java.math.BigDecimal;

/**
 * Entity class representing a type of bet.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public class BetType {
    private int id;
    private String name;
    private String description;
    private BigDecimal multiplier;

    /**
     * Default constructor.
     */
    public BetType() {
    }

    /**
     * Constructor with parameters.
     * 
     * @param id          bet type identifier
     * @param name        bet type name
     * @param description bet type description
     * @param multiplier  win multiplier
     */
    public BetType(int id, String name, String description, BigDecimal multiplier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.multiplier = multiplier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public String toString() {
        return "BetType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", multiplier=" + multiplier +
                '}';
    }
}

