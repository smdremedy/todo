package com.adaptris.todoekspert;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class TodoListActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD = 123;
    private static final String LOG_TAG = TodoListActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.todoListView)
    ListView todoListView;
    @Bind(R.id.fab)
    FloatingActionButton fab;


    @Inject
    LoginManager loginManager;

    @Inject
    ParseTodoService parseTodoService;

    @Inject
    TodoDao todoDao;

    private SimpleCursorAdapter adapter;
    private String[] from = new String[]{TodoDao.C_CONTENT, TodoDao.C_DONE};
    private int[] to = new int[] {R.id.itemCheckBox, R.id.itemCheckBox};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TodoApplication.getTodoComponent().inject(this);
        if(loginManager.hasToLogin()) {
            goToLogin();
            return;
        }
        setContentView(R.layout.activity_todo_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        adapter = new SimpleCursorAdapter(this, R.layout.todo_list_item, null, from, to, 0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if(columnIndex == cursor.getColumnIndex(TodoDao.C_DONE)) {
                    CheckBox checkBox = (CheckBox) view;
                    checkBox.setChecked(cursor.getInt(columnIndex) > 0);
                    return true;
                }
                return false;
            }
        });
        todoListView.setAdapter(adapter);
        refreshCursor();

    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                //Toast.makeText(getApplicationContext(), "Logout!",  Toast.LENGTH_SHORT).show();
//                Snackbar.make(todoListView, "Logout!", Snackbar.LENGTH_INDEFINITE)
//                        .setAction("Undo", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                            }
//                        }).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loginManager.logout();
                        goToLogin();
                    }
                });
                builder.setNegativeButton(android.R.string.no, null);
                builder.create().show();
                break;
            case R.id.action_add:
                Intent intent = new Intent(this, AddTodoActivity.class);

                startActivityForResult(intent, REQUEST_CODE_ADD);
                break;
            case R.id.action_refresh:
                refreshList();

                break;
            case R.id.action_sort:
                //TODO
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void refreshList() {


        parseTodoService.getTodos(loginManager.getToken(), new Callback<GetTodosResponse>() {
            @Override
            public void success(GetTodosResponse getTodosResponse, Response response) {
                for(Todo todo : getTodosResponse.results) {
                    Timber.d("TODO:" + todo);
                    todoDao.insertOrUpdate(todo);
                }
                refreshCursor();
                Snackbar.make(todoListView, "Refreshed", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, "Cannot get Todos");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD) {
            Todo todo = (Todo) data.getParcelableExtra(AddTodoActivity.TODO_EXTRA);

            Log.d(LOG_TAG, "Result:" + resultCode + " content:"
                    + todo.getContent() + " done: " + todo.isDone());
            refreshList();

        }
    }



    private void refreshCursor() {
        Cursor cursor = todoDao.query(loginManager.getUserId(), true);

        adapter.changeCursor(cursor);
    }

}
