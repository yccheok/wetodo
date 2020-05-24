package com.yocto.wetodo.model;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class SimpleTodo {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "name")
    private String title;

    @ColumnInfo(name = "checked")
    private boolean checked;

    @ColumnInfo(name = "order")
    private int order;

    public SimpleTodo() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTodo that = (SimpleTodo) o;

        if (id != that.id) return false;
        if (checked != that.checked) return false;
        if (order != that.order) return false;
        return title != null ? title.equals(that.title) : that.title == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (checked ? 1 : 0);
        result = 31 * result + order;
        return result;
    }
}
