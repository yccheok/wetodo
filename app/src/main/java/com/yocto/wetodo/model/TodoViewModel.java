package com.yocto.wetodo.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.yocto.wetodo.repository.TodoRepository;

public class TodoViewModel extends ViewModel {
    private static final int PAGE_SIZE = 56;

    private LiveData<PagedList<Todoable>> todoablesPagedListLiveData;

    public TodoViewModel() {
    }

    public LiveData<PagedList<Todoable>> getTodoablesPagedListLiveData() {
        // Lazy evaluation.
        if (todoablesPagedListLiveData != null) {
            return todoablesPagedListLiveData;
        }

        todoablesPagedListLiveData = new LivePagedListBuilder<>(
                (DataSource.Factory<Integer, Todoable>)TodoRepository.INSTANCE.getTodosDataSourceFactory(),
                PAGE_SIZE
        ).build();

        return todoablesPagedListLiveData;
    }
}
