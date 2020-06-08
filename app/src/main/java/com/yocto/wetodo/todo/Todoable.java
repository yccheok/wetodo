package com.yocto.wetodo.todo;

public interface Todoable {
    int TODO_TYPE = 0;
    int SIMPLE_TODO_TYPE = 1;

    long getId();
    boolean equals(Object o);
    int getItemViewType();
}
