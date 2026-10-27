package com.wamashudunetshisaulu.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.models.Recipe;
import com.wamashudunetshisaulu.myapplication.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;

/** Lists the recipes the user can cook right now; tapping one opens its details. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface Listener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        final Recipe r = recipes.get(position);
        h.name.setText(r.getName());
        h.summary.setText("Ready \u2022 " + TextFormat.count(r.getIngredients().size(), "ingredient", "ingredients"));
        h.itemView.setOnClickListener(v -> listener.onRecipeClick(r));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, summary;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvRecipeName);
            summary = itemView.findViewById(R.id.tvRecipeSummary);
        }
    }
}
