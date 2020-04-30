package com.yocto.wetodo.repository;

import androidx.lifecycle.LiveData;

import com.yocto.wetodo.model.TodoFolder;

import java.util.List;

import static com.yocto.wetodo.repository.Utils.getExecutorForRepository;

public enum TodoFolderRepository {
    INSTANCE;

    public LiveData<Boolean> isExist(String name) {
        TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
        return todoFolderDao.isExist(name);
    }

    public boolean isExistSync(String name) {
        TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
        return todoFolderDao.isExistSync(name);
    }

    public LiveData<List<TodoFolder>> getTodoFolders() {
        TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
        return todoFolderDao.getTodoFolders();
    }

    public List<TodoFolder> getTodoFoldersSync() {
        TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
        return todoFolderDao.getTodoFoldersSync();
    }

    public void insert(List<TodoFolder> todoFolders) {
        TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
        todoFolderDao.insert(todoFolders);
    }

    public void insertAsync(List<TodoFolder> todoFolders) {
        getExecutorForRepository().execute(() -> {
            insert(todoFolders);
        });
    }

    public void insert(TodoFolder todoFolder, List<UpdateOrder> updateOrders) {
        TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
        todoFolderDao.insert(todoFolder, updateOrders);
    }

    public void insertAsync(TodoFolder todoFolder, List<UpdateOrder> updateOrders) {
        getExecutorForRepository().execute(() -> {
            insert(todoFolder, updateOrders);
        });
    }

    public void updateOrdersAsync(List<UpdateOrder> updateOrders) {
        getExecutorForRepository().execute(() -> {
            TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
            todoFolderDao.updateOrders(updateOrders);
        });
    }

    public void updateNameAsync(long id, String name, String originalName, long syncedTimestamp) {
        getExecutorForRepository().execute(() -> {
            WeTodoRoomDatabase.instance().runInTransaction(() -> {
                TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
                todoFolderDao.updateName(id, name, syncedTimestamp);

                //TODO:
                //NoteDao noteDao = WeNoteRoomDatabase.instance().noteDao();
                //noteDao.renameLabels(originalName, name, syncedTimestamp);
            });
        });
    }

    public void updateColorAsync(long id, int colorIndex, int customColor, long syncedTimestamp) {
        getExecutorForRepository().execute(() -> {
            TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
            todoFolderDao.updateColor(id, colorIndex, customColor, syncedTimestamp);
        });
    }

    public void delete(TodoFolder todoFolder) {
        TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
        todoFolderDao.delete(todoFolder);
    }

    public void permanentDelete(TodoFolder todoFolder) {
        WeTodoRoomDatabase.instance().runInTransaction(() -> {
            final long syncedTimestamp = System.currentTimeMillis();

            TodoFolderDao todoFolderDao = WeTodoRoomDatabase.instance().todoFolderDao();
            todoFolderDao.permanentDelete(todoFolder);

            //TODO:
            //NoteDao noteDao = WeNoteRoomDatabase.instance().noteDao();
            //noteDao.renameLabels(tabInfo.getName(), null, syncedTimestamp);
        });
    }

    public void permanentDeleteAsync(TodoFolder todoFolder) {
        getExecutorForRepository().execute(() -> {
            permanentDelete(todoFolder);
        });
    }
}
