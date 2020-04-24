package com.yocto.wetodo.model;

import android.util.SparseArray;

import androidx.room.TypeConverter;

public class TodoFolderTypeConverter {
    private static final SparseArray<TodoFolder.Type> map = new SparseArray<>();
    static {
        for (TodoFolder.Type type : TodoFolder.Type.values()) {
            map.put(type.code, type);
        }
    }

    @TypeConverter
    public static TodoFolder.Type toProjectInfoType(int code) {
        return map.get(code);
    }

    @TypeConverter
    public static int toCode(TodoFolder.Type type) {
        return type.code;
    }
}
