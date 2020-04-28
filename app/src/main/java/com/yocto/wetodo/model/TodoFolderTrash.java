package com.yocto.wetodo.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "todo_folder_trash")
public class TodoFolderTrash implements Parcelable {
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    @NonNull
    private final String uuid;

    @ColumnInfo(name = "synced_timestamp")
    private final long syncedTimestamp;

    @Ignore
    public TodoFolderTrash(String uuid) {
        this(uuid, System.currentTimeMillis());
    }

    public TodoFolderTrash(String uuid, long syncedTimestamp) {
        this.uuid = uuid;
        this.syncedTimestamp = syncedTimestamp;
    }

    protected TodoFolderTrash(Parcel in) {
        uuid = in.readString();
        syncedTimestamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeLong(syncedTimestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TodoFolderTrash> CREATOR = new Creator<TodoFolderTrash>() {
        @Override
        public TodoFolderTrash createFromParcel(Parcel in) {
            return new TodoFolderTrash(in);
        }

        @Override
        public TodoFolderTrash[] newArray(int size) {
            return new TodoFolderTrash[size];
        }
    };

    public long getSyncedTimestamp() {
        return syncedTimestamp;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoFolderTrash todoFolderTrash = (TodoFolderTrash) o;

        if (syncedTimestamp != todoFolderTrash.syncedTimestamp) return false;
        return uuid.equals(todoFolderTrash.uuid);
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + (int) (syncedTimestamp ^ (syncedTimestamp >>> 32));
        return result;
    }
}