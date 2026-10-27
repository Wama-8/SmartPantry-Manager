package com.wamashudunetshisaulu.myapplication.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.database.DatabaseHelper;
import com.wamashudunetshisaulu.myapplication.models.PantryItem;
import com.wamashudunetshisaulu.myapplication.utils.DateUtils;
import com.wamashudunetshisaulu.myapplication.utils.IngredientCatalog;
import com.wamashudunetshisaulu.myapplication.utils.InputValidator;
import com.wamashudunetshisaulu.myapplication.utils.QuantityFormat;

import java.util.Calendar;

/**
 * Screen 2: Add/Edit Ingredient.
 * Receives EXTRA_ITEM_ID through an explicit Intent: missing = add a new item, present = edit that item.
 * The name field suggests ingredients used by the recipes, but ANY name may be typed (custom ingredient).
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    /** Units offered in the dropdown. They all map to safe conversions in UnitConverter. */
    private static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs", "slice", "clove"};

    private static final String STATE_EXPIRY = "state_expiry_iso";
    private static final String STATE_UNIT = "state_unit";

    private DatabaseHelper db;
    private IngredientCatalog catalog;
    private long editId = -1;
    private String expiryIso; // null = no expiry date
    private String unit = UNITS[0];

    private TextInputLayout nameLayout, quantityLayout;
    private MaterialAutoCompleteTextView nameInput, unitInput;
    private TextInputEditText quantityInput, expiryInput;
    private TextView nameHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        db = DatabaseHelper.getInstance(this);
        catalog = new IngredientCatalog(db.getAllRecipes());

        nameLayout = findViewById(R.id.tilName);
        quantityLayout = findViewById(R.id.tilQuantity);
        nameInput = findViewById(R.id.etName);
        nameHint = findViewById(R.id.tvNameHint);
        quantityInput = findViewById(R.id.etQuantity);
        unitInput = findViewById(R.id.actUnit);
        expiryInput = findViewById(R.id.etExpiry);
        TextView title = findViewById(R.id.tvFormTitle);

        nameInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, catalog.knownNames()));
        unitInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, UNITS));
        unitInput.setOnItemClickListener((p, v, position, id) -> unit = UNITS[position]);

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void afterTextChanged(Editable s) {
                nameLayout.setError(null);
                updateHint(s == null ? "" : s.toString());
            }
        });
        updateHint("");

        editId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (editId != -1) {
            PantryItem item = db.getPantryItem(editId);
            if (item == null) { // deleted meanwhile: fail safely
                Toast.makeText(this, "That ingredient no longer exists.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            title.setText("Edit Ingredient");
            // After a rotation Android restores typed text itself; only fill from the database on
            // first creation so the user's unsaved edits are not overwritten.
            if (savedInstanceState == null) {
                nameInput.setText(item.getName(), false);
                quantityInput.setText(QuantityFormat.number(item.getQuantity()));
                for (String u : UNITS) {
                    if (u.equalsIgnoreCase(item.getUnit())) unit = u;
                }
                expiryIso = item.getExpiryDate();
            }
        } else {
            title.setText("Add Ingredient");
        }

        // Restore the chosen unit and expiry date after rotation (the variables do not survive).
        if (savedInstanceState != null) {
            expiryIso = savedInstanceState.getString(STATE_EXPIRY);
            String savedUnit = savedInstanceState.getString(STATE_UNIT);
            if (savedUnit != null) unit = savedUnit;
        }
        unitInput.setText(unit, false);
        expiryInput.setText(expiryIso == null ? "" : expiryIso);

        expiryInput.setOnClickListener(v -> showDatePicker());
        ((TextInputLayout) findViewById(R.id.tilExpiry)).setEndIconOnClickListener(v -> showDatePicker());
        findViewById(R.id.btnClearDate).setOnClickListener(v -> {
            expiryIso = null;
            expiryInput.setText("");
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_EXPIRY, expiryIso);
        outState.putString(STATE_UNIT, unit);
    }

    /** Tells the user whether recipes use what they typed, so a custom ingredient is never a surprise. */
    private void updateHint(String typed) {
        String t = typed.trim();
        if (t.isEmpty()) {
            nameHint.setText("Suggestions come from our recipes, but you can type any ingredient.");
        } else if (catalog.isKnown(t)) {
            int n = catalog.usageCount(t);
            nameHint.setText("Used in " + n + (n == 1 ? " recipe" : " recipes") + ".");
        } else {
            nameHint.setText("Custom ingredient: saved in your pantry, but no recipe uses it.");
        }
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            expiryIso = DateUtils.toIso(year, month, day);
            expiryInput.setText(expiryIso);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Validates the form; saves only when everything is valid, otherwise shows errors next to the fields. */
    private void save() {
        String name = nameInput.getText() == null ? "" : nameInput.getText().toString().trim();
        String qtyText = quantityInput.getText() == null ? "" : quantityInput.getText().toString();

        String nameError = InputValidator.validateName(name);
        String qtyError = InputValidator.validateQuantity(qtyText);
        nameLayout.setError(nameError);
        quantityLayout.setError(qtyError);
        if (nameError != null || qtyError != null) return;

        double quantity = InputValidator.parseQuantity(qtyText);
        PantryItem item = new PantryItem(editId, name, quantity, unit, expiryIso);

        boolean ok;
        if (editId == -1) ok = db.addPantryItem(item) != -1;
        else ok = db.updatePantryItem(item);

        if (ok) {
            Toast.makeText(this, editId == -1 ? "Ingredient added" : "Ingredient updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save the ingredient. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
