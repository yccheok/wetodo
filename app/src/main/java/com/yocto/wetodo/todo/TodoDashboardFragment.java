package com.yocto.wetodo.todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yocto.wetodo.R;
import com.yocto.wetodo.model.SimpleTodo;
import com.yocto.wetodo.model.Todo;
import com.yocto.wetodo.model.TodoFolder;
import com.yocto.wetodo.repository.TodoViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TodoDashboardFragment extends Fragment {
    private static final String INTENT_EXTRA_TODO_FOLDER = "INTENT_EXTRA_TODO_FOLDER";

    private TodoFolder todoFolder;
    private TodoViewModel todoViewModel;

    private RecyclerView recyclerView;
    private TodoableAdapter todoableAdapter;

    private final List<Todo> todos = new ArrayList<>();

    private final Set<Long> expandedIds = new HashSet<>();

    private final TodosObserver todosObserver = new TodosObserver();

    private class TodosObserver implements Observer<List<Todo>> {

        @Override
        public void onChanged(List<Todo> todos) {
            if (todos != TodoDashboardFragment.this.todos) {
                TodoDashboardFragment.this.todos.clear();
                TodoDashboardFragment.this.todos.addAll(todos);
            }

            List<Todoable> todoables = new ArrayList<>();
            for (Todo todo : todos) {
                // Should we perform copy operation?
                todoables.add(todo);

                if (expandedIds.contains(todo.getId())) {
                    for (SimpleTodo simpleTodo : todo.getSimpleTodos()) {
                        // Should we perform copy operation?
                        todoables.add(simpleTodo);
                    }
                }
            }

            todoableAdapter.submitList(todoables);
        }
    }

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

        final ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
        this.todoViewModel = viewModelProvider.get(TodoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_dashboard_fragment, container, false);

        this.recyclerView = view.findViewById(R.id.recycler_view);
        this.todoableAdapter = new TodoableAdapter(this);
        this.recyclerView.setAdapter(todoableAdapter);

        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        this.todoViewModel.getTodosLiveData().removeObservers(viewLifecycleOwner);
        this.todoViewModel.getTodosLiveData().observe(viewLifecycleOwner, todosObserver);

        return view;
    }

    public void expand(Todo todo) {
        expandedIds.add(todo.getPlainTodo().getId());
        todosObserver.onChanged(todos);
    }

    public void toggle(Todo todo) {
        if (expandedIds.contains(todo.getId())) {
            collapse(todo);
        } else {
            expand(todo);
        }
    }

    private void collapse(Todo todo) {
        expandedIds.remove(todo.getPlainTodo().getId());
        todosObserver.onChanged(todos);
    }

    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }
}
