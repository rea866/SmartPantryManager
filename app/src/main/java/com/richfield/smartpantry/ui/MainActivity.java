package com.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.db.DatabaseHelper;
import com.richfield.smartpantry.logic.ExpiryUtils;
import com.richfield.smartpantry.logic.NameUtils;
import com.richfield.smartpantry.model.Ingredient;
import com.richfield.smartpantry.util.Prefs;

import java.time.LocalDate;
import java.util.List;

/**
 * Home screen: the pantry list (READ), with buttons to add (CREATE), edit (UPDATE) and delete (DELETE).
 */
public class MainActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private RecyclerView recycler;
    private TextView tvEmpty;
    private TextView tvBanner;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        db = new DatabaseHelper(this);

        recycler = findViewById(R.id.recyclerPantry);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvBanner = findViewById(R.id.tvBanner);
        bottomNav = findViewById(R.id.bottomNav);

        adapter = new PantryAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> openEditor(-1));

        NavHelper.setup(this, bottomNav, R.id.nav_pantry);
    }

    /** Reload every time the screen becomes visible so changes made elsewhere show up. */
    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.getMenu().findItem(R.id.nav_pantry).setChecked(true);
        loadPantry();
    }

    private void loadPantry() {
        List<Ingredient> items = db.getAllIngredients();
        boolean alerts = Prefs.isExpiryAlertsEnabled(this);
        adapter.setData(items, alerts);

        boolean empty = items.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);

        // Warning banner for expired / expiring-soon items.
        int count = 0;
        LocalDate today = LocalDate.now();
        for (Ingredient item : items) {
            Long days = ExpiryUtils.daysUntil(item.getExpiryDate(), today);
            if (days != null && days <= ExpiryUtils.SOON_DAYS) count++;
        }
        if (alerts && count > 0) {
            tvBanner.setText(getString(R.string.alert_banner, count));
            tvBanner.setVisibility(View.VISIBLE);
        } else {
            tvBanner.setVisibility(View.GONE);
        }
    }

    /** Opens the Add/Edit screen. An id of -1 means "add a new ingredient". */
    private void openEditor(long ingredientId) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_INGREDIENT_ID, ingredientId);
        startActivity(intent);
    }

    @Override
    public void onEdit(Ingredient item) {
        openEditor(item.getId());
    }

    @Override
    public void onDelete(final Ingredient item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_msg, NameUtils.title(item.getName())))
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    db.deleteIngredient(item.getId());
                    Toast.makeText(this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                    loadPantry();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            openEditor(-1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
