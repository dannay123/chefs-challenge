package com.chefchallenge.client.model;

public class DishDisplayDTO {
    private String description;
    private String visualDetails;
    public DishDisplayDTO() {}
    public String getDescription()   { return description; }
    public String getVisualDetails() { return visualDetails; }
    public void setDescription(String v)   { this.description = v; }
    public void setVisualDetails(String v) { this.visualDetails = v; }
}
