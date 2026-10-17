package com.richfield.smartpantry.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.db.DatabaseHelper;
import com.richfield.smartpantry.logic.ExpiryUtils;
import com.richfield.smartpantry.logic.UnitConverter;
import com.richfield.smartpantry.model.Ingredient;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Form used both to add a new ingredient and to edit an existing one.
 * The Intent extra EXTRA_INGREDIENT_ID tells it which mode to use (-1 = add).
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_INGREDIENT_ID = "ingredient_id";

    private DatabaseHelper db;
    private long editId = -1;
    private String selectedExpiry = null; // yyyy-MM-dd or null

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputEditText etName;
    private TextInputEditText etQuantity;
    private TextInputEditText etExpiry;
    private Spinner spUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnClearDate = findViewById(R.id.btnClearDate);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, UnitConverter.UNITS);
        spUnit.setAdapter(unitAdapter);

        // Edit mode: pre-fill the form with the stored values.
        editId = getIntent().getLongExtra(EXTRA_INGREDIENT_ID, -1);
        if (editId != -1) {
            Ingredient existing = db.getIngredient(editId);
            if (existing == null) { // deleted in the meantime
                finish();
                return;
            }
            getSupportActionBar().setTitle(R.string.title_edit);
            etName.setText(existing.getName());
            etQuantity.setText(UnitConverter.formatQuantity(existing.getQuantity()));
            selectUnit(existing.getUnit());
            if (ExpiryUtils.parse(existing.getExpiryDate()) != null) {
                selectedExpiry = existing.getExpiryDate();
                etExpiry.setText(selectedExpiry);
            }
        } else {
            getSupportActionBar().setTitle(R.string.title_add);
        }

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnClearDate.setOnClickListener(v -> {
            selectedExpiry = null;
            etExpiry.setText("");
        });
        btnSave.setOnClickListener(v -> saveIngredient());
    }

    private void selectUnit(String unit) {
        String canonical = UnitConverter.canonical(unit);
        for (int i = 0; i < UnitConverter.UNITS.length; i++) {
            if (UnitConverter.UNITS[i].equals(canonical)) {
                spUnit.setSelection(i);
                return;
            }
        }
    }

    private void showDatePicker() {
        LocalDate base = ExpiryUtils.parse(selectedExpiry);
        if (base == null) base = LocalDate.now();

        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedExpiry = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
            etExpiry.setText(selectedExpiry);
        }, base.getYear(), base.getMonthValue() - 1, base.getDayOfMonth()).show();
    }

    /** Validates the form; if everything is fine, inserts or updates the row and closes the screen. */
    private void saveIngredient() {
        layoutName.setError(null);
        layoutQuantity.setError(null);
        boolean valid = true;

        // --- validate name ---
        String name = String.valueOf(etName.getText()).trim();
        if (name.isEmpty()) {
            layoutName.setError(getString(R.string.err_name_required));
            valid = false;
        } else if (!name.matches(".*\\p{L}.*")) {
            layoutName.setError(getString(R.string.err_name_invalid));
            valid = false;
        }

        // --- validate quantity ---
        String qtyText = String.valueOf(etQuantity.getText()).trim();
        double quantity = 0;
        if (qtyText.isEmpty()) {
            layoutQuantity.setError(getString(R.string.err_qty_required));
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(qtyText);
            } catch (NumberFormatException e) {
                quantity = -1;
            }
            if (!(quantity > 0) || Double.isInfinite(quantity)) {
                layoutQuantity.setError(getString(R.string.err_qty_invalid));
                valid = false;
            }
        }

        if (!valid) return;

        String unit = (String) spUnit.getSelectedItem();
        Ingredient ingredient = new Ingredient(editId, name, quantity, unit, selectedExpiry);

        if (editId == -1) {
            long newId = db.addIngredient(ingredient);
            if (newId == -1) {
                Toast.makeText(this, R.string.toast_save_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.toast_added, Toast.LENGTH_SHORT).show();
        } else {
            db.updateIngredient(ingredient);
            Toast.makeText(this, R.string.toast_updated, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
