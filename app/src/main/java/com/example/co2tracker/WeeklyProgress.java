package com.example.co2tracker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weekly_progress")
public class WeeklyProgress {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userId;
    private long weekStartDate;
    private double currentProgress;
    private double weeklyGoal;

    public WeeklyProgress(String userId, long weekStartDate, double currentProgress, double weeklyGoal) {
        this.userId = userId;
        this.weekStartDate = weekStartDate;
        this.currentProgress = currentProgress;
        this.weeklyGoal = weeklyGoal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public long getWeekStartDate() { return weekStartDate; }
    public void setWeekStartDate(long weekStartDate) { this.weekStartDate = weekStartDate; }
    public double getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(double currentProgress) { this.currentProgress = currentProgress; }
    public double getWeeklyGoal() { return weeklyGoal; }
    public void setWeeklyGoal(double weeklyGoal) { this.weeklyGoal = weeklyGoal; }
}