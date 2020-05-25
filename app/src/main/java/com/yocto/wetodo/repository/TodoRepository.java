package com.yocto.wetodo.repository;

import androidx.paging.DataSource;

import com.yocto.wetodo.model.Todo;

public enum TodoRepository {
    INSTANCE;

    public DataSource.Factory<Integer, Todo> getTodos() {
        TodoDao todoDao = WeTodoRoomDatabase.instance().todoDao();
        return todoDao.getTodos();
    }
}
