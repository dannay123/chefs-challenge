package com.chefchallenge.client.model;

public class StartSessionResponse {
    private String        sessionId;
    private DishDisplayDTO dishDisplay;
    private int           timeLimit;
    public StartSessionResponse() {}
    public String         getSessionId()   { return sessionId; }
    public DishDisplayDTO getDishDisplay() { return dishDisplay; }
    public int            getTimeLimit()   { return timeLimit; }
    public void setSessionId(String v)            { this.sessionId = v; }
    public void setDishDisplay(DishDisplayDTO v)  { this.dishDisplay = v; }
    public void setTimeLimit(int v)               { this.timeLimit = v; }
}
