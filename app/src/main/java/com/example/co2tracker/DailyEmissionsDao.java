package com.example.co2tracker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface DailyEmissionsDao {
    @Query("SELECT * FROM daily_emissions WHERE userId = :userId AND date >= :weekStart ORDER BY date ASC")
    LiveData<List<DailyEmissions>> getCurrentWeekEmissions(String userId, long weekStart);

    @Query("SELECT * FROM daily_emissions WHERE userId = :userId AND date = :date LIMIT 1")
    DailyEmissions getEmissionsForDate(String userId, long date);

    @Insert
    void insert(DailyEmissions emissions);

    @Update
    void update(DailyEmissions emissions);

    @Query("DELETE FROM daily_emissions WHERE userId = :userId")
    void deleteUserEmissions(String userId);
}