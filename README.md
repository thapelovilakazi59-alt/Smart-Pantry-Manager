# Smart Pantry Manager

## Description
Smart Pantry Manager is a Java Android application that helps users reduce food waste by tracking ingredients they already have at home and suggesting recipes they can cook **strictly** from those leftovers — no shopping trip required.

## Database Choice: SQLite
I chose **SQLite** (via `SQLiteOpenHelper`) because:
- It is the standard local database approach taught in the Mobile App Development 700 module.
- It requires no internet connection, API keys, or external cloud setup.
- Data persists entirely on-device, making the app fast and private.
- It demonstrates full CRUD operations clearly without backend complexity.

## Features
- **Pantry Management** — Add, edit, delete ingredients (name, quantity, unit, expiry date).
- **Suggested Recipes** — Strict-matching algorithm shows only recipes you can fully make now.
- **Recipe Detail** — Full ingredient list and step-by-step cooking instructions.
- **Settings** — Toggle expiry alerts and dark mode.
- **18 Pre-loaded Recipes** — Seeded into the database on first app launch.

## Screens
1. MainActivity (Home / Launcher)
2. PantryListActivity (View all pantry items)
3. AddEditIngredientActivity (Add or edit an ingredient)
4. SuggestedRecipesActivity (Recipes matching your pantry)
5. RecipeDetailActivity (Full recipe view)
6. SettingsActivity (Preferences)

## Setup & Run Instructions
1. Open Android Studio (latest stable version recommended).
2. Select **File → Open** and choose the `SmartPantryManager` folder.
3. Let Gradle sync complete (may take a few minutes on first open).
4. Connect an Android emulator (API 24+) or a physical device with USB debugging enabled.
5. Click the **Run** button (green triangle) or press `Shift + F10`.
6. The app will build, install, and launch automatically.

## Project Structure
```
app/src/main/java/com/example/smartpantry/
├── MainActivity.java
├── PantryListActivity.java
├── AddEditIngredientActivity.java
├── SuggestedRecipesActivity.java
├── RecipeDetailActivity.java
├── SettingsActivity.java
├── database/
│   └── DatabaseHelper.java
├── model/
│   ├── PantryItem.java
│   ├── Recipe.java
│   └── RecipeIngredient.java
└── adapter/
    ├── PantryAdapter.java
    └── RecipeAdapter.java
```

## Strict Matching Rule
A recipe only appears in suggestions if **every** ingredient it requires is present in the pantry in at least the required quantity. Partial matches are excluded. The algorithm also handles basic unit conversions (g↔kg, ml↔l) and singular/plural name tolerance.

## Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- compileSdk 34
- minSdk 24
- Java 8+