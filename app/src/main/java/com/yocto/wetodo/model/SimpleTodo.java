package com.yocto.wetodo.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
    tableName = "simple_todo",
    foreignKeys ={
        @ForeignKey(
            onDelete = CASCADE,
            entity = PlainTodo.class,
            parentColumns = "id",
            childColumns = "plain_todo_id"
        )
    },
    indices = {
        @Index("plain_todo_id")
    }
)
public class SimpleTodo implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "name")
    private String title;

    @ColumnInfo(name = "checked")
    private boolean checked;

    @ColumnInfo(name = "order")
    private int order;

    @ColumnInfo(name = "plain_todo_id")
    private long plainTodoId;

    public SimpleTodo() {
    }

    protected SimpleTodo(Parcel in) {
        id = in.readLong();
        title = in.readString();
        checked = in.readByte() != 0;
        order = in.readInt();
        plainTodoId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeInt(order);
        dest.writeLong(plainTodoId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SimpleTodo> CREATOR = new Creator<SimpleTodo>() {
        @Override
        public SimpleTodo createFromParcel(Parcel in) {
            return new SimpleTodo(in);
        }

        @Override
        public SimpleTodo[] newArray(int size) {
            return new SimpleTodo[size];
        }
    };

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

    public long getPlainTodoId() {
        return plainTodoId;
    }

    public void setPlainTodoId(long plainTodoId) {
        this.plainTodoId = plainTodoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTodo that = (SimpleTodo) o;

        if (id != that.id) return false;
        if (checked != that.checked) return false;
        if (order != that.order) return false;
        if (plainTodoId != that.plainTodoId) return false;
        return title != null ? title.equals(that.title) : that.title == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (checked ? 1 : 0);
        result = 31 * result + order;
        result = 31 * result + (int) (plainTodoId ^ (plainTodoId >>> 32));
        return result;
    }
}
