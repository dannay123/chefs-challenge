package com.chefchallenge.server.model;

import java.util.Set;

public class Dish {

    private final String      name;
    private final String      description;
    private final String      visualDetails;
    private final Difficulty  difficulty;
    private final int         timeLimit;
    private final Set<String> ingredients;
    private final Set<String> techniques;

    public Dish(String name, String description, String visualDetails,
                Difficulty difficulty, int timeLimit,
                Set<String> ingredients, Set<String> techniques) {
        this.name          = name;
        this.description   = description;
        this.visualDetails = visualDetails;
        this.difficulty    = difficulty;
        this.timeLimit     = timeLimit;
        this.ingredients   = ingredients;
        this.techniques    = techniques;
    }

    public String      getName()         { return name; }
    public String      getDescription()  { return description; }
    public String      getVisualDetails(){ return visualDetails; }
    public Difficulty  getDifficulty()   { return difficulty; }
    public int         getTimeLimit()    { return timeLimit; }
    public Set<String> getIngredients()  { return ingredients; }
    public Set<String> getTechniques()   { return techniques; }
}
