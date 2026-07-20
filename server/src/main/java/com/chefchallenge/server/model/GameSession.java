package com.chefchallenge.server.model;

import java.util.*;

/**
 * Mutable session state for one player's game.
 *
 * Data structures:
 *   List<Dish>    dishQueue          — ordered queue of 6 dishes; O(1) access by index
 *   Set<String>   revealedIngredients — tracks which hints were given this dish; O(1) contains
 *   Set<String>   revealedTechniques  — same for techniques
 */
public class GameSession {

    private static final int HINTS_PER_DISH = 3;
    private static final int HINT_COST      = 10;

    private final String     sessionId;
    private final List<Dish> dishQueue;
    private int              currentIndex   = 0;
    private int              score          = 0;

    // Reset on each new dish
    private int          hintsRemaining     = HINTS_PER_DISH;
    private Set<String>  revealedIngredients = new HashSet<>();
    private Set<String>  revealedTechniques  = new HashSet<>();

    public GameSession(String sessionId, List<Dish> dishQueue) {
        this.sessionId = sessionId;
        this.dishQueue = Collections.unmodifiableList(new ArrayList<>(dishQueue));
    }

    public Dish getCurrentDish() {
        if (isOver()) throw new IllegalStateException("Session is already over.");
        return dishQueue.get(currentIndex);
    }

    public boolean isOver() { return currentIndex >= dishQueue.size(); }

    /** Advance to the next dish. Resets all hint state. */
    public void advanceToNextDish() {
        currentIndex++;
        hintsRemaining = HINTS_PER_DISH;
        revealedIngredients = new HashSet<>();
        revealedTechniques  = new HashSet<>();
    }

    public void addScore(int delta) { this.score += delta; }

    public void deductHintCost() {
        score = Math.max(0, score - HINT_COST);
        hintsRemaining--;
    }

    public String     getSessionId()           { return sessionId; }
    public int        getCurrentIndex()        { return currentIndex; }
    public int        getTotalDishes()         { return dishQueue.size(); }
    public int        getScore()               { return score; }
    public int        getHintsRemaining()      { return hintsRemaining; }
    public Set<String> getRevealedIngredients(){ return revealedIngredients; }
    public Set<String> getRevealedTechniques() { return revealedTechniques; }

    public static int getHintCost()      { return HINT_COST; }
    public static int getHintsPerDish()  { return HINTS_PER_DISH; }
}
