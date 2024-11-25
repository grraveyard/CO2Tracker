// app/src/main/java/com/example/co2tracker/Food.java
package com.example.co2tracker;

public class Food {
    private String name;
    private String servingDescription;
    private int servingGrams;
    private double co2;

    public Food(String name, String servingDescription, int servingGrams, double co2) {
        this.name = name;
        this.servingDescription = servingDescription;
        this.servingGrams = servingGrams;
        this.co2 = co2;
    }

    public String getName() { return name; }
    public String getServingDescription() { return servingDescription; }
    public int getServingGrams() { return servingGrams; }
    public double getCo2() { return co2; }

    @Override
    public String toString() {
        return name;
    }
}