package com.wamashudunetshisaulu.myapplication.activities;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wamashudunetshisaulu.myapplication.R;

/**
 * Shared bottom navigation for the three main screens (Pantry, Recipes, Settings).
 * Each tab is an Activity opened with an explicit Intent. Pantry is the root screen, so going
 * back to it clears the screens above it instead of stacking new copies.
 */
public final class BottomNav {

    private BottomNav() { }

    public static void setup(final Activity activity, BottomNavigationView nav, final int selectedId) {
        nav.setSelectedItemId(selectedId);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) return true;
            if (id == R.id.nav_pantry) {
                openPantry(activity);
            } else {
                Class<?> target = id == R.id.nav_recipes ? SuggestedRecipesActivity.class : SettingsActivity.class;
                Intent i = new Intent(activity, target);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(i);
                // Recipes <-> Settings: replace the current screen so Back always returns to Pantry.
                if (selectedId != R.id.nav_pantry) activity.finish();
            }
            return true;
        });
    }

    /** Returns to the Pantry screen, closing any screens opened above it. */
    public static void openPantry(Activity activity) {
        Intent i = new Intent(activity, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(i);
    }
}
