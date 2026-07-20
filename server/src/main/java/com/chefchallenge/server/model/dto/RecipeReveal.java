package com.chefchallenge.server.model.dto;

import java.util.List;

/** Reveals the secret recipe — only included in submit/timeout responses, never before. */
public class RecipeReveal {
    private String dishName;
    private List<String> ingredients;
    private List<String> techniques;

    public RecipeReveal() {}
    public RecipeReveal(String dishName, List<String> ingredients, List<String> techniques) {
        this.dishName = dishName;
        this.ingredients = ingredients;
        this.techniques = techniques;
    }

    public String       getDishName()    { return dishName; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getTechniques()  { return techniques; }
    public void setDishName(String n)        { this.dishName = n; }
    public void setIngredients(List<String> i){ this.ingredients = i; }
    public void setTechniques(List<String> t) { this.techniques = t; }
}
