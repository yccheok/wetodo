package com.yocto.wetodo.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.yocto.wetodo.model.TodoFolder;

import java.util.List;

@Dao
public abstract class TodoFolderDao {
    @Query("SELECT * FROM todo_folder order by \"order\" asc")
    public abstract LiveData<List<TodoFolder>> getTodoFolders();
}
