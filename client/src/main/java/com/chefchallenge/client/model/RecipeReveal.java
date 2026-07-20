package com.chefchallenge.client.model;
import java.util.List;
public class RecipeReveal {
    private String dishName;
    private List<String> ingredients;
    private List<String> techniques;
    public RecipeReveal() {}
    public String       getDishName()    { return dishName; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getTechniques()  { return techniques; }
    public void setDishName(String v)        { this.dishName = v; }
    public void setIngredients(List<String> v){ this.ingredients = v; }
    public void setTechniques(List<String> v) { this.techniques = v; }
}
