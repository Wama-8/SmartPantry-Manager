package com.wamashudunetshisaulu.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.adapters.RecipeAdapter;
import com.wamashudunetshisaulu.myapplication.database.DatabaseHelper;
import com.wamashudunetshisaulu.myapplication.matching.RecipeMatcher;
import com.wamashudunetshisaulu.myapplication.models.Recipe;

import java.util.List;

/** Screen 3: Suggested Recipes. Shows ONLY recipes that RecipeMatcher says can be fully prepared. */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        db = DatabaseHelper.getInstance(this);
        emptyView = findViewById(R.id.tvNoRecipes);

        RecyclerView list = findViewById(R.id.rvRecipes);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this);
        list.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Recipe> matches = RecipeMatcher.findMatches(db.getAllRecipes(), db.getAllPantryItems());
        adapter.setData(matches);
        emptyView.setVisibility(matches.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent i = new Intent(this, RecipeDetailActivity.class);
        i.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(i);
    }
}
