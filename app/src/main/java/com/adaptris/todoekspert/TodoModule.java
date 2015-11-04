package com.adaptris.todoekspert;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.GsonBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module
public class TodoModule {


    private Context context;

    public TodoModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    public SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Singleton
    @Provides
    public LoginManager provideLoginManager(SharedPreferences sharedPreferences) {
        return new LoginManager(sharedPreferences);
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Url {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DateFormat {
    }

    @Provides
    @Url
    public String provideEndpoint() {
        return "https://api.parse.com/1";
    }

    @Provides
    @DateFormat
    public String provideFormat() {
        return "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    }

    @Singleton
    @Provides
    public ParseTodoService provideTodoService(@Url String endpoint,
                                               @DateFormat String dateFormat) {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setEndpoint(endpoint);
        builder.setLogLevel(RestAdapter.LogLevel.FULL);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat(dateFormat);
        builder.setConverter(new GsonConverter(gsonBuilder.create()));
        RestAdapter adapter = builder.build();

        return adapter.create(ParseTodoService.class);
    }
}
