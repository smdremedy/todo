package com.adaptris.todoekspert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTodoActivity extends AppCompatActivity {

    public static final String CONTENT_EXTRA = "CONTENT_EXTRA";
    public static final String TODO_EXTRA = "TODO_EXTRA";

    public static final int RESULT_DONE = RESULT_FIRST_USER;
    public static final int RESULT_DONE_2 = RESULT_FIRST_USER + 1;

    @Bind(R.id.contentEditText)
    EditText contentEditText;
    @Bind(R.id.doneCheckBox)
    CheckBox doneCheckBox;
    @Bind(R.id.addButton)
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.addButton)
    public void addClicked() {

        String content = contentEditText.getText().toString();
        if(content.length() > 4 ) {
            Intent data = new Intent();
            Todo todo = new Todo();
            todo.content = content;
            todo.done = doneCheckBox.isChecked();
            data.putExtra(TODO_EXTRA, todo);
            setResult(RESULT_OK, data);
            finish();
        } else {
            contentEditText.setError(getString(R.string.content_too_short));
        }

    }
}
