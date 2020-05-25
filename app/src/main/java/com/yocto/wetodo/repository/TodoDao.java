package com.yocto.wetodo.repository;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.yocto.wetodo.model.Todo;

@Dao
public abstract class TodoDao {
    // The Integer type parameter tells Room to use a
    // PositionalDataSource object.
    @Query("SELECT * FROM plain_todo ORDER BY \"order\" ASC")
    public abstract DataSource.Factory<Integer, Todo> getTodos();
}
