package com.example.a402312338_ngonini_mobileappdev700_assignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

/**
 * Settings fragment providing quick actions for database resets, sample data, and app info.
 * Variable names strictly follow snake_case convention.
 */
public class SettingsFragment extends Fragment {

    private DatabaseHelper db_helper;
    private MaterialButton btn_seed_sample_pantry;
    private MaterialButton btn_clear_pantry;
    private MaterialButton btn_reset_recipes;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_settings, container, false);

        db_helper = new DatabaseHelper(requireContext());

        btn_seed_sample_pantry = fragment_view.findViewById(R.id.btn_seed_sample_pantry);
        btn_clear_pantry = fragment_view.findViewById(R.id.btn_clear_pantry);
        btn_reset_recipes = fragment_view.findViewById(R.id.btn_reset_recipes);

        btn_seed_sample_pantry.setOnClickListener(v_click -> {
            if (!isAdded() || getContext() == null) return;
            boolean success = db_helper.loadSamplePantryData();
            if (success) {
                Toast.makeText(requireContext(), R.string.toast_sample_loaded, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), R.string.toast_error_operation, Toast.LENGTH_SHORT).show();
            }
        });

        btn_clear_pantry.setOnClickListener(v_click -> {
            if (!isAdded() || getContext() == null) return;
            new AlertDialog.Builder(requireContext())
                    .setTitle("Clear Pantry")
                    .setMessage("Are you sure you want to clear all ingredients from your pantry?")
                    .setPositiveButton("Clear All", (dialog_interface, which_idx) -> {
                        if (!isAdded() || getContext() == null) return;
                        boolean success = db_helper.clearPantry();
                        if (success) {
                            Toast.makeText(requireContext(), R.string.toast_pantry_cleared, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), R.string.toast_error_operation, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
        });

        btn_reset_recipes.setOnClickListener(v_click -> {
            if (!isAdded() || getContext() == null) return;
            boolean success = db_helper.resetRecipeDatabase();
            if (success) {
                Toast.makeText(requireContext(), R.string.toast_recipes_reset, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), R.string.toast_error_operation, Toast.LENGTH_SHORT).show();
            }
        });

        return fragment_view;
    }
}