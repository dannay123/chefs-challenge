package com.chefchallenge.server.model.dto;

// Information firewall: ONLY these two fields — never name, ingredients, techniques, difficulty, or timeLimit
public class DishDisplayDTO {
    private String description;
    private String visualDetails;

    public DishDisplayDTO() {}
    public DishDisplayDTO(String description, String visualDetails) {
        this.description   = description;
        this.visualDetails = visualDetails;
    }

    public String getDescription()   { return description; }
    public String getVisualDetails() { return visualDetails; }
    public void setDescription(String v)   { this.description = v; }
    public void setVisualDetails(String v) { this.visualDetails = v; }
}
