package com.example.test;

import android.content.Context;
import android.content.SharedPreferences;

public class PasswordManager {

    private final SharedPreferences sharedPreferences;

    public PasswordManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences("password", Context.MODE_PRIVATE);
    }

    public boolean setPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        sharedPreferences.edit().putString("password", password).apply();
        return true;
    }

    public boolean isPasswordSet() {
        return sharedPreferences.contains("password");
    }

    public boolean verifyPassword(String password) {
        String savedPassword = sharedPreferences.getString("password", null);
        if (password == null || savedPassword == null) {
            return false;
        }
        return savedPassword.equals(password);
    }
}
