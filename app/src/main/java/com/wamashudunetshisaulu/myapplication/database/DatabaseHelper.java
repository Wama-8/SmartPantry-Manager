package com.wamashudunetshisaulu.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wamashudunetshisaulu.myapplication.models.PantryItem;
import com.wamashudunetshisaulu.myapplication.models.Recipe;
import com.wamashudunetshisaulu.myapplication.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite storage for pantry items and recipes.
 * onCreate runs only when the database file is first created, so the seed recipes
 * are inserted exactly once and never duplicated when the app is reopened.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "smartpantry.db";
    public static final int DB_VERSION = 1;

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) instance = new DatabaseHelper(context.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // needed for ON DELETE CASCADE
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_item ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "quantity REAL NOT NULL,"
                + "unit TEXT NOT NULL,"
                + "expiry_date TEXT)");
        db.execSQL("CREATE TABLE recipe ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "instructions TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredient ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "recipe_id INTEGER NOT NULL,"
                + "name TEXT NOT NULL,"
                + "quantity REAL NOT NULL,"
                + "unit TEXT NOT NULL,"
                + "FOREIGN KEY (recipe_id) REFERENCES recipe(id) ON DELETE CASCADE)");
        for (Recipe r : SeedRecipes.build()) insertRecipe(db, r);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 only: no migrations needed yet. Add them here if the schema changes.
    }

    // ---------------------------------------------------------------- recipes

    private static void insertRecipe(SQLiteDatabase db, Recipe r) {
        ContentValues cv = new ContentValues();
        cv.put("name", r.getName());
        cv.put("instructions", r.getInstructions());
        long recipeId = db.insertOrThrow("recipe", null, cv);
        for (RecipeIngredient i : r.getIngredients()) {
            ContentValues ic = new ContentValues();
            ic.put("recipe_id", recipeId);
            ic.put("name", i.getName());
            ic.put("quantity", i.getQuantity());
            ic.put("unit", i.getUnit());
            db.insertOrThrow("recipe_ingredient", null, ic);
        }
    }

    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = getReadableDatabase();
        List<Recipe> list = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT id, name, instructions FROM recipe ORDER BY name COLLATE NOCASE", null)) {
            while (c.moveToNext()) {
                Recipe r = new Recipe(c.getLong(0), c.getString(1), c.getString(2));
                loadIngredients(db, r);
                list.add(r);
            }
        }
        return list;
    }

    /** Returns null when no recipe has this id. */
    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT id, name, instructions FROM recipe WHERE id = ?",
                new String[]{String.valueOf(id)})) {
            if (!c.moveToFirst()) return null;
            Recipe r = new Recipe(c.getLong(0), c.getString(1), c.getString(2));
            loadIngredients(db, r);
            return r;
        }
    }

    public int countRecipes() {
        try (Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM recipe", null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    private void loadIngredients(SQLiteDatabase db, Recipe r) {
        try (Cursor c = db.rawQuery("SELECT id, recipe_id, name, quantity, unit FROM recipe_ingredient "
                + "WHERE recipe_id = ? ORDER BY id", new String[]{String.valueOf(r.getId())})) {
            while (c.moveToNext()) {
                r.getIngredients().add(new RecipeIngredient(
                        c.getLong(0), c.getLong(1), c.getString(2), c.getDouble(3), c.getString(4)));
            }
        }
    }

    // ----------------------------------------------------------- pantry CRUD

    /** Returns the new row id, or -1 if the insert failed. */
    public long addPantryItem(PantryItem item) {
        return getWritableDatabase().insert("pantry_item", null, toValues(item));
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, name, quantity, unit, expiry_date FROM pantry_item ORDER BY name COLLATE NOCASE", null)) {
            while (c.moveToNext()) list.add(fromCursor(c));
        }
        return list;
    }

    /** Returns null when no item has this id. */
    public PantryItem getPantryItem(long id) {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, name, quantity, unit, expiry_date FROM pantry_item WHERE id = ?",
                new String[]{String.valueOf(id)})) {
            return c.moveToFirst() ? fromCursor(c) : null;
        }
    }

    public boolean updatePantryItem(PantryItem item) {
        return getWritableDatabase().update("pantry_item", toValues(item), "id = ?",
                new String[]{String.valueOf(item.getId())}) > 0;
    }

    public boolean deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry_item", "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    private static ContentValues toValues(PantryItem item) {
        ContentValues cv = new ContentValues();
        cv.put("name", item.getName());
        cv.put("quantity", item.getQuantity());
        cv.put("unit", item.getUnit());
        if (item.getExpiryDate() == null) cv.putNull("expiry_date");
        else cv.put("expiry_date", item.getExpiryDate());
        return cv;
    }

    private static PantryItem fromCursor(Cursor c) {
        return new PantryItem(c.getLong(0), c.getString(1), c.getDouble(2), c.getString(3),
                c.isNull(4) ? null : c.getString(4));
    }
}
