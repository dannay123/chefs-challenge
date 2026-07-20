package com.chefchallenge.client.model;
public class HintResponse {
    private String hintType, revealed, message;
    private int hintsRemaining, newScore;
    public HintResponse() {}
    public String getHintType()        { return hintType; }
    public String getRevealed()        { return revealed; }
    public String getMessage()         { return message; }
    public int    getHintsRemaining()  { return hintsRemaining; }
    public int    getNewScore()        { return newScore; }
    public void setHintType(String v)       { this.hintType = v; }
    public void setRevealed(String v)       { this.revealed = v; }
    public void setMessage(String v)        { this.message = v; }
    public void setHintsRemaining(int v)    { this.hintsRemaining = v; }
    public void setNewScore(int v)          { this.newScore = v; }
}
