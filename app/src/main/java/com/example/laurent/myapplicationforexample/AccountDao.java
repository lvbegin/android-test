package com.example.laurent.myapplicationforexample;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM account WHERE uid = :userId")
    Account findByUid(int userId);

    @Insert
    void insert(Account account);

    @Update
    public void update(Account users);

    @Delete
    void delete(Account account);
}