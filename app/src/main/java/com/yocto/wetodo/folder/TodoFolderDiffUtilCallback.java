package com.yocto.wetodo.folder;

import androidx.recyclerview.widget.DiffUtil;

import com.yocto.wetodo.Utils;
import com.yocto.wetodo.model.TodoFolder;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;

import static com.yocto.wetodo.Utils.Assert;

public class TodoFolderDiffUtilCallback extends DiffUtil.Callback {
    private static final int TYPE_LOADING = 1;
    private static final int TYPE_PADDING_TOP = 2;
    private static final int TYPE_CONTENT = 3;
    private static final int TYPE_FOOTER = 4;

    private final boolean newTopPaddingSectionVisible;
    private final boolean oldTopPaddingSectionVisible;
    private final List<TodoFolder> newTodoFolders;
    private final List<TodoFolder> oldTodoFolders;
    private final TodoFolder newEditedTodoFolder;
    private final TodoFolder oldEditedTodoFolder;
    private final boolean newHasFooter;
    private final boolean oldHasFooter;
    private final Section.State newState;
    private final Section.State oldState;

    public TodoFolderDiffUtilCallback(
            boolean newTopPaddingSectionVisible,
            boolean oldTopPaddingSectionVisible,
            List<TodoFolder> newTodoFolders,
            List<TodoFolder> oldTodoFolders,
            TodoFolder newEditedTodoFolder,
            TodoFolder oldEditedTodoFolder,
            boolean newHasFooter,
            boolean oldHasFooter,
            Section.State newState,
            Section.State oldState
    ) {
        this.newTopPaddingSectionVisible = newTopPaddingSectionVisible;
        this.oldTopPaddingSectionVisible = oldTopPaddingSectionVisible;

        this.newTodoFolders = newTodoFolders;
        this.oldTodoFolders = oldTodoFolders;

        this.newEditedTodoFolder = newEditedTodoFolder;
        this.oldEditedTodoFolder = oldEditedTodoFolder;

        this.newHasFooter = newHasFooter;
        this.oldHasFooter = oldHasFooter;

        this.newState = newState;
        this.oldState = oldState;

        Assert (this.newState == Section.State.LOADED || this.newState == Section.State.LOADING);
        Assert (this.oldState == Section.State.LOADED || this.oldState == Section.State.LOADING);
        Assert ((this.newTopPaddingSectionVisible && this.newState == Section.State.LOADED) || (!this.newTopPaddingSectionVisible && this.newState == Section.State.LOADING));
        Assert ((this.oldTopPaddingSectionVisible && this.oldState == Section.State.LOADED) || (!this.oldTopPaddingSectionVisible && this.oldState == Section.State.LOADING));
        Assert ((this.newHasFooter && this.newState == Section.State.LOADED) || (!this.newHasFooter && this.newState == Section.State.LOADING));
        Assert ((this.oldHasFooter && this.oldState == Section.State.LOADED) || (!this.oldHasFooter && this.oldState == Section.State.LOADING));
    }

    @Override
    public int getOldListSize() {
        int oldSize = 0;

        if (oldState == Section.State.LOADING) {
            return oldSize + 1;
        }

        Assert (oldState == Section.State.LOADED);

        if (oldTopPaddingSectionVisible) {
            oldSize++;
        }

        if (oldHasFooter) {
            oldSize++;
        }

        oldSize = oldSize + oldTodoFolders.size();

        return oldSize;
    }

    @Override
    public int getNewListSize() {
        int newSize = 0;

        if (newState == Section.State.LOADING) {
            return newSize + 1;
        }

        Assert (newState == Section.State.LOADED);

        if (newTopPaddingSectionVisible) {
            newSize++;
        }

        if (newHasFooter) {
            newSize++;
        }

        newSize = newSize + newTodoFolders.size();

        return newSize;
    }

    private int getOldType(int oldItemPosition) {
        if (oldItemPosition == 0) {
            if (oldState == Section.State.LOADING) {
                return TYPE_LOADING;
            }

            if (oldTopPaddingSectionVisible) {
                return TYPE_PADDING_TOP;
            }
        }

        if (oldHasFooter) {
            if (oldItemPosition == (getOldListSize()-1)) {
                return TYPE_FOOTER;
            }
        }

        return TYPE_CONTENT;
    }

    private int getNewType(int newItemPosition) {
        if (newItemPosition == 0) {
            if (newState == Section.State.LOADING) {
                return TYPE_LOADING;
            }

            if (newTopPaddingSectionVisible) {
                return TYPE_PADDING_TOP;
            }
        }

        if (newHasFooter) {
            if (newItemPosition == (getNewListSize()-1)) {
                return TYPE_FOOTER;
            }
        }

        return TYPE_CONTENT;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        final int oldType = getOldType(oldItemPosition);
        final int newType = getNewType(newItemPosition);

        if (oldType == TYPE_FOOTER && newType == TYPE_FOOTER) {
            return true;
        } else if (oldType != TYPE_CONTENT) {
            return oldType == newType;
        }

        if (newType != TYPE_CONTENT) {
            return false;
        }

        Assert (oldType == TYPE_CONTENT);
        Assert (newType == TYPE_CONTENT);

        final TodoFolder oldTodoFolder = oldTodoFolders.get(oldTopPaddingSectionVisible ? oldItemPosition-1 : oldItemPosition);
        final TodoFolder newTodoFolder = newTodoFolders.get(newTopPaddingSectionVisible ? newItemPosition-1 : newItemPosition);

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
        final int oldType = getOldType(oldItemPosition);
        final int newType = getNewType(newItemPosition);

        if (oldType == TYPE_FOOTER && newType == TYPE_FOOTER) {
            return Utils.equals(newEditedTodoFolder, oldEditedTodoFolder);
        } else if (oldType != TYPE_CONTENT) {
            return oldType == newType;
        }

        if (newType != TYPE_CONTENT) {
            return false;
        }

        Assert (oldType == TYPE_CONTENT);
        Assert (newType == TYPE_CONTENT);

        final TodoFolder oldTodoFolder = oldTodoFolders.get(oldTopPaddingSectionVisible ? oldItemPosition-1 : oldItemPosition);
        final TodoFolder newTodoFolder = newTodoFolders.get(newTopPaddingSectionVisible ? newItemPosition-1 : newItemPosition);

        return oldTodoFolder.equals(newTodoFolder);
    }
}
