package com.example.co2tracker;

public class Task {
    private final String title;
    private final String description;
    private final int points;
    private final double co2Savings;
    private boolean completed;

    public Task(String title, String description, int points, double co2Savings) {
        this.title = title;
        this.description = description;
        this.points = points;
        this.co2Savings = co2Savings;
        this.completed = false;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPoints() { return points; }
    public double getCo2Savings() { return co2Savings; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}