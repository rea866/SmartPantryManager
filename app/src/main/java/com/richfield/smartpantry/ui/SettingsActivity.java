package com.richfield.smartpantry.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.db.DatabaseHelper;
import com.richfield.smartpantry.util.Prefs;

/**
 * Settings screen: two toggles stored in SharedPreferences, plus a "clear pantry" action.
 */
public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar(findViewById(R.id.toolbar));

        db = new DatabaseHelper(this);

        MaterialSwitch switchAlerts = findViewById(R.id.switchAlerts);
        MaterialSwitch switchIgnore = findViewById(R.id.switchIgnoreExpired);
        Button btnClear = findViewById(R.id.btnClearPantry);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Set the initial state first, then attach the listeners.
        switchAlerts.setChecked(Prefs.isExpiryAlertsEnabled(this));
        switchIgnore.setChecked(Prefs.isIgnoreExpired(this));
        switchAlerts.setOnCheckedChangeListener((button, checked) -> Prefs.setExpiryAlertsEnabled(this, checked));
        switchIgnore.setOnCheckedChangeListener((button, checked) -> Prefs.setIgnoreExpired(this, checked));

        btnClear.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_reset_title)
                .setMessage(R.string.dialog_reset_msg)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    db.deleteAllIngredients();
                    Toast.makeText(this, R.string.toast_pantry_cleared, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show());

        NavHelper.setup(this, bottomNav, R.id.nav_settings);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
