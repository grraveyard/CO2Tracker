package com.example.co2tracker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily_emissions")
public class DailyEmissions {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userId;
    private long date;
    private double foodEmissions;
    private double carEmissions;
    private double totalEmissions;
    private int dayOfWeek;

    public DailyEmissions(String userId, long date, int dayOfWeek) {
        this.userId = userId;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.foodEmissions = 0.0;
        this.carEmissions = 0.0;
        this.totalEmissions = 0.0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public double getFoodEmissions() { return foodEmissions; }
    public void setFoodEmissions(double foodEmissions) {
        this.foodEmissions = foodEmissions;
        updateTotal();
    }

    public double getCarEmissions() { return carEmissions; }
    public void setCarEmissions(double carEmissions) {
        this.carEmissions = carEmissions;
        updateTotal();
    }

    public double getTotalEmissions() { return totalEmissions; }
    public void setTotalEmissions(double totalEmissions) {
        this.totalEmissions = totalEmissions;
    }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    private void updateTotal() {
        this.totalEmissions = this.foodEmissions + this.carEmissions;
    }
}