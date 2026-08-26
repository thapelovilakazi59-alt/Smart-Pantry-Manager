# Smart Pantry Manager — Method Connection Map

This document lists every significant method in the app and describes how it connects to other methods, classes, and Android components.

---

## 1. DatabaseHelper.java

### `DatabaseHelper(Context context)`
- **Calls**: `super(context, DATABASE_NAME, null, DATABASE_VERSION)` (SQLiteOpenHelper constructor)
- **Used by**: Every Activity that needs database access (instantiated in `onCreate()`)

### `onCreate(SQLiteDatabase db)`
- **Calls**: `execSQL()` (3 times to create tables), `seedRecipes(db)`
- **Triggered by**: Android system on first app install / when database file does not exist

### `onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)`
- **Calls**: `execSQL()` (DROP TABLE), `onCreate(db)`
- **Triggered by**: Android system when DATABASE_VERSION increases

### `addPantryItem(PantryItem item)`
- **Calls**: `getWritableDatabase()`, `insert()`, `db.close()`
- **Called by**: `AddEditIngredientActivity.saveItem()` (when adding new item)
- **Returns**: `long` (row ID of new record)

### `getAllPantryItems()`
- **Calls**: `getReadableDatabase()`, `query()`, cursor iteration, `db.close()`
- **Called by**: `PantryListActivity.loadPantryItems()`, `getSuggestedRecipes()`
- **Returns**: `List<PantryItem>`

### `getPantryItem(int id)`
- **Calls**: `getReadableDatabase()`, `query()`, cursor extraction, `db.close()`
- **Called by**: `AddEditIngredientActivity.loadExistingItem()` (to populate edit form)
- **Returns**: `PantryItem` or `null`

### `updatePantryItem(PantryItem item)`
- **Calls**: `getWritableDatabase()`, `update()`, `db.close()`
- **Called by**: `AddEditIngredientActivity.saveItem()` (when editing existing item)
- **Returns**: `int` (number of rows affected)

### `deletePantryItem(int id)`
- **Calls**: `getWritableDatabase()`, `delete()`, `db.close()`
- **Called by**: `PantryListActivity.onItemLongClick()` (after user confirms delete)

### `getAllRecipes()`
- **Calls**: `getReadableDatabase()`, `query()`, `extractRecipe()`, `getIngredientsForRecipe()`, `db.close()`
- **Called by**: `getSuggestedRecipes()`
- **Returns**: `List<Recipe>` with fully populated ingredient lists

### `getRecipe(int id)`
- **Calls**: `getReadableDatabase()`, `query()`, `extractRecipe()`, `getIngredientsForRecipe()`, `db.close()`
- **Called by**: `RecipeDetailActivity.displayRecipe()`
- **Returns**: `Recipe` with ingredient list or `null`

### `getIngredientsForRecipe(int recipeId)`
- **Calls**: `getReadableDatabase()`, `query()`, cursor iteration, `db.close()`
- **Called by**: `getAllRecipes()`, `getRecipe()`
- **Returns**: `List<RecipeIngredient>`

### `getSuggestedRecipes()` — **CORE BUSINESS LOGIC**
- **Calls**: `getAllRecipes()`, `getAllPantryItems()`, `canMakeRecipe()`
- **Called by**: `SuggestedRecipesActivity.loadSuggestedRecipes()`
- **Returns**: `List<Recipe>` containing ONLY strict matches

### `canMakeRecipe(Recipe recipe, List<PantryItem> pantry)`
- **Calls**: `hasIngredientInPantry()` for every ingredient in the recipe
- **Called by**: `getSuggestedRecipes()`
- **Returns**: `boolean` — `true` only if ALL ingredients are found

### `hasIngredientInPantry(RecipeIngredient req, List<PantryItem> pantry)`
- **Calls**: `normalizeName()`, `namesMatch()`, `normalizeUnit()`, `unitsCompatible()`, `convertQuantity()`
- **Called by**: `canMakeRecipe()`
- **Returns**: `boolean` — `true` if pantry has sufficient quantity of matching ingredient

### `normalizeName(String name)`, `namesMatch(String a, String b)`, `normalizeUnit(String unit)`, `unitsCompatible(String u1, String u2)`, `convertQuantity(double qty, String fromUnit, String toUnit)`
- **Called by**: `hasIngredientInPantry()`
- **Purpose**: Tolerance for singular/plural and basic unit conversion (g↔kg, ml↔l)

### `seedRecipes(SQLiteDatabase db)`
- **Calls**: `addRecipe()` 18 times
- **Called by**: `onCreate()`

### `addRecipe(SQLiteDatabase db, String name, String desc, String prepTime, String steps, String[] ingredients)`
- **Calls**: `db.insert()` (recipes table), `db.insert()` (recipe_ingredients table)
- **Called by**: `seedRecipes()`

---

## 2. PantryAdapter.java

### `PantryAdapter(List<PantryItem> items, OnPantryItemClickListener listener)`
- **Stores**: item list and click listener reference
- **Used by**: `PantryListActivity` (set on RecyclerView)

### `updateList(List<PantryItem> newItems)`
- **Calls**: `notifyDataSetChanged()`
- **Called by**: `PantryListActivity.loadPantryItems()`

### `onCreateViewHolder(ViewGroup parent, int viewType)`
- **Calls**: `LayoutInflater.inflate(R.layout.item_pantry, ...)`
- **Returns**: `PantryViewHolder`

### `onBindViewHolder(PantryViewHolder holder, int position)`
- **Calls**: `listener.onItemClick()`, `listener.onItemLongClick()`
- **Binds**: PantryItem data to TextViews in the card layout

### `PantryViewHolder(View itemView)`
- **Finds views**: `tvPantryName`, `tvPantryQty`, `tvPantryExpiry` by ID

---

## 3. RecipeAdapter.java

### `RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener)`
- **Stores**: recipe list and click listener reference
- **Used by**: `SuggestedRecipesActivity` (set on RecyclerView)

### `updateList(List<Recipe> newRecipes)`
- **Calls**: `notifyDataSetChanged()`
- **Called by**: `SuggestedRecipesActivity.loadSuggestedRecipes()`

### `onCreateViewHolder(ViewGroup parent, int viewType)`
- **Calls**: `LayoutInflater.inflate(R.layout.item_recipe, ...)`
- **Returns**: `RecipeViewHolder`

### `onBindViewHolder(RecipeViewHolder holder, int position)`
- **Calls**: `listener.onRecipeClick()`
- **Binds**: Recipe name, description, prep time, ingredient count to card

### `RecipeViewHolder(View itemView)`
- **Finds views**: `tvRecipeName`, `tvRecipeDesc`, `tvRecipePrep`, `tvRecipeIngredients`

---

## 4. MainActivity.java

### `onCreate(Bundle savedInstanceState)`
- **Finds views**: `btnPantry`, `btnRecipes`, `btnSettings`
- **Sets listeners**: 3 `OnClickListener` lambdas
- **Calls**: `startActivity()` with explicit Intents to:
  - `PantryListActivity`
  - `SuggestedRecipesActivity`
  - `SettingsActivity`

---

## 5. PantryListActivity.java

### `onCreate(Bundle savedInstanceState)`
- **Calls**: `setContentView()`, `findViewById()` for RecyclerView, empty TextView, FAB
- **Instantiates**: `DatabaseHelper`
- **Sets**: `LinearLayoutManager` on RecyclerView
- **Calls**: `loadPantryItems()`
- **Sets listener**: `fabAdd.setOnClickListener()` → launches `AddEditIngredientActivity` via Intent

### `onResume()`
- **Calls**: `loadPantryItems()` — refreshes list when returning from Add/Edit

### `loadPantryItems()`
- **Calls**: `dbHelper.getAllPantryItems()`
- **Updates UI**: Shows/hides `tvEmpty`, creates/updates `PantryAdapter`, sets on RecyclerView

### `onItemClick(PantryItem item)` — implements PantryAdapter interface
- **Calls**: `startActivity()` with Intent to `AddEditIngredientActivity`, passing `pantry_id` extra

### `onItemLongClick(PantryItem item)` — implements PantryAdapter interface
- **Calls**: `AlertDialog.Builder` → `dbHelper.deletePantryItem()` → `loadPantryItems()`

---

## 6. AddEditIngredientActivity.java

### `onCreate(Bundle savedInstanceState)`
- **Calls**: `setContentView()`, `findViewById()` for all form fields
- **Instantiates**: `DatabaseHelper`
- **Sets**: `ArrayAdapter` on `spinnerUnit`
- **Checks**: `getIntent().hasExtra("pantry_id")`
  - If YES: calls `loadExistingItem(id)`, sets title to "Edit Ingredient"
  - If NO: sets title to "Add Ingredient"
- **Sets listeners**: `btnPickDate` → `showDatePicker()`, `btnSave` → validates then `saveItem()`

### `loadExistingItem(int id)`
- **Calls**: `dbHelper.getPantryItem(id)`
- **Populates**: `etName`, `etQuantity`, `etExpiry`, `spinnerUnit` selection

### `validateInput()`
- **Checks**: name not empty, quantity not empty, quantity > 0, valid number format
- **Called by**: `saveItem()`
- **Returns**: `boolean`

### `saveItem()`
- **Calls**: `validateInput()`
- **Creates**: `PantryItem` object from form fields
- **If pantryId == -1**: calls `dbHelper.addPantryItem(item)` → Toast "added"
- **Else**: calls `dbHelper.updatePantryItem(item)` → Toast "updated"
- **Calls**: `finish()` → returns to `PantryListActivity`

### `showDatePicker()`
- **Calls**: `DatePickerDialog` constructor with `DatePickerDialog.OnDateSetListener`
- **Sets**: `etExpiry` text to formatted date string

---

## 7. SuggestedRecipesActivity.java

### `onCreate(Bundle savedInstanceState)`
- **Calls**: `setContentView()`, `findViewById()` for RecyclerView and empty TextView
- **Instantiates**: `DatabaseHelper`
- **Sets**: `LinearLayoutManager` on RecyclerView
- **Calls**: `loadSuggestedRecipes()`

### `onResume()`
- **Calls**: `loadSuggestedRecipes()` — refreshes when pantry changes

### `loadSuggestedRecipes()`
- **Calls**: `dbHelper.getSuggestedRecipes()` — **STRICT MATCHING**
- **Updates UI**: Shows/hides `tvEmpty`, creates/updates `RecipeAdapter`

### `onRecipeClick(Recipe recipe)` — implements RecipeAdapter interface
- **Calls**: `startActivity()` with Intent to `RecipeDetailActivity`, passing `recipe_id` extra

---

## 8. RecipeDetailActivity.java

### `onCreate(Bundle savedInstanceState)`
- **Calls**: `setContentView()`, `findViewById()` for all detail TextViews
- **Instantiates**: `DatabaseHelper`
- **Reads**: `getIntent().getIntExtra("recipe_id", -1)`
- **Calls**: `displayRecipe(recipeId)`

### `displayRecipe(int id)`
- **Calls**: `dbHelper.getRecipe(id)`
- **Sets text**: `tvName`, `tvDesc`, `tvPrep`
- **Builds string**: ingredient list with bullet points → `tvIngredients`
- **Sets text**: formatted steps → `tvSteps`

---

## 9. SettingsActivity.java

### `onCreate(Bundle savedInstanceState)`
- **Calls**: `setContentView()`, `findViewById()` for two Switches
- **Gets**: `SharedPreferences` instance
- **Loads**: saved boolean values for expiry alert and dark mode
- **Sets listeners**: `OnCheckedChangeListener` on both switches

### Switch Listeners (anonymous inner classes)
- **Expiry switch**: `prefs.edit().putBoolean()` → Toast feedback
- **Dark mode switch**: `prefs.edit().putBoolean()` → `AppCompatDelegate.setDefaultNightMode()`

---

## Data Flow Summary

```
User opens app
    ↓
MainActivity ──Intent──→ PantryListActivity
    ↓                        ↓
    ↓                   loadPantryItems()
    ↓                        ↓
    ↓              dbHelper.getAllPantryItems()
    ↓                        ↓
    ↓                   PantryAdapter displays cards
    ↓                        ↓
    ↓              User taps FAB → AddEditIngredientActivity
    ↓                        ↓
    ↓              validateInput() → saveItem()
    ↓                        ↓
    ↓              dbHelper.addPantryItem() / updatePantryItem()
    ↓                        ↓
    ↓              finish() → onResume() → loadPantryItems()
    ↓
User taps "Suggested Recipes"
    ↓
SuggestedRecipesActivity
    ↓
loadSuggestedRecipes()
    ↓
dbHelper.getSuggestedRecipes()
    ↓
getAllRecipes() + getAllPantryItems()
    ↓
canMakeRecipe() → hasIngredientInPantry() [for every ingredient]
    ↓
RecipeAdapter displays strict-match cards
    ↓
User taps recipe card
    ↓
Intent with recipe_id → RecipeDetailActivity
    ↓
dbHelper.getRecipe(id) → displayRecipe()
```
