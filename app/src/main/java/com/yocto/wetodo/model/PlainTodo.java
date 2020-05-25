package com.yocto.wetodo.model;

import android.os.Parcel;
import android.os.Parcelable;

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
public class PlainTodo implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "folder")
    private String folder;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "checked")
    private boolean checked;

    public PlainTodo() {
    }

    protected PlainTodo(Parcel in) {
        id = in.readLong();
        folder = in.readString();
        title = in.readString();
        checked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(folder);
        dest.writeString(title);
        dest.writeByte((byte) (checked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlainTodo> CREATOR = new Creator<PlainTodo>() {
        @Override
        public PlainTodo createFromParcel(Parcel in) {
            return new PlainTodo(in);
        }

        @Override
        public PlainTodo[] newArray(int size) {
            return new PlainTodo[size];
        }
    };

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
