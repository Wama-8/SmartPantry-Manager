# Smart Pantry Manager

A Java Android application that helps reduce food waste. The user records the
ingredients they currently have, and the app suggests only the recipes they can
cook **right now** with those ingredients.

**Module:** Mobile App Development 700, Practical Assignment
**Language:** Java (no Kotlin) | **Database:** SQLite | **Min SDK:** 24 | **Compile/Target SDK:** 34

Repository: https://github.com/Wama-8/SmartPantry-Manager

## Features

- **Pantry List** – shows all stored ingredients in a RecyclerView with a custom adapter. Items close to expiry are highlighted.
- **Add / Edit Ingredient** – name, quantity, unit and an optional expiry date, with validation messages next to the fields.
- **Delete** – removes an ingredient after a confirmation dialog.
- **Suggested Recipes** – lists only recipes that can be fully prepared with the current pantry.
- **Recipe Detail** – shows the recipe's ingredients with quantities and its preparation steps.
- **Settings** – choose how many days before expiry an item is highlighted (3, 7 or 14). The choice is stored in SharedPreferences.
- 20 seed recipes are inserted once, the first time the database is created.

## Strict recipe matching

A recipe appears in Suggested Recipes **only** when:

1. every required ingredient is in the pantry,
2. the available quantity of each ingredient is enough, after unit conversion, and
3. ingredient names match after normalisation (case, extra spaces, simple plurals such as "tomatoes" and "tomato").

If one ingredient is missing or short, the recipe is excluded. There are no partial matches.

Unit conversion is deliberately conservative (`UnitConverter.java`):

- Mass is converted to grams (g, kg, oz, lb).
- Volume is converted to millilitres (ml, l, tsp, tbsp, cup).
- Counts are plain items (pcs and similar).
- Mass, volume and counts are **never** converted into each other.
- A unit the converter does not recognise (for example `slice` or `clove`) is only compared with the identical unit.

The logic lives in `matching/RecipeMatcher.java` and does not depend on Android, so it can be unit tested.

## Why SQLite

- Pantry items and recipes are structured, related data: a recipe has many ingredient lines.
- SQLite is built into Android, works offline and needs no server or extra library.
- The data must still be there after the app is closed and reopened, which a local database provides.
- Foreign keys (`ON DELETE CASCADE`) keep recipe ingredients linked to their recipe.
- `SQLiteOpenHelper.onCreate` runs only when the database file is first created, so the seed recipes are never duplicated.

## Schema

| Table | Columns |
|-------|---------|
| `pantry_item` | id (PK), name, quantity, unit, expiry_date (optional, `yyyy-MM-dd`) |
| `recipe` | id (PK), name, instructions |
| `recipe_ingredient` | id (PK), recipe_id (FK to recipe), name, quantity, unit |

## Project structure

```
app/src/main/java/com/wamashudunetshisaulu/myapplication/
  activities/   MainActivity, AddEditIngredientActivity, SuggestedRecipesActivity,
                RecipeDetailActivity, SettingsActivity
  adapters/     PantryAdapter, RecipeAdapter
  database/     DatabaseHelper, SeedRecipes
  matching/     RecipeMatcher, UnitConverter
  models/       PantryItem, Recipe, RecipeIngredient
  utils/        IngredientNormalizer, InputValidator, DateUtils, QuantityFormat, AppPrefs
app/src/main/res/layout/   XML layouts for each screen and list item
app/src/test/              unit tests (matching and utilities)
```

## Setup

1. Install [Android Studio](https://developer.android.com/studio) (includes the JDK and Android SDK).
2. Clone the repository:
   ```
   git clone https://github.com/Wama-8/SmartPantry-Manager.git
   ```
3. In Android Studio choose **File > Open** and select the cloned folder.
4. Wait for **Gradle sync** to finish. If Android Studio reports *"SDK location not found"*, open
   **File > Project Structure > SDK Location** and select your Android SDK folder, or let Android Studio
   create `local.properties` for you. `local.properties` is machine specific and is **not** committed to Git.
5. Install the SDK Platform for API 34 if Android Studio asks for it (**Tools > SDK Manager**).

## Run

1. Create an emulator in **Tools > Device Manager**, or connect a phone with USB debugging enabled.
2. Press **Run** (the green triangle) with the `app` configuration selected.

### Run the unit tests

Right-click `app/src/test` in the Project view and choose **Run Tests**, or run `./gradlew test` in the terminal
(`gradlew test` in PowerShell on Windows).

### Quick demo

1. Add: Egg 6 pcs, Milk 500 ml, Butter 100 g, Bread 4 slice, Cheese 100 g.
2. Open **Suggested Recipes**: recipes that can be fully made from these ingredients appear.
3. Delete Cheese, or lower the Butter quantity, and open Suggested Recipes again: recipes that need more than you have disappear.

## Out of scope

No Google Maps, GPS, shopping-location features, payments or Google Play publishing.