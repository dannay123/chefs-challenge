package com.chefchallenge.server.model.dto;

import java.util.List;

public class SubmitRequest {
    private List<String> ingredients;
    private List<String> techniques;

    public SubmitRequest() {}
    public List<String> getIngredients()           { return ingredients; }
    public List<String> getTechniques()            { return techniques; }
    public void setIngredients(List<String> v)     { this.ingredients = v; }
    public void setTechniques(List<String> v)      { this.techniques = v; }
}
