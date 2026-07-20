package com.chefchallenge.server.service;

import com.chefchallenge.server.model.Dish;
import com.chefchallenge.server.model.Tier;
import com.chefchallenge.server.model.dto.GradeResult;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SimilarityEngine {

    public GradeResult grade(Set<String> playerIngredients, Set<String> playerTechniques,
                             Dish recipe, int currentScore) {
        long ingMatches = playerIngredients.stream()
                .filter(recipe.getIngredients()::contains).count();
        double ingScore = (double) ingMatches / recipe.getIngredients().size();

        long techMatches = playerTechniques.stream()
                .filter(recipe.getTechniques()::contains).count();
        double techScore = (double) techMatches / recipe.getTechniques().size();

        double similarity = (ingScore + techScore) / 2.0;

        Tier tier;
        int  points;
        if      (similarity >= 0.90) { tier = Tier.PERFECT;  points = 100; }
        else if (similarity >= 0.60) { tier = Tier.GOOD;     points = 50;  }
        else if (similarity >= 0.30) { tier = Tier.BAD;      points = 0;   }
        else                         { tier = Tier.TERRIBLE; points = -30; }

        return new GradeResult(tier.name(), similarity, ingScore, techScore, points, currentScore + points);
    }

    public GradeResult gradeTimeout(Dish recipe, int currentScore) {
        return new GradeResult(Tier.TIMEOUT.name(), 0.0, 0.0, 0.0, 0, currentScore);
    }
}
