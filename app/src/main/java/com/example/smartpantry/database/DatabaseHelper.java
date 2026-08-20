package com.example.smartpantry.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper handles all SQLite operations for Smart Pantry Manager.
 * Tables: pantry_items, recipes, recipe_ingredients
 * Pre-seeds 18 recipes on first creation.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table: pantry_items
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QTY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // Table: recipes
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_DESC = "description";
    public static final String COL_RECIPE_PREP = "prep_time";
    public static final String COL_RECIPE_STEPS = "steps";

    // Table: recipe_ingredients
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QTY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPantry = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QTY + " REAL NOT NULL, " +
                COL_PANTRY_UNIT + " TEXT NOT NULL, " +
                COL_PANTRY_EXPIRY + " TEXT)";

        String createRecipes = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_DESC + " TEXT, " +
                COL_RECIPE_PREP + " TEXT, " +
                COL_RECIPE_STEPS + " TEXT NOT NULL)";

        String createRecipeIngredients = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_RI_NAME + " TEXT NOT NULL, " +
                COL_RI_QTY + " REAL NOT NULL, " +
                COL_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_RECIPE_ID + "))";

        db.execSQL(createPantry);
        db.execSQL(createRecipes);
        db.execSQL(createRecipeIngredients);

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // ===================== PANTRY CRUD =====================

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, item.getName().trim().toLowerCase());
        values.put(COL_PANTRY_QTY, item.getQuantity());
        values.put(COL_PANTRY_UNIT, item.getUnit().trim().toLowerCase());
        values.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        long id = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return id;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, COL_PANTRY_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                PantryItem item = new PantryItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)));
                item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QTY)));
                item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)));
                item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY)));
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public PantryItem getPantryItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_PANTRY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        PantryItem item = null;
        if (cursor.moveToFirst()) {
            item = new PantryItem();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)));
            item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QTY)));
            item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)));
            item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY)));
        }
        cursor.close();
        db.close();
        return item;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, item.getName().trim().toLowerCase());
        values.put(COL_PANTRY_QTY, item.getQuantity());
        values.put(COL_PANTRY_UNIT, item.getUnit().trim().toLowerCase());
        values.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        int rows = db.update(TABLE_PANTRY, values, COL_PANTRY_ID + "=?",
                new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    public void deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_PANTRY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ===================== RECIPE QUERIES =====================

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, COL_RECIPE_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                Recipe r = extractRecipe(cursor);
                r.setIngredients(getIngredientsForRecipe(r.getId()));
                recipes.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recipes;
    }

    public Recipe getRecipe(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, COL_RECIPE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            recipe = extractRecipe(cursor);
            recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
        }
        cursor.close();
        db.close();
        return recipe;
    }

    private Recipe extractRecipe(Cursor cursor) {
        Recipe r = new Recipe();
        r.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)));
        r.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)));
        r.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_DESC)));
        r.setPrepTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_PREP)));
        r.setSteps(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_STEPS)));
        return r;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(int recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null, COL_RI_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                RecipeIngredient ri = new RecipeIngredient();
                ri.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RI_ID)));
                ri.setRecipeId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RI_RECIPE_ID)));
                ri.setIngredientName(cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_NAME)));
                ri.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RI_QTY)));
                ri.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_UNIT)));
                list.add(ri);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // ===================== STRICT MATCHING (CORE LOGIC) =====================

    /**
     * Returns only recipes where EVERY required ingredient is present in the pantry
     * in at least the required quantity.
     * Handles basic unit normalization and singular/plural tolerance.
     */
    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> allRecipes = getAllRecipes();
        List<PantryItem> pantry = getAllPantryItems();
        List<Recipe> suggested = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (canMakeRecipe(recipe, pantry)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    private boolean canMakeRecipe(Recipe recipe, List<PantryItem> pantry) {
        for (RecipeIngredient req : recipe.getIngredients()) {
            if (!hasIngredientInPantry(req, pantry)) {
                return false; // STRICT: missing even one ingredient disqualifies recipe
            }
        }
        return true;
    }

    private boolean hasIngredientInPantry(RecipeIngredient req, List<PantryItem> pantry) {
        String reqName = normalizeName(req.getIngredientName());
        double reqQty = req.getQuantity();
        String reqUnit = normalizeUnit(req.getUnit());

        for (PantryItem item : pantry) {
            String panName = normalizeName(item.getName());
            String panUnit = normalizeUnit(item.getUnit());

            if (namesMatch(reqName, panName) && unitsCompatible(reqUnit, panUnit)) {
                double pantryQty = convertQuantity(item.getQuantity(), panUnit, reqUnit);
                if (pantryQty >= reqQty) {
                    return true;
                }
            }
        }
        return false;
    }

    // Basic singular/plural and case normalization
    private String normalizeName(String name) {
        return name.trim().toLowerCase().replaceAll("s$", "");
    }

    private boolean namesMatch(String a, String b) {
        return a.equals(b) || a.startsWith(b) || b.startsWith(a);
    }

    private String normalizeUnit(String unit) {
        String u = unit.trim().toLowerCase();
        switch (u) {
            case "kg": case "kilogram": case "kilograms": return "kg";
            case "g": case "gram": case "grams": return "g";
            case "l": case "liter": case "liters": case "litre": case "litres": return "l";
            case "ml": case "milliliter": case "milliliters": return "ml";
            case "tbsp": case "tablespoon": case "tablespoons": return "tbsp";
            case "tsp": case "teaspoon": case "teaspoons": return "tsp";
            case "cup": case "cups": return "cup";
            case "piece": case "pieces": case "pc": case "pcs": return "piece";
            default: return u;
        }
    }

    private boolean unitsCompatible(String u1, String u2) {
        return u1.equals(u2) || areSameDimension(u1, u2);
    }

    private boolean areSameDimension(String u1, String u2) {
        // Mass
        if ((u1.equals("kg") || u1.equals("g")) && (u2.equals("kg") || u2.equals("g"))) return true;
        // Volume
        if ((u1.equals("l") || u1.equals("ml")) && (u2.equals("l") || u2.equals("ml"))) return true;
        return false;
    }

    private double convertQuantity(double qty, String fromUnit, String toUnit) {
        if (fromUnit.equals(toUnit)) return qty;
        // Mass conversions
        if (fromUnit.equals("kg") && toUnit.equals("g")) return qty * 1000;
        if (fromUnit.equals("g") && toUnit.equals("kg")) return qty / 1000;
        // Volume conversions
        if (fromUnit.equals("l") && toUnit.equals("ml")) return qty * 1000;
        if (fromUnit.equals("ml") && toUnit.equals("l")) return qty / 1000;
        return qty; // fallback: assume same if unitsCompatible passed
    }

    // ===================== SEED DATA (18 RECIPES) =====================

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Scrambled Eggs on Toast",
                "Quick breakfast using eggs and bread",
                "10 mins",
                "1. Whisk eggs with salt and pepper.\n2. Melt butter in a pan.\n3. Cook eggs on low heat, stirring gently.\n4. Serve on toasted bread.",
                new String[]{"eggs|3|piece", "bread|2|slice", "butter|10|g"});

        addRecipe(db, "Tomato Omelette",
                "Simple omelette with fresh tomatoes",
                "15 mins",
                "1. Beat eggs in a bowl.\n2. Dice tomatoes.\n3. Pour eggs into heated buttered pan.\n4. Add tomatoes, fold omelette.\n5. Cook until set.",
                new String[]{"eggs|2|piece", "tomato|1|piece", "butter|10|g"});

        addRecipe(db, "Cheese Toastie",
                "Grilled cheese sandwich",
                "10 mins",
                "1. Butter one side of each bread slice.\n2. Place cheese between unbuttered sides.\n3. Grill in pan until golden and cheese melts.",
                new String[]{"bread|2|slice", "cheese|2|slice", "butter|10|g"});

        addRecipe(db, "Garlic Butter Pasta",
                "Pasta tossed in garlic butter",
                "20 mins",
                "1. Boil pasta in salted water until al dente.\n2. Melt butter in a pan, add minced garlic.\n3. Drain pasta and toss in garlic butter.\n4. Season with salt and pepper.",
                new String[]{"pasta|200|g", "butter|30|g", "garlic|2|clove"});

        addRecipe(db, "Fried Rice",
                "Classic leftover rice dish",
                "20 mins",
                "1. Heat oil in a wok or large pan.\n2. Scramble the egg and set aside.\n3. Stir-fry rice with soy sauce.\n4. Add egg back in and mix well.",
                new String[]{"rice|250|g", "eggs|1|piece", "soy sauce|15|ml", "oil|15|ml"});

        addRecipe(db, "Tomato Pasta Sauce",
                "Simple marinara-style sauce",
                "25 mins",
                "1. Sauté garlic in olive oil.\n2. Add chopped tomatoes and simmer 15 mins.\n3. Season with salt and sugar.\n4. Toss with cooked pasta.",
                new String[]{"tomato|4|piece", "garlic|2|clove", "olive oil|30|ml", "pasta|200|g"});

        addRecipe(db, "Banana Pancakes",
                "Sweet pancakes using ripe bananas",
                "20 mins",
                "1. Mash bananas in a bowl.\n2. Add eggs and flour, mix to a batter.\n3. Cook spoonfuls in a buttered pan.\n4. Flip when bubbles appear.",
                new String[]{"banana|2|piece", "eggs|2|piece", "flour|100|g", "butter|20|g"});

        addRecipe(db, "Potato Hash",
                "Crispy pan-fried potatoes",
                "25 mins",
                "1. Dice potatoes into small cubes.\n2. Fry in oil until golden and crispy.\n3. Season with salt and pepper.\n4. Optional: top with a fried egg.",
                new String[]{"potato|3|piece", "oil|30|ml", "eggs|1|piece"});

        addRecipe(db, "Vegetable Stir Fry",
                "Quick mixed vegetable dish",
                "15 mins",
                "1. Chop all vegetables into bite-sized pieces.\n2. Heat oil in wok on high heat.\n3. Stir-fry vegetables for 5-7 mins.\n4. Add soy sauce and toss.",
                new String[]{"carrot|2|piece", "onion|1|piece", "bell pepper|1|piece", "oil|15|ml", "soy sauce|15|ml"});

        addRecipe(db, "Egg Fried Noodles",
                "Asian-style egg noodles",
                "15 mins",
                "1. Cook noodles according to package.\n2. Scramble egg in a pan.\n3. Add drained noodles and soy sauce.\n4. Toss together and serve hot.",
                new String[]{"noodles|150|g", "eggs|2|piece", "soy sauce|15|ml", "oil|10|ml"});

        addRecipe(db, "Cheesy Baked Potato",
                "Baked potato loaded with cheese",
                "60 mins",
                "1. Prick potato and bake at 200°C for 45 mins.\n2. Slice open and fluff inside.\n3. Stuff with cheese and butter.\n4. Return to oven until cheese melts.",
                new String[]{"potato|2|piece", "cheese|100|g", "butter|20|g"});

        addRecipe(db, "Rice and Beans",
                "Hearty vegetarian staple",
                "30 mins",
                "1. Cook rice in boiling water.\n2. Heat beans in a separate pot.\n3. Season beans with salt and pepper.\n4. Serve beans over rice.",
                new String[]{"rice|200|g", "beans|1|can", "salt|5|g"});

        addRecipe(db, "Butter Chicken (Simple)",
                "Creamy tomato chicken curry",
                "35 mins",
                "1. Sauté chicken pieces until browned.\n2. Add tomato and simmer 15 mins.\n3. Stir in butter and cream.\n4. Serve with rice.",
                new String[]{"chicken|300|g", "tomato|3|piece", "butter|30|g", "cream|100|ml", "rice|200|g"});

        addRecipe(db, "Tuna Pasta Salad",
                "Cold pasta salad with tuna",
                "15 mins",
                "1. Cook pasta, rinse under cold water.\n2. Drain tuna and flake into a bowl.\n3. Mix pasta, tuna, and mayonnaise.\n4. Season and chill before serving.",
                new String[]{"pasta|200|g", "tuna|1|can", "mayonnaise|30|g"});

        addRecipe(db, "French Toast",
                "Classic sweet breakfast",
                "15 mins",
                "1. Whisk eggs with milk and cinnamon.\n2. Dip bread slices into mixture.\n3. Fry in buttered pan until golden.\n4. Serve with syrup if available.",
                new String[]{"bread|4|slice", "eggs|2|piece", "milk|60|ml", "butter|20|g"});

        addRecipe(db, "Chicken Fried Rice",
                "Fried rice with chicken pieces",
                "25 mins",
                "1. Cook rice and let cool.\n2. Stir-fry diced chicken until cooked.\n3. Add rice, soy sauce, and peas.\n4. Toss together and serve.",
                new String[]{"rice|250|g", "chicken|200|g", "soy sauce|15|ml", "oil|15|ml", "eggs|1|piece"});

        addRecipe(db, "Mushroom Omelette",
                "Savory mushroom-filled omelette",
                "15 mins",
                "1. Slice mushrooms.\n2. Sauté mushrooms in butter.\n3. Pour beaten eggs over mushrooms.\n4. Fold and cook until set.",
                new String[]{"eggs|3|piece", "mushroom|100|g", "butter|15|g"});

        addRecipe(db, "Spaghetti Aglio e Olio",
                "Classic Italian garlic pasta",
                "20 mins",
                "1. Cook spaghetti in salted water.\n2. Slowly cook garlic in olive oil until golden.\n3. Toss spaghetti in garlic oil.\n4. Season with chili flakes and parsley.",
                new String[]{"spaghetti|200|g", "garlic|4|clove", "olive oil|45|ml"});
    }

    private void addRecipe(SQLiteDatabase db, String name, String desc, String prepTime,
                           String steps, String[] ingredients) {
        ContentValues rv = new ContentValues();
        rv.put(COL_RECIPE_NAME, name);
        rv.put(COL_RECIPE_DESC, desc);
        rv.put(COL_RECIPE_PREP, prepTime);
        rv.put(COL_RECIPE_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, rv);

        for (String ing : ingredients) {
            String[] parts = ing.split("\\|");
            ContentValues iv = new ContentValues();
            iv.put(COL_RI_RECIPE_ID, recipeId);
            iv.put(COL_RI_NAME, parts[0].trim().toLowerCase());
            iv.put(COL_RI_QTY, Double.parseDouble(parts[1]));
            iv.put(COL_RI_UNIT, parts[2].trim().toLowerCase());
            db.insert(TABLE_RECIPE_INGREDIENTS, null, iv);
        }
    }
}
