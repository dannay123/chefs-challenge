package com.chefchallenge.server.repository;

import com.chefchallenge.server.model.Difficulty;
import com.chefchallenge.server.model.Dish;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Hardcoded dish database. All recipes live here — never exposed to client directly.
 *
 * Data structure rationale:
 *   List<Dish> per difficulty tier — indexed random selection via Collections.shuffle().
 *   Set<String> for ingredients/techniques — O(1) contains() in SimilarityEngine.
 */
@Repository
public class DishRepository {

    public static final Set<String> ALL_INGREDIENTS = Set.of(
        "Pizza Dough", "Tomato Sauce", "Mozzarella", "Basil", "Pasta",
        "Eggs", "Pancetta", "Parmesan", "Romaine Lettuce", "Croutons",
        "Beef Patty", "Brioche Bun", "Chicken", "Bell Pepper", "Chocolate"
    );

    public static final Set<String> ALL_TECHNIQUES = Set.of(
        "BAKE", "BOIL", "FRY", "GRILL", "CHOP", "MIX", "SEASON", "FOLD", "MARINATE", "TOSS"
    );

    private final List<Dish> easyDishes;
    private final List<Dish> mediumDishes;
    private final List<Dish> hardDishes;

    public DishRepository() {
        easyDishes = List.of(
            buildMargheritaPizza(),
            buildCaesarSalad(),
            buildBeefBurger()
        );
        mediumDishes = List.of(
            buildCarbonara(),
            buildFrenchOmelette(),
            buildChickenStirFry()
        );
        hardDishes = List.of(
            buildChickenParmesan(),
            buildPastaBake()
        );
    }

    public List<Dish> getEasyDishes()   { return easyDishes; }
    public List<Dish> getMediumDishes() { return mediumDishes; }
    public List<Dish> getHardDishes()   { return hardDishes; }

    // ── EASY ─────────────────────────────────────────────────────────────────

    private Dish buildMargheritaPizza() {
        return new Dish(
            "Margherita Pizza",
            "A round flatbread with golden-brown edges, covered in vivid red sauce " +
            "with scattered white melted patches and small dark green flecks.",
            "Golden-brown crust | Bubbling white cheese pools | Bright red sauce base",
            Difficulty.EASY, 60,
            new LinkedHashSet<>(List.of("Pizza Dough", "Tomato Sauce", "Mozzarella")),
            new LinkedHashSet<>(List.of("BAKE", "SEASON"))
        );
    }

    private Dish buildCaesarSalad() {
        return new Dish(
            "Caesar Salad",
            "Tall dark green leaves stacked high, embedded with golden crunch pieces, " +
            "dusted with pale white shavings, and coated in a glossy ivory dressing.",
            "Dark crisp leaves | Golden crouton chunks | White cheese shavings",
            Difficulty.EASY, 60,
            new LinkedHashSet<>(List.of("Romaine Lettuce", "Croutons", "Parmesan")),
            new LinkedHashSet<>(List.of("CHOP", "TOSS"))
        );
    }

    private Dish buildBeefBurger() {
        return new Dish(
            "Beef Burger",
            "A glossy toasted golden bun split open around a thick dark char-marked slab, " +
            "with bright red sauce visible between the layers.",
            "Char marks on dark patty | Glossy golden bun | Red sauce peeking through",
            Difficulty.EASY, 60,
            new LinkedHashSet<>(List.of("Beef Patty", "Brioche Bun", "Tomato Sauce")),
            new LinkedHashSet<>(List.of("GRILL", "SEASON"))
        );
    }

    // ── MEDIUM ────────────────────────────────────────────────────────────────

    private Dish buildCarbonara() {
        return new Dish(
            "Spaghetti Carbonara",
            "Long twisted pale strands coated in a rich yellow-white cream, " +
            "with crispy pink curls embedded throughout and a fine pale cheese dusting.",
            "Silky pale yellow sauce | Crispy pink meat pieces | Fine cheese dusting",
            Difficulty.MEDIUM, 75,
            new LinkedHashSet<>(List.of("Pasta", "Eggs", "Pancetta", "Parmesan")),
            new LinkedHashSet<>(List.of("BOIL", "FRY", "MIX"))
        );
    }

    private Dish buildFrenchOmelette() {
        return new Dish(
            "French Omelette",
            "A smooth pale yellow crescent with a golden exterior, oozing white from " +
            "the folded centre, with flecks of pink and green inside.",
            "Pale yellow crescent shape | Melted white interior | Pink and green flecks",
            Difficulty.MEDIUM, 75,
            new LinkedHashSet<>(List.of("Eggs", "Mozzarella", "Basil", "Pancetta")),
            new LinkedHashSet<>(List.of("MIX", "FOLD", "FRY"))
        );
    }

    private Dish buildChickenStirFry() {
        return new Dish(
            "Chicken Stir Fry",
            "Pale white strips and vivid red-green chunks coated in a dark glossy sauce, " +
            "scattered with bright green herb flecks.",
            "Dark glossy sauce coating | Vivid red-green vegetable chunks | Pale white meat strips",
            Difficulty.MEDIUM, 75,
            new LinkedHashSet<>(List.of("Chicken", "Bell Pepper", "Basil", "Tomato Sauce")),
            new LinkedHashSet<>(List.of("CHOP", "FRY", "TOSS"))
        );
    }

    // ── HARD ──────────────────────────────────────────────────────────────────

    private Dish buildChickenParmesan() {
        return new Dish(
            "Chicken Parmesan",
            "A golden-crusted white slab buried under vivid red sauce and two melted cheeses, " +
            "one white and stringy, one pale and dusted, with dark green scattered on top, bronzed from heat.",
            "Golden breaded crust | Double melted cheese layer | Bronzed oven-finished top",
            Difficulty.HARD, 90,
            new LinkedHashSet<>(List.of("Chicken", "Tomato Sauce", "Mozzarella", "Parmesan", "Basil")),
            new LinkedHashSet<>(List.of("MARINATE", "FRY", "BAKE", "SEASON"))
        );
    }

    private Dish buildPastaBake() {
        return new Dish(
            "Pasta Bake",
            "A bubbling deep casserole of curved pasta shapes in vivid red sauce with dark meat chunks, " +
            "topped with a golden-brown crackling double-cheese crust.",
            "Bubbly golden cheese crust | Curved pasta shapes visible | Dark meat chunks in red sauce",
            Difficulty.HARD, 90,
            new LinkedHashSet<>(List.of("Pasta", "Tomato Sauce", "Mozzarella", "Parmesan", "Beef Patty")),
            new LinkedHashSet<>(List.of("BOIL", "MIX", "BAKE", "SEASON"))
        );
    }
}
