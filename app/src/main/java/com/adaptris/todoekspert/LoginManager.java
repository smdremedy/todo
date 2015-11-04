package com.adaptris.todoekspert;

import android.content.SharedPreferences;
import android.text.TextUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class LoginManager {

    public static final String TOKEN_PREFS_KEY = "token";
    public static final String USER_ID_PREFS_KEY = "userId";


    private String userId;
    private String token;

    private SharedPreferences preferences;

    @Inject
    public LoginManager(SharedPreferences preferences) {
        this.preferences = preferences;

        userId = preferences.getString(USER_ID_PREFS_KEY, null);
        token = preferences.getString(TOKEN_PREFS_KEY, null);
    }

    public void login(String userId, String token) {
        this.userId = userId;
        this.token = token;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_PREFS_KEY, token);
        editor.putString(USER_ID_PREFS_KEY, userId);

        editor.apply();

    }

    public void logout() {
        userId = null;
        token = null;

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean hasToLogin() {
        return TextUtils.isEmpty(userId) || TextUtils.isEmpty(token);
    }

}
