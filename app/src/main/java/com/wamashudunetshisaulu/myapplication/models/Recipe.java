package com.wamashudunetshisaulu.myapplication.models;

import java.util.ArrayList;
import java.util.List;

/** A recipe with its preparation steps and required ingredients. */
public class Recipe {
    private long id;
    private String name;
    private String instructions;
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe(long id, String name, String instructions) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getInstructions() { return instructions; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
}
