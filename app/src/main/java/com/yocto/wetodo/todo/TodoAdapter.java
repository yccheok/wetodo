package com.yocto.wetodo.todo;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.yocto.wetodo.Utils;
import com.yocto.wetodo.model.PlainTodo;
import com.yocto.wetodo.model.Todo;

public class TodoAdapter extends PagedListAdapter<Todo, TodoViewHolder> {
    protected TodoAdapter(@NonNull DiffUtil.ItemCallback<Todo> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {

    }

    private static DiffUtil.ItemCallback<Todo> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Todo>() {

            @Override
            public boolean areItemsTheSame(Todo oldTodo, Todo newTodo) {
                final PlainTodo oldPlainTodo = oldTodo.getPlainTodo();
                final PlainTodo newPlainTodo = newTodo.getPlainTodo();
                final long oldId = oldPlainTodo.getId();
                final long newId = newPlainTodo.getId();

                if (Utils.isValidId(oldId) && Utils.isValidId(newId)) {
                    return oldId == newId;
                }

                final String oldUuid = oldPlainTodo.getUuid();
                final String newUuid = newPlainTodo.getUuid();

                return Utils.equals(oldUuid, newUuid);
            }

            @Override
            public boolean areContentsTheSame(Todo oldTodo, Todo newTodo) {
                return oldTodo.equals(newTodo);
            }
        };

}
