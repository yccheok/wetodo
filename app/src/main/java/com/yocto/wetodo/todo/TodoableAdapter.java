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
import com.yocto.wetodo.model.Todoable;

public class TodoableAdapter extends PagedListAdapter<Todoable, TodoableAdapter.ViewHolder> {
    protected TodoableAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public TodoableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_adapter, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TodoableAdapter.ViewHolder holder, int position) {
        Todoable todoable = getItem(position);

        if (todoable == null) {
            return;
        }

        final TextView titleTextView = holder.titleTextView;

        titleTextView.setText(todoable.getTitle());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.title_text_view);
        }
    }

    private static DiffUtil.ItemCallback<Todoable> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Todoable>() {

            @Override
            public boolean areItemsTheSame(Todoable oldTodoable, Todoable newTodoable) {
                return oldTodoable.getId() == newTodoable.getId();
            }

            @Override
            public boolean areContentsTheSame(Todoable oldTodoable, Todoable newTodoable) {
                return oldTodoable.equals(newTodoable);
            }
        };

}
