package com.example.a402312338_ngonini_mobileappdev700_assignment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for Adding new ingredients or Editing existing pantry ingredients.
 * Performs strict form input validation.
 * Variable names strictly follow snake_case convention.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    private DatabaseHelper db_helper;

    private TextView tv_form_title;
    private TextInputLayout til_ingredient_name;
    private AutoCompleteTextView actv_ingredient_name;

    private TextInputLayout til_quantity;
    private TextInputEditText et_quantity;

    private TextInputLayout til_unit;
    private AutoCompleteTextView actv_unit;

    private TextInputLayout til_expiry_date;
    private TextInputEditText et_expiry_date;

    private MaterialButton btn_cancel;
    private MaterialButton btn_save_ingredient;

    private int editing_item_id = -1;
    private PantryItem existing_pantry_item;

    private static final String[] SUGGESTED_INGREDIENTS = {
            "Eggs", "Milk", "Butter", "Cheese", "Bread", "Tomatoes", "Garlic", "Onions",
            "Pasta", "Flour", "Rice", "Sugar", "Chicken Breast", "Potatoes", "Carrots",
            "Olive Oil", "Salt", "Black Pepper", "Soy Sauce", "Broccoli", "Bell Pepper",
            "Oats", "Honey", "Bananas", "Apples", "Avocado", "Cucumber", "Celery", "Mayonnaise"
    };

    private static final String[] MEASUREMENT_UNITS = {
            "units", "g", "kg", "ml", "l", "tbsp", "tsp", "cups", "loaf", "slices", "head", "cloves", "stalks"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        db_helper = new DatabaseHelper(this);

        tv_form_title = findViewById(R.id.tv_form_title);
        til_ingredient_name = findViewById(R.id.til_ingredient_name);
        actv_ingredient_name = findViewById(R.id.actv_ingredient_name);

        til_quantity = findViewById(R.id.til_quantity);
        et_quantity = findViewById(R.id.et_quantity);

        til_unit = findViewById(R.id.til_unit);
        actv_unit = findViewById(R.id.actv_unit);

        til_expiry_date = findViewById(R.id.til_expiry_date);
        et_expiry_date = findViewById(R.id.et_expiry_date);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save_ingredient = findViewById(R.id.btn_save_ingredient);

        // Setup AutoComplete suggestions
        ArrayAdapter<String> ing_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, SUGGESTED_INGREDIENTS);
        actv_ingredient_name.setAdapter(ing_adapter);

        // Setup Units dropdown
        ArrayAdapter<String> unit_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, MEASUREMENT_UNITS);
        actv_unit.setAdapter(unit_adapter);

        // Setup DatePicker Dialog
        et_expiry_date.setOnClickListener(v_click -> showDatePickerDialog());

        // Check if editing existing item
        if (getIntent().hasExtra("EXTRA_ITEM_ID")) {
            editing_item_id = getIntent().getIntExtra("EXTRA_ITEM_ID", -1);
            if (editing_item_id != -1) {
                existing_pantry_item = db_helper.getPantryItemById(editing_item_id);
                if (existing_pantry_item != null) {
                    tv_form_title.setText(R.string.edit_ingredient);
                    actv_ingredient_name.setText(existing_pantry_item.getIngredientName());

                    double qty_val = existing_pantry_item.getItemQuantity();
                    String qty_str = (qty_val == (long) qty_val) ? String.format(Locale.ROOT, "%d", (long) qty_val) : String.format(Locale.ROOT, "%.2f", qty_val);
                    et_quantity.setText(qty_str);

                    actv_unit.setText(existing_pantry_item.getItemUnit(), false);
                    et_expiry_date.setText(existing_pantry_item.getExpiryDate());
                }
            }
        }

        btn_cancel.setOnClickListener(v_click -> finish());
        btn_save_ingredient.setOnClickListener(v_click -> saveIngredient());
    }

    private void showDatePickerDialog() {
        Calendar calendar_obj = Calendar.getInstance();
        String current_date_str = et_expiry_date.getText() != null ? et_expiry_date.getText().toString().trim() : "";
        if (!current_date_str.isEmpty()) {
            try {
                String[] parts = current_date_str.split("-");
                if (parts.length == 3) {
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]) - 1;
                    int day = Integer.parseInt(parts[2]);
                    calendar_obj.set(year, month, day);
                }
            } catch (Exception ignored) {}
        }

        int year_val = calendar_obj.get(Calendar.YEAR);
        int month_val = calendar_obj.get(Calendar.MONTH);
        int day_val = calendar_obj.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog date_picker = new DatePickerDialog(this, (view_picker, year_selected, month_selected, day_selected) -> {
            String formatted_date = String.format(Locale.ROOT, "%04d-%02d-%02d", year_selected, month_selected + 1, day_selected);
            et_expiry_date.setText(formatted_date);
        }, year_val, month_val, day_val);

        date_picker.show();
    }

    private void saveIngredient() {
        // Reset errors
        til_ingredient_name.setError(null);
        til_quantity.setError(null);
        til_unit.setError(null);

        String name_input = actv_ingredient_name.getText() != null ? actv_ingredient_name.getText().toString().trim() : "";
        String qty_input = et_quantity.getText() != null ? et_quantity.getText().toString().trim() : "";
        String unit_input = actv_unit.getText() != null ? actv_unit.getText().toString().trim() : "";
        String expiry_input = et_expiry_date.getText() != null ? et_expiry_date.getText().toString().trim() : "";

        boolean is_valid = true;

        if (name_input.isEmpty()) {
            til_ingredient_name.setError(getString(R.string.error_name_required));
            is_valid = false;
        }

        double parsed_qty = 0.0;
        if (qty_input.isEmpty()) {
            til_quantity.setError(getString(R.string.error_quantity_invalid));
            is_valid = false;
        } else {
            try {
                parsed_qty = Double.parseDouble(qty_input);
                if (parsed_qty <= 0 || Double.isNaN(parsed_qty) || Double.isInfinite(parsed_qty)) {
                    til_quantity.setError(getString(R.string.error_quantity_invalid));
                    is_valid = false;
                }
            } catch (NumberFormatException e_err) {
                til_quantity.setError(getString(R.string.error_quantity_invalid));
                is_valid = false;
            }
        }

        if (unit_input.isEmpty()) {
            til_unit.setError(getString(R.string.error_unit_required));
            is_valid = false;
        }

        if (!is_valid) {
            return;
        }

        try {
            if (editing_item_id != -1 && existing_pantry_item != null) {
                existing_pantry_item.setIngredientName(name_input);
                existing_pantry_item.setItemQuantity(parsed_qty);
                existing_pantry_item.setItemUnit(unit_input);
                existing_pantry_item.setExpiryDate(expiry_input);

                int rows_affected = db_helper.updatePantryItem(existing_pantry_item);
                if (rows_affected > 0) {
                    Toast.makeText(this, R.string.toast_item_updated, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.toast_error_save, Toast.LENGTH_SHORT).show();
                }
            } else {
                PantryItem new_item = new PantryItem(name_input, parsed_qty, unit_input, expiry_input);
                long new_id = db_helper.addPantryItem(new_item);
                if (new_id != -1) {
                    Toast.makeText(this, R.string.toast_item_added, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.toast_error_save, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e_err) {
            e_err.printStackTrace();
            Toast.makeText(this, R.string.toast_error_save, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item_menu) {
        if (item_menu.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item_menu);
    }
}