package com.chefchallenge.server.model.dto;

public class StartSessionResponse {
    private String        sessionId;
    private DishDisplayDTO dishDisplay;
    private int           timeLimit;

    public StartSessionResponse() {}
    public StartSessionResponse(String sessionId, DishDisplayDTO dishDisplay, int timeLimit) {
        this.sessionId   = sessionId;
        this.dishDisplay = dishDisplay;
        this.timeLimit   = timeLimit;
    }

    public String         getSessionId()   { return sessionId; }
    public DishDisplayDTO getDishDisplay() { return dishDisplay; }
    public int            getTimeLimit()   { return timeLimit; }
    public void setSessionId(String v)            { this.sessionId = v; }
    public void setDishDisplay(DishDisplayDTO v)  { this.dishDisplay = v; }
    public void setTimeLimit(int v)               { this.timeLimit = v; }
}
