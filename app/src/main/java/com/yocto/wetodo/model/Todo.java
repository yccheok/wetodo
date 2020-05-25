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

}
