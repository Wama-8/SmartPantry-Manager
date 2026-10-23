package com.wamashudunetshisaulu.myapplication.matching;

import static org.junit.Assert.*;

import com.wamashudunetshisaulu.myapplication.database.SeedRecipes;
import com.wamashudunetshisaulu.myapplication.models.PantryItem;
import com.wamashudunetshisaulu.myapplication.models.Recipe;
import com.wamashudunetshisaulu.myapplication.models.RecipeIngredient;
import com.wamashudunetshisaulu.myapplication.utils.IngredientNormalizer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeMatcherTest {

    private static PantryItem p(String n, double q, String u) { return new PantryItem(0, n, q, u, null); }

    private static Recipe cheeseOmelette() {
        for (Recipe r : SeedRecipes.build()) if (r.getName().equals("Cheese Omelette")) return r;
        throw new AssertionError("seed recipe missing");
    }

    private static List<PantryItem> omeletteStock() {
        List<PantryItem> l = new ArrayList<>();
        l.add(p("Eggs", 3, ""));
        l.add(p("Cheese", 50, "g"));
        l.add(p("Butter", 10, "g"));
        return l;
    }

    @Test public void seedHasAtLeast15UniqueRecipesWithIngredientsAndSteps() {
        List<Recipe> all = SeedRecipes.build();
        assertTrue(all.size() >= 15 && all.size() <= 20);
        Set<String> names = new HashSet<>();
        for (Recipe r : all) {
            assertTrue("duplicate name " + r.getName(), names.add(r.getName()));
            assertFalse(r.getIngredients().isEmpty());
            assertFalse(r.getInstructions().trim().isEmpty());
            for (RecipeIngredient i : r.getIngredients()) assertTrue(i.getQuantity() > 0);
        }
    }

    @Test public void recipeAppearsWhenEverythingIsAvailable() {
        assertTrue(RecipeMatcher.canMake(cheeseOmelette(), omeletteStock()));
    }

    @Test public void missingIngredientExcludesRecipe() {
        List<PantryItem> s = omeletteStock();
        s.remove(2); // no butter
        assertFalse(RecipeMatcher.canMake(cheeseOmelette(), s));
    }

    @Test public void insufficientQuantityExcludesRecipe() {
        List<PantryItem> s = omeletteStock();
        s.set(0, p("Eggs", 2, ""));
        assertFalse(RecipeMatcher.canMake(cheeseOmelette(), s));
    }

    @Test public void exactQuantityIsEnough() {
        assertTrue(RecipeMatcher.canMake(cheeseOmelette(), omeletteStock()));
    }

    @Test public void unitConversionKgToG() {
        List<PantryItem> s = omeletteStock();
        s.set(1, p("cheese", 0.05, "kg")); // 50 g
        assertTrue(RecipeMatcher.canMake(cheeseOmelette(), s));
        s.set(1, p("cheese", 0.04, "kg")); // 40 g
        assertFalse(RecipeMatcher.canMake(cheeseOmelette(), s));
    }

    @Test public void incompatibleUnitsFailConservatively() {
        List<PantryItem> s = omeletteStock();
        s.set(1, p("cheese", 500, "ml")); // volume cannot satisfy a mass requirement
        assertFalse(RecipeMatcher.canMake(cheeseOmelette(), s));
        List<PantryItem> t = omeletteStock();
        t.set(0, p("eggs", 3, "g")); // eggs counted in grams is not the same as 3 eggs
        assertFalse(RecipeMatcher.canMake(cheeseOmelette(), t));
    }

    @Test public void volumeConversions() {
        UnitConverter.Amount cup = UnitConverter.toBase(1, "cup");
        assertEquals("volume", cup.dimension);
        assertEquals(250.0, cup.value, 1e-9);
        assertEquals(15.0, UnitConverter.toBase(1, "Tbsp").value, 1e-9);
        assertEquals(1000.0, UnitConverter.toBase(1, "L").value, 1e-9);
        assertNotEquals(UnitConverter.toBase(1, "g").dimension, UnitConverter.toBase(1, "ml").dimension);
    }

    @Test public void duplicatePantryRowsAreAddedUp() {
        List<PantryItem> s = omeletteStock();
        s.set(1, p("cheese", 30, "g"));
        s.add(p("Cheese", 20, "g"));
        assertTrue(RecipeMatcher.canMake(cheeseOmelette(), s));
    }

    @Test public void normalizationHandlesPluralsAndCase() {
        assertEquals("tomato", IngredientNormalizer.normalize(" Tomatoes "));
        assertEquals("egg", IngredientNormalizer.normalize("EGGS"));
        assertEquals("berry", IngredientNormalizer.normalize("berries"));
        assertEquals("red onion", IngredientNormalizer.normalize("Red  Onions"));
        assertEquals("potato", IngredientNormalizer.normalize("potatoes"));
        assertEquals("hummus", IngredientNormalizer.normalize("hummus"));
        assertEquals("peach", IngredientNormalizer.normalize("peaches"));
        assertEquals("", IngredientNormalizer.normalize(null));
    }

    @Test public void emptyPantryMatchesNothing() {
        assertTrue(RecipeMatcher.findMatches(SeedRecipes.build(), new ArrayList<PantryItem>()).isEmpty());
    }

    @Test public void zeroQuantityStockDoesNotCount() {
        List<PantryItem> s = omeletteStock();
        s.set(2, p("butter", 0, "g"));
        assertFalse(RecipeMatcher.canMake(cheeseOmelette(), s));
    }

    @Test public void recipeWithNoIngredientsIsNeverSuggested() {
        assertFalse(RecipeMatcher.canMake(new Recipe(1, "Empty", "x"), omeletteStock()));
    }

    @Test public void recipesAppearAndDisappearAsPantryChanges() {
        List<PantryItem> s = omeletteStock();
        List<Recipe> all = SeedRecipes.build();
        List<String> names = new ArrayList<>();
        for (Recipe r : RecipeMatcher.findMatches(all, s)) names.add(r.getName());
        assertTrue(names.contains("Cheese Omelette"));
        s.remove(1); // remove cheese
        names.clear();
        for (Recipe r : RecipeMatcher.findMatches(all, s)) names.add(r.getName());
        assertFalse(names.contains("Cheese Omelette"));
    }
}
