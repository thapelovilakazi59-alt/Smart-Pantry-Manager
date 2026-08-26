package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.adapter.RecipeAdapter;
import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.Recipe;

import java.util.List;

/**
 * SuggestedRecipesActivity
 * Executes the strict-matching algorithm against the user's pantry.
 * Displays ONLY recipes where every required ingredient is present
 * in sufficient quantity. Shows feedback when no recipes match.
 */
public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private RecipeAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        setTitle("Suggested Recipes");

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerRecipes);
        tvEmpty = findViewById(R.id.tvEmptyRecipes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadSuggestedRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes(); // Refresh when pantry changes
    }

    private void loadSuggestedRecipes() {
        List<Recipe> recipes = dbHelper.getSuggestedRecipes();
        if (recipes.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new RecipeAdapter(recipes, this);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateList(recipes);
            }
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getId());
        startActivity(intent);
    }
}
