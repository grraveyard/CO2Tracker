package com.example.co2tracker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface WeeklyProgressDao {
    @Insert
    void insert(WeeklyProgress progress);

    @Update
    void update(WeeklyProgress progress);

    @Query("SELECT * FROM weekly_progress WHERE userId = :userId AND weekStartDate = :weekStartDate LIMIT 1")
    WeeklyProgress getUserWeekProgress(String userId, long weekStartDate);

    @Query("DELETE FROM weekly_progress WHERE userId = :userId")
    void deleteUserProgress(String userId);
}