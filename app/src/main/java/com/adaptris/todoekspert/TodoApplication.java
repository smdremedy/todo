package com.adaptris.todoekspert;

import android.app.Application;
import android.preference.PreferenceManager;

import timber.log.Timber;

public class TodoApplication extends Application {


    public static TodoComponent getTodoComponent() {
        return todoComponent;
    }

    private static TodoComponent todoComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        todoComponent = DaggerTodoComponent.builder()
                .todoModule(new TodoModule(this))
                .build();

        Timber.plant(new Timber.DebugTree());

    }
}
