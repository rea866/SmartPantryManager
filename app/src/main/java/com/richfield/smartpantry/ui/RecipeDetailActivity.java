package com.richfield.smartpantry.ui;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.db.DatabaseHelper;
import com.richfield.smartpantry.logic.NameUtils;
import com.richfield.smartpantry.logic.UnitConverter;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

/**
 * Shows the full ingredient list and method for one recipe.
 * Receives the recipe id (and optionally the missing ingredient) through Intent extras.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";
    public static final String EXTRA_MISSING = "missing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        String missing = getIntent().getStringExtra(EXTRA_MISSING);

        DatabaseHelper db = new DatabaseHelper(this);
        Recipe recipe = db.getRecipe(recipeId);
        db.close();

        if (recipe == null) {
            Toast.makeText(this, R.string.recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSupportActionBar().setTitle(recipe.getName());

        // Status line: everything present, or which ingredient is missing.
        TextView tvStatus = findViewById(R.id.tvStatus);
        if (missing == null) {
            tvStatus.setText(R.string.status_all_present);
            tvStatus.setTextColor(getColor(R.color.green_primary));
        } else {
            tvStatus.setText(getString(R.string.info_missing, NameUtils.title(missing)));
            tvStatus.setTextColor(getColor(R.color.warn_orange));
        }

        // Ingredient list (built in code because the number of lines varies per recipe).
        LinearLayout list = findViewById(R.id.llIngredients);
        for (RecipeIngredient ri : recipe.getIngredients()) {
            TextView line = new TextView(this);
            line.setText(describe(ri));
            line.setTextSize(16);
            line.setPadding(0, 4, 0, 4);
            list.addView(line);
        }

        ((TextView) findViewById(R.id.tvSteps)).setText(recipe.getSteps());
    }

    /** e.g. "3 x Egg", "150 g Flour", "Salt (to taste)". */
    private String describe(RecipeIngredient ri) {
        String name = NameUtils.title(ri.getName());
        if (ri.getQuantity() <= 0) {
            return "\u2022 " + name + " " + getString(R.string.to_taste);
        }
        String qty = UnitConverter.formatQuantity(ri.getQuantity());
        if ("pcs".equals(UnitConverter.canonical(ri.getUnit()))) {
            return "\u2022 " + qty + " \u00D7 " + name;
        }
        return "\u2022 " + qty + " " + ri.getUnit() + " " + name;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
