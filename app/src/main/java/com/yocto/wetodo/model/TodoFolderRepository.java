package com.yocto.wetodo.model;

import androidx.lifecycle.LiveData;

import com.yocto.wetodo.repository.TodoFolderDao;
import com.yocto.wetodo.repository.WeTodoRoomDatabase;

import java.util.List;

public enum TodoFolderRepository {
    INSTANCE;

    public LiveData<List<TodoFolder>> getTodoFolders() {
        TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
        return todoFolderDao.getTodoFolders();
    }
}
