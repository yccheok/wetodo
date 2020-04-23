package com.yocto.wetodo.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.yocto.wetodo.model.ProjectInfo;

import java.util.List;

@Dao
public abstract class ProjectInfoDao {
    @Query("SELECT * FROM project_info order by \"order\" asc")
    public abstract LiveData<List<ProjectInfo>> getProjectInfos();
}
