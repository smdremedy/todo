package com.adaptris.todoekspert;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.GsonBuilder;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.usernameEditText)
    EditText usernameEditText;
    @Bind(R.id.passwordEditText)
    EditText passwordEditText;
    @Bind(R.id.loginButton)
    Button loginButton;
    @Bind(R.id.signupButton)
    Button signupButton;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;


    @Inject
    LoginManager loginManager;

    @Inject
    ParseTodoService parseTodoService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(LOG_TAG, "onCreate:" + savedInstanceState);
        Log.d(LOG_TAG, "Started:" + this);
        ButterKnife.bind(this);

        TodoApplication.getTodoComponent().inject(this);

        if (BuildConfig.DEBUG) {
            usernameEditText.setText("test");
            passwordEditText.setText("test");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @OnClick(R.id.loginButton)
    public void loginClicked() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean hasErrors = false;

        if (username.isEmpty()) {
            usernameEditText.setError(getString(R.string.this_field_cannot_be_empty));
            hasErrors = true;
        }

        if (password.isEmpty()) {
            passwordEditText.setError(getString(R.string.this_field_cannot_be_empty));
            hasErrors = true;
        }


        if (!hasErrors) {
            login(username, password);

        }


    }

    private void login(String username, final String password) {

        AsyncTask<String, Integer, LoginResponse> asyncTask = new AsyncTask<String, Integer, LoginResponse>() {
            @Override
            protected LoginResponse doInBackground(String... params) {



                try {
                    LoginResponse loginResponse = parseTodoService.login(params[0], params[1]);
                    Log.d(LOG_TAG, "Response in POJO:" + loginResponse.toString());
                    return loginResponse;

                } catch (RetrofitError error) {
                    Log.e(LOG_TAG, "Error:" + error);

                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progressBar.setProgress(values[0]);
                loginButton.setText(values[0].toString());
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loginButton.setEnabled(false);

            }

            @Override
            protected void onPostExecute(LoginResponse result) {
                super.onPostExecute(result);
                loginButton.setEnabled(true);

                Log.d(LOG_TAG, "Result:" + result);
                if(result != null) {

                    loginManager.login(result.getObjectId(), result.getSessionToken());

                    loginButton.setText("Finished");

                    Log.d(LOG_TAG, "Finished:" + LoginActivity.this);

                    Intent intent = new Intent(getApplicationContext(), TodoListActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        };


        asyncTask.execute(username, password);


    }

}
