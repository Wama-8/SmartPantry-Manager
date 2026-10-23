package com.wamashudunetshisaulu.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.adapters.PantryAdapter;
import com.wamashudunetshisaulu.myapplication.database.DatabaseHelper;
import com.wamashudunetshisaulu.myapplication.models.PantryItem;
import com.wamashudunetshisaulu.myapplication.utils.AppPrefs;

import java.util.List;

/** Screen 1: Pantry List. Entry point to every other screen. */
public class MainActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DatabaseHelper.getInstance(this);
        emptyView = findViewById(R.id.tvEmpty);

        RecyclerView list = findViewById(R.id.rvPantry);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        list.setAdapter(adapter);

        ExtendedFloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class))); // no id = add mode

        findViewById(R.id.btnSuggested).setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        findViewById(R.id.btnSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    /** Reload every time we come back, so edits, deletes and Settings changes show immediately. */
    @Override
    protected void onResume() {
        super.onResume();
        List<PantryItem> items = db.getAllPantryItems();
        adapter.setData(items, AppPrefs.getExpiryWarningDays(this));
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent i = new Intent(this, AddEditIngredientActivity.class);
        i.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(i);
    }

    @Override
    public void onDelete(final PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient?")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, which) -> {
                    if (db.deletePantryItem(item.getId())) {
                        onResume();
                    } else {
                        Toast.makeText(this, "Could not delete the item.", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
