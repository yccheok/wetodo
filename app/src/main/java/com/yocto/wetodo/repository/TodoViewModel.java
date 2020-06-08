package com.yocto.wetodo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yocto.wetodo.model.Todo;

import java.util.List;

public class TodoViewModel extends ViewModel {
    private LiveData<List<Todo>> todosLiveData;

    public TodoViewModel() {
    }

    public LiveData<List<Todo>> getTodosLiveData() {
        // Lazy evaluation.
        if (todosLiveData != null) {
            return todosLiveData;
        }

        todosLiveData = TodoRepository.INSTANCE.getTodosLiveData();

        return todosLiveData;
    }
}
