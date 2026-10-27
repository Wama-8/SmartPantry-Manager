package com.wamashudunetshisaulu.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.adapters.RecipeAdapter;
import com.wamashudunetshisaulu.myapplication.database.DatabaseHelper;
import com.wamashudunetshisaulu.myapplication.matching.RecipeMatcher;
import com.wamashudunetshisaulu.myapplication.models.Recipe;
import com.wamashudunetshisaulu.myapplication.utils.TextFormat;

import java.util.List;

/**
 * Screen 3: Suggested Recipes. Candidates come ONLY from the recipe database (db.getAllRecipes());
 * a recipe is shown only when RecipeMatcher says every ingredient is in the pantry in a sufficient
 * quantity. Custom pantry ingredients are never turned into recipes.
 */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private View emptyView;
    private TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        db = DatabaseHelper.getInstance(this);
        emptyView = findViewById(R.id.llNoRecipes);
        subtitle = findViewById(R.id.tvRecipesSubtitle);

        RecyclerView list = findViewById(R.id.rvRecipes);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this);
        list.setAdapter(adapter);

        findViewById(R.id.btnEmptyPantry).setOnClickListener(v -> BottomNav.openPantry(this));
        BottomNav.setup(this, (BottomNavigationView) findViewById(R.id.bottomNav), R.id.nav_recipes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Recipe> matches = RecipeMatcher.findMatches(db.getAllRecipes(), db.getAllPantryItems());
        adapter.setData(matches);
        emptyView.setVisibility(matches.isEmpty() ? View.VISIBLE : View.GONE);
        subtitle.setText(matches.isEmpty()
                ? "From our recipe collection, using only what you have"
                : TextFormat.count(matches.size(), "recipe", "recipes") + " you can cook now, from our recipe collection");
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent i = new Intent(this, RecipeDetailActivity.class);
        i.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(i);
    }
}
