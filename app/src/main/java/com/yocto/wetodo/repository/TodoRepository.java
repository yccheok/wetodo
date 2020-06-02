package com.yocto.wetodo.repository;

import androidx.paging.DataSource;

import com.yocto.wetodo.model.Todo;
import com.yocto.wetodo.model.Todoable;

import java.util.List;

public enum TodoRepository {
    INSTANCE;

    public DataSource.Factory<Integer, ? extends Todoable> getTodosDataSourceFactory() {
        TodoDao todoDao = WeTodoRoomDatabase.instance().todoDao();
        return todoDao.getTodosDataSourceFactory();
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
