package com.richfield.smartpantry.logic;

import java.util.Locale;

/**
 * Handles unit names and conversions so that, for example, 0.5 kg of flour can satisfy a
 * recipe that needs 250 g.
 *
 * Mass and volume units are converted to a shared base where 1 g is treated as 1 ml
 * (a water-density approximation that is good enough for everyday cooking). Countable
 * items ("pcs") are only compared with other countable items.
 */
public final class UnitConverter {

    /** Units offered in the unit drop-down on the Add/Edit screen. */
    public static final String[] UNITS = {"pcs", "g", "kg", "ml", "l", "tsp", "tbsp", "cup", "oz", "lb"};

    public enum Dimension { COUNT, MASS, VOLUME, NONE }

    private UnitConverter() { }

    /** Maps spelling variants ("Grams", "pieces", "tablespoons") to one canonical unit. */
    public static String canonical(String unit) {
        if (unit == null) return "";
        String u = unit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "pc": case "pcs": case "piece": case "pieces": case "whole": case "unit": case "units":
                return "pcs";
            case "g": case "gram": case "grams":
                return "g";
            case "kg": case "kgs": case "kilogram": case "kilograms":
                return "kg";
            case "ml": case "millilitre": case "millilitres": case "milliliter": case "milliliters":
                return "ml";
            case "l": case "litre": case "litres": case "liter": case "liters":
                return "l";
            case "tsp": case "teaspoon": case "teaspoons":
                return "tsp";
            case "tbsp": case "tablespoon": case "tablespoons":
                return "tbsp";
            case "cup": case "cups":
                return "cup";
            case "oz": case "ounce": case "ounces":
                return "oz";
            case "lb": case "lbs": case "pound": case "pounds":
                return "lb";
            default:
                return u;
        }
    }

    public static Dimension dimension(String unit) {
        switch (canonical(unit)) {
            case "pcs":
                return Dimension.COUNT;
            case "g": case "kg": case "oz": case "lb":
                return Dimension.MASS;
            case "ml": case "l": case "tsp": case "tbsp": case "cup":
                return Dimension.VOLUME;
            default:
                return Dimension.NONE;
        }
    }

    /** True when two quantities in these units can be meaningfully compared. */
    public static boolean canCompare(String unitA, String unitB) {
        Dimension a = dimension(unitA);
        Dimension b = dimension(unitB);
        if (a == Dimension.NONE || b == Dimension.NONE) return false;
        if (a == Dimension.COUNT || b == Dimension.COUNT) return a == b;
        return true; // mass <-> volume allowed via the 1 g ~ 1 ml approximation
    }

    /** Converts a quantity to the base unit (g / ml / pcs). */
    public static double toBase(double quantity, String unit) {
        switch (canonical(unit)) {
            case "kg":   return quantity * 1000.0;
            case "oz":   return quantity * 28.3495;
            case "lb":   return quantity * 453.592;
            case "l":    return quantity * 1000.0;
            case "tsp":  return quantity * 5.0;
            case "tbsp": return quantity * 15.0;
            case "cup":  return quantity * 240.0;
            default:     return quantity; // g, ml, pcs
        }
    }

    /** Formats 2.0 as "2" and 0.25 as "0.25". */
    public static String formatQuantity(double q) {
        if (Math.abs(q - Math.rint(q)) < 0.0001) {
            return String.valueOf((long) Math.rint(q));
        }
        return String.format(Locale.US, "%.2f", q).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}
