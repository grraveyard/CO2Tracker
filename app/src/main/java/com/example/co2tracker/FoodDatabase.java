package com.example.co2tracker;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class FoodDatabase {
    public static List<Food> getFoods(Context context) {
        List<Food> foods = new ArrayList<>();

        // Grains
        foods.add(new Food(
                context.getString(R.string.food_white_rice),
                context.getString(R.string.serving_cup_cooked),
                150, 120
        ));
        foods.add(new Food(
                context.getString(R.string.food_wheat_bread),
                context.getString(R.string.serving_bread_slices),
                50, 80
        ));
        foods.add(new Food(
                context.getString(R.string.food_pasta),
                context.getString(R.string.serving_cup_cooked),
                140, 140
        ));
        foods.add(new Food(
                context.getString(R.string.food_quinoa),
                context.getString(R.string.serving_cup_cooked),
                185, 90
        ));

        // Vegetables
        foods.add(new Food(
                context.getString(R.string.food_potatoes),
                context.getString(R.string.serving_medium),
                150, 40
        ));
        foods.add(new Food(
                context.getString(R.string.food_tomatoes),
                context.getString(R.string.serving_medium),
                125, 35
        ));
        foods.add(new Food(
                context.getString(R.string.food_lettuce),
                context.getString(R.string.serving_2_cups),
                100, 20
        ));
        foods.add(new Food(
                context.getString(R.string.food_carrots),
                context.getString(R.string.serving_1_cup),
                128, 25
        ));

        // Proteins
        foods.add(new Food(
                context.getString(R.string.food_chicken),
                context.getString(R.string.serving_medium_piece),
                120, 650
        ));
        foods.add(new Food(
                context.getString(R.string.food_beef),
                context.getString(R.string.serving_medium_piece),
                150, 2200
        ));
        foods.add(new Food(
                context.getString(R.string.food_tofu),
                context.getString(R.string.serving_half_cup),
                124, 120
        ));
        foods.add(new Food(
                context.getString(R.string.food_eggs),
                context.getString(R.string.serving_2_large),
                100, 200
        ));

        return foods;
    }
}