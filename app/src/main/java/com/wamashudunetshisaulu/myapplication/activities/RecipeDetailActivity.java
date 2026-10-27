package com.wamashudunetshisaulu.myapplication.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.database.DatabaseHelper;
import com.wamashudunetshisaulu.myapplication.models.Recipe;
import com.wamashudunetshisaulu.myapplication.models.RecipeIngredient;
import com.wamashudunetshisaulu.myapplication.utils.QuantityFormat;
import com.wamashudunetshisaulu.myapplication.utils.StepParser;
import com.wamashudunetshisaulu.myapplication.utils.TextFormat;

import java.util.List;

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

        List<String> steps = StepParser.split(recipe.getInstructions());
        ((TextView) findViewById(R.id.tvDetailMeta)).setText(
                TextFormat.count(recipe.getIngredients().size(), "ingredient", "ingredients")
                        + " · " + TextFormat.count(steps.size(), "step", "steps"));

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout ingredients = findViewById(R.id.llIngredients);
        List<RecipeIngredient> list = recipe.getIngredients();
        for (int n = 0; n < list.size(); n++) {
            RecipeIngredient i = list.get(n);
            View row = inflater.inflate(R.layout.item_ingredient_row, ingredients, false);
            ((TextView) row.findViewById(R.id.tvIngName)).setText(TextFormat.capitalize(i.getName()));
            ((TextView) row.findViewById(R.id.tvIngAmount)).setText(QuantityFormat.amount(i.getQuantity(), i.getUnit()));
            if (n == list.size() - 1) row.findViewById(R.id.vDivider).setVisibility(View.GONE);
            ingredients.addView(row);
        }

        LinearLayout stepsBox = findViewById(R.id.llSteps);
        for (int n = 0; n < steps.size(); n++) {
            View row = inflater.inflate(R.layout.item_step, stepsBox, false);
            ((TextView) row.findViewById(R.id.tvStepNumber)).setText(String.valueOf(n + 1));
            ((TextView) row.findViewById(R.id.tvStepText)).setText(steps.get(n));
            stepsBox.addView(row);
        }
    }
}
