package com.yocto.wetodo.todo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.yocto.wetodo.folder.TodoFolderSettingsFragment;
import com.yocto.wetodo.model.TodoFolder;

import java.util.List;

import static com.yocto.wetodo.Utils.Assert;
import static com.yocto.wetodo.model.TodoFolder.Type.Custom;
import static com.yocto.wetodo.model.TodoFolder.Type.Settings;

public class TodoFragmentStateAdapter extends FragmentStateAdapter {
    private final List<TodoFolder> todoFoldersReference;

    public TodoFragmentStateAdapter(@NonNull Fragment fragment, List<TodoFolder> todoFoldersReference) {
        super(fragment);

        this.todoFoldersReference = todoFoldersReference;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        final TodoFolder todoFolder = this.todoFoldersReference.get(position);
        final TodoFolder.Type type = todoFolder.getType();

        if (type == TodoFolder.Type.Inbox || type == Custom) {
            return TodoDashboardFragment.newInstance(todoFolder);
        } else if (type == Settings) {
            return TodoFolderSettingsFragment.newInstance();
        } else {
            Assert (false);
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return todoFoldersReference.size();
    }

    @Override
    public long getItemId(int position) {
        return todoFoldersReference.get(position).getId();
    }

    @Override
    public boolean containsItem (long itemId) {
        // TODO: Optimization required.
        
        for (TodoFolder todoFolder : todoFoldersReference) {
            if (todoFolder.getId() == itemId) {
                return true;
            }
        }
        return false;
    }
}
