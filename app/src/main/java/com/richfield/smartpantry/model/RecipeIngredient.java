package com.richfield.smartpantry.model;

/**
 * One ingredient line required by a recipe.
 * A quantity of 0 means "to taste" (e.g. salt): the ingredient only has to be present in the pantry.
 */
public class RecipeIngredient {

    private final String name;
    private final double quantity;
    private final String unit;

    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
}
