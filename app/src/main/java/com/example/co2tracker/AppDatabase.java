package com.example.co2tracker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
    entities = {
        DailyEmissions.class,
        WeeklyProgress.class
    },
    version = 5,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract DailyEmissionsDao dailyEmissionsDao();
    public abstract WeeklyProgressDao weeklyProgressDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "co2_tracker_database"
            )
            .fallbackToDestructiveMigration()
            .addCallback(roomCallback)
            .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Database initialization can be done here if needed
        }
    };
}