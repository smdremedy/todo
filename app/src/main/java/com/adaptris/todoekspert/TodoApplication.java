package com.adaptris.todoekspert;

import android.app.Application;
import android.preference.PreferenceManager;

public class TodoApplication extends Application {


    private LoginManager loginManager;

    public LoginManager getLoginManager() {
        return loginManager;
    }





    @Override
    public void onCreate() {
        super.onCreate();

        com.adaptris.todoekspert.DaggerTodoComponent.builder();

        loginManager = new LoginManager(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
    }
}
