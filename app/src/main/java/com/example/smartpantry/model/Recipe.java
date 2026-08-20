package com.example.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a Recipe.
 * A recipe has a header (name, description, prep time) and a list of required ingredients.
 */
public class Recipe {
    private int id;
    private String name;
    private String description;
    private String prepTime;
    private String steps;
    private List<RecipeIngredient> ingredients;

    public Recipe() {
        this.ingredients = new ArrayList<>();
    }

    public Recipe(int id, String name, String description, String prepTime, String steps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.prepTime = prepTime;
        this.steps = steps;
        this.ingredients = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPrepTime() { return prepTime; }
    public void setPrepTime(String prepTime) { this.prepTime = prepTime; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }

    public void addIngredient(RecipeIngredient ingredient) {
        this.ingredients.add(ingredient);
    }
}
