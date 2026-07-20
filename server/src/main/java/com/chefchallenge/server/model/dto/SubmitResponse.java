package com.chefchallenge.server.model.dto;

public class SubmitResponse {
    private GradeResult    grade;
    private RecipeReveal   recipeReveal;
    private DishDisplayDTO nextDish;    // null when sessionOver
    private int            timeLimit;   // time limit for nextDish (0 when sessionOver)
    private boolean        sessionOver;

    public SubmitResponse() {}

    public GradeResult    getGrade()        { return grade; }
    public RecipeReveal   getRecipeReveal() { return recipeReveal; }
    public DishDisplayDTO getNextDish()     { return nextDish; }
    public int            getTimeLimit()    { return timeLimit; }
    public boolean        isSessionOver()   { return sessionOver; }

    public void setGrade(GradeResult v)          { this.grade = v; }
    public void setRecipeReveal(RecipeReveal v)  { this.recipeReveal = v; }
    public void setNextDish(DishDisplayDTO v)    { this.nextDish = v; }
    public void setTimeLimit(int v)              { this.timeLimit = v; }
    public void setSessionOver(boolean v)        { this.sessionOver = v; }
}
