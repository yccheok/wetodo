package com.yocto.wetodo.todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yocto.wetodo.R;
import com.yocto.wetodo.model.TodoFolder;
import com.yocto.wetodo.model.TodoViewModel;
import com.yocto.wetodo.model.Todoable;

public class TodoDashboardFragment extends Fragment {
    private static final String INTENT_EXTRA_TODO_FOLDER = "INTENT_EXTRA_TODO_FOLDER";

    private TodoFolder todoFolder;
    private TodoViewModel todoViewModel;

    private RecyclerView recyclerView;
    private TodoableAdapter todoableAdapter;

    private final TodoablesPagedListObserver todoablesPagedListObserver = new TodoablesPagedListObserver();

    private class TodoablesPagedListObserver implements Observer<PagedList<Todoable>> {

        @Override
        public void onChanged(PagedList<Todoable> todoablePagedList) {
            todoableAdapter.submitList(todoablePagedList);
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
        this.todoableAdapter = new TodoableAdapter();

        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.recyclerView.setAdapter(todoableAdapter);

        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        this.todoViewModel.getTodoablesPagedListLiveData().removeObservers(viewLifecycleOwner);
        this.todoViewModel.getTodoablesPagedListLiveData().observe(viewLifecycleOwner, todoablesPagedListObserver);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
