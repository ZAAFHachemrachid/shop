package com.example.shop.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.shop.db.ShopDatabase;
import com.example.shop.db.dao.UserDao;
import com.example.shop.models.User;
import com.example.shop.utils.PasswordUtils;

public class AuthManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";

    private static AuthManager instance;
    private final SharedPreferences prefs;
    private final UserDao userDao;
    private User currentUser;

    private AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userDao = new UserDao(ShopDatabase.getInstance(context));
        loadCurrentUser();
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadCurrentUser() {
        long userId = prefs.getLong(KEY_USER_ID, -1);
        if (userId != -1) {
            currentUser = userDao.findById(userId);
        }
    }

    public boolean register(String email, String password, String name) {
        if (!PasswordUtils.isPasswordValid(password)) {
            return false;
        }

        if (userDao.emailExists(email)) {
            return false;
        }

        String passwordHash = PasswordUtils.hashPassword(password);
        User user = new User(email, name, passwordHash);
        long userId = userDao.insert(user);

        if (userId != -1) {
            user.setId(userId);
            setCurrentUser(user);
            return true;
        }

        return false;
    }

    public boolean login(String email, String password) {
        User user = userDao.findByEmail(email);
        if (user != null && PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
            setCurrentUser(user);
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_NAME)
            .apply();
    }

    private void setCurrentUser(User user) {
        currentUser = user;
        prefs.edit()
            .putLong(KEY_USER_ID, user.getId())
            .putString(KEY_USER_EMAIL, user.getEmail())
            .putString(KEY_USER_NAME, user.getName())
            .apply();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean updateProfile(String name) {
        if (currentUser == null) {
            return false;
        }

        currentUser.setName(name);
        int updated = userDao.update(currentUser);
        if (updated > 0) {
            setCurrentUser(currentUser);
            return true;
        }
        return false;
    }

    public boolean changePassword(String currentPassword, String newPassword) {
        if (currentUser == null || !PasswordUtils.isPasswordValid(newPassword)) {
            return false;
        }

        // Verify current password
        if (!PasswordUtils.verifyPassword(currentPassword, currentUser.getPasswordHash())) {
            return false;
        }

        // Update password
        currentUser.setPasswordHash(PasswordUtils.hashPassword(newPassword));
        return userDao.update(currentUser) > 0;
    }
}