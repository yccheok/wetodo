package com.yocto.wetodo.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.yocto.wetodo.model.TodoFolder;
import com.yocto.wetodo.model.TodoFolderTrash;

import java.util.List;

@Dao
public abstract class TodoFolderDao {
    @Query("SELECT EXISTS(SELECT 1 FROM todo_folder WHERE name = :name LIMIT 1)")
    public abstract LiveData<Boolean> isExist(String name);

    @Query("SELECT EXISTS(SELECT 1 FROM todo_folder WHERE name = :name LIMIT 1)")
    public abstract boolean isExistSync(String name);

    @Query("SELECT * FROM todo_folder order by \"order\" asc")
    public abstract List<TodoFolder> getTodoFoldersSync();

    @Query("SELECT * FROM todo_folder where type = :type order by \"order\" asc")
    public abstract List<TodoFolder> getTodoFoldersSync(int type);

    @Query("SELECT * FROM todo_folder order by \"order\" asc")
    public abstract LiveData<List<TodoFolder>> getTodoFolders();

    @Query("SELECT * FROM todo_folder_trash")
    public abstract List<TodoFolderTrash> getTodoFolderTrashes();

    @Query("UPDATE todo_folder SET 'order' = :order WHERE id = :id")
    public abstract void updateOrder(long id, int order);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract List<Long> insert(List<TodoFolder> todoFolders);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(TodoFolder todoFolder);

    @Transaction
    public void insert(TodoFolder todoFolder, List<UpdateOrder> updateOrders) {
        updateOrders(updateOrders);
        insert(todoFolder);
    }

    @Transaction
    public void updateOrders(List<UpdateOrder> updateOrders) {
        for (UpdateOrder updateOrder : updateOrders) {
            updateOrder(updateOrder.id, updateOrder.order);
        }
    }

    @Transaction
    public void updateOrders() {
        List<UpdateOrder> updateOrders = com.yocto.wetodo.Utils.updateTodoFolderOrder(getTodoFoldersSync());
        updateOrders(updateOrders);
    }

    @Query("UPDATE todo_folder SET name = :name, synced_timestamp = :syncedTimestamp WHERE id = :id")
    public abstract void updateName(long id, String name, long syncedTimestamp);

    @Query("UPDATE todo_folder SET color_index = :colorIndex, custom_color = :customColor, synced_timestamp = :syncedTimestamp WHERE id = :id")
    public abstract void updateColor(long id, int colorIndex, int customColor, long syncedTimestamp);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(TodoFolderTrash todoFolderTrash);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertTodoFolderTrashes(List<TodoFolderTrash> todoFolderTrashes);

    @Delete
    public abstract void delete(TodoFolder todoFolder);

    @Query("DELETE FROM todo_folder_trash WHERE synced_timestamp < :minSyncedTimestamp")
    public abstract void deleteOldTodoFolderTrash(long minSyncedTimestamp);

    @Query("DELETE FROM todo_folder_trash WHERE uuid = :uuid")
    public abstract int deleteTodoFolderTrash(String uuid);

    @Transaction
    public void permanentDelete(TodoFolder todoFolder) {
        delete(todoFolder);
        TodoFolderTrash todoFolderTrash = new TodoFolderTrash(todoFolder.getUuid());
        insert(todoFolderTrash);
    }

    @Transaction
    public void permanentDelete(List<TodoFolder> todoFolders) {
        for (TodoFolder todoFolder : todoFolders) {
            permanentDelete(todoFolder);
        }
    }
}
