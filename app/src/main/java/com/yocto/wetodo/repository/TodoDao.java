package com.yocto.wetodo.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.yocto.wetodo.model.PlainTodo;
import com.yocto.wetodo.model.SimpleTodo;
import com.yocto.wetodo.model.Todo;

import java.util.List;

@Dao
public abstract class TodoDao {
    @Query("SELECT * FROM plain_todo ORDER BY \"order\" ASC")
    public abstract LiveData<List<Todo>> getTodosLiveData();

    @Query("SELECT * FROM plain_todo ORDER BY \"order\" ASC")
    public abstract List<Todo> getTodos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(PlainTodo plainTodo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract List<Long> insertSimpleTodos(List<SimpleTodo> simpleTodos);

    @Transaction
    public long insert(Todo todo) {
        final PlainTodo plainTodo = todo.getPlainTodo();

        final long plainTodoId = insert(plainTodo);

        todo.getPlainTodo().setId(plainTodoId);

        final List<SimpleTodo> simpleTodos = todo.getSimpleTodos();

        for (SimpleTodo simpleTodo : simpleTodos) {
            simpleTodo.setPlainTodoId(plainTodoId);
        }

        List<Long> simpleTodoIds = insertSimpleTodos(simpleTodos);

        for (int i=0, ei=simpleTodoIds.size(); i<ei; i++) {
            simpleTodos.get(i).setId(simpleTodoIds.get(i));
        }

        return plainTodoId;
    }
}
