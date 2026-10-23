package com.example.a402312338_ngonini_mobileappdev700_assignment;

import java.util.Locale;

/**
 * Utility class for normalizing ingredient names and checking quantity/unit compatibility.
 * Variables strictly use snake_case.
 */
public class StringNormalizer {

    /**
     * Normalizes ingredient names by converting to lowercase, trimming whitespace,
     * and reducing basic plurals to singular form (e.g., "tomatoes" -> "tomato", "eggs" -> "egg").
     */
    public static String normalizeIngredientName(String input_name) {
        if (input_name == null) {
            return "";
        }
        String clean_name = input_name.trim().toLowerCase(Locale.ROOT);

        // Basic plural normalization rules
        if (clean_name.endsWith("ies") && clean_name.length() > 3) {
            clean_name = clean_name.substring(0, clean_name.length() - 3) + "y"; // e.g. berries -> berry
        } else if (clean_name.endsWith("es") && clean_name.length() > 3) {
            if (clean_name.endsWith("tomatoes") || clean_name.endsWith("potatoes")) {
                clean_name = clean_name.substring(0, clean_name.length() - 2); // tomatoes -> tomato
            } else if (clean_name.endsWith("ches") || clean_name.endsWith("shes") || clean_name.endsWith("xes")) {
                clean_name = clean_name.substring(0, clean_name.length() - 2);
            } else {
                clean_name = clean_name.substring(0, clean_name.length() - 1);
            }
        } else if (clean_name.endsWith("s") && !clean_name.endsWith("ss") && clean_name.length() > 2) {
            clean_name = clean_name.substring(0, clean_name.length() - 1); // e.g. eggs -> egg, carrots -> carrot
        }

        return clean_name;
    }

    /**
     * Checks if two ingredient names match after normalization.
     */
    public static boolean areIngredientsMatching(String name_one, String name_two) {
        String norm_one = normalizeIngredientName(name_one);
        String norm_two = normalizeIngredientName(name_two);
        if (norm_one.equals(norm_two)) {
            return true;
        }
        // Substring / contains check for compound names (e.g. "chicken breast" vs "chicken")
        return norm_one.contains(norm_two) || norm_two.contains(norm_one);
    }

    /**
     * Converts a quantity to a standardized base unit quantity (e.g., g for mass, ml for volume).
     */
    public static double convertToBaseQuantity(double input_qty, String unit_str) {
        if (unit_str == null) {
            return input_qty;
        }
        String clean_unit = unit_str.trim().toLowerCase(Locale.ROOT);
        switch (clean_unit) {
            case "kg":
            case "kilogram":
            case "kilograms":
                return input_qty * 1000.0; // convert to grams
            case "l":
            case "liter":
            case "liters":
            case "litre":
            case "litres":
                return input_qty * 1000.0; // convert to ml
            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return input_qty * 15.0; // convert to ml
            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return input_qty * 5.0; // convert to ml
            case "cup":
            case "cups":
                return input_qty * 240.0; // convert to ml
            default:
                return input_qty;
        }
    }

    /**
     * Returns true if the pantry quantity satisfies or exceeds the required quantity.
     */
    public static boolean isQuantitySufficient(double pantry_qty, String pantry_unit, double req_qty, String req_unit) {
        double normalized_pantry = convertToBaseQuantity(pantry_qty, pantry_unit);
        double normalized_req = convertToBaseQuantity(req_qty, req_unit);
        return normalized_pantry >= (normalized_req - 0.001);
    }
}