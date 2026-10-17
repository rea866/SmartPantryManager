package com.richfield.smartpantry.logic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Normalises ingredient names so that trivial differences do not break matching:
 * "Tomatoes", "tomato" and " TOMATO " all become "tomato".
 */
public final class NameUtils {

    /** A few common synonyms, applied after singularising. */
    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        ALIASES.put("olive oil", "oil");
        ALIASES.put("sunflower oil", "oil");
        ALIASES.put("vegetable oil", "oil");
        ALIASES.put("cooking oil", "oil");
        ALIASES.put("canola oil", "oil");
        ALIASES.put("spaghetti", "pasta");
        ALIASES.put("penne", "pasta");
        ALIASES.put("fusilli", "pasta");
        ALIASES.put("yogurt", "yoghurt");
        ALIASES.put("chili powder", "chilli powder");
    }

    private NameUtils() { }

    /** Returns the canonical matching key for an ingredient name. */
    public static String normalize(String raw) {
        if (raw == null) return "";
        String cleaned = raw.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\s]", " ").trim();
        if (cleaned.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String word : cleaned.split("\\s+")) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(singular(word));
        }
        String key = sb.toString();
        String alias = ALIASES.get(key);
        return alias != null ? alias : key;
    }

    /** Very small English singulariser: enough for food words, not a full NLP solution. */
    private static String singular(String w) {
        int n = w.length();
        if (n <= 3) return w;
        if (w.equals("leaves")) return "leaf";
        if (w.equals("loaves")) return "loaf";
        if (w.endsWith("ies")) return w.substring(0, n - 3) + "y";   // cherries -> cherry
        if (w.endsWith("oes")) return w.substring(0, n - 2);         // tomatoes -> tomato
        if (w.endsWith("ches") || w.endsWith("shes") || w.endsWith("xes") || w.endsWith("sses")) {
            return w.substring(0, n - 2);                            // peaches -> peach
        }
        if (w.endsWith("ss") || w.endsWith("us") || w.endsWith("is")) return w; // hummus, couscous
        if (w.endsWith("s")) return w.substring(0, n - 1);           // eggs -> egg
        return w;
    }

    /** Capitalises the first letter for display. */
    public static String title(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.isEmpty()) return "";
        return t.substring(0, 1).toUpperCase(Locale.ROOT) + t.substring(1);
    }
}
