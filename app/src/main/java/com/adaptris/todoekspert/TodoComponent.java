package com.adaptris.todoekspert;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TodoModule.class)
public interface TodoComponent {
    void inject(LoginActivity loginActivity);
    void inject(TodoListActivity todoListActivity);
}
