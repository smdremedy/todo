package com.adaptris.todoekspert;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(LOG_TAG, "onCreate:" + savedInstanceState);
        Log.d(LOG_TAG, "Started:" + this);
        ButterKnife.bind(this);
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

    private void login(String username, String password) {

        AsyncTask<String, Integer, Boolean> asyncTask = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    for (int i = 0; i < 100; i++) {
                        Thread.sleep(50);
                        publishProgress(i);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "test".equals(params[0]) && "test".equals(params[1]);
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
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                loginButton.setEnabled(true);
                Log.d(LOG_TAG, "Result:" + result);
                if(result) {
                    loginButton.setText("Finished");
                    finish();
                    Log.d(LOG_TAG, "Finished:" + LoginActivity.this);
                }


            }
        };


        asyncTask.execute(username, password);


    }

}
