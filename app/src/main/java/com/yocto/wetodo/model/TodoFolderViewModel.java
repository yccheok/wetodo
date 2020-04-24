package com.yocto.wetodo.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TodoFolderViewModel extends ViewModel {
    private final LiveData<List<TodoFolder>> todoFoldersLiveData;

    public TodoFolderViewModel() {
        todoFoldersLiveData = TodoFolderRepository.INSTANCE.getTodoFolders();
    }

    public LiveData<List<TodoFolder>> getTabInfosLiveData() {
        return todoFoldersLiveData;
    }
}
