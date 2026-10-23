package com.example.a402312338_ngonini_mobileappdev700_assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite Database Helper for local data persistence.
 * Handles Pantry Items CRUD operations, Recipe seeding (20 pre-loaded recipes),
 * and the Core Strict-Matching Algorithm.
 * All internal variable names strictly follow snake_case naming conventions.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Common Columns
    public static final String COLUMN_ID = "_id";

    // Pantry Table Columns
    public static final String COLUMN_PANTRY_NAME = "ingredient_name";
    public static final String COLUMN_PANTRY_QTY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY = "expiry_date";

    // Recipes Table Columns
    public static final String COLUMN_RECIPE_NAME = "recipe_name";
    public static final String COLUMN_RECIPE_CATEGORY = "category";
    public static final String COLUMN_RECIPE_PREP_TIME = "prep_time_minutes";
    public static final String COLUMN_RECIPE_INSTRUCTIONS = "instructions";

    // Recipe Ingredients Table Columns
    public static final String COLUMN_RI_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RI_NAME = "ingredient_name";
    public static final String COLUMN_RI_QTY = "quantity";
    public static final String COLUMN_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db_instance) {
        // Create Pantry Table
        String create_pantry_table = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
                COLUMN_PANTRY_QTY + " REAL NOT NULL, " +
                COLUMN_PANTRY_UNIT + " TEXT NOT NULL, " +
                COLUMN_PANTRY_EXPIRY + " TEXT);";

        // Create Recipes Table
        String create_recipes_table = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                COLUMN_RECIPE_CATEGORY + " TEXT, " +
                COLUMN_RECIPE_PREP_TIME + " INTEGER, " +
                COLUMN_RECIPE_INSTRUCTIONS + " TEXT NOT NULL);";

        // Create Recipe Ingredients Table
        String create_recipe_ingredients_table = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COLUMN_RI_NAME + " TEXT NOT NULL, " +
                COLUMN_RI_QTY + " REAL NOT NULL, " +
                COLUMN_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

        db_instance.execSQL(create_pantry_table);
        db_instance.execSQL(create_recipes_table);
        db_instance.execSQL(create_recipe_ingredients_table);

        // Seed 20 Default Recipes
        seedDefaultRecipes(db_instance);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db_instance, int old_version, int new_version) {
        db_instance.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db_instance.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db_instance.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db_instance);
    }

    // ==========================================
    // PANTRY CRUD OPERATIONS
    // ==========================================

    public long addPantryItem(PantryItem item_obj) {
        if (item_obj == null) return -1;
        try {
            SQLiteDatabase db_write = this.getWritableDatabase();
            ContentValues content_values = new ContentValues();
            content_values.put(COLUMN_PANTRY_NAME, item_obj.getIngredientName() != null ? item_obj.getIngredientName().trim() : "");
            content_values.put(COLUMN_PANTRY_QTY, item_obj.getItemQuantity());
            content_values.put(COLUMN_PANTRY_UNIT, item_obj.getItemUnit() != null ? item_obj.getItemUnit().trim() : "");
            content_values.put(COLUMN_PANTRY_EXPIRY, item_obj.getExpiryDate() != null ? item_obj.getExpiryDate().trim() : "");

            return db_write.insert(TABLE_PANTRY, null, content_values);
        } catch (Exception e_err) {
            e_err.printStackTrace();
            return -1;
        }
    }

    public List<PantryItem> getAllPantryItems(String search_query) {
        List<PantryItem> item_list = new ArrayList<>();
        Cursor cursor_obj = null;
        try {
            SQLiteDatabase db_read = this.getReadableDatabase();

            String select_query = "SELECT * FROM " + TABLE_PANTRY;
            String[] selection_args = null;

            if (search_query != null && !search_query.trim().isEmpty()) {
                select_query += " WHERE " + COLUMN_PANTRY_NAME + " LIKE ?";
                selection_args = new String[]{"%" + search_query.trim() + "%"};
            }

            select_query += " ORDER BY " + COLUMN_PANTRY_NAME + " ASC";

            cursor_obj = db_read.rawQuery(select_query, selection_args);

            if (cursor_obj != null && cursor_obj.moveToFirst()) {
                int idx_id = cursor_obj.getColumnIndexOrThrow(COLUMN_ID);
                int idx_name = cursor_obj.getColumnIndexOrThrow(COLUMN_PANTRY_NAME);
                int idx_qty = cursor_obj.getColumnIndexOrThrow(COLUMN_PANTRY_QTY);
                int idx_unit = cursor_obj.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT);
                int idx_expiry = cursor_obj.getColumnIndexOrThrow(COLUMN_PANTRY_EXPIRY);

                do {
                    int item_id = cursor_obj.getInt(idx_id);
                    String name_val = cursor_obj.getString(idx_name);
                    double qty_val = cursor_obj.getDouble(idx_qty);
                    String unit_val = cursor_obj.getString(idx_unit);
                    String expiry_val = cursor_obj.getString(idx_expiry);

                    PantryItem item_node = new PantryItem(item_id, name_val, qty_val, unit_val, expiry_val);
                    item_list.add(item_node);
                } while (cursor_obj.moveToNext());
            }
        } catch (Exception e_err) {
            e_err.printStackTrace();
        } finally {
            if (cursor_obj != null && !cursor_obj.isClosed()) {
                cursor_obj.close();
            }
        }
        return item_list;
    }

    public PantryItem getPantryItemById(int target_id) {
        Cursor cursor_obj = null;
        PantryItem item_obj = null;
        try {
            SQLiteDatabase db_read = this.getReadableDatabase();
            cursor_obj = db_read.query(TABLE_PANTRY, null, COLUMN_ID + "=?",
                    new String[]{String.valueOf(target_id)}, null, null, null);

            if (cursor_obj != null && cursor_obj.moveToFirst()) {
                int idx_id = cursor_obj.getColumnIndexOrThrow(COLUMN_ID);
                int idx_name = cursor_obj.getColumnIndexOrThrow(COLUMN_PANTRY_NAME);
                int idx_qty = cursor_obj.getColumnIndexOrThrow(COLUMN_PANTRY_QTY);
                int idx_unit = cursor_obj.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT);
                int idx_expiry = cursor_obj.getColumnIndexOrThrow(COLUMN_PANTRY_EXPIRY);

                item_obj = new PantryItem(
                        cursor_obj.getInt(idx_id),
                        cursor_obj.getString(idx_name),
                        cursor_obj.getDouble(idx_qty),
                        cursor_obj.getString(idx_unit),
                        cursor_obj.getString(idx_expiry)
                );
            }
        } catch (Exception e_err) {
            e_err.printStackTrace();
        } finally {
            if (cursor_obj != null && !cursor_obj.isClosed()) {
                cursor_obj.close();
            }
        }
        return item_obj;
    }

    public int updatePantryItem(PantryItem item_obj) {
        if (item_obj == null) return 0;
        try {
            SQLiteDatabase db_write = this.getWritableDatabase();
            ContentValues content_values = new ContentValues();
            content_values.put(COLUMN_PANTRY_NAME, item_obj.getIngredientName() != null ? item_obj.getIngredientName().trim() : "");
            content_values.put(COLUMN_PANTRY_QTY, item_obj.getItemQuantity());
            content_values.put(COLUMN_PANTRY_UNIT, item_obj.getItemUnit() != null ? item_obj.getItemUnit().trim() : "");
            content_values.put(COLUMN_PANTRY_EXPIRY, item_obj.getExpiryDate() != null ? item_obj.getExpiryDate().trim() : "");

            return db_write.update(TABLE_PANTRY, content_values, COLUMN_ID + "=?",
                    new String[]{String.valueOf(item_obj.getItemId())});
        } catch (Exception e_err) {
            e_err.printStackTrace();
            return 0;
        }
    }

    public int deletePantryItem(int target_id) {
        try {
            SQLiteDatabase db_write = this.getWritableDatabase();
            return db_write.delete(TABLE_PANTRY, COLUMN_ID + "=?", new String[]{String.valueOf(target_id)});
        } catch (Exception e_err) {
            e_err.printStackTrace();
            return 0;
        }
    }

    public boolean clearPantry() {
        try {
            SQLiteDatabase db_write = this.getWritableDatabase();
            db_write.delete(TABLE_PANTRY, null, null);
            return true;
        } catch (Exception e_err) {
            e_err.printStackTrace();
            return false;
        }
    }

    public boolean loadSamplePantryData() {
        SQLiteDatabase db_write = null;
        try {
            db_write = this.getWritableDatabase();
            db_write.beginTransaction();
            clearPantry();
            addPantryItem(new PantryItem("Eggs", 12, "units", "2025-12-31"));
            addPantryItem(new PantryItem("Bread", 1, "loaf", "2025-12-28"));
            addPantryItem(new PantryItem("Butter", 250, "g", "2026-01-15"));
            addPantryItem(new PantryItem("Cheese", 300, "g", "2026-01-10"));
            addPantryItem(new PantryItem("Milk", 1000, "ml", "2025-12-30"));
            addPantryItem(new PantryItem("Tomatoes", 5, "units", "2025-12-29"));
            addPantryItem(new PantryItem("Garlic", 1, "head", "2026-02-01"));
            addPantryItem(new PantryItem("Pasta", 500, "g", "2026-06-01"));
            addPantryItem(new PantryItem("Olive Oil", 500, "ml", "2026-12-01"));
            addPantryItem(new PantryItem("Salt", 1000, "g", "2027-01-01"));
            db_write.setTransactionSuccessful();
            return true;
        } catch (Exception e_err) {
            e_err.printStackTrace();
            return false;
        } finally {
            if (db_write != null && db_write.inTransaction()) {
                db_write.endTransaction();
            }
        }
    }

    // ==========================================
    // RECIPES & STRICT-MATCHING ALGORITHM
    // ==========================================

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipe_list = new ArrayList<>();
        Cursor cursor_obj = null;
        try {
            SQLiteDatabase db_read = this.getReadableDatabase();

            cursor_obj = db_read.rawQuery("SELECT * FROM " + TABLE_RECIPES + " ORDER BY " + COLUMN_RECIPE_NAME + " ASC", null);

            if (cursor_obj != null && cursor_obj.moveToFirst()) {
                int idx_id = cursor_obj.getColumnIndexOrThrow(COLUMN_ID);
                int idx_name = cursor_obj.getColumnIndexOrThrow(COLUMN_RECIPE_NAME);
                int idx_cat = cursor_obj.getColumnIndexOrThrow(COLUMN_RECIPE_CATEGORY);
                int idx_prep = cursor_obj.getColumnIndexOrThrow(COLUMN_RECIPE_PREP_TIME);
                int idx_inst = cursor_obj.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS);

                do {
                    int recipe_id = cursor_obj.getInt(idx_id);
                    String r_name = cursor_obj.getString(idx_name);
                    String r_cat = cursor_obj.getString(idx_cat);
                    int r_prep = cursor_obj.getInt(idx_prep);
                    String r_inst = cursor_obj.getString(idx_inst);

                    Recipe recipe_node = new Recipe(recipe_id, r_name, r_cat, r_prep, r_inst);
                    recipe_node.setIngredientList(getRecipeIngredients(recipe_id));
                    recipe_list.add(recipe_node);
                } while (cursor_obj.moveToNext());
            }
        } catch (Exception e_err) {
            e_err.printStackTrace();
        } finally {
            if (cursor_obj != null && !cursor_obj.isClosed()) {
                cursor_obj.close();
            }
        }
        return recipe_list;
    }

    public List<RecipeIngredient> getRecipeIngredients(int recipe_id) {
        List<RecipeIngredient> ingredient_list = new ArrayList<>();
        Cursor cursor_obj = null;
        try {
            SQLiteDatabase db_read = this.getReadableDatabase();

            cursor_obj = db_read.query(TABLE_RECIPE_INGREDIENTS, null, COLUMN_RI_RECIPE_ID + "=?",
                    new String[]{String.valueOf(recipe_id)}, null, null, null);

            if (cursor_obj != null && cursor_obj.moveToFirst()) {
                int idx_id = cursor_obj.getColumnIndexOrThrow(COLUMN_ID);
                int idx_rec_id = cursor_obj.getColumnIndexOrThrow(COLUMN_RI_RECIPE_ID);
                int idx_name = cursor_obj.getColumnIndexOrThrow(COLUMN_RI_NAME);
                int idx_qty = cursor_obj.getColumnIndexOrThrow(COLUMN_RI_QTY);
                int idx_unit = cursor_obj.getColumnIndexOrThrow(COLUMN_RI_UNIT);

                do {
                    RecipeIngredient ing_obj = new RecipeIngredient(
                            cursor_obj.getInt(idx_id),
                            cursor_obj.getInt(idx_rec_id),
                            cursor_obj.getString(idx_name),
                            cursor_obj.getDouble(idx_qty),
                            cursor_obj.getString(idx_unit)
                    );
                    ingredient_list.add(ing_obj);
                } while (cursor_obj.moveToNext());
            }
        } catch (Exception e_err) {
            e_err.printStackTrace();
        } finally {
            if (cursor_obj != null && !cursor_obj.isClosed()) {
                cursor_obj.close();
            }
        }
        return ingredient_list;
    }

    public Recipe getRecipeById(int target_recipe_id) {
        Cursor cursor_obj = null;
        Recipe recipe_obj = null;
        try {
            SQLiteDatabase db_read = this.getReadableDatabase();
            cursor_obj = db_read.query(TABLE_RECIPES, null, COLUMN_ID + "=?",
                    new String[]{String.valueOf(target_recipe_id)}, null, null, null);

            if (cursor_obj != null && cursor_obj.moveToFirst()) {
                int idx_id = cursor_obj.getColumnIndexOrThrow(COLUMN_ID);
                int idx_name = cursor_obj.getColumnIndexOrThrow(COLUMN_RECIPE_NAME);
                int idx_cat = cursor_obj.getColumnIndexOrThrow(COLUMN_RECIPE_CATEGORY);
                int idx_prep = cursor_obj.getColumnIndexOrThrow(COLUMN_RECIPE_PREP_TIME);
                int idx_inst = cursor_obj.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS);

                recipe_obj = new Recipe(
                        cursor_obj.getInt(idx_id),
                        cursor_obj.getString(idx_name),
                        cursor_obj.getString(idx_cat),
                        cursor_obj.getInt(idx_prep),
                        cursor_obj.getString(idx_inst)
                );
                recipe_obj.setIngredientList(getRecipeIngredients(target_recipe_id));
            }
        } catch (Exception e_err) {
            e_err.printStackTrace();
        } finally {
            if (cursor_obj != null && !cursor_obj.isClosed()) {
                cursor_obj.close();
            }
        }
        return recipe_obj;
    }

    /**
     * CORE STRICT-MATCHING ALGORITHM
     * Evaluates all recipes against the user's current pantry items in SQLite.
     * @param strict_only If true, returns recipes where 100% of required ingredients are present in pantry.
     *                    If false, returns recipes missing exactly 1 ingredient ("Almost There").
     */
    public List<Recipe> getSuggestedRecipes(boolean strict_only) {
        List<Recipe> all_recipes = getAllRecipes();
        List<PantryItem> pantry_items = getAllPantryItems(null);
        List<Recipe> matching_recipes = new ArrayList<>();

        for (Recipe recipe_node : all_recipes) {
            List<RecipeIngredient> required_ingredients = recipe_node.getIngredientList();
            int total_required = required_ingredients != null ? required_ingredients.size() : 0;
            int available_count = 0;

            if (required_ingredients != null) {
                for (RecipeIngredient req_ing : required_ingredients) {
                    boolean found_in_pantry = false;
                    double pantry_qty_found = 0.0;

                    for (PantryItem p_item : pantry_items) {
                        if (StringNormalizer.areIngredientsMatching(p_item.getIngredientName(), req_ing.getIngredientName())) {
                            if (StringNormalizer.isQuantitySufficient(
                                    p_item.getItemQuantity(), p_item.getItemUnit(),
                                    req_ing.getRequiredQuantity(), req_ing.getIngredientUnit())) {
                                found_in_pantry = true;
                                pantry_qty_found = p_item.getItemQuantity();
                                break;
                            }
                        }
                    }

                    req_ing.setIsInPantry(found_in_pantry);
                    req_ing.setPantryQuantity(pantry_qty_found);

                    if (found_in_pantry) {
                        available_count++;
                    }
                }
            }

            recipe_node.setTotalIngredientCount(total_required);
            recipe_node.setAvailableIngredientCount(available_count);

            if (strict_only) {
                // Strict-matching rule: EVERY single ingredient must be present in sufficient quantity
                if (total_required > 0 && available_count == total_required) {
                    matching_recipes.add(recipe_node);
                }
            } else {
                // "Almost There" rule: Missing exactly 1 ingredient
                if (total_required > 0 && available_count == (total_required - 1)) {
                    matching_recipes.add(recipe_node);
                }
            }
        }

        return matching_recipes;
    }

    /**
     * Deducts recipe ingredient quantities from pantry items when user cooks a recipe.
     */
    public boolean deductRecipeIngredientsFromPantry(int recipe_id) {
        Recipe recipe_obj = getRecipeById(recipe_id);
        if (recipe_obj == null || recipe_obj.getIngredientList() == null) return false;

        SQLiteDatabase db_write = null;
        try {
            db_write = this.getWritableDatabase();
            db_write.beginTransaction();

            List<PantryItem> pantry_list = getAllPantryItems(null);

            for (RecipeIngredient req_ing : recipe_obj.getIngredientList()) {
                for (PantryItem p_item : pantry_list) {
                    if (StringNormalizer.areIngredientsMatching(p_item.getIngredientName(), req_ing.getIngredientName())) {
                        double remaining_qty = p_item.getItemQuantity() - req_ing.getRequiredQuantity();
                        if (remaining_qty <= 0.001) {
                            deletePantryItem(p_item.getItemId());
                        } else {
                            p_item.setItemQuantity(remaining_qty);
                            updatePantryItem(p_item);
                        }
                        break;
                    }
                }
            }
            db_write.setTransactionSuccessful();
            return true;
        } catch (Exception e_err) {
            e_err.printStackTrace();
            return false;
        } finally {
            if (db_write != null && db_write.inTransaction()) {
                db_write.endTransaction();
            }
        }
    }

    public boolean resetRecipeDatabase() {
        SQLiteDatabase db_write = null;
        try {
            db_write = this.getWritableDatabase();
            db_write.beginTransaction();
            db_write.delete(TABLE_RECIPE_INGREDIENTS, null, null);
            db_write.delete(TABLE_RECIPES, null, null);
            seedDefaultRecipes(db_write);
            db_write.setTransactionSuccessful();
            return true;
        } catch (Exception e_err) {
            e_err.printStackTrace();
            return false;
        } finally {
            if (db_write != null && db_write.inTransaction()) {
                db_write.endTransaction();
            }
        }
    }

    // ==========================================
    // PRE-LOADED 20 RECIPES SEEDING
    // ==========================================

    private void seedDefaultRecipes(SQLiteDatabase db_inst) {
        if (db_inst == null) return;
        boolean is_nested_tx = db_inst.inTransaction();
        if (!is_nested_tx) {
            db_inst.beginTransaction();
        }
        try {
            insertRecipeWithIngredients(db_inst, "Tomato Pasta", "Italian", 20,
                    "1. Boil pasta in salted water until al dente.\n2. Sauté minced garlic in olive oil.\n3. Add diced tomatoes and simmer for 10 minutes.\n4. Toss pasta in sauce and serve hot.",
                    new String[][]{{"Pasta", "250", "g"}, {"Tomatoes", "3", "units"}, {"Garlic", "2", "cloves"}, {"Olive Oil", "15", "ml"}, {"Salt", "5", "g"}});

            insertRecipeWithIngredients(db_inst, "Garlic Fried Rice", "Asian", 15,
                    "1. Heat vegetable oil in a skillet.\n2. Fry minced garlic until golden brown.\n3. Add cooked rice and soy sauce, stirring on high heat.\n4. Push rice aside, scramble eggs, and mix together.",
                    new String[][]{{"Rice", "200", "g"}, {"Garlic", "3", "cloves"}, {"Eggs", "2", "units"}, {"Soy Sauce", "15", "ml"}, {"Vegetable Oil", "15", "ml"}});

            insertRecipeWithIngredients(db_inst, "Classic Omelette", "Breakfast", 10,
                    "1. Whisk eggs with salt and pepper in a bowl.\n2. Melt butter in a non-stick pan over medium heat.\n3. Pour egg mixture and cook until set.\n4. Sprinkle shredded cheese, fold in half, and serve.",
                    new String[][]{{"Eggs", "3", "units"}, {"Butter", "15", "g"}, {"Salt", "2", "g"}, {"Black Pepper", "1", "g"}, {"Cheese", "50", "g"}});

            insertRecipeWithIngredients(db_inst, "Grilled Cheese Sandwich", "Snacks", 10,
                    "1. Butter one side of each bread slice.\n2. Place cheese between unbuttered sides of bread.\n3. Grill sandwich on skillet until golden brown on both sides and cheese is melted.",
                    new String[][]{{"Bread", "2", "slices"}, {"Cheese", "60", "g"}, {"Butter", "15", "g"}});

            insertRecipeWithIngredients(db_inst, "French Toast", "Breakfast", 15,
                    "1. Whisk eggs, milk, and cinnamon in a shallow bowl.\n2. Dip bread slices into mixture until coated.\n3. Melt butter on skillet and fry bread until golden on both sides.",
                    new String[][]{{"Bread", "4", "slices"}, {"Eggs", "2", "units"}, {"Milk", "100", "ml"}, {"Cinnamon", "2", "g"}, {"Butter", "20", "g"}});

            insertRecipeWithIngredients(db_inst, "Fluffy Pancakes", "Breakfast", 20,
                    "1. Mix flour, sugar, and baking powder.\n2. Whisk milk, egg, and melted butter in another bowl.\n3. Combine wet and dry ingredients.\n4. Pour batter onto hot skillet and cook until golden brown.",
                    new String[][]{{"Flour", "200", "g"}, {"Milk", "250", "ml"}, {"Eggs", "1", "units"}, {"Sugar", "30", "g"}, {"Butter", "30", "g"}});

            insertRecipeWithIngredients(db_inst, "Mashed Potatoes", "Side Dish", 25,
                    "1. Peel and boil potatoes in salted water until tender.\n2. Drain water and mash potatoes.\n3. Stir in butter and warm milk until smooth.\n4. Season with salt and pepper.",
                    new String[][]{{"Potatoes", "500", "g"}, {"Butter", "40", "g"}, {"Milk", "60", "ml"}, {"Salt", "5", "g"}, {"Black Pepper", "2", "g"}});

            insertRecipeWithIngredients(db_inst, "Vegetable Soup", "Soup", 35,
                    "1. Dice carrots, potatoes, onions, and celery.\n2. Sauté onions and garlic in olive oil.\n3. Add remaining vegetables, water, and salt.\n4. Simmer for 25 minutes until tender.",
                    new String[][]{{"Carrots", "2", "units"}, {"Potatoes", "2", "units"}, {"Onions", "1", "units"}, {"Garlic", "2", "cloves"}, {"Salt", "5", "g"}});

            insertRecipeWithIngredients(db_inst, "Chicken Stir Fry", "Dinner", 25,
                    "1. Slice chicken breast into thin strips.\n2. Stir fry chicken in oil until browned.\n3. Add broccoli florets and sliced bell pepper.\n4. Pour soy sauce and cook for 5 minutes.",
                    new String[][]{{"Chicken Breast", "300", "g"}, {"Soy Sauce", "30", "ml"}, {"Broccoli", "150", "g"}, {"Garlic", "2", "cloves"}, {"Bell Pepper", "1", "units"}});

            insertRecipeWithIngredients(db_inst, "Creamy Tomato Soup", "Soup", 25,
                    "1. Sauté chopped onions and garlic in butter.\n2. Add canned or fresh tomatoes and vegetable stock.\n3. Simmer for 15 minutes and blend until smooth.\n4. Stir in cream and season to taste.",
                    new String[][]{{"Tomatoes", "4", "units"}, {"Onions", "1", "units"}, {"Garlic", "2", "cloves"}, {"Butter", "20", "g"}, {"Milk", "100", "ml"}});

            insertRecipeWithIngredients(db_inst, "Garlic Bread", "Appetizer", 15,
                    "1. Mix softened butter with minced garlic and parsley.\n2. Spread butter mixture generously on sliced bread.\n3. Bake in preheated oven at 180°C for 10 minutes until crispy.",
                    new String[][]{{"Bread", "1", "loaf"}, {"Garlic", "4", "cloves"}, {"Butter", "50", "g"}});

            insertRecipeWithIngredients(db_inst, "Fresh Guacamole", "Dip", 10,
                    "1. Mash ripe avocados in a bowl.\n2. Finely chop tomatoes and onions.\n3. Mix ingredients with lime juice and salt.\n4. Serve immediately with chips.",
                    new String[][]{{"Avocado", "2", "units"}, {"Tomatoes", "1", "units"}, {"Onions", "0.5", "units"}, {"Salt", "3", "g"}});

            insertRecipeWithIngredients(db_inst, "Banana Oatmeal", "Breakfast", 10,
                    "1. Boil milk or water in a small pot.\n2. Stir in oats and cook on medium heat for 5 minutes.\n3. Slice banana into oatmeal and drizzle honey.",
                    new String[][]{{"Oats", "80", "g"}, {"Milk", "250", "ml"}, {"Bananas", "1", "units"}, {"Honey", "15", "ml"}});

            insertRecipeWithIngredients(db_inst, "Cheesy Macaroni", "Comfort Food", 20,
                    "1. Boil pasta in water and drain.\n2. Melt butter, add milk and cheese to create cheese sauce.\n3. Stir cooked pasta into sauce until fully coated.",
                    new String[][]{{"Pasta", "200", "g"}, {"Cheese", "100", "g"}, {"Butter", "20", "g"}, {"Milk", "100", "ml"}});

            insertRecipeWithIngredients(db_inst, "Banana Smoothie", "Beverage", 5,
                    "1. Add ripe bananas, milk, and honey to a blender.\n2. Blend on high speed for 1 minute until creamy and smooth.\n3. Pour into glasses and serve cold.",
                    new String[][]{{"Bananas", "2", "units"}, {"Milk", "250", "ml"}, {"Honey", "15", "ml"}});

            insertRecipeWithIngredients(db_inst, "Scrambled Eggs with Cheese", "Breakfast", 8,
                    "1. Whisk eggs in a bowl with a pinch of salt.\n2. Melt butter in a non-stick skillet over low heat.\n3. Pour eggs and gently fold until soft curds form.\n4. Top with shredded cheese and serve.",
                    new String[][]{{"Eggs", "3", "units"}, {"Cheese", "40", "g"}, {"Butter", "10", "g"}, {"Salt", "2", "g"}});

            insertRecipeWithIngredients(db_inst, "Potato Salad", "Side Dish", 30,
                    "1. Boil diced potatoes until fork tender; drain and cool.\n2. Hard boil eggs, peel, and chop finely.\n3. Mix mayonnaise, chopped onions, potatoes, and eggs.\n4. Season with salt and pepper.",
                    new String[][]{{"Potatoes", "400", "g"}, {"Eggs", "2", "units"}, {"Onions", "0.5", "units"}, {"Salt", "3", "g"}});

            insertRecipeWithIngredients(db_inst, "Cucumber Salad", "Salad", 10,
                    "1. Thinly slice fresh cucumbers.\n2. Whisk olive oil, salt, and black pepper.\n3. Toss cucumbers with dressing and serve chilled.",
                    new String[][]{{"Cucumber", "2", "units"}, {"Olive Oil", "20", "ml"}, {"Salt", "2", "g"}, {"Black Pepper", "1", "g"}});

            insertRecipeWithIngredients(db_inst, "Fruit Salad", "Dessert", 10,
                    "1. Chop apples and bananas into bite-sized pieces.\n2. Combine chopped fruits in a bowl.\n3. Drizzle with honey and toss gently.",
                    new String[][]{{"Apples", "2", "units"}, {"Bananas", "2", "units"}, {"Honey", "20", "ml"}});

            insertRecipeWithIngredients(db_inst, "Chicken Salad", "Salad", 20,
                    "1. Shred cooked chicken breast.\n2. Combine shredded chicken with diced celery.\n3. Mix with mayonnaise, salt, and black pepper.",
                    new String[][]{{"Chicken Breast", "250", "g"}, {"Celery", "2", "stalks"}, {"Salt", "2", "g"}, {"Black Pepper", "1", "g"}});

            if (!is_nested_tx) {
                db_inst.setTransactionSuccessful();
            }
        } catch (Exception e_err) {
            e_err.printStackTrace();
        } finally {
            if (!is_nested_tx && db_inst.inTransaction()) {
                db_inst.endTransaction();
            }
        }
    }

    private void insertRecipeWithIngredients(SQLiteDatabase db_inst, String name, String category, int prep_time, String instructions, String[][] ingredients) {
        ContentValues cv_recipe = new ContentValues();
        cv_recipe.put(COLUMN_RECIPE_NAME, name);
        cv_recipe.put(COLUMN_RECIPE_CATEGORY, category);
        cv_recipe.put(COLUMN_RECIPE_PREP_TIME, prep_time);
        cv_recipe.put(COLUMN_RECIPE_INSTRUCTIONS, instructions);

        long recipe_id = db_inst.insert(TABLE_RECIPES, null, cv_recipe);

        for (String[] ing : ingredients) {
            ContentValues cv_ing = new ContentValues();
            cv_ing.put(COLUMN_RI_RECIPE_ID, recipe_id);
            cv_ing.put(COLUMN_RI_NAME, ing[0]);
            cv_ing.put(COLUMN_RI_QTY, Double.parseDouble(ing[1]));
            cv_ing.put(COLUMN_RI_UNIT, ing[2]);
            db_inst.insert(TABLE_RECIPE_INGREDIENTS, null, cv_ing);
        }
    }
}