package com.example.a402312338_ngonini_mobileappdev700_assignment;

/**
 * Model class for an ingredient required by a recipe.
 * Variable names strictly follow snake_case convention.
 */
public class RecipeIngredient {
    private int ingredient_id;
    private int recipe_id;
    private String ingredient_name;
    private double required_quantity;
    private String ingredient_unit;
    private boolean is_in_pantry;
    private double pantry_quantity;

    public RecipeIngredient() {
    }

    public RecipeIngredient(int ingredient_id, int recipe_id, String ingredient_name, double required_quantity, String ingredient_unit) {
        this.ingredient_id = ingredient_id;
        this.recipe_id = recipe_id;
        this.ingredient_name = ingredient_name;
        this.required_quantity = required_quantity;
        this.ingredient_unit = ingredient_unit;
    }

    public RecipeIngredient(String ingredient_name, double required_quantity, String ingredient_unit) {
        this.ingredient_name = ingredient_name;
        this.required_quantity = required_quantity;
        this.ingredient_unit = ingredient_unit;
    }

    public int getIngredientId() {
        return ingredient_id;
    }

    public void setIngredientId(int ingredient_id) {
        this.ingredient_id = ingredient_id;
    }

    public int getRecipeId() {
        return recipe_id;
    }

    public void setRecipeId(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getIngredientName() {
        return ingredient_name;
    }

    public void setIngredientName(String ingredient_name) {
        this.ingredient_name = ingredient_name;
    }

    public double getRequiredQuantity() {
        return required_quantity;
    }

    public void setRequiredQuantity(double required_quantity) {
        this.required_quantity = required_quantity;
    }

    public String getIngredientUnit() {
        return ingredient_unit;
    }

    public void setIngredientUnit(String ingredient_unit) {
        this.ingredient_unit = ingredient_unit;
    }

    public boolean isInPantry() {
        return is_in_pantry;
    }

    public void setIsInPantry(boolean is_in_pantry) {
        this.is_in_pantry = is_in_pantry;
    }

    public double getPantryQuantity() {
        return pantry_quantity;
    }

    public void setPantryQuantity(double pantry_quantity) {
        this.pantry_quantity = pantry_quantity;
    }
}