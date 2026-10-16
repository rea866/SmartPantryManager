package com.richfield.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.richfield.smartpantry.model.Ingredient;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Local SQLite database for the app (via SQLiteOpenHelper).
 *
 * Tables:
 *   pantry_items       - what the user has at home (full CRUD from the UI)
 *   recipes            - the recipe collection (seeded on first run)
 *   recipe_ingredients - the ingredient lines of each recipe (many rows per recipe)
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // pantry_items
    private static final String T_PANTRY = "pantry_items";
    private static final String C_ID = "_id";
    private static final String C_NAME = "name";
    private static final String C_QTY = "quantity";
    private static final String C_UNIT = "unit";
    private static final String C_EXPIRY = "expiry_date";

    // recipes
    private static final String T_RECIPES = "recipes";
    private static final String C_STEPS = "steps";

    // recipe_ingredients
    private static final String T_RI = "recipe_ingredients";
    private static final String C_RI_RECIPE = "recipe_id";

    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // needed for ON DELETE CASCADE
    }

    /** Called once, the first time the database is opened: creates tables and seeds recipes. */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_PANTRY + " ("
                + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_NAME + " TEXT NOT NULL, "
                + C_QTY + " REAL NOT NULL, "
                + C_UNIT + " TEXT NOT NULL, "
                + C_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + T_RECIPES + " ("
                + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_NAME + " TEXT NOT NULL, "
                + C_STEPS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + T_RI + " ("
                + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_RI_RECIPE + " INTEGER NOT NULL, "
                + C_NAME + " TEXT NOT NULL, "
                + C_QTY + " REAL NOT NULL, "
                + C_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + C_RI_RECIPE + ") REFERENCES " + T_RECIPES + "(" + C_ID + ") ON DELETE CASCADE)");

        seedRecipes(db);
    }

    /** Simple upgrade strategy for this assignment: rebuild everything. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_RI);
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + T_PANTRY);
        onCreate(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        for (Recipe recipe : SeedData.getRecipes()) {
            ContentValues rv = new ContentValues();
            rv.put(C_NAME, recipe.getName());
            rv.put(C_STEPS, recipe.getSteps());
            long recipeId = db.insert(T_RECIPES, null, rv);

            for (RecipeIngredient ri : recipe.getIngredients()) {
                ContentValues iv = new ContentValues();
                iv.put(C_RI_RECIPE, recipeId);
                iv.put(C_NAME, ri.getName());
                iv.put(C_QTY, ri.getQuantity());
                iv.put(C_UNIT, ri.getUnit());
                db.insert(T_RI, null, iv);
            }
        }
    }

    // ------------------------------------------------------------------
    // PANTRY: Create / Read / Update / Delete
    // ------------------------------------------------------------------

    /** CREATE - returns the new row id, or -1 on failure. */
    public long addIngredient(Ingredient item) {
        return getWritableDatabase().insert(T_PANTRY, null, toValues(item));
    }

    /** READ (one) - returns null if the id does not exist. */
    public Ingredient getIngredient(long id) {
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, C_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? fromCursor(c) : null;
        }
    }

    /** READ (all) - sorted alphabetically for the list screen. */
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> items = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, null, null, null, null,
                C_NAME + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                items.add(fromCursor(c));
            }
        }
        return items;
    }

    /** UPDATE - returns the number of rows changed. */
    public int updateIngredient(Ingredient item) {
        return getWritableDatabase().update(T_PANTRY, toValues(item), C_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    /** DELETE - returns the number of rows removed. */
    public int deleteIngredient(long id) {
        return getWritableDatabase().delete(T_PANTRY, C_ID + "=?", new String[]{String.valueOf(id)});
    }

    /** Removes every pantry item (used by the Settings screen). */
    public void deleteAllIngredients() {
        getWritableDatabase().delete(T_PANTRY, null, null);
    }

    private ContentValues toValues(Ingredient item) {
        ContentValues cv = new ContentValues();
        cv.put(C_NAME, item.getName());
        cv.put(C_QTY, item.getQuantity());
        cv.put(C_UNIT, item.getUnit());
        cv.put(C_EXPIRY, item.getExpiryDate()); // null is stored as SQL NULL
        return cv;
    }

    private Ingredient fromCursor(Cursor c) {
        return new Ingredient(
                c.getLong(c.getColumnIndexOrThrow(C_ID)),
                c.getString(c.getColumnIndexOrThrow(C_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(C_QTY)),
                c.getString(c.getColumnIndexOrThrow(C_UNIT)),
                c.getString(c.getColumnIndexOrThrow(C_EXPIRY)));
    }

    // ------------------------------------------------------------------
    // RECIPES (read only)
    // ------------------------------------------------------------------

    /** Loads every recipe together with its ingredient lines. */
    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = getReadableDatabase();
        Map<Long, Recipe> byId = new LinkedHashMap<>();

        try (Cursor c = db.query(T_RECIPES, null, null, null, null, null, C_NAME + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndexOrThrow(C_ID));
                byId.put(id, new Recipe(id,
                        c.getString(c.getColumnIndexOrThrow(C_NAME)),
                        c.getString(c.getColumnIndexOrThrow(C_STEPS))));
            }
        }

        try (Cursor c = db.query(T_RI, null, null, null, null, null, C_ID + " ASC")) {
            while (c.moveToNext()) {
                Recipe recipe = byId.get(c.getLong(c.getColumnIndexOrThrow(C_RI_RECIPE)));
                if (recipe != null) {
                    recipe.addIngredient(readRecipeIngredient(c));
                }
            }
        }
        return new ArrayList<>(byId.values());
    }

    /** Loads a single recipe (with ingredients), or null if not found. */
    public Recipe getRecipe(long recipeId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] arg = {String.valueOf(recipeId)};
        Recipe recipe = null;

        try (Cursor c = db.query(T_RECIPES, null, C_ID + "=?", arg, null, null, null)) {
            if (c.moveToFirst()) {
                recipe = new Recipe(recipeId,
                        c.getString(c.getColumnIndexOrThrow(C_NAME)),
                        c.getString(c.getColumnIndexOrThrow(C_STEPS)));
            }
        }
        if (recipe == null) return null;

        try (Cursor c = db.query(T_RI, null, C_RI_RECIPE + "=?", arg, null, null, C_ID + " ASC")) {
            while (c.moveToNext()) {
                recipe.addIngredient(readRecipeIngredient(c));
            }
        }
        return recipe;
    }

    private RecipeIngredient readRecipeIngredient(Cursor c) {
        return new RecipeIngredient(
                c.getString(c.getColumnIndexOrThrow(C_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(C_QTY)),
                c.getString(c.getColumnIndexOrThrow(C_UNIT)));
    }
}
