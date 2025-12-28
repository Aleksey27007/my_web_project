package com.totalizator.model;

/**
 * Entity class representing user role in the system.
 * 
 * @author Totalizator Team
 * @version 1.0
 */
public class Role {
    private int id;
    private String name;
    private String description;

    /**
     * Default constructor.
     */
    public Role() {
    }

    /**
     * Constructor with parameters.
     * 
     * @param id          role identifier
     * @param name        role name
     * @param description role description
     */
    public Role(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

