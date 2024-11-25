// app/src/main/java/com/example/co2tracker/IngredientItem.java
package com.example.co2tracker;

public class IngredientItem {
    private Food selectedFood;
    private int servings = 1;

    public Food getSelectedFood() { return selectedFood; }
    public void setSelectedFood(Food food) { this.selectedFood = food; }
    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }
}