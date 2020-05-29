package com.yocto.wetodo.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static com.yocto.wetodo.Utils.generateUUID;

@Entity(
    tableName = "plain_todo",
    indices = {
        @Index("folder"),
        @Index("uuid"),
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

    @ColumnInfo(name = "uuid")
    private final String uuid;

    @Ignore
    public PlainTodo() {
        this(generateUUID());
    }

    public PlainTodo(String uuid) {
        this.uuid = uuid;
    }

    protected PlainTodo(Parcel in) {
        id = in.readLong();
        folder = in.readString();
        title = in.readString();
        checked = in.readByte() != 0;
        uuid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(folder);
        dest.writeString(title);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeString(uuid);
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

    public String getUuid() {
        return uuid;
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
        if (title != null ? !title.equals(plainTodo.title) : plainTodo.title != null) return false;
        return uuid.equals(plainTodo.uuid);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (folder != null ? folder.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (checked ? 1 : 0);
        result = 31 * result + uuid.hashCode();
        return result;
    }
}
