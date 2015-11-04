package com.adaptris.todoekspert;

import android.app.IntentService;
import android.content.Intent;

import javax.inject.Inject;

import timber.log.Timber;

public class RefreshIntentService extends IntentService {


    @Inject
    ParseTodoService parseTodoService;
    @Inject
    LoginManager loginManager;
    @Inject
    TodoDao todoDao;

    public RefreshIntentService() {
        super(RefreshIntentService.class.getSimpleName());
        TodoApplication.getTodoComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GetTodosResponse response = parseTodoService.getTodos(loginManager.getToken());

        for(Todo todo : response.results) {
            Timber.d("TODO:" + todo);
            todoDao.insertOrUpdate(todo);
        }


    }
}
