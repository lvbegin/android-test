package com.example.laurent.myapplicationforexample;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by laurent on 3/03/18.
 */

@Database(entities = {Account.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AccountDao accountDao();

    public static AppDatabase create(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "account-db").allowMainThreadQueries().build();

    }

}