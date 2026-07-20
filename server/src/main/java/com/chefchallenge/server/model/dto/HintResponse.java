package com.chefchallenge.server.model.dto;

public class HintResponse {
    private String hintType;
    private String revealed;
    private int    hintsRemaining;
    private int    newScore;
    private String message;

    public HintResponse() {}
    public HintResponse(String hintType, String revealed, int hintsRemaining,
                        int newScore, String message) {
        this.hintType = hintType; this.revealed = revealed;
        this.hintsRemaining = hintsRemaining; this.newScore = newScore;
        this.message = message;
    }

    public String getHintType()        { return hintType; }
    public String getRevealed()        { return revealed; }
    public int    getHintsRemaining()  { return hintsRemaining; }
    public int    getNewScore()        { return newScore; }
    public String getMessage()         { return message; }
    public void setHintType(String v)       { this.hintType = v; }
    public void setRevealed(String v)       { this.revealed = v; }
    public void setHintsRemaining(int v)    { this.hintsRemaining = v; }
    public void setNewScore(int v)          { this.newScore = v; }
    public void setMessage(String v)        { this.message = v; }
}
