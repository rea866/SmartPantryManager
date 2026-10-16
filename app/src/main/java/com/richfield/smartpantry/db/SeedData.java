package com.richfield.smartpantry.db;

import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * The built-in recipe collection that is inserted into the database on first run.
 * Kept as plain Java objects (no Android classes) so it can also be used in unit tests.
 *
 * Units: pcs, g, ml, tbsp. Staples such as salt and oil are "to taste" (quantity 0),
 * meaning they only need to be present in the pantry.
 */
public final class SeedData {

    private SeedData() { }

    public static List<Recipe> getRecipes() {
        List<Recipe> list = new ArrayList<>();

        list.add(recipe("Tomato Omelette",
                "1. Dice the tomato.\n2. Beat the eggs with a pinch of salt.\n3. Melt the butter in a pan over medium heat.\n"
                        + "4. Pour in the eggs, scatter the tomato on top and cook until just set.\n5. Fold in half and serve hot.",
                ing("egg", 3, "pcs"), ing("tomato", 1, "pcs"), ing("butter", 10, "g"), taste("salt")));

        list.add(recipe("Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Whisk the eggs with a pinch of salt.\n3. Melt the butter in a pan on low heat.\n"
                        + "4. Add the eggs and stir gently until soft and creamy.\n5. Spoon over the toast.",
                ing("egg", 2, "pcs"), ing("bread", 2, "pcs"), ing("butter", 10, "g"), taste("salt")));

        list.add(recipe("Plain Pancakes",
                "1. Whisk the flour, sugar, milk and egg into a smooth batter.\n2. Rest the batter for 10 minutes.\n"
                        + "3. Melt a little butter in a pan.\n4. Pour in a ladle of batter and cook until bubbles appear.\n5. Flip and cook the other side until golden.",
                ing("flour", 150, "g"), ing("milk", 250, "ml"), ing("egg", 1, "pcs"), ing("sugar", 20, "g"), ing("butter", 10, "g")));

        list.add(recipe("Egg Fried Rice",
                "1. Dice the onion and grate the carrot.\n2. Heat oil in a pan and fry the onion and carrot for 3 minutes.\n"
                        + "3. Push to the side, scramble the eggs in the pan.\n4. Add the cooked rice and soy sauce.\n5. Stir-fry for 4 minutes and serve.",
                ing("rice", 150, "g"), ing("egg", 2, "pcs"), ing("onion", 1, "pcs"), ing("carrot", 1, "pcs"),
                ing("soy sauce", 2, "tbsp"), taste("oil")));

        list.add(recipe("Cheese Toastie",
                "1. Butter one side of each slice of bread.\n2. Place the cheese between the slices, butter side out.\n"
                        + "3. Toast in a pan for 3 minutes per side until golden and melted.",
                ing("bread", 2, "pcs"), ing("cheese", 40, "g"), ing("butter", 10, "g")));

        list.add(recipe("Tomato Pasta",
                "1. Boil the pasta in salted water until tender, then drain.\n2. Fry the chopped onion and garlic in oil until soft.\n"
                        + "3. Add the chopped tomatoes and simmer for 10 minutes.\n4. Toss the pasta through the sauce and serve.",
                ing("pasta", 200, "g"), ing("tomato", 3, "pcs"), ing("onion", 1, "pcs"), ing("garlic", 2, "pcs"),
                taste("oil"), taste("salt")));

        list.add(recipe("Buttery Mashed Potato",
                "1. Peel and chop the potatoes.\n2. Boil in salted water for 15-20 minutes until soft.\n3. Drain and mash.\n"
                        + "4. Stir in the butter and warm milk until smooth.",
                ing("potato", 500, "g"), ing("butter", 30, "g"), ing("milk", 100, "ml"), taste("salt")));

        list.add(recipe("Hearty Vegetable Soup",
                "1. Chop all the vegetables into small cubes.\n2. Fry the onion and garlic in oil for 3 minutes.\n"
                        + "3. Add the potato, carrot and 750 ml of water.\n4. Simmer for 25 minutes, season with salt and serve.",
                ing("potato", 2, "pcs"), ing("carrot", 2, "pcs"), ing("onion", 1, "pcs"), ing("garlic", 1, "pcs"),
                taste("oil"), taste("salt")));

        list.add(recipe("Chicken Rice Bowl",
                "1. Cook the rice according to the packet.\n2. Slice the chicken and onion.\n3. Fry the chicken in oil until golden.\n"
                        + "4. Add the onion and soy sauce and cook for 5 minutes.\n5. Serve over the rice.",
                ing("chicken breast", 300, "g"), ing("rice", 150, "g"), ing("onion", 1, "pcs"),
                ing("soy sauce", 2, "tbsp"), taste("oil")));

        list.add(recipe("Tuna Mayo Sandwich",
                "1. Drain the tuna.\n2. Mix the tuna with the mayonnaise.\n3. Spread on one slice of bread, top with the other and cut in half.",
                ing("tuna", 1, "pcs"), ing("mayonnaise", 2, "tbsp"), ing("bread", 2, "pcs")));

        list.add(recipe("Banana Oat Porridge",
                "1. Simmer the oats and milk in a pot for 5 minutes, stirring.\n2. Slice the banana.\n"
                        + "3. Pour the porridge into a bowl, top with banana and drizzle with honey.",
                ing("oats", 50, "g"), ing("milk", 250, "ml"), ing("banana", 1, "pcs"), ing("honey", 1, "tbsp")));

        list.add(recipe("Peanut Butter Banana Toast",
                "1. Toast the bread.\n2. Spread the peanut butter on both slices.\n3. Top with sliced banana.",
                ing("bread", 2, "pcs"), ing("peanut butter", 2, "tbsp"), ing("banana", 1, "pcs")));

        list.add(recipe("Avocado Toast",
                "1. Toast the bread.\n2. Mash the avocado with a squeeze of lemon juice and salt.\n3. Spread on the toast and serve.",
                ing("avocado", 1, "pcs"), ing("bread", 2, "pcs"), ing("lemon", 1, "pcs"), taste("salt")));

        list.add(recipe("Cheese Omelette",
                "1. Beat the eggs with a pinch of salt.\n2. Melt the butter in a pan.\n3. Pour in the eggs and cook until nearly set.\n"
                        + "4. Sprinkle the grated cheese on one half, fold over and serve.",
                ing("egg", 3, "pcs"), ing("cheese", 40, "g"), ing("butter", 10, "g"), taste("salt")));

        list.add(recipe("Garlic Butter Rice",
                "1. Cook the rice and keep it warm.\n2. Melt the butter in a pan and fry the minced garlic for 1 minute.\n"
                        + "3. Stir in the rice, season with salt and serve.",
                ing("rice", 150, "g"), ing("butter", 20, "g"), ing("garlic", 2, "pcs"), taste("salt")));

        list.add(recipe("Quick Bean Chilli",
                "1. Fry the chopped onion and garlic in oil.\n2. Add the chilli powder and cook for 30 seconds.\n"
                        + "3. Add the chopped tomatoes and beans.\n4. Simmer for 20 minutes and serve.",
                ing("kidney beans", 400, "g"), ing("onion", 1, "pcs"), ing("tomato", 3, "pcs"), ing("garlic", 2, "pcs"),
                taste("chilli powder"), taste("oil")));

        list.add(recipe("Macaroni Cheese",
                "1. Boil the macaroni until tender and drain.\n2. Melt the butter, stir in the flour and cook for 1 minute.\n"
                        + "3. Slowly whisk in the milk until thick.\n4. Stir in the grated cheese, then the macaroni. Serve.",
                ing("macaroni", 200, "g"), ing("cheese", 100, "g"), ing("milk", 250, "ml"),
                ing("butter", 20, "g"), ing("flour", 20, "g")));

        list.add(recipe("French Toast",
                "1. Whisk the eggs, milk and sugar in a shallow dish.\n2. Dip each slice of bread until soaked.\n"
                        + "3. Fry in butter for 2-3 minutes per side until golden.",
                ing("bread", 4, "pcs"), ing("egg", 2, "pcs"), ing("milk", 100, "ml"), ing("sugar", 10, "g"), ing("butter", 10, "g")));

        list.add(recipe("Red Lentil Curry",
                "1. Fry the chopped onion and garlic in oil.\n2. Stir in the curry powder.\n3. Add the lentils, chopped tomatoes and 500 ml water.\n"
                        + "4. Simmer for 20 minutes until the lentils are soft.",
                ing("lentils", 200, "g"), ing("onion", 1, "pcs"), ing("tomato", 2, "pcs"), ing("garlic", 2, "pcs"),
                taste("curry powder"), taste("oil")));

        list.add(recipe("Loaded Baked Potato",
                "1. Prick the potatoes and bake at 200 C for 60 minutes.\n2. Cut open and fluff the insides with a fork.\n"
                        + "3. Add the butter, top with grated cheese and salt, and return to the oven for 5 minutes.",
                ing("potato", 2, "pcs"), ing("cheese", 60, "g"), ing("butter", 15, "g"), taste("salt")));

        list.add(recipe("Banana Yoghurt Bowl",
                "1. Spoon the yoghurt into a bowl.\n2. Slice the banana on top.\n3. Sprinkle with oats and drizzle with honey.",
                ing("yoghurt", 150, "g"), ing("banana", 1, "pcs"), ing("honey", 1, "tbsp"), ing("oats", 30, "g")));

        return list;
    }

    private static Recipe recipe(String name, String steps, RecipeIngredient... ingredients) {
        Recipe r = new Recipe(-1, name, steps);
        for (RecipeIngredient i : ingredients) {
            r.addIngredient(i);
        }
        return r;
    }

    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    /** An "to taste" ingredient: only needs to be present in the pantry. */
    private static RecipeIngredient taste(String name) {
        return new RecipeIngredient(name, 0, "");
    }
}
