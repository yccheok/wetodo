package com.yocto.wetodo.model;

public interface Todoable {
    String getTitle();
    long getId();
    boolean equals(Object o);
}
