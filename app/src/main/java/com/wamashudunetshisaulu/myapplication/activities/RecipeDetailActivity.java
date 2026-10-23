package com.wamashudunetshisaulu.myapplication.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.database.DatabaseHelper;
import com.wamashudunetshisaulu.myapplication.models.Recipe;
import com.wamashudunetshisaulu.myapplication.models.RecipeIngredient;
import com.wamashudunetshisaulu.myapplication.utils.QuantityFormat;

/** Screen 4: Recipe Detail. Receives EXTRA_RECIPE_ID through an explicit Intent. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        long id = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = id == -1 ? null : DatabaseHelper.getInstance(this).getRecipe(id);
        if (recipe == null) {
            Toast.makeText(this, "Recipe not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tvDetailName)).setText(recipe.getName());

        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient i : recipe.getIngredients()) {
            sb.append("• ").append(i.getName()).append(": ")
              .append(QuantityFormat.amount(i.getQuantity(), i.getUnit())).append('\n');
        }
        ((TextView) findViewById(R.id.tvDetailIngredients)).setText(sb.toString().trim());
        ((TextView) findViewById(R.id.tvDetailInstructions)).setText(recipe.getInstructions());
    }
}
