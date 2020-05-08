package com.yocto.wetodo.folder;

import androidx.recyclerview.widget.DiffUtil;

import com.yocto.wetodo.Utils;
import com.yocto.wetodo.model.TodoFolder;

import java.util.List;

public class TodoFolderDiffUtilCallback extends DiffUtil.Callback {
    private final List<TodoFolder> newTodoFolders;
    private final List<TodoFolder> oldTodoFolders;
    private final TodoFolder newEditedTodoFolder;
    private final TodoFolder oldEditedTodoFolder;

    public TodoFolderDiffUtilCallback(
            List<TodoFolder> newTodoFolders,
            List<TodoFolder> oldTodoFolders,
            TodoFolder newEditedTodoFolder,
            TodoFolder oldEditedTodoFolder
    ) {
        this.newTodoFolders = newTodoFolders;
        this.oldTodoFolders = oldTodoFolders;

        this.newEditedTodoFolder = newEditedTodoFolder;
        this.oldEditedTodoFolder = oldEditedTodoFolder;
    }

    @Override
    public int getOldListSize() {
        // +1 for footer.
        return oldTodoFolders.size() + 1;
    }

    @Override
    public int getNewListSize() {
        // +1 for footer.
        return newTodoFolders.size() + 1;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        boolean isOldFooter = (oldItemPosition == oldTodoFolders.size());
        boolean isNewFooter = (newItemPosition == newTodoFolders.size());

        if (isOldFooter && isNewFooter) {
            return true;
        }

        if (isOldFooter) {
            return false;
        }

        if (isNewFooter) {
            return false;
        }

        final TodoFolder oldTodoFolder = oldTodoFolders.get(oldItemPosition);
        final TodoFolder newTodoFolder = newTodoFolders.get(newItemPosition);
        final long oldId = oldTodoFolder.getId();
        final long newId = newTodoFolder.getId();

        if (Utils.isValidId(oldId) && Utils.isValidId(newId)) {
            return oldId == newId;
        }

        final String oldUuid = oldTodoFolder.getUuid();
        final String newUuid = newTodoFolder.getUuid();

        return Utils.equals(oldUuid, newUuid);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        boolean isOldFooter = (oldItemPosition == oldTodoFolders.size());
        boolean isNewFooter = (newItemPosition == newTodoFolders.size());

        if (isOldFooter && isNewFooter) {
            return newEditedTodoFolder != null ? newEditedTodoFolder.equals(oldEditedTodoFolder) : oldEditedTodoFolder == null;
        }

        if (isOldFooter) {
            return false;
        }

        if (isNewFooter) {
            return false;
        }

        final TodoFolder oldTodoFolder = oldTodoFolders.get(oldItemPosition);
        final TodoFolder newTodoFolder = newTodoFolders.get(newItemPosition);

        return oldTodoFolder.equals(newTodoFolder);
    }
}

