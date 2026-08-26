package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.database.DatabaseHelper;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

/**
 * RecipeDetailActivity
 * Displays full recipe information: name, description, prep time,
 * complete ingredient list with quantities, and step-by-step method.
 * Receives recipe_id via Intent extras from SuggestedRecipesActivity.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvName, tvDesc, tvPrep, tvIngredients, tvSteps;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dbHelper = new DatabaseHelper(this);
        tvName = findViewById(R.id.tvDetailName);
        tvDesc = findViewById(R.id.tvDetailDesc);
        tvPrep = findViewById(R.id.tvDetailPrep);
        tvIngredients = findViewById(R.id.tvDetailIngredients);
        tvSteps = findViewById(R.id.tvDetailSteps);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        if (recipeId != -1) {
            displayRecipe(recipeId);
        }
    }

    private void displayRecipe(int id) {
        Recipe recipe = dbHelper.getRecipe(id);
        if (recipe == null) return;

        tvName.setText(recipe.getName());
        tvDesc.setText(recipe.getDescription());
        tvPrep.setText("Preparation time: " + recipe.getPrepTime());

        StringBuilder ingBuilder = new StringBuilder("Ingredients:\n");
        for (RecipeIngredient ri : recipe.getIngredients()) {
            ingBuilder.append("• ")
                    .append(capitalize(ri.getIngredientName()))
                    .append(" — ")
                    .append(ri.getQuantity())
                    .append(" ")
                    .append(ri.getUnit())
                    .append("\n");
        }
        tvIngredients.setText(ingBuilder.toString());

        tvSteps.setText("Method:\n" + recipe.getSteps().replace("\n", "\n"));
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
