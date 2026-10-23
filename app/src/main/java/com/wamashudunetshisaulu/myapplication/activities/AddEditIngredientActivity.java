package com.wamashudunetshisaulu.myapplication.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.database.DatabaseHelper;
import com.wamashudunetshisaulu.myapplication.models.PantryItem;
import com.wamashudunetshisaulu.myapplication.utils.DateUtils;
import com.wamashudunetshisaulu.myapplication.utils.InputValidator;
import com.wamashudunetshisaulu.myapplication.utils.QuantityFormat;

import java.util.Calendar;

/**
 * Screen 2: Add/Edit Ingredient.
 * Receives EXTRA_ITEM_ID through an explicit Intent: missing = add a new item, present = edit that item.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    /** Units offered in the spinner. They all map to safe conversions in UnitConverter. */
    private static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs", "slice", "clove"};

    private DatabaseHelper db;
    private long editId = -1;
    private String expiryIso; // null = no expiry date

    private TextInputLayout nameLayout, quantityLayout;
    private TextInputEditText nameInput, quantityInput, expiryInput;
    private Spinner unitSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        db = DatabaseHelper.getInstance(this);

        nameLayout = findViewById(R.id.tilName);
        quantityLayout = findViewById(R.id.tilQuantity);
        nameInput = findViewById(R.id.etName);
        quantityInput = findViewById(R.id.etQuantity);
        expiryInput = findViewById(R.id.etExpiry);
        unitSpinner = findViewById(R.id.spUnit);
        TextView title = findViewById(R.id.tvFormTitle);

        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UNITS);
        unitSpinner.setAdapter(unitAdapter);

        editId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (editId != -1) {
            PantryItem item = db.getPantryItem(editId);
            if (item == null) { // deleted meanwhile: fail safely
                Toast.makeText(this, "That ingredient no longer exists.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            title.setText("Edit Ingredient");
            nameInput.setText(item.getName());
            quantityInput.setText(QuantityFormat.number(item.getQuantity()));
            for (int i = 0; i < UNITS.length; i++) {
                if (UNITS[i].equalsIgnoreCase(item.getUnit())) unitSpinner.setSelection(i);
            }
            expiryIso = item.getExpiryDate();
            expiryInput.setText(expiryIso == null ? "" : expiryIso);
        } else {
            title.setText("Add Ingredient");
        }

        expiryInput.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btnClearDate).setOnClickListener(v -> {
            expiryIso = null;
            expiryInput.setText("");
        });
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
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
        String unit = (String) unitSpinner.getSelectedItem();
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
