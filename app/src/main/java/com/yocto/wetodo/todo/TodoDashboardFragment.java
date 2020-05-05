package com.yocto.wetodo.todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.yocto.wetodo.R;
import com.yocto.wetodo.model.TodoFolder;

public class TodoDashboardFragment extends Fragment {
    private static final String INTENT_EXTRA_TODO_FOLDER = "INTENT_EXTRA_TODO_FOLDER";

    private TodoFolder todoFolder;

    public static TodoDashboardFragment newInstance(TodoFolder todoFolder) {
        TodoDashboardFragment todoDashboardFragment = new TodoDashboardFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(INTENT_EXTRA_TODO_FOLDER, todoFolder);
        todoDashboardFragment.setArguments(arguments);
        return todoDashboardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        this.todoFolder = arguments.getParcelable(INTENT_EXTRA_TODO_FOLDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_dashboard_fragment, container, false);

        return view;
    }
}
