package com.yocto.wetodo.repository;

import androidx.lifecycle.LiveData;

import com.yocto.wetodo.model.Todo;

import java.util.List;

public enum TodoRepository {
    INSTANCE;

    public LiveData<List<Todo>> getTodosLiveData() {
        TodoDao todoDao = WeTodoRoomDatabase.instance().todoDao();
        return todoDao.getTodosLiveData();
    }

    public List<Todo> getTodos() {
        TodoDao todoDao = WeTodoRoomDatabase.instance().todoDao();
        return todoDao.getTodos();
    }

    public long insert(Todo todo) {
        TodoDao todoDao = WeTodoRoomDatabase.instance().todoDao();
        return todoDao.insert(todo);
    }
}
