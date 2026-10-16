package com.richfield.smartpantry.ui;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.richfield.smartpantry.R;

/**
 * Wires up the bottom navigation bar shared by the three top-level screens
 * (Pantry, Recipes, Settings). Navigation between them uses explicit Intents.
 */
final class NavHelper {

    private NavHelper() { }

    static void setup(final Activity current, BottomNavigationView nav, final int selectedId) {
        nav.setSelectedItemId(selectedId); // set before the listener so it does not fire
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) return true;

            Class<?> target;
            if (id == R.id.nav_pantry) {
                target = MainActivity.class;
            } else if (id == R.id.nav_recipes) {
                target = SuggestedRecipesActivity.class;
            } else {
                target = SettingsActivity.class;
            }

            Intent intent = new Intent(current, target);
            if (target == MainActivity.class) {
                // Pantry is the home screen: go back to it instead of stacking a second copy.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
            current.startActivity(intent);
            current.overridePendingTransition(0, 0);
            if (!(current instanceof MainActivity)) {
                current.finish(); // keep the back stack short: Home -> one top-level screen
            }
            return true;
        });
    }
}
