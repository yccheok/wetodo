package com.yocto.wetodo.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.yocto.wetodo.R;
import com.yocto.wetodo.Utils;
import com.yocto.wetodo.model.PlainTodo;
import com.yocto.wetodo.model.Todo;

public class TodoAdapter extends PagedListAdapter<Todo, TodoAdapter.ViewHolder> {
    protected TodoAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public TodoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_adapter, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TodoAdapter.ViewHolder holder, int position) {
        Todo todo = getItem(position);

        if (todo == null) {
            return;
        }

        final TextView titleTextView = holder.titleTextView;

        titleTextView.setText(todo.getPlainTodo().getTitle());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.title_text_view);
        }
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
