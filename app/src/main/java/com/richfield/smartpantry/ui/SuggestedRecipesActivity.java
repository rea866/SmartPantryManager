package com.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.db.DatabaseHelper;
import com.richfield.smartpantry.logic.RecipeMatcher;
import com.richfield.smartpantry.util.Prefs;

import java.util.List;

/**
 * Runs the strict-matching logic against the current pantry and lists the results.
 * "Ready to cook" = strict matches only. "Almost there" (bonus) = missing exactly one
 * ingredient, kept in a clearly separate list.
 */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private RecyclerView recycler;
    private TextView tvEmpty;
    private Button btnReady;
    private Button btnAlmost;
    private MaterialButtonToggleGroup toggleGroup;
    private BottomNavigationView bottomNav;

    private RecipeMatcher.Output output; // latest matching result

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        setSupportActionBar(findViewById(R.id.toolbar));

        db = new DatabaseHelper(this);

        recycler = findViewById(R.id.recyclerRecipes);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnReady = findViewById(R.id.btnReady);
        btnAlmost = findViewById(R.id.btnAlmost);
        toggleGroup = findViewById(R.id.toggleGroup);
        bottomNav = findViewById(R.id.bottomNav);

        adapter = new RecipeAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked && output != null) showCurrentList();
        });
        toggleGroup.check(R.id.btnReady);

        NavHelper.setup(this, bottomNav, R.id.nav_recipes);
    }

    /** Re-run the matching each time the screen is shown, so it reflects the latest pantry. */
    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.getMenu().findItem(R.id.nav_recipes).setChecked(true);
        output = RecipeMatcher.match(db.getAllRecipes(), db.getAllIngredients(), Prefs.isIgnoreExpired(this));

        btnReady.setText(getString(R.string.tab_ready, output.getReady().size()));
        btnAlmost.setText(getString(R.string.tab_almost, output.getAlmostThere().size()));
        showCurrentList();
    }

    private void showCurrentList() {
        boolean almost = toggleGroup.getCheckedButtonId() == R.id.btnAlmost;
        List<RecipeMatcher.Result> list = almost ? output.getAlmostThere() : output.getReady();
        adapter.setData(list, almost);

        tvEmpty.setText(almost ? R.string.empty_almost : R.string.empty_suggestions);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRecipeClicked(RecipeMatcher.Result result) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, result.getRecipe().getId());
        if (!result.getMissing().isEmpty()) {
            intent.putExtra(RecipeDetailActivity.EXTRA_MISSING, String.join(", ", result.getMissing()));
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
