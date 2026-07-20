package com.chefchallenge.server.service;

import com.chefchallenge.server.model.*;
import com.chefchallenge.server.model.dto.*;
import com.chefchallenge.server.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Manages all active game sessions.
 *
 * Data structure: ConcurrentHashMap<String, GameSession>
 *   Key:   sessionId (UUID)
 *   Value: GameSession
 *   Why:   O(1) lookup; thread-safe for concurrent HTTP requests.
 */
@Service
public class SessionService {

    private final DishRepository  dishRepo;
    private final SimilarityEngine scorer;
    private final HintService      hintService;
    private final Random           random = new Random();

    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();

    public SessionService(DishRepository dishRepo, SimilarityEngine scorer, HintService hintService) {
        this.dishRepo    = dishRepo;
        this.scorer      = scorer;
        this.hintService = hintService;
    }

    // ── Start ─────────────────────────────────────────────────────────────────

    public StartSessionResponse startSession() {
        List<Dish> queue = new ArrayList<>();
        queue.addAll(pickRandom(dishRepo.getEasyDishes(),   2));
        queue.addAll(pickRandom(dishRepo.getMediumDishes(), 2));
        queue.addAll(pickRandom(dishRepo.getHardDishes(),   2));

        String      sessionId = UUID.randomUUID().toString();
        GameSession session   = new GameSession(sessionId, queue);
        sessions.put(sessionId, session);

        Dish first = session.getCurrentDish();
        return new StartSessionResponse(sessionId, toDisplayDTO(first), first.getTimeLimit());
    }

    // ── Hint ──────────────────────────────────────────────────────────────────

    public HintResponse requestHint(String sessionId, String hintType) {
        GameSession session = getSession(sessionId);
        return hintService.giveHint(session, hintType);
    }

    // ── Submit ────────────────────────────────────────────────────────────────

    public SubmitResponse submitDish(String sessionId, SubmitRequest request) {
        GameSession session = getSession(sessionId);
        Dish        dish    = session.getCurrentDish();

        Set<String> pIng  = request.getIngredients() != null ? new HashSet<>(request.getIngredients()) : new HashSet<>();
        Set<String> pTech = request.getTechniques()  != null ? new HashSet<>(request.getTechniques())  : new HashSet<>();

        GradeResult grade = scorer.grade(pIng, pTech, dish, session.getScore());
        session.addScore(grade.getPointsEarned());

        return buildSubmitResponse(session, grade, dish);
    }

    // ── Timeout ───────────────────────────────────────────────────────────────

    public SubmitResponse handleTimeout(String sessionId) {
        GameSession session = getSession(sessionId);
        Dish        dish    = session.getCurrentDish();
        GradeResult grade   = scorer.gradeTimeout(dish, session.getScore());
        return buildSubmitResponse(session, grade, dish);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private SubmitResponse buildSubmitResponse(GameSession session, GradeResult grade, Dish gradedDish) {
        RecipeReveal reveal = new RecipeReveal(
            gradedDish.getName(),
            new ArrayList<>(gradedDish.getIngredients()),
            new ArrayList<>(gradedDish.getTechniques())
        );

        session.advanceToNextDish();

        SubmitResponse resp = new SubmitResponse();
        resp.setGrade(grade);
        resp.setRecipeReveal(reveal);
        resp.setSessionOver(session.isOver());

        if (!session.isOver()) {
            Dish next = session.getCurrentDish();
            resp.setNextDish(toDisplayDTO(next));
            resp.setTimeLimit(next.getTimeLimit());
        }
        return resp;
    }

    private GameSession getSession(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null) throw new NoSuchElementException("Session not found: " + sessionId);
        if (session.isOver())  throw new IllegalStateException("Session is already complete.");
        return session;
    }

    private DishDisplayDTO toDisplayDTO(Dish dish) {
        return new DishDisplayDTO(dish.getDescription(), dish.getVisualDetails());
    }

    private <T> List<T> pickRandom(List<T> pool, int n) {
        List<T> copy = new ArrayList<>(pool);
        Collections.shuffle(copy, random);
        return copy.subList(0, Math.min(n, copy.size()));
    }
}
