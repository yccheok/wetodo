package com.yocto.wetodo.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yocto.wetodo.R;
import com.yocto.wetodo.model.SimpleTodo;
import com.yocto.wetodo.model.Todo;

public class TodoableAdapter extends ListAdapter<Todoable,  RecyclerView.ViewHolder> {
    private final TodoDashboardFragment todoDashboardFragment;

    private class TodoViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecyclerView recyclerView = todoDashboardFragment.getRecyclerView();
                    int position = recyclerView.getChildAdapterPosition(view);
                    Todoable todoable = getItem(position);
                    Todo todo = (Todo)todoable;
                    todoDashboardFragment.toggle(todo);
                }
            });
        }
    }

    private class SimpleTodoViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        public SimpleTodoViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
        }
    }

    public TodoableAdapter(TodoDashboardFragment todoDashboardFragment) {
        super(DIFF_CALLBACK);

        this.todoDashboardFragment = todoDashboardFragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Todoable.TODO_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_adapter, parent, false);
            return new TodoViewHolder(view);
        } else if (viewType == Todoable.SIMPLE_TODO_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_todo_adapter, parent, false);
            return new SimpleTodoViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Todoable todoable = getItem(position);
        final int itemViewType = todoable.getItemViewType();

        if (itemViewType == Todoable.TODO_TYPE) {
            onBindTodoViewHolder((TodoViewHolder)holder, (Todo)todoable);
            return;
        }

        if (itemViewType == Todoable.SIMPLE_TODO_TYPE) {
            onBindSimpleTodoViewHolder((SimpleTodoViewHolder)holder, (SimpleTodo)todoable);
            return;
        }
    }

    private void onBindTodoViewHolder(TodoViewHolder todoViewHolder, Todo todo) {
        todoViewHolder.titleTextView.setText(todo.getPlainTodo().getTitle());
    }

    private void onBindSimpleTodoViewHolder(SimpleTodoViewHolder simpleTodoViewHolder, SimpleTodo simpleTodo) {
        simpleTodoViewHolder.titleTextView.setText(simpleTodo.getTitle());
    }

    @Override
    public int getItemViewType(int position) {
        Todoable todoable = getItem(position);
        return todoable.getItemViewType();
    }

    private static DiffUtil.ItemCallback<Todoable> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Todoable>() {

            @Override
            public boolean areItemsTheSame(Todoable oldTodoable, Todoable newTodoable) {
                return oldTodoable.getItemViewType() == newTodoable.getItemViewType() && oldTodoable.getId() == newTodoable.getId();
            }

            @Override
            public boolean areContentsTheSame(Todoable oldTodoable, Todoable newTodoable) {
                return oldTodoable.equals(newTodoable);
            }
        };
}
