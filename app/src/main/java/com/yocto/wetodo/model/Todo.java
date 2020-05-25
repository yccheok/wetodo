package com.yocto.wetodo.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class Todo {
    @Embedded
    private PlainTodo plainTodo = new PlainTodo();

    @Relation(
        parentColumn = "id",
        entityColumn = "plain_todo_id",
        entity = SimpleTodo.class
    )
    private List<SimpleTodo> simpleTodos = new ArrayList<>();

    public PlainTodo getPlainTodo() {
        return plainTodo;
    }

    public void setPlainTodo(PlainTodo plainTodo) {
        this.plainTodo = plainTodo;
    }

    public List<SimpleTodo> getSimpleTodos() {
        return simpleTodos;
    }

    public void setSimpleTodos(List<SimpleTodo> simpleTodos) {
        this.simpleTodos = simpleTodos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Todo todo = (Todo) o;

        if (!plainTodo.equals(todo.plainTodo)) return false;
        return simpleTodos.equals(todo.simpleTodos);
    }

    @Override
    public int hashCode() {
        int result = plainTodo.hashCode();
        result = 31 * result + simpleTodos.hashCode();
        return result;
    }
}
