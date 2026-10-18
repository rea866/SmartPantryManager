package com.richfield.smartpantry.logic;

import com.richfield.smartpantry.model.Ingredient;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The STRICT-MATCHING engine - the core business logic of the app.
 *
 * A recipe is "ready" only if EVERY ingredient it needs is in the pantry in at least the
 * required quantity. A recipe that is missing exactly one ingredient goes into a separate
 * "almost there" list (optional bonus feature); it is never mixed into the ready list.
 */
public final class RecipeMatcher {

    private static final double EPSILON = 1e-6;

    /** A recipe together with the names of the ingredients the user is missing. */
    public static final class Result {
        private final Recipe recipe;
        private final List<String> missing;

        Result(Recipe recipe, List<String> missing) {
            this.recipe = recipe;
            this.missing = missing;
        }

        public Recipe getRecipe() { return recipe; }
        /** Empty for a ready recipe; one entry for an "almost there" recipe. */
        public List<String> getMissing() { return missing; }
    }

    /** The two separate result lists. */
    public static final class Output {
        private final List<Result> ready = new ArrayList<>();
        private final List<Result> almostThere = new ArrayList<>();

        public List<Result> getReady() { return ready; }
        public List<Result> getAlmostThere() { return almostThere; }
    }

    private RecipeMatcher() { }

    public static Output match(List<Recipe> recipes, List<Ingredient> pantry, boolean ignoreExpired) {
        return match(recipes, pantry, ignoreExpired, LocalDate.now());
    }

    /**
     * @param ignoreExpired when true, pantry items past their expiry date are not counted
     * @param today         passed in (instead of read inside) so the logic is easy to unit test
     */
    public static Output match(List<Recipe> recipes, List<Ingredient> pantry,
                               boolean ignoreExpired, LocalDate today) {
        // Step 1: group usable pantry items by normalised name (handles "tomato" vs "Tomatoes").
        Map<String, List<Ingredient>> stock = new HashMap<>();
        for (Ingredient item : pantry) {
            if (item.getQuantity() <= 0) continue;
            if (ignoreExpired && ExpiryUtils.isExpired(item.getExpiryDate(), today)) continue;
            String key = NameUtils.normalize(item.getName());
            stock.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
        }

        // Step 2: check every ingredient of every recipe.
        Output output = new Output();
        for (Recipe recipe : recipes) {
            List<String> missing = new ArrayList<>();
            for (RecipeIngredient need : recipe.getIngredients()) {
                List<Ingredient> have = stock.get(NameUtils.normalize(need.getName()));
                if (!hasEnough(need, have)) {
                    missing.add(need.getName());
                }
            }
            if (missing.isEmpty()) {
                output.ready.add(new Result(recipe, missing));
            } else if (missing.size() == 1) {
                output.almostThere.add(new Result(recipe, missing));
            }
            // Recipes missing 2+ ingredients are simply not shown.
        }
        return output;
    }

    /** True when the pantry holds at least the quantity the recipe line asks for. */
    static boolean hasEnough(RecipeIngredient need, List<Ingredient> have) {
        if (have == null || have.isEmpty()) return false;
        if (need.getQuantity() <= 0) return true; // "to taste": presence is enough

        double total = 0;
        for (Ingredient item : have) {
            // Several pantry rows for the same ingredient are added together.
            if (UnitConverter.canCompare(need.getUnit(), item.getUnit())) {
                total += UnitConverter.toBase(item.getQuantity(), item.getUnit());
            }
        }
        return total + EPSILON >= UnitConverter.toBase(need.getQuantity(), need.getUnit());
    }
}
