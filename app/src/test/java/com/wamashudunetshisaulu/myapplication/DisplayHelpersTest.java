package com.wamashudunetshisaulu.myapplication;

import static org.junit.Assert.*;

import com.wamashudunetshisaulu.myapplication.database.SeedRecipes;
import com.wamashudunetshisaulu.myapplication.models.Recipe;
import com.wamashudunetshisaulu.myapplication.utils.StepParser;
import com.wamashudunetshisaulu.myapplication.utils.TextFormat;

import org.junit.Test;

import java.util.List;

public class DisplayHelpersTest {

    @Test public void capitalizeAndInitial() {
        assertEquals("Cheese", TextFormat.capitalize(" cheese "));
        assertEquals("Red onion", TextFormat.capitalize("red onion"));
        assertEquals("", TextFormat.capitalize(null));
        assertEquals("C", TextFormat.initial("cheese"));
        assertEquals("?", TextFormat.initial("  "));
    }

    @Test public void pluralCount() {
        assertEquals("1 ingredient", TextFormat.count(1, "ingredient", "ingredients"));
        assertEquals("0 ingredients", TextFormat.count(0, "ingredient", "ingredients"));
        assertEquals("5 ingredients", TextFormat.count(5, "ingredient", "ingredients"));
    }

    @Test public void stepsAreSplitAndNumbersRemoved() {
        List<String> s = StepParser.split("1. Whisk the eggs.\n2. Heat the pan.\r\n3) Serve.\n\n");
        assertEquals(3, s.size());
        assertEquals("Whisk the eggs.", s.get(0));
        assertEquals("Serve.", s.get(2));
        assertTrue(StepParser.split(null).isEmpty());
        assertEquals("No numbering here", StepParser.split("No numbering here").get(0));
    }

    @Test public void everySeedRecipeHasAtLeastTwoSteps() {
        for (Recipe r : SeedRecipes.build()) {
            assertTrue(r.getName(), StepParser.split(r.getInstructions()).size() >= 2);
        }
    }
}
