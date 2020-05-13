package com.yocto.wetodo.folder;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.yocto.wetodo.R;
import com.yocto.wetodo.WeTodoOptions;
import com.yocto.wetodo.model.TodoFolder;
import com.yocto.wetodo.model.TodoFolderViewModel;

public class TodoFolderConfirmDeleteDialogFragment extends DialogFragment {
    private static final String INTENT_EXTRA_TODO_FOLDER = "INTENT_EXTRA_TODO_FOLDER";

    public static TodoFolderConfirmDeleteDialogFragment newInstance(TodoFolder todoFolder) {
        TodoFolderConfirmDeleteDialogFragment todoFolderConfirmDeleteDialogFragment = new TodoFolderConfirmDeleteDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(INTENT_EXTRA_TODO_FOLDER, todoFolder);
        todoFolderConfirmDeleteDialogFragment.setArguments(arguments);
        return todoFolderConfirmDeleteDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle arguments = getArguments();
        TodoFolder todoFolder = arguments.getParcelable(INTENT_EXTRA_TODO_FOLDER);

        String message = getString(R.string.remove_tag_template, todoFolder.getName());

        final FragmentActivity fragmentActivity = getActivity();
        final ViewModelProvider viewModelProvider = new ViewModelProvider(fragmentActivity);

        return new AlertDialog.Builder(fragmentActivity)
                .setMessage(message)
                .setPositiveButton(R.string.remove, (dialog, which) -> {
                    // A dirty hacking way, to ensure we stay at Tab Settings page.
                    WeTodoOptions.setSelectedTodoFolderIndex(
                            WeTodoOptions.getSelectedTodoFolderIndex()-1
                    );

                    viewModelProvider.get(TodoFolderViewModel.class).permanentDeleteAsync(todoFolder);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
