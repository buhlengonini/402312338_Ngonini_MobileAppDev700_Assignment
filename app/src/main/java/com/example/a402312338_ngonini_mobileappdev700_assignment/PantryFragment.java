package com.example.a402312338_ngonini_mobileappdev700_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying the user's current pantry ingredients from SQLite database.
 * Internal variables strictly follow snake_case convention.
 */
public class PantryFragment extends Fragment implements PantryAdapter.OnPantryItemActionListener {

    private DatabaseHelper db_helper;
    private RecyclerView rv_pantry;
    private LinearLayout layout_empty_pantry;
    private EditText et_search_pantry;
    private FloatingActionButton fab_add_ingredient;
    private PantryAdapter pantry_adapter;
    private List<PantryItem> current_pantry_list;

    public PantryFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_pantry, container, false);

        db_helper = new DatabaseHelper(requireContext());

        rv_pantry = fragment_view.findViewById(R.id.rv_pantry);
        layout_empty_pantry = fragment_view.findViewById(R.id.layout_empty_pantry);
        et_search_pantry = fragment_view.findViewById(R.id.et_search_pantry);
        fab_add_ingredient = fragment_view.findViewById(R.id.fab_add_ingredient);

        rv_pantry.setLayoutManager(new LinearLayoutManager(requireContext()));
        current_pantry_list = new ArrayList<>();
        pantry_adapter = new PantryAdapter(requireContext(), current_pantry_list, this);
        rv_pantry.setAdapter(pantry_adapter);

        fab_add_ingredient.setOnClickListener(v_click -> {
            Intent add_intent = new Intent(requireContext(), AddEditIngredientActivity.class);
            startActivity(add_intent);
        });

        et_search_pantry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s_seq, int start_idx, int count_idx, int after_idx) {}

            @Override
            public void onTextChanged(CharSequence s_seq, int start_idx, int before_idx, int count_idx) {
                loadPantryData(s_seq.toString());
            }

            @Override
            public void afterTextChanged(Editable s_edit) {}
        });

        return fragment_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String current_search = et_search_pantry != null ? et_search_pantry.getText().toString() : "";
        loadPantryData(current_search);
    }

    private void loadPantryData(String search_query) {
        current_pantry_list = db_helper.getAllPantryItems(search_query);
        pantry_adapter.updateList(current_pantry_list);

        if (current_pantry_list.isEmpty()) {
            layout_empty_pantry.setVisibility(View.VISIBLE);
            rv_pantry.setVisibility(View.GONE);
        } else {
            layout_empty_pantry.setVisibility(View.GONE);
            rv_pantry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditItem(PantryItem item_obj) {
        Intent edit_intent = new Intent(requireContext(), AddEditIngredientActivity.class);
        edit_intent.putExtra("EXTRA_ITEM_ID", item_obj.getItemId());
        startActivity(edit_intent);
    }

    @Override
    public void onDeleteItem(PantryItem item_obj) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_msg, item_obj.getIngredientName()))
                .setPositiveButton(R.string.btn_delete, (dialog_interface, which_idx) -> {
                    db_helper.deletePantryItem(item_obj.getItemId());
                    Toast.makeText(requireContext(), R.string.toast_item_deleted, Toast.LENGTH_SHORT).show();
                    String current_search = et_search_pantry.getText().toString();
                    loadPantryData(current_search);
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}