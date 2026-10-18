package com.richfield.smartpantry.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.richfield.smartpantry.db.SeedData;
import com.richfield.smartpantry.model.Ingredient;
import com.richfield.smartpantry.model.Recipe;

import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the strict-matching rule (assignment section 2.3).
 * Run with: Right-click this file in Android Studio -> Run 'RecipeMatcherTest'.
 */
public class RecipeMatcherTest {

    private static final String OMELETTE = "Tomato Omelette";
    private final List<Recipe> recipes = SeedData.getRecipes();
    private final LocalDate today = LocalDate.of(2026, 9, 30);

    private Ingredient item(String name, double qty, String unit, String expiry) {
        return new Ingredient(0, name, qty, unit, expiry);
    }

    /** Everything the Tomato Omelette needs (eggs, tomato, butter, salt). */
    private List<Ingredient> omelettePantry() {
        List<Ingredient> pantry = new ArrayList<>();
        pantry.add(item("Eggs", 3, "pcs", null));
        pantry.add(item("Tomatoes", 1, "pcs", null));
        pantry.add(item("butter", 10, "g", null));
        pantry.add(item("salt", 500, "g", null));
        return pantry;
    }

    private boolean contains(List<RecipeMatcher.Result> results, String recipeName) {
        for (RecipeMatcher.Result r : results) {
            if (r.getRecipe().getName().equals(recipeName)) return true;
        }
        return false;
    }

    @Test
    public void seedDataHasAtLeastFifteenRecipes() {
        assertTrue(recipes.size() >= 15);
    }

    @Test
    public void emptyPantrySuggestsNothing() {
        RecipeMatcher.Output out = RecipeMatcher.match(recipes, new ArrayList<Ingredient>(), true, today);
        assertTrue(out.getReady().isEmpty());
        assertTrue(out.getAlmostThere().isEmpty());
    }

    @Test
    public void allIngredientsPresentIsSuggested_evenWithPluralNames() {
        RecipeMatcher.Output out = RecipeMatcher.match(recipes, omelettePantry(), true, today);
        assertTrue(contains(out.getReady(), OMELETTE)); // "Tomatoes"/"Eggs" match "tomato"/"egg"
    }

    @Test
    public void oneMissingIngredientIsExcludedFromStrictList() {
        List<Ingredient> pantry = omelettePantry();
        pantry.remove(1); // remove the tomatoes
        RecipeMatcher.Output out = RecipeMatcher.match(recipes, pantry, true, today);
        assertFalse(contains(out.getReady(), OMELETTE));
        assertTrue(contains(out.getAlmostThere(), OMELETTE)); // separate "almost there" list
    }

    @Test
    public void insufficientQuantityIsExcluded() {
        List<Ingredient> pantry = omelettePantry();
        pantry.set(0, item("Eggs", 2, "pcs", null)); // recipe needs 3
        RecipeMatcher.Output out = RecipeMatcher.match(recipes, pantry, true, today);
        assertFalse(contains(out.getReady(), OMELETTE));
    }

    @Test
    public void unitsAreConverted() {
        List<Ingredient> pantry = omelettePantry();
        pantry.set(2, item("butter", 0.01, "kg", null)); // 0.01 kg == 10 g
        RecipeMatcher.Output out = RecipeMatcher.match(recipes, pantry, true, today);
        assertTrue(contains(out.getReady(), OMELETTE));
    }

    @Test
    public void quantitiesAcrossSeveralRowsAreAddedTogether() {
        List<Ingredient> pantry = omelettePantry();
        pantry.set(0, item("egg", 2, "pcs", null));
        pantry.add(item("eggs", 1, "pcs", null));
        RecipeMatcher.Output out = RecipeMatcher.match(recipes, pantry, true, today);
        assertTrue(contains(out.getReady(), OMELETTE));
    }

    @Test
    public void expiredItemsAreIgnoredWhenSettingIsOn() {
        List<Ingredient> pantry = omelettePantry();
        pantry.set(3, item("salt", 500, "g", "2026-09-01")); // expired
        assertFalse(contains(RecipeMatcher.match(recipes, pantry, true, today).getReady(), OMELETTE));
        assertTrue(contains(RecipeMatcher.match(recipes, pantry, false, today).getReady(), OMELETTE));
    }

    @Test
    public void nameNormalisation() {
        assertEquals("tomato", NameUtils.normalize("Tomatoes"));
        assertEquals("tomato", NameUtils.normalize("  TOMATO "));
        assertEquals("oil", NameUtils.normalize("Olive Oil"));
        assertEquals("berry", NameUtils.normalize("berries"));
    }
}
