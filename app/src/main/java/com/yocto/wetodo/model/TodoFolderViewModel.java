package com.yocto.wetodo.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yocto.wetodo.WeTodoOptions;
import com.yocto.wetodo.repository.TodoFolderRepository;
import com.yocto.wetodo.repository.UpdateOrder;

import java.util.List;

public class TodoFolderViewModel extends ViewModel {
    private final LiveData<List<TodoFolder>> todoFoldersLiveData;

    public TodoFolderViewModel() {
        todoFoldersLiveData = TodoFolderRepository.INSTANCE.getTodoFolders();
    }

    public LiveData<List<TodoFolder>> getTodoFoldersLiveData() {
        return todoFoldersLiveData;
    }

    public void insert(List<TodoFolder> todoFolders) {
        TodoFolderRepository.INSTANCE.insert(todoFolders);

        WeTodoOptions.setSyncRequired(true);
    }

    public void insertAsync(List<TodoFolder> todoFolders) {
        TodoFolderRepository.INSTANCE.insertAsync(todoFolders);

        WeTodoOptions.setSyncRequired(true);
    }

    public void insertAsync(TodoFolder todoFolder, List<UpdateOrder> updateOrders) {
        TodoFolderRepository.INSTANCE.insertAsync(todoFolder, updateOrders);

        WeTodoOptions.setSyncRequired(true);
    }

    public void updateNameAsync(long id, String name, String originalName, long syncedTimestamp) {
        TodoFolderRepository.INSTANCE.updateNameAsync(id, name, originalName, syncedTimestamp);

        WeTodoOptions.setSyncRequired(true);
    }

    public void updateColorAsync(long id, int colorIndex, int customColor, long syncedTimestamp) {
        TodoFolderRepository.INSTANCE.updateColorAsync(id, colorIndex, customColor, syncedTimestamp);

        WeTodoOptions.setSyncRequired(true);
    }

    public void updateOrdersAsync(List<UpdateOrder> updateOrders) {
        TodoFolderRepository.INSTANCE.updateOrdersAsync(updateOrders);

        // Not need WeTodoOptions.setSyncRequired(true); if order is changed. Just like we don't
        // provide syncedTimestamp for updateOrdersAsync.
    }

    public void permanentDeleteAsync(TodoFolder todoFolder) {
        TodoFolderRepository.INSTANCE.permanentDeleteAsync(todoFolder);

        WeTodoOptions.setSyncRequired(true);
    }

    public void permanentDelete(TodoFolder todoFolder) {
        TodoFolderRepository.INSTANCE.permanentDelete(todoFolder);

        WeTodoOptions.setSyncRequired(true);
    }

    public void delete(TodoFolder todoFolder) {
        TodoFolderRepository.INSTANCE.delete(todoFolder);

        WeTodoOptions.setSyncRequired(true);
    }
}
