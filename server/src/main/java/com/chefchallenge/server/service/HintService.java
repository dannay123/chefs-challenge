package com.chefchallenge.server.service;

import com.chefchallenge.server.model.Dish;
import com.chefchallenge.server.model.GameSession;
import com.chefchallenge.server.model.dto.HintResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HintService {

    private final Random random = new Random();

    /**
     * Reveals one random unrevealed item from the current recipe.
     * Deducts hint cost from session score and decrements hints remaining.
     *
     * @throws IllegalStateException if no hints remaining or all items already revealed
     */
    public HintResponse giveHint(GameSession session, String hintType) {
        if (session.getHintsRemaining() <= 0)
            throw new IllegalStateException("No hints remaining for this dish.");
        if (session.getScore() < GameSession.getHintCost())
            throw new IllegalStateException("Not enough points to use a hint.");

        Dish dish = session.getCurrentDish();
        String revealed;

        if ("INGREDIENT".equals(hintType)) {
            List<String> unrevealed = dish.getIngredients().stream()
                    .filter(i -> !session.getRevealedIngredients().contains(i))
                    .collect(Collectors.toList());
            if (unrevealed.isEmpty())
                throw new IllegalStateException("All ingredients for this dish have already been revealed.");
            revealed = unrevealed.get(random.nextInt(unrevealed.size()));
            session.getRevealedIngredients().add(revealed);

        } else if ("TECHNIQUE".equals(hintType)) {
            List<String> unrevealed = dish.getTechniques().stream()
                    .filter(t -> !session.getRevealedTechniques().contains(t))
                    .collect(Collectors.toList());
            if (unrevealed.isEmpty())
                throw new IllegalStateException("All techniques for this dish have already been revealed.");
            revealed = unrevealed.get(random.nextInt(unrevealed.size()));
            session.getRevealedTechniques().add(revealed);

        } else {
            throw new IllegalArgumentException("hintType must be 'INGREDIENT' or 'TECHNIQUE'.");
        }

        session.deductHintCost();

        String msg = "INGREDIENT".equals(hintType)
                ? "One of the ingredients is: " + revealed
                : "One of the techniques is: " + revealed;

        return new HintResponse(hintType, revealed, session.getHintsRemaining(),
                                session.getScore(), msg);
    }
}
