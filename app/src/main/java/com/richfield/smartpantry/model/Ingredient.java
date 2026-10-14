package com.richfield.smartpantry.model;

/**
 * An item the user currently has in their pantry (one row in the pantry_items table).
 */
public class Ingredient {

    private long id;              // database primary key (-1 when not yet saved)
    private String name;          // e.g. "Tomatoes"
    private double quantity;      // amount in the given unit
    private String unit;          // e.g. "g", "ml", "pcs"
    private String expiryDate;    // ISO date (yyyy-MM-dd) or null when there is none

    public Ingredient(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
