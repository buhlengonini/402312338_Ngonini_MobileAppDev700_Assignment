package com.example.a402312338_ngonini_mobileappdev700_assignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for Recipe entity.
 * Variable names strictly follow snake_case convention.
 */
public class Recipe {
    private int recipe_id;
    private String recipe_name;
    private String recipe_category;
    private int prep_time_minutes;
    private String recipe_instructions;
    private List<RecipeIngredient> ingredient_list;
    private int available_ingredient_count;
    private int total_ingredient_count;

    public Recipe() {
        this.ingredient_list = new ArrayList<>();
    }

    public Recipe(int recipe_id, String recipe_name, String recipe_category, int prep_time_minutes, String recipe_instructions) {
        this.recipe_id = recipe_id;
        this.recipe_name = recipe_name;
        this.recipe_category = recipe_category;
        this.prep_time_minutes = prep_time_minutes;
        this.recipe_instructions = recipe_instructions;
        this.ingredient_list = new ArrayList<>();
    }

    public int getRecipeId() {
        return recipe_id;
    }

    public void setRecipeId(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getRecipeName() {
        return recipe_name;
    }

    public void setRecipeName(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public String getRecipeCategory() {
        return recipe_category;
    }

    public void setRecipeCategory(String recipe_category) {
        this.recipe_category = recipe_category;
    }

    public int getPrepTimeMinutes() {
        return prep_time_minutes;
    }

    public void setPrepTimeMinutes(int prep_time_minutes) {
        this.prep_time_minutes = prep_time_minutes;
    }

    public String getRecipeInstructions() {
        return recipe_instructions;
    }

    public void setRecipeInstructions(String recipe_instructions) {
        this.recipe_instructions = recipe_instructions;
    }

    public List<RecipeIngredient> getIngredientList() {
        return ingredient_list;
    }

    public void setIngredientList(List<RecipeIngredient> ingredient_list) {
        this.ingredient_list = ingredient_list;
    }

    public int getAvailableIngredientCount() {
        return available_ingredient_count;
    }

    public void setAvailableIngredientCount(int available_ingredient_count) {
        this.available_ingredient_count = available_ingredient_count;
    }

    public int getTotalIngredientCount() {
        return total_ingredient_count;
    }

    public void setTotalIngredientCount(int total_ingredient_count) {
        this.total_ingredient_count = total_ingredient_count;
    }

    public boolean isStrictMatch() {
        return total_ingredient_count > 0 && available_ingredient_count == total_ingredient_count;
    }

    public boolean isAlmostThereMatch() {
        return total_ingredient_count > 0 && available_ingredient_count == (total_ingredient_count - 1);
    }
}