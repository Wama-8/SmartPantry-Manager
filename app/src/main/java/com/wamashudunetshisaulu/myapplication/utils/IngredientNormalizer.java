package com.wamashudunetshisaulu.myapplication.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Turns ingredient names into a comparable form: lower case, single spaces,
 * and a simple singular form ("Tomatoes " and "tomato" both become "tomato").
 */
public final class IngredientNormalizer {

    /** Words that end in "s" but are already singular. */
    private static final Set<String> KEEP_AS_IS = new HashSet<>(Arrays.asList(
            "hummus", "couscous", "asparagus", "molasses", "swiss", "citrus", "watercress"));

    private IngredientNormalizer() { }

    public static String normalize(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (s.isEmpty()) return s;
        // Singularise only the last word: "red onions" -> "red onion".
        int space = s.lastIndexOf(' ');
        String head = space >= 0 ? s.substring(0, space + 1) : "";
        return head + singular(s.substring(space + 1));
    }

    private static String singular(String w) {
        if (w.length() <= 3 || KEEP_AS_IS.contains(w)) return w;
        if (w.endsWith("ies")) return w.substring(0, w.length() - 3) + "y";          // berries -> berry
        if (w.endsWith("oes")) return w.substring(0, w.length() - 2);                // tomatoes -> tomato
        if (w.endsWith("ches") || w.endsWith("shes") || w.endsWith("xes")) {
            return w.substring(0, w.length() - 2);                                   // peaches -> peach
        }
        if (w.endsWith("ss") || w.endsWith("us")) return w;                          // cress, hummus
        if (w.endsWith("s")) return w.substring(0, w.length() - 1);                  // eggs -> egg
        return w;
    }
}
