package com.adaptris.todoekspert;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
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

    private RefreshAsyncTask refreshAsyncTask = null;

    @Inject
    LoginManager loginManager;

    @Inject
    ParseTodoService parseTodoService;

    @Inject
    TodoDao todoDao;

    private TodoAdapter adapter;

    class TodoAdapter extends BaseAdapter {

        private List<Todo> todos = new ArrayList<>();
        private final LayoutInflater from;

        public TodoAdapter() {
            from = LayoutInflater.from(TodoListActivity.this);
        }

        @Override
        public int getCount() {
            return todos.size();
        }

        @Override
        public Todo getItem(int position) {
            return todos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Timber.d("Pos:" + position + " view:" + convertView);

            View view = convertView;
            if(view == null) {
                view = (View) from.inflate(R.layout.todo_list_item, parent, false);
            }

            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if(viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.checkBox = (CheckBox) view.findViewById(R.id.itemCheckBox);
                viewHolder.button = (Button) view.findViewById(R.id.itemButton);
                view.setTag(viewHolder);

            }

            Todo todo = getItem(position);
            viewHolder.checkBox.setText(todo.getContent());
            viewHolder.checkBox.setChecked(todo.isDone());
            viewHolder.button.setEnabled(todo.isDone());
            return view;
        }

        public void addAll(List<Todo> results) {
            todos.addAll(results);
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        CheckBox checkBox;
        Button button;
    }


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
        adapter = new TodoAdapter();

        todoListView.setAdapter(adapter);
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
        if(refreshAsyncTask == null) {
            refreshAsyncTask = new RefreshAsyncTask();
            refreshAsyncTask.execute();
        }
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

    class RefreshAsyncTask extends AsyncTask<Void, Void, GetTodosResponse> {

        @Override
        protected GetTodosResponse doInBackground(Void... params) {
            try {
                return parseTodoService.getTodos(loginManager.getToken());
            } catch (RetrofitError error) {
                Timber.e(error, "Cannot get Todos");
                return null;
            }
        }

        @Override
        protected void onPostExecute(GetTodosResponse response) {
            super.onPostExecute(response);

            for(Todo todo : response.results) {
                Timber.d("TODO:" + todo);
                todoDao.insertOrUpdate(todo);
            }
            adapter.addAll(response.results);

            refreshAsyncTask = null;

            Snackbar.make(todoListView, "Refreshed", Snackbar.LENGTH_SHORT).show();
        }
    }

}
