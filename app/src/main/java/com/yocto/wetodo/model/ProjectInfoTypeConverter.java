package com.yocto.wetodo.model;

import android.util.SparseArray;

import androidx.room.TypeConverter;

public class ProjectInfoTypeConverter {
    private static final SparseArray<ProjectInfo.Type> map = new SparseArray<>();
    static {
        for (ProjectInfo.Type type : ProjectInfo.Type.values()) {
            map.put(type.code, type);
        }
    }

    @TypeConverter
    public static ProjectInfo.Type toProjectInfoType(int code) {
        return map.get(code);
    }

    @TypeConverter
    public static int toCode(ProjectInfo.Type type) {
        return type.code;
    }
}
