package com.wamashudunetshisaulu.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.adapters.PantryAdapter;
import com.wamashudunetshisaulu.myapplication.database.DatabaseHelper;
import com.wamashudunetshisaulu.myapplication.models.PantryItem;
import com.wamashudunetshisaulu.myapplication.utils.AppPrefs;
import com.wamashudunetshisaulu.myapplication.utils.DateUtils;
import com.wamashudunetshisaulu.myapplication.utils.IngredientCatalog;
import com.wamashudunetshisaulu.myapplication.utils.TextFormat;

import java.util.Date;
import java.util.List;

/** Screen 1: Pantry List. Root of the bottom navigation. */
public class MainActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private View emptyView;
    private TextView summary;
    private IngredientCatalog catalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DatabaseHelper.getInstance(this);
        catalog = new IngredientCatalog(db.getAllRecipes());
        emptyView = findViewById(R.id.llEmpty);
        summary = findViewById(R.id.tvSummary);

        RecyclerView list = findViewById(R.id.rvPantry);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        list.setAdapter(adapter);

        // Swipe a card away to delete it; Snackbar offers Undo.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
                PantryItem item = adapter.getItem(vh.getAdapterPosition());
                if (item == null) { refresh(); return; }
                onDelete(item);
            }
        }).attachToRecyclerView(list);

        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class))); // no id = add mode
        findViewById(R.id.btnEmptyAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));

        BottomNav.setup(this, (BottomNavigationView) findViewById(R.id.bottomNav), R.id.nav_pantry);
    }

    /** Reload every time we come back, so edits, deletes and Settings changes show immediately. */
    @Override
    protected void onResume() {
        super.onResume();
        ((BottomNavigationView) findViewById(R.id.bottomNav)).getMenu().findItem(R.id.nav_pantry).setChecked(true);
        refresh();
    }

    private void refresh() {
        List<PantryItem> items = db.getAllPantryItems();
        int warn = AppPrefs.getExpiryWarningDays(this);
        adapter.setData(items, warn, catalog);
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);

        if (items.isEmpty()) {
            summary.setText("Cook with what you already have");
        } else {
            int attention = 0;
            Date now = new Date();
            for (PantryItem i : items) {
                Integer d = DateUtils.daysUntil(i.getExpiryDate(), now);
                if (d != null && d <= warn) attention++;
            }
            String text = TextFormat.count(items.size(), "ingredient", "ingredients");
            if (attention > 0) text += " · " + attention + " need attention";
            summary.setText(text);
        }
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent i = new Intent(this, AddEditIngredientActivity.class);
        i.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(i);
    }

    /** Deletes immediately, then offers Undo (re-adds the same values). */
    @Override
    public void onDelete(final PantryItem item) {
        if (!db.deletePantryItem(item.getId())) {
            Snackbar.make(findViewById(R.id.root), "Could not delete the item.", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.bottomNav).show();
            refresh();
            return;
        }
        refresh();
        Snackbar.make(findViewById(R.id.root), TextFormat.capitalize(item.getName()) + " removed", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.bottomNav)
                .setAction("Undo", v -> {
                    db.addPantryItem(new PantryItem(-1, item.getName(), item.getQuantity(),
                            item.getUnit(), item.getExpiryDate()));
                    refresh();
                }).show();
    }
}
