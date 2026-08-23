package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.adapter.PantryAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;

import java.util.List;

/**
 * PantryListActivity
 * Displays all pantry items in a RecyclerView.
 * Supports: view list, add new item, edit existing item, delete item.
 * Uses PantryAdapter with custom ViewHolder pattern.
 */
public class PantryListActivity extends AppCompatActivity
        implements PantryAdapter.OnPantryItemClickListener {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private Button btnAddIngredient;
    private PantryAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);
        setTitle("My Pantry");

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerPantry);
        tvEmpty = findViewById(R.id.tvEmptyPantry);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPantryItems();

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(PantryListActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems(); // Refresh list when returning from Add/Edit
    }

    private void loadPantryItems() {
        List<PantryItem> items = dbHelper.getAllPantryItems();
        if (items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new PantryAdapter(items, this);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateList(items);
            }
        }
    }

    @Override
    public void onItemClick(PantryItem item) {
        // Edit existing item
        Intent intent = new Intent(PantryListActivity.this, AddEditIngredientActivity.class);
        intent.putExtra("pantry_id", item.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(PantryItem item) {
        // Delete with confirmation dialog
        showDeleteConfirmation(item);
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        // Delete via visible button with confirmation dialog
        showDeleteConfirmation(item);
    }

    /**
     * Shows a confirmation dialog before deleting a pantry item.
     * Called by both long-press and Delete button.
     */
    private void showDeleteConfirmation(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Remove " + capitalize(item.getName()) + " from pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    loadPantryItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
