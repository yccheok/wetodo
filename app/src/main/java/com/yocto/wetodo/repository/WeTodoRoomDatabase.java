package com.yocto.wetodo.repository;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.yocto.wetodo.WeTodoApplication;
import com.yocto.wetodo.model.PlainTodo;
import com.yocto.wetodo.model.SimpleTodo;
import com.yocto.wetodo.model.Todo;
import com.yocto.wetodo.model.TodoFolder;
import com.yocto.wetodo.model.TodoFolderTrash;

@Database(
    entities = {
        PlainTodo.class,
        SimpleTodo.class,
        TodoFolder.class,
        TodoFolderTrash.class
    },
    version = 1
)
public abstract class WeTodoRoomDatabase extends RoomDatabase {
    private volatile static WeTodoRoomDatabase INSTANCE;

    private static final String NAME = "wetodo";

    public abstract TodoFolderDao todoFolderDao();

    public static WeTodoRoomDatabase instance() {
        if (INSTANCE == null) {
            synchronized (WeTodoRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            WeTodoApplication.instance(),
                            WeTodoRoomDatabase.class,
                            NAME
                    )
                    .build();
                }
            }
        }

        return INSTANCE;
    }
}
