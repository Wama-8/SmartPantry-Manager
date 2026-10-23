package com.wamashudunetshisaulu.myapplication.matching;

import com.wamashudunetshisaulu.myapplication.models.PantryItem;
import com.wamashudunetshisaulu.myapplication.models.Recipe;
import com.wamashudunetshisaulu.myapplication.models.RecipeIngredient;
import com.wamashudunetshisaulu.myapplication.utils.IngredientNormalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strict matcher: a recipe qualifies ONLY if every ingredient is in the pantry
 * in a sufficient quantity. No partial matches. Anything we cannot compare
 * safely (e.g. grams vs millilitres) counts as "not available".
 */
public final class RecipeMatcher {

    /** Tolerance for floating point rounding after unit conversion. */
    private static final double EPSILON = 1e-9;

    private RecipeMatcher() { }

    /** Returns only the recipes that can be fully prepared from the pantry. */
    public static List<Recipe> findMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : recipes) {
            if (canMake(r, pantry)) result.add(r);
        }
        return result;
    }

    public static boolean canMake(Recipe recipe, List<PantryItem> pantry) {
        if (recipe == null || recipe.getIngredients().isEmpty()) return false;

        // Stock per "normalised name|dimension"; several pantry rows of the same item are added up.
        Map<String, Double> stock = new HashMap<>();
        for (PantryItem p : pantry) {
            if (p.getQuantity() <= 0) continue;
            String name = IngredientNormalizer.normalize(p.getName());
            if (name.isEmpty()) continue;
            UnitConverter.Amount a = UnitConverter.toBase(p.getQuantity(), p.getUnit());
            String key = name + "|" + a.dimension;
            stock.merge(key, a.value, Double::sum);
        }

        for (RecipeIngredient need : recipe.getIngredients()) {
            String name = IngredientNormalizer.normalize(need.getName());
            UnitConverter.Amount a = UnitConverter.toBase(need.getQuantity(), need.getUnit());
            String key = name + "|" + a.dimension;
            Double have = stock.get(key);
            if (have == null || have + EPSILON < a.value) return false; // missing, incompatible unit or too little
            stock.put(key, have - a.value); // a repeated ingredient line uses up stock
        }
        return true;
    }
}
