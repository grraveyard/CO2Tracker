package com.example.co2tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_LOGGED_IN_USER = "loggedInUser";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PASSWORD = "user_password";
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_POINTS = "points";
    private static final String KEY_USERNAME_TO_EMAIL = "usernameToEmail";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String TAG = "UserManager";
    private final SharedPreferences prefs;

    public UserManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean registerUser(String username, String email, String password) {
        if (isEmailRegistered(email) || isUsernameRegistered(username)) {
            Log.d(TAG, "Registration failed: email or username already exists");
            return false;
        }
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(email, password);
        editor.putString(username, email);  // Store username to email mapping
        editor.putString(KEY_USERNAME_TO_EMAIL + "_" + email, username);  // Store email to username mapping
        editor.putString(KEY_USERNAME_TO_EMAIL + "_" + username, email);  // Store username to email mapping
        editor.apply();
        
        Log.d(TAG, "User registered successfully: " + username);
        return true;
    }

    public boolean loginUser(String loginId, String password, boolean rememberMe) {
        Log.d(TAG, "Attempting login for: " + loginId);
        
        // First check if loginId is an email
        String email = loginId;
        String username = null;
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(loginId).matches()) {
            // If not an email, try to get the email from username
            email = prefs.getString(loginId, null);
            username = loginId;
            if (email == null) {
                Log.d(TAG, "Login failed: username not found");
                return false; // Username not found
            }
        } else {
            username = prefs.getString(KEY_USERNAME_TO_EMAIL + "_" + email, null);
        }

        String savedPassword = prefs.getString(email, null);
        if (savedPassword != null && savedPassword.equals(password)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_LOGGED_IN_USER, username);
            editor.putBoolean("rememberMe", rememberMe);
            editor.putString(KEY_USER_EMAIL, email);
            editor.putBoolean(KEY_LOGGED_IN, true);
            editor.apply();
            
            Log.d(TAG, "Login successful for user: " + username);
            return true;
        }
        
        Log.d(TAG, "Login failed: invalid password");
        return false;
    }

    public void logoutUser() {
        Log.d(TAG, "Logging out user: " + getLoggedInUser());
        clearUserData();
    }

    public void clearUserData() {
        String currentEmail = prefs.getString(KEY_USER_EMAIL, null);
        Log.d(TAG, "Clearing user data for: " + currentEmail);
        
        SharedPreferences.Editor editor = prefs.edit();
        
        // Clear session data
        editor.remove(KEY_LOGGED_IN_USER);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_PASSWORD);
        editor.remove(KEY_POINTS);
        editor.putBoolean(KEY_LOGGED_IN, false);
        
        // Do NOT remove username-to-email mappings as they're needed for future logins
        editor.apply();
    }

    public boolean isLoggedIn() {
        boolean loggedIn = prefs.getBoolean(KEY_LOGGED_IN, false);
        Log.d(TAG, "Checking login status: " + loggedIn);
        return loggedIn;
    }

    public boolean isUserLoggedIn() {
        boolean loggedIn = isLoggedIn() && getLoggedInUser() != null;
        Log.d(TAG, "Checking user login status: " + loggedIn + ", user: " + getLoggedInUser());
        return loggedIn;
    }

    public String getLoggedInUser() {
        String user = prefs.getString(KEY_LOGGED_IN_USER, null);
        Log.d(TAG, "Getting logged in user: " + user);
        return user;
    }

    public String getCurrentUserEmail() {
        String email = prefs.getString(KEY_USER_EMAIL, null);
        Log.d(TAG, "Getting current user email: " + email);
        return email;
    }

    public boolean isEmailRegistered(String email) {
        return prefs.contains(email);
    }

    public boolean isUsernameRegistered(String username) {
        return prefs.contains(username);
    }

    public boolean verifyPassword(String password) {
        String currentEmail = getCurrentUserEmail();
        if (currentEmail == null) return false;
        String savedPassword = prefs.getString(currentEmail, null);
        return savedPassword != null && savedPassword.equals(password);
    }

    public boolean updatePassword(String newPassword) {
        String currentEmail = getCurrentUserEmail();
        if (currentEmail == null) return false;
        
        prefs.edit().putString(currentEmail, newPassword).apply();
        return true;
    }

    public int getPoints() {
        String userId = getLoggedInUser();
        if (userId == null) return 0;
        return prefs.getInt(KEY_POINTS + "_" + userId, 0);
    }

    public void setPoints(int points) {
        String userId = getLoggedInUser();
        if (userId != null) {
            Log.d(TAG, "Setting points for user " + userId + " to " + points);
            prefs.edit().putInt(KEY_POINTS + "_" + userId, points).apply();
        }
    }

    public void addPoints(int points) {
        int currentPoints = getPoints();
        setPoints(currentPoints + points);
    }

    public String getUsernameFromEmail(String email) {
        return prefs.getString(KEY_USERNAME_TO_EMAIL + "_" + email, null);
    }

    public boolean isDarkModeEnabled() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkModeEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "en");
    }

    public void setLanguage(String languageCode) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }
}