package com.yocto.wetodo.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "plain_todo",
    indices = {
        @Index("folder")
    }
)
public class PlainTodo {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "folder")
    private String folder;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "checked")
    private boolean checked;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlainTodo plainTodo = (PlainTodo) o;

        if (id != plainTodo.id) return false;
        if (checked != plainTodo.checked) return false;
        if (folder != null ? !folder.equals(plainTodo.folder) : plainTodo.folder != null)
            return false;
        return title != null ? title.equals(plainTodo.title) : plainTodo.title == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (folder != null ? folder.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (checked ? 1 : 0);
        return result;
    }
}
