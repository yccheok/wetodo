package com.yocto.wetodo.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class Todo implements Parcelable, Todoable {
    @Embedded
    private PlainTodo plainTodo = new PlainTodo();

    @Relation(
        parentColumn = "id",
        entityColumn = "plain_todo_id",
        entity = SimpleTodo.class
    )
    private List<SimpleTodo> simpleTodos = new ArrayList<>();

    public Todo() {
    }

    protected Todo(Parcel in) {
        plainTodo = in.readParcelable(PlainTodo.class.getClassLoader());
        simpleTodos = in.createTypedArrayList(SimpleTodo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(plainTodo, flags);
        dest.writeTypedList(simpleTodos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Todo> CREATOR = new Creator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel in) {
            return new Todo(in);
        }

        @Override
        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };

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

    @Override
    public String getTitle() {
        return plainTodo.getTitle();
    }

    @Override
    public long getId() {
        return plainTodo.getId();
    }
}
