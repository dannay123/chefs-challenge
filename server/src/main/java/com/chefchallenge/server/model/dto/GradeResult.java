package com.chefchallenge.server.model.dto;

public class GradeResult {
    private String tier;
    private double similarityScore;
    private double ingredientScore;
    private double techniqueScore;
    private int    pointsEarned;
    private int    totalScore;

    public GradeResult() {}
    public GradeResult(String tier, double similarityScore, double ingredientScore,
                       double techniqueScore, int pointsEarned, int totalScore) {
        this.tier            = tier;
        this.similarityScore = similarityScore;
        this.ingredientScore = ingredientScore;
        this.techniqueScore  = techniqueScore;
        this.pointsEarned    = pointsEarned;
        this.totalScore      = totalScore;
    }

    public String getTier()            { return tier; }
    public double getSimilarityScore() { return similarityScore; }
    public double getIngredientScore() { return ingredientScore; }
    public double getTechniqueScore()  { return techniqueScore; }
    public int    getPointsEarned()    { return pointsEarned; }
    public int    getTotalScore()      { return totalScore; }

    public void setTier(String v)            { this.tier = v; }
    public void setSimilarityScore(double v) { this.similarityScore = v; }
    public void setIngredientScore(double v) { this.ingredientScore = v; }
    public void setTechniqueScore(double v)  { this.techniqueScore = v; }
    public void setPointsEarned(int v)       { this.pointsEarned = v; }
    public void setTotalScore(int v)         { this.totalScore = v; }
}
