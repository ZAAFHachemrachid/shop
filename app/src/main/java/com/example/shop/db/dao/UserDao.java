package com.example.shop.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.shop.models.User;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    int update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE id = :userId")
    User findById(long userId);

    @Query("SELECT * FROM users WHERE email = :email")
    User findByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password_hash = :passwordHash")
    User authenticate(String email, String passwordHash);

    @Query("UPDATE users SET password_hash = :newPasswordHash WHERE id = :userId AND password_hash = :currentPasswordHash")
    int updatePassword(long userId, String currentPasswordHash, String newPasswordHash);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    boolean exists(String email);
}