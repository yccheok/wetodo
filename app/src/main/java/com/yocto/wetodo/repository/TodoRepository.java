package com.yocto.wetodo.repository;

import androidx.paging.DataSource;
import androidx.paging.PagingSource;

import com.yocto.wetodo.model.Todo;
import com.yocto.wetodo.model.Todoable;

import java.util.List;

public enum TodoRepository {
    INSTANCE;

    public PagingSource<Integer, ? extends Todoable> getTodosPagingSource() {
        TodoDao todoDao = WeTodoRoomDatabase.instance().todoDao();
        return todoDao.getTodosPagingSource();
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
