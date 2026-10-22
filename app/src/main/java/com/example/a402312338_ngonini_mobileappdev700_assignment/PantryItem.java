package com.example.a402312338_ngonini_mobileappdev700_assignment;

/**
 * Represents a single pantry ingredient item stored locally in SQLite database.
 * Variable names strictly follow snake_case convention.
 */
public class PantryItem {
    private int item_id;
    private String ingredient_name;
    private double item_quantity;
    private String item_unit;
    private String expiry_date;

    public PantryItem() {
    }

    public PantryItem(int item_id, String ingredient_name, double item_quantity, String item_unit, String expiry_date) {
        this.item_id = item_id;
        this.ingredient_name = ingredient_name;
        this.item_quantity = item_quantity;
        this.item_unit = item_unit;
        this.expiry_date = expiry_date;
    }

    public PantryItem(String ingredient_name, double item_quantity, String item_unit, String expiry_date) {
        this.ingredient_name = ingredient_name;
        this.item_quantity = item_quantity;
        this.item_unit = item_unit;
        this.expiry_date = expiry_date;
    }

    public int getItemId() {
        return item_id;
    }

    public void setItemId(int item_id) {
        this.item_id = item_id;
    }

    public String getIngredientName() {
        return ingredient_name;
    }

    public void setIngredientName(String ingredient_name) {
        this.ingredient_name = ingredient_name;
    }

    public double getItemQuantity() {
        return item_quantity;
    }

    public void setItemQuantity(double item_quantity) {
        this.item_quantity = item_quantity;
    }

    public String getItemUnit() {
        return item_unit;
    }

    public void setItemUnit(String item_unit) {
        this.item_unit = item_unit;
    }

    public String getExpiryDate() {
        return expiry_date;
    }

    public void setExpiryDate(String expiry_date) {
        this.expiry_date = expiry_date;
    }
}