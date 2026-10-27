package com.wamashudunetshisaulu.myapplication.utils;

import com.wamashudunetshisaulu.myapplication.models.Recipe;
import com.wamashudunetshisaulu.myapplication.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The ingredients used by the recipe database. The user may add ANY ingredient to the pantry
 * ("custom" ingredients); this class tells which names the recipes actually use, so the app can
 * offer name suggestions and explain that a custom ingredient does not change the suggestions.
 * Names are compared after normalisation (case and simple plurals), exactly like the matcher.
 */
public final class IngredientCatalog {

    private final Map<String, Integer> usage = new HashMap<>();      // normalised name -> number of recipes
    private final Map<String, String> display = new TreeMap<>();     // normalised name -> name to show

    public IngredientCatalog(List<Recipe> recipes) {
        if (recipes == null) return;
        for (Recipe r : recipes) {
            Set<String> seenInThisRecipe = new HashSet<>();
            for (RecipeIngredient i : r.getIngredients()) {
                String key = IngredientNormalizer.normalize(i.getName());
                if (key.isEmpty()) continue;
                if (!display.containsKey(key)) display.put(key, TextFormat.capitalize(i.getName()));
                if (seenInThisRecipe.add(key)) {
                    Integer old = usage.get(key);
                    usage.put(key, old == null ? 1 : old + 1);
                }
            }
        }
    }

    /** How many recipes use this ingredient (0 = a custom ingredient no recipe needs). */
    public int usageCount(String name) {
        Integer n = usage.get(IngredientNormalizer.normalize(name));
        return n == null ? 0 : n;
    }

    public boolean isKnown(String name) {
        return usageCount(name) > 0;
    }

    /** Alphabetical names for the auto-complete list. */
    public List<String> knownNames() {
        List<String> names = new ArrayList<>(display.values());
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        return names;
    }
}
