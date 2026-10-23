package com.wamashudunetshisaulu.myapplication.database;

import com.wamashudunetshisaulu.myapplication.models.Recipe;
import com.wamashudunetshisaulu.myapplication.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/** Built-in recipes inserted once, when the database is first created. Pure Java (no Android). */
public final class SeedRecipes {

    private SeedRecipes() { }

    private static Recipe r(List<Recipe> list, String name, String steps, Object... ing) {
        Recipe recipe = new Recipe(0, name, steps);
        for (int i = 0; i < ing.length; i += 3) {
            recipe.getIngredients().add(new RecipeIngredient(
                    0, 0, (String) ing[i], ((Number) ing[i + 1]).doubleValue(), (String) ing[i + 2]));
        }
        list.add(recipe);
        return recipe;
    }

    public static List<Recipe> build() {
        List<Recipe> l = new ArrayList<>();
        r(l, "Scrambled Eggs on Toast",
          "1. Whisk the eggs with the milk and a pinch of salt.\n2. Melt the butter in a pan on medium heat.\n3. Pour in the eggs and stir gently until just set.\n4. Toast the bread and serve the eggs on top.",
          "egg", 3, "", "milk", 50, "ml", "butter", 10, "g", "bread", 2, "slice");
        r(l, "Plain Pancakes",
          "1. Mix the flour, egg and milk into a smooth batter.\n2. Heat a little butter in a pan.\n3. Pour in a ladle of batter and cook 1-2 minutes per side.\n4. Serve warm.",
          "flour", 200, "g", "egg", 2, "", "milk", 300, "ml", "butter", 20, "g");
        r(l, "Tomato Pasta",
          "1. Boil the pasta in salted water until tender.\n2. Fry the garlic and onion in oil until soft.\n3. Add the chopped tomatoes and simmer for 10 minutes.\n4. Toss the pasta through the sauce.",
          "pasta", 250, "g", "tomato", 4, "", "onion", 1, "", "garlic", 2, "clove", "olive oil", 2, "tbsp");
        r(l, "Cheese Omelette",
          "1. Beat the eggs with salt and pepper.\n2. Heat the butter in a pan.\n3. Pour in the eggs, scatter the cheese on top.\n4. Fold the omelette and serve.",
          "egg", 3, "", "cheese", 50, "g", "butter", 10, "g");
        r(l, "Egg Fried Rice",
          "1. Cook the rice and let it cool.\n2. Scramble the eggs in hot oil and set aside.\n3. Fry the rice and chopped onion for 5 minutes.\n4. Stir in the eggs and soy sauce and serve.",
          "rice", 300, "g", "egg", 2, "", "onion", 1, "", "soy sauce", 2, "tbsp", "vegetable oil", 1, "tbsp");
        r(l, "Potato Mash",
          "1. Peel and cube the potatoes.\n2. Boil for 15-20 minutes until soft.\n3. Drain and mash with the butter and milk.\n4. Season with salt.",
          "potato", 600, "g", "butter", 30, "g", "milk", 100, "ml");
        r(l, "Banana Oat Porridge",
          "1. Heat the oats and milk in a pot, stirring for 5 minutes.\n2. Slice the banana on top.\n3. Drizzle with honey and serve.",
          "oats", 80, "g", "milk", 300, "ml", "banana", 1, "", "honey", 1, "tbsp");
        r(l, "Garlic Butter Toast",
          "1. Mash the butter with the crushed garlic.\n2. Spread on the bread.\n3. Grill until golden.",
          "bread", 4, "slice", "butter", 40, "g", "garlic", 2, "clove");
        r(l, "Cheese Sandwich",
          "1. Butter the bread.\n2. Layer the cheese between the slices.\n3. Toast in a pan until golden on both sides.",
          "bread", 2, "slice", "cheese", 40, "g", "butter", 10, "g");
        r(l, "Tomato Soup",
          "1. Soften the onion and garlic in the oil.\n2. Add the tomatoes and stock and simmer 20 minutes.\n3. Blend until smooth and season.",
          "tomato", 6, "", "onion", 1, "", "garlic", 2, "clove", "vegetable stock", 500, "ml", "olive oil", 1, "tbsp");
        r(l, "Simple Pancakes with Banana",
          "1. Mash the banana and mix with the eggs and flour.\n2. Fry spoonfuls in butter until golden.\n3. Serve with honey.",
          "banana", 2, "", "egg", 2, "", "flour", 100, "g", "butter", 10, "g", "honey", 1, "tbsp");
        r(l, "Chicken and Rice",
          "1. Brown the chicken pieces in oil.\n2. Add the chopped onion and rice and stir 2 minutes.\n3. Pour in the stock, cover and simmer 18 minutes.\n4. Rest 5 minutes before serving.",
          "chicken breast", 400, "g", "rice", 200, "g", "onion", 1, "", "chicken stock", 500, "ml", "vegetable oil", 1, "tbsp");
        r(l, "Cheesy Mashed Potato Bake",
          "1. Boil and mash the potatoes with butter and milk.\n2. Spread in a dish and top with the cheese.\n3. Bake at 200 C for 20 minutes.",
          "potato", 800, "g", "butter", 30, "g", "milk", 100, "ml", "cheese", 100, "g");
        r(l, "Vegetable Stir-Fry",
          "1. Slice the carrots, onion and pepper.\n2. Stir-fry in hot oil for 6 minutes.\n3. Add the soy sauce and garlic and toss for 1 minute.",
          "carrot", 2, "", "onion", 1, "", "bell pepper", 1, "", "garlic", 2, "clove", "soy sauce", 2, "tbsp", "vegetable oil", 1, "tbsp");
        r(l, "Buttered Pasta with Cheese",
          "1. Boil the pasta until tender and drain.\n2. Toss with the butter.\n3. Stir through the grated cheese and serve.",
          "pasta", 200, "g", "butter", 20, "g", "cheese", 50, "g");
        r(l, "French Toast",
          "1. Whisk eggs and milk together.\n2. Soak the bread slices.\n3. Fry in butter until golden on both sides.",
          "bread", 4, "slice", "egg", 2, "", "milk", 100, "ml", "butter", 20, "g");
        r(l, "Baked Potatoes with Butter",
          "1. Prick the potatoes and bake at 200 C for 60 minutes.\n2. Split open and add the butter and salt.",
          "potato", 4, "", "butter", 40, "g");
        r(l, "Oat Pancakes",
          "1. Blend the oats, banana, egg and milk.\n2. Cook spoonfuls in a lightly oiled pan.\n3. Flip when bubbles appear.",
          "oats", 100, "g", "banana", 1, "", "egg", 1, "", "milk", 150, "ml", "vegetable oil", 1, "tsp");
        r(l, "Carrot and Rice Pilaf",
          "1. Soften the onion and grated carrot in butter.\n2. Add the rice and stock.\n3. Cover and simmer 15 minutes until absorbed.",
          "rice", 200, "g", "carrot", 2, "", "onion", 1, "", "butter", 15, "g", "vegetable stock", 400, "ml");
        r(l, "Garlic Chicken",
          "1. Rub the chicken with crushed garlic and oil.\n2. Pan-fry 6-7 minutes per side until cooked through.\n3. Rest and slice before serving.",
          "chicken breast", 500, "g", "garlic", 4, "clove", "olive oil", 2, "tbsp");
        return l;
    }
}
