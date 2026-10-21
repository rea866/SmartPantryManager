# Smart Pantry Manager

An Android app (written in **Java**) that helps reduce food waste. The user records the ingredients
they actually have at home (their "pantry") and the app suggests recipes they can cook **using
strictly those ingredients** - no recipe is suggested unless every ingredient it needs is already
in the pantry, in at least the required quantity.

Module: Mobile App Development 700 - Practical Assignment (individual).

## Features

- **Pantry management** - add, edit and delete ingredients (name, quantity, unit, optional expiry date) with input validation.
- **Pantry list** - RecyclerView with a custom adapter, bound to the SQLite database.
- **Recipe collection** - 21 recipes pre-loaded into the database on first run.
- **Suggested Recipes** - runs the strict-matching rule against the pantry; shows a friendly message when nothing matches.
- **Almost There (bonus)** - a separate list of recipes missing exactly one ingredient.
- **Recipe detail** - full ingredient list and method.
- **Settings** - expiring-soon alerts toggle, "ignore expired ingredients" toggle, clear-pantry action.
- **Bottom navigation** between Pantry / Recipes / Settings, using Intents.

No Google Maps, mapping SDK or location/GPS services are used, and the app requests no permissions.

## The strict-matching rule

Implemented in `logic/RecipeMatcher.java` (unit-tested in `RecipeMatcherTest`):

- A recipe is suggested only if **every** ingredient is present in at least the required quantity.
- Names are normalised so "Tomatoes", "tomato" and " TOMATO " match (`logic/NameUtils.java`); a few synonyms (e.g. "olive oil" -> "oil") are supported.
- Units are converted (`logic/UnitConverter.java`): 0.5 kg satisfies 500 g, 1 cup satisfies 240 ml. Mass and volume are compared with the approximation 1 g = 1 ml. Countable items ("pcs") are only compared with other "pcs".
- Several pantry rows for the same ingredient are added together.
- Staples such as salt and oil are "to taste" in recipes: they only need to be present.
- Expired items can be excluded from matching (Settings).

## Database choice: SQLite

The app uses on-device **SQLite** through `SQLiteOpenHelper` (`db/DatabaseHelper.java`).
Reasons: it works offline with no server or account, it is the approach covered in the module's persistent
data chapter, and the data (pantry items and a fixed recipe set) is small and relational.

Tables: `pantry_items`, `recipes`, `recipe_ingredients` (one recipe has many ingredient rows).
Data persists after the app is closed and reopened.

## Project structure

```
app/src/main/java/com/richfield/smartpantry/
  model/   Ingredient, Recipe, RecipeIngredient
  db/      DatabaseHelper (SQLite CRUD), SeedData (21 recipes)
  logic/   RecipeMatcher, NameUtils, UnitConverter, ExpiryUtils
  ui/      MainActivity, AddEditIngredientActivity, SuggestedRecipesActivity,
           RecipeDetailActivity, SettingsActivity, PantryAdapter, RecipeAdapter, NavHelper
  util/    Prefs (SharedPreferences)
app/src/test/  RecipeMatcherTest (JUnit)
```

## Setup / run instructions

1. Install **Android Studio** (Hedgehog 2023.1 or newer; it bundles the JDK 17 that this project needs).
2. Choose **File > Open** and select the `SmartPantryManager` folder. Let Gradle sync finish
   (an internet connection is needed the first time to download dependencies).
3. Create an emulator (**Tools > Device Manager**, any device on Android 8.0 / API 26 or newer) or connect a
   physical phone with USB debugging enabled.
4. Press **Run** (green triangle). The app opens on the Pantry screen.
5. To run the unit tests: right-click `RecipeMatcherTest` > **Run**.

Requirements: minSdk 26 (Android 8.0), compileSdk/targetSdk 34, Java 17.

## Quick demo script

1. Add: Eggs 3 pcs, Tomatoes 1 pcs, Butter 10 g, Salt 100 g.
2. Open **Recipes** - "Tomato Omelette" appears under *Ready to cook*.
3. Go back to Pantry and delete the tomatoes - the omelette moves to *Almost there* (missing: Tomato).
4. Edit the eggs to 2 pcs and add tomatoes back - the omelette disappears (needs 3 eggs).
