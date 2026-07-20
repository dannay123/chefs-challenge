package com.chefchallenge.server.model.dto;

public class HintRequest {
    private String hintType; // "INGREDIENT" or "TECHNIQUE"
    public HintRequest() {}
    public String getHintType()        { return hintType; }
    public void setHintType(String v)  { this.hintType = v; }
}
