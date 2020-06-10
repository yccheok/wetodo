package com.yocto.wetodo.todo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.yocto.wetodo.R;

public class NewTodoFragmentActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private NewTodoFragment newTodoFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle = this.getIntent().getExtras();

        setContentView(R.layout.new_todo_fragment_activity);

        initToolbar();

        if (savedInstanceState == null) {
            newTodoFragment = NewTodoFragment.newInstance(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.content, newTodoFragment).commit();
        } else {
            this.newTodoFragment = (NewTodoFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        }
    }

    private void initToolbar() {
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
    }
}
