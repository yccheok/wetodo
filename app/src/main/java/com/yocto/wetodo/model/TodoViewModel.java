package com.yocto.wetodo.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.yocto.wetodo.repository.TodoRepository;

public class TodoViewModel extends ViewModel {
    private static final int PAGE_SIZE = 56;

    private LiveData<PagedList<Todo>> todosPagedListLiveData;

    public TodoViewModel() {
    }

    public LiveData<PagedList<Todo>> getTodosPagedListLiveData() {
        // Lazy evaluation.
        if (todosPagedListLiveData != null) {
            return todosPagedListLiveData;
        }

        todosPagedListLiveData = new LivePagedListBuilder<>(
                TodoRepository.INSTANCE.getTodos(),
                PAGE_SIZE
        ).build();
        
        return todosPagedListLiveData;
    }
}
