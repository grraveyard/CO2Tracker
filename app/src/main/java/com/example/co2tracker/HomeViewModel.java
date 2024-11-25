package com.example.co2tracker;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class HomeViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final MutableLiveData<WeeklyProgress> weeklyProgress = new MutableLiveData<>();
    private final UserManager userManager;
    private String currentUserId;

    public HomeViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        userManager = new UserManager(application);
        currentUserId = userManager.getLoggedInUser();
        loadWeeklyProgress();
    }

    public void refreshUser() {
        currentUserId = userManager.getLoggedInUser();
        loadWeeklyProgress();
    }

    private void loadWeeklyProgress() {
        if (currentUserId == null || currentUserId.isEmpty()) {
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            long currentWeekStart = getStartOfWeek();
            WeeklyProgress progress = database.weeklyProgressDao().getUserWeekProgress(currentUserId, currentWeekStart);
            
            if (progress == null) {
                progress = new WeeklyProgress(currentUserId, currentWeekStart, 0.0, 35.0);
                database.weeklyProgressDao().insert(progress);
            }
            
            weeklyProgress.postValue(progress);
        });
    }

    public LiveData<WeeklyProgress> getWeeklyProgress() {
        return weeklyProgress;
    }

    public LiveData<List<DailyEmissions>> getDailyEmissions() {
        return database.dailyEmissionsDao().getCurrentWeekEmissions(currentUserId, getStartOfWeek());
    }

    public void updateEmissions(double emissions, boolean isFood) {
        if (currentUserId == null || currentUserId.isEmpty()) {
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long today = cal.getTimeInMillis();

            // Only store actual emissions, not savings
            if (emissions > 0) {
                DailyEmissions todayEmissions = database.dailyEmissionsDao()
                        .getEmissionsForDate(currentUserId, today);

                if (todayEmissions == null) {
                    todayEmissions = new DailyEmissions(currentUserId, today, cal.get(Calendar.DAY_OF_WEEK));
                }

                if (isFood) {
                    todayEmissions.setFoodEmissions(emissions);
                } else {
                    todayEmissions.setCarEmissions(emissions);
                }

                if (todayEmissions.getId() == 0) {
                    database.dailyEmissionsDao().insert(todayEmissions);
                } else {
                    database.dailyEmissionsDao().update(todayEmissions);
                }
            }
        });
    }

    public void updateCO2Savings(double savings) {
        if (currentUserId == null || currentUserId.isEmpty()) {
            return;
        }

        // Ensure savings is always positive
        final double positiveSavings = Math.abs(savings);

        Executors.newSingleThreadExecutor().execute(() -> {
            long weekStart = getStartOfWeek();
            WeeklyProgress progress = database.weeklyProgressDao().getUserWeekProgress(currentUserId, weekStart);
            
            if (progress == null) {
                progress = new WeeklyProgress(currentUserId, weekStart, positiveSavings, 35.0);
                database.weeklyProgressDao().insert(progress);
            } else {
                progress.setCurrentProgress(progress.getCurrentProgress() + positiveSavings);
                database.weeklyProgressDao().update(progress);
            }

            // Important: Update the LiveData after database update
            weeklyProgress.postValue(progress);

            // Award points based on CO2 savings
            int points = (int) (positiveSavings * 10); // 10 points per kg of CO2 saved
            userManager.addPoints(points);
        });
    }

    private long getStartOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}