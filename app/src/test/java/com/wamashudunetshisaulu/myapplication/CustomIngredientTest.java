package com.wamashudunetshisaulu.myapplication;

import static org.junit.Assert.*;

import com.wamashudunetshisaulu.myapplication.database.SeedRecipes;
import com.wamashudunetshisaulu.myapplication.matching.RecipeMatcher;
import com.wamashudunetshisaulu.myapplication.models.PantryItem;
import com.wamashudunetshisaulu.myapplication.models.Recipe;
import com.wamashudunetshisaulu.myapplication.utils.IngredientCatalog;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CustomIngredientTest {

    private static PantryItem p(String n, double q, String u) { return new PantryItem(0, n, q, u, null); }

    private static List<PantryItem> omeletteStock() {
        List<PantryItem> l = new ArrayList<>();
        l.add(p("Eggs", 3, "pcs"));
        l.add(p("Cheese", 50, "g"));
        l.add(p("Butter", 10, "g"));
        return l;
    }

    private static boolean contains(List<Recipe> list, String name) {
        for (Recipe r : list) if (r.getName().equals(name)) return true;
        return false;
    }

    @Test public void customIngredientsNeverChangeSuggestions() {
        List<Recipe> all = SeedRecipes.build();
        List<PantryItem> stock = omeletteStock();
        List<Recipe> before = RecipeMatcher.findMatches(all, stock);

        stock.add(p("Dragon fruit", 2, "pcs"));       // my own ingredient, not in any recipe
        stock.add(p("Mom's secret spice", 1, "tbsp"));
        List<Recipe> after = RecipeMatcher.findMatches(all, stock);

        assertEquals(before.size(), after.size());
        assertTrue(contains(after, "Cheese Omelette"));
    }

    @Test public void suggestionsComeOnlyFromTheRecipeDatabase() {
        List<Recipe> all = SeedRecipes.build();
        List<PantryItem> stock = new ArrayList<>();
        stock.add(p("Dragon fruit", 2, "pcs"));
        assertTrue(RecipeMatcher.findMatches(all, stock).isEmpty());   // nothing is invented
        for (Recipe r : RecipeMatcher.findMatches(all, omeletteStock())) {
            assertTrue(contains(all, r.getName()));
        }
    }

    @Test public void catalogKnowsRecipeIngredientsAndNormalisesNames() {
        IngredientCatalog c = new IngredientCatalog(SeedRecipes.build());
        assertTrue(c.isKnown("Eggs"));
        assertTrue(c.isKnown("  CHEESE "));
        assertTrue(c.isKnown("tomatoes"));
        assertFalse(c.isKnown("Dragon fruit"));
        assertFalse(c.isKnown(""));
        assertTrue(c.usageCount("egg") >= 3);
    }

    @Test public void catalogNamesAreSortedUniqueAndCapitalised() {
        List<String> names = new IngredientCatalog(SeedRecipes.build()).knownNames();
        assertFalse(names.isEmpty());
        assertTrue(names.contains("Egg"));
        assertEquals(names.size(), new java.util.HashSet<>(names).size());
        for (int i = 1; i < names.size(); i++) {
            assertTrue(names.get(i - 1).compareToIgnoreCase(names.get(i)) <= 0);
        }
        assertEquals(Character.toUpperCase(names.get(0).charAt(0)), names.get(0).charAt(0));
    }

    @Test public void emptyOrNullRecipeListIsSafe() {
        assertTrue(new IngredientCatalog(null).knownNames().isEmpty());
        assertFalse(new IngredientCatalog(new ArrayList<Recipe>()).isKnown("egg"));
    }
}
