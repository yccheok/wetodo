package com.yocto.wetodo.folder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.yocto.wetodo.MainActivity;
import com.yocto.wetodo.R;
import com.yocto.wetodo.Utils;
import com.yocto.wetodo.WeTodoOptions;
import com.yocto.wetodo.helper.ItemTouchHelperAdapter;
import com.yocto.wetodo.helper.ItemTouchHelperViewHolder;
import com.yocto.wetodo.model.TodoFolder;
import com.yocto.wetodo.repository.UpdateOrder;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import static com.yocto.wetodo.Utils.dpToPixel;
import static com.yocto.wetodo.Utils.ready;
import static com.yocto.wetodo.Utils.sortTodoFoldersBasedOnOriginal;
import static com.yocto.wetodo.font.Utils.getBodyTypeFace;
import static com.yocto.wetodo.model.Utils.getCountByLabel;
import static com.yocto.wetodo.ui.Utils.getColor;
import static com.yocto.wetodo.ui.Utils.isCustomColorIndex;
import static com.yocto.wetodo.ui.Utils.isDarkTheme;
import static com.yocto.wetodo.ui.Utils.isLightBackgroundColor;
import static com.yocto.wetodo.ui.Utils.isPureDarkTheme;

public class TodoFolderSection extends Section implements ItemTouchHelperAdapter {
    private final TodoFolderSettingsFragment todoFolderSettingsFragment;
    private int colorPickerBorderColor;
    private int borderWidthPx;

    private void init(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.colorPickerBorderColor, typedValue, true);
        colorPickerBorderColor = typedValue.data;
        borderWidthPx = dpToPixel(1);
    }

    public TodoFolderSection(TodoFolderSettingsFragment todoFolderSettingsFragment) {
        super(SectionParameters.builder().itemResourceId(R.layout.todo_folder_item_section)
                .footerResourceId(R.layout.todo_folder_footer_section)
                .loadingResourceId(R.layout.todo_folder_loading_section)
                .build());

        this.todoFolderSettingsFragment = todoFolderSettingsFragment;

        init(this.todoFolderSettingsFragment.getContext());
    }

    @Override
    public int getContentItemsTotal() {
        return this.todoFolderSettingsFragment.getFilteredTodoFolders().size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(View view) {
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
        TodoFolder editedTodoFolder = this.todoFolderSettingsFragment.getEditedTodoFolder();

        FooterViewHolder footerViewHolder = (FooterViewHolder)holder;

        if (editedTodoFolder == null) {
            footerViewHolder.todoFolderItemSection.setVisibility(View.GONE);
            footerViewHolder.addImageButton.setVisibility(View.VISIBLE);
            footerViewHolder.addItemTextView.setVisibility(View.VISIBLE);
            footerViewHolder.editText.clearFocus();
            return;
        }

        footerViewHolder.todoFolderItemSection.setVisibility(View.VISIBLE);
        footerViewHolder.addImageButton.setVisibility(View.GONE);
        footerViewHolder.addItemTextView.setVisibility(View.GONE);

        footerViewHolder.editText.requestFocus();
        Utils.placeCursorAtEndOfText(footerViewHolder.editText);

        footerViewHolder.setColor(editedTodoFolder.getColor());
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TodoFolder todoFolder = this.todoFolderSettingsFragment.getFilteredTodoFolders().get(position);

        ItemViewHolder itemViewHolder = (ItemViewHolder)holder;

        itemViewHolder.editText.setTag(R.id.todo_folder, todoFolder);

        if (todoFolder.isImmutableType()) {
            itemViewHolder.deleteImageButton.setVisibility(View.INVISIBLE);
            itemViewHolder.editText.setVisibility(View.GONE);
            itemViewHolder.textView.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.deleteImageButton.setVisibility(View.VISIBLE);
            itemViewHolder.editText.setVisibility(View.VISIBLE);
            itemViewHolder.textView.setVisibility(View.GONE);
        }

        final String pageTitle = Utils.getPageTitle(todoFolder);
        itemViewHolder.textView.setText(pageTitle);
        itemViewHolder.editText.setText(pageTitle);

        if (todoFolder.isFocused()) {
            itemViewHolder.dragImageButton.setVisibility(View.GONE);
            itemViewHolder.confirmImageButton.setVisibility(View.VISIBLE);

            // Handled in onViewAttachedToWindow.
            // onBindItemViewHolder will not called all the time when a view is visible.
            // It will only be called when a view is recycled.
            /*
            itemViewHolder.editText.requestFocus();
            Utils.placeCursorAtEndOfText(itemViewHolder.editText);
            */
        } else {
            itemViewHolder.dragImageButton.setVisibility(View.VISIBLE);
            itemViewHolder.confirmImageButton.setVisibility(View.GONE);
            itemViewHolder.editText.post(itemViewHolder.editText::clearFocus);
        }

        final int color = todoFolder.getColor();
        if (isCustomColorIndex(todoFolder.getColorIndex())) {
            if (isDarkTheme() || isPureDarkTheme()) {
                if (isLightBackgroundColor(color)) {
                    itemViewHolder.setColor(color, getColor(android.R.color.transparent));
                } else {
                    itemViewHolder.setColor(color, colorPickerBorderColor);
                }
            } else {
                if (isLightBackgroundColor(color)) {
                    itemViewHolder.setColor(color, colorPickerBorderColor);
                } else {
                    itemViewHolder.setColor(color, getColor(android.R.color.transparent));
                }
            }
        } else {
            itemViewHolder.setColor(color, getColor(android.R.color.transparent));
        }

    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (roomNotReady()) {
            return false;
        }

        SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter = todoFolderSettingsFragment.getSectionedRecyclerViewAdapter();

        Section section = sectionedRecyclerViewAdapter.getSectionForPosition(fromPosition);

        if (section != this) {
            return false;
        }

        final int fromPositionInSection = sectionedRecyclerViewAdapter.getPositionInSection(fromPosition);
        final int toPositionInSection = sectionedRecyclerViewAdapter.getPositionInSection(toPosition);

        final List<TodoFolder> filteredTodoFolders = todoFolderSettingsFragment.getFilteredTodoFolders();
        final TodoFolder fromTodoFolder = filteredTodoFolders.get(fromPositionInSection);
        final TodoFolder toTodoFolder = filteredTodoFolders.get(toPositionInSection);

        final List<TodoFolder> todoFolders = todoFolderSettingsFragment.getTodoFolderViewModel().getTodoFoldersLiveData().getValue();

        final int fromMasterPosition = todoFolders.indexOf(fromTodoFolder);
        final int toMasterPosition = todoFolders.indexOf(toTodoFolder);

        TodoFolder from = todoFolders.get(fromMasterPosition);
        TodoFolder to = todoFolders.get(toMasterPosition);
        todoFolders.set(fromMasterPosition, to);
        todoFolders.set(toMasterPosition, from);

        // For optimization, we don't update the order id yet.

        todoFolderSettingsFragment.onChanged();

        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public final View itemView;
        public final ImageButton deleteImageButton;
        public final ImageButton colorImageButton;
        public final EditText editText;
        public final TextView textView;
        public final ImageButton dragImageButton;
        public final ImageButton confirmImageButton;

        private void setFocused(TodoFolder todoFolder, boolean focused) {
            final List<TodoFolder> filteredTodoFolders = todoFolderSettingsFragment.getFilteredTodoFolders();
            for (TodoFolder t : filteredTodoFolders) {
                if (t == todoFolder) {
                    todoFolder.setFocused(focused);
                } else {
                    t.setFocused(false);
                }
            }
        }

        public ItemViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.deleteImageButton = itemView.findViewById(R.id.delete_image_button);
            this.colorImageButton = itemView.findViewById(R.id.color_image_button);
            this.editText = itemView.findViewById(R.id.edit_text);
            this.textView = itemView.findViewById(R.id.text_view);
            this.dragImageButton = itemView.findViewById(R.id.drag_image_button);
            this.confirmImageButton = itemView.findViewById(R.id.confirm_image_button);

            this.dragImageButton.setVisibility(View.VISIBLE);
            this.confirmImageButton.setVisibility(View.GONE);

            Utils.setCustomTypeFace(this.editText, getBodyTypeFace());
            Utils.setCustomTypeFace(this.textView, getBodyTypeFace());

            this.textView.setOnClickListener(view -> {
                Utils.hideKeyboard(todoFolderSettingsFragment);
                setFocused(null, false);
                todoFolderSettingsFragment.onChanged();
            });

            this.editText.setOnEditorActionListener((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_NULL) {
                    commit(editText);
                    return true;
                }

                return false;
            });

            this.editText.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    editText.setCursorVisible(false);
                    editText.setCursorVisible(true);

                    TodoFolder todoFolder = (TodoFolder)editText.getTag(R.id.todo_folder);

                    boolean focused = todoFolder.isFocused();
                    if (focused) {
                        editText.requestFocus();
                        Utils.placeCursorAtEndOfText(editText);
                    }
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                }
            });

            this.confirmImageButton.setOnClickListener(view -> {
                commit(editText);
            });

            this.dragImageButton.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    todoFolderSettingsFragment.getOnStartDragListener().onStartDrag(this);
                    return true;
                }

                return false;
            });


            this.editText.setOnFocusChangeListener((view, b) -> {
                final int positionInSection = getPositionInSection();
                if (positionInSection < 0) {
                    return;
                }
                final TodoFolder todoFolder = getTodoFolder(positionInSection);

                if (b) {
                    dragImageButton.setVisibility(View.GONE);
                    confirmImageButton.setVisibility(View.VISIBLE);

                    setFocused(todoFolder, true);

                } else {
                    dragImageButton.setVisibility(View.VISIBLE);
                    confirmImageButton.setVisibility(View.GONE);

                    editText.setText(todoFolder.getName());
                }
            });

            this.deleteImageButton.setOnClickListener(view -> {
                if (roomNotReady()) {
                    return;
                }

                final int positionInSection = getPositionInSection();
                if (positionInSection < 0) {
                    return;
                }
                final TodoFolder todoFolder = getTodoFolder(positionInSection);

                final LiveData<Integer> countLiveData = getCountByLabel(todoFolder.getName());

                ready(countLiveData, todoFolderSettingsFragment, integer -> {
                    if (integer > 0) {
                        todoFolderSettingsFragment.showTodoFolderConfirmDeleteDialogFragment(todoFolder);
                    } else {
                        final List<TodoFolder> todoFolders = todoFolderSettingsFragment.getTodoFolderViewModel().getTodoFoldersLiveData().getValue();

                        todoFolders.remove(todoFolder);

                        todoFolderSettingsFragment.onChanged();
                        todoFolderSettingsFragment.postWaitForAnimationsToFinish(() -> todoFolderSettingsFragment.getTodoFolderViewModel().permanentDeleteAsync(todoFolder));

                        // If the deleted tab info is currently on focus...
                        if (todoFolder.isFocused()) {
                            Utils.hideKeyboard(todoFolderSettingsFragment);
                        }
                    }
                });
            });

            this.colorImageButton.setOnClickListener(view -> {
                if (roomNotReady()) {
                    return;
                }

                final int positionInSection = getPositionInSection();
                if (positionInSection < 0) {
                    return;
                }
                final TodoFolder todoFolder = getTodoFolder(positionInSection);

                todoFolderSettingsFragment.showColorPickerDialog(positionInSection, todoFolder.getColor());
            });
        }

        private int getPositionInSection() {
            final int position = todoFolderSettingsFragment.getRecyclerView().getChildAdapterPosition(itemView);

            if (position < 0) {
                return -1;
            }

            final SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter = todoFolderSettingsFragment.getSectionedRecyclerViewAdapter();
            final int positionInSection = sectionedRecyclerViewAdapter.getPositionInSection(position);
            return positionInSection;
        }

        private TodoFolder getTodoFolder(int positionInSection) {
            final TodoFolder todoFolder = todoFolderSettingsFragment.getFilteredTodoFolders().get(positionInSection);
            return todoFolder;
        }

        private void commit(EditText editText) {
            if (roomNotReady()) {
                return;
            }

            final int positionInSection = getPositionInSection();
            if (positionInSection < 0) {
                return;
            }
            final TodoFolder todoFolder = getTodoFolder(positionInSection);

            boolean needToUpdte = false;

            final List<TodoFolder> filteredTodoFolers = todoFolderSettingsFragment.getFilteredTodoFolders();

            final String name = editText.getText().toString().trim();
            final String originalName = todoFolder.getName();

            if (Utils.isNullOrEmpty(name)) {
                editText.setText(originalName);
            } else if (!name.equals(originalName)) {
                // Naming clashed?
                boolean clashed = false;
                for (TodoFolder t : filteredTodoFolers) {
                    if (name.equals(t.getName())) {
                        clashed = true;
                        break;
                    }
                }

                if (clashed) {
                    String string = todoFolderSettingsFragment.getString(R.string.another_tab_with_same_name_template, name);
                    showConflictSnackbar(string);
                    return;
                } else {
                    todoFolder.setName(name);
                    needToUpdte = true;
                }

                editText.setText(name);
            } else {
                editText.setText(name);
            }

            Utils.hideKeyboard(todoFolderSettingsFragment);

            final long syncedTimestamp = System.currentTimeMillis();
            todoFolder.setFocused(false);
            todoFolder.setSyncedTimestamp(syncedTimestamp);
            todoFolderSettingsFragment.onChanged();

            if (needToUpdte) {
                todoFolderSettingsFragment.postWaitForAnimationsToFinish(() -> todoFolderSettingsFragment.getTodoFolderViewModel().updateNameAsync(todoFolder.getId(), name, originalName, syncedTimestamp));
            }
        }

        public void setColor(int color, int borderColor) {
            GradientDrawable gradientDrawable = ((GradientDrawable)this.colorImageButton.getDrawable());
            gradientDrawable.setColor(color);
            gradientDrawable.setStroke(borderWidthPx, borderColor);
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {
            // For optimization, we only update the order id here.
            List<UpdateOrder> updateOrders = Utils.updateTodoFolderOrder(todoFolderSettingsFragment.getTodoFolderViewModel().getTodoFoldersLiveData().getValue());
            todoFolderSettingsFragment.getTodoFolderViewModel().updateOrdersAsync(updateOrders);
        }
    }

    private void showConflictSnackbar(String message) {
        MainActivity mainActivity = (MainActivity) todoFolderSettingsFragment.getActivity();

        mainActivity.showSnackbar(message, R.string.dismiss, v -> {
            mainActivity.hideSnackbar();
        });
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public final ImageButton addImageButton;
        public final TextView addItemTextView;
        public final View todoFolderItemSection;

        public final ImageButton deleteImageButton;
        public final ImageButton colorImageButton;
        public final EditText editText;
        public final TextView textView;
        public final ImageButton dragImageButton;
        public final ImageButton confirmImageButton;

        public FooterViewHolder(View itemView) {
            super(itemView);

            this.addImageButton = itemView.findViewById(R.id.add_image_button);
            this.addItemTextView = itemView.findViewById(R.id.add_item_text_view);
            this.todoFolderItemSection = itemView.findViewById(R.id.todo_folder_item_section);
            this.deleteImageButton = itemView.findViewById(R.id.delete_image_button);
            this.colorImageButton = itemView.findViewById(R.id.color_image_button);
            this.editText = itemView.findViewById(R.id.edit_text);
            this.textView = itemView.findViewById(R.id.text_view);
            this.dragImageButton = itemView.findViewById(R.id.drag_image_button);
            this.confirmImageButton = itemView.findViewById(R.id.confirm_image_button);

            this.dragImageButton.setVisibility(View.GONE);
            this.confirmImageButton.setVisibility(View.VISIBLE);

            Utils.setCustomTypeFace(this.addItemTextView, getBodyTypeFace());
            Utils.setCustomTypeFace(this.editText, getBodyTypeFace());

            this.deleteImageButton.setOnClickListener(view -> {
                exitEditMode();
                Utils.hideKeyboard(todoFolderSettingsFragment);
            });

            this.confirmImageButton.setOnClickListener(view -> {
                commit(editText);
            });

            this.editText.setOnEditorActionListener((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_NULL) {
                    commit(editText);
                    return true;
                }

                return false;
            });

            this.editText.setOnFocusChangeListener((view, b) -> {
                if (b) {
                } else {
                    exitEditMode();
                }
            });

            this.addImageButton.setOnClickListener(view -> enterEditMode());

            this.addItemTextView.setOnClickListener(view -> enterEditMode());

            this.colorImageButton.setOnClickListener(view -> {
                final TodoFolder latestTodoFolder = todoFolderSettingsFragment.getEditedTodoFolder();

                if (latestTodoFolder == null) {
                    // Not much we can do.
                    return;
                }

                todoFolderSettingsFragment.showColorPickerDialog(
                        -1,
                        latestTodoFolder.getColor()
                );
            });
        }

        private void commit(EditText editText) {
            boolean needToUpdte = false;

            final List<TodoFolder> filteredTodoFolders = todoFolderSettingsFragment.getFilteredTodoFolders();

            final TodoFolder editedTodoFolder = todoFolderSettingsFragment.getEditedTodoFolder();
            final String name = editText.getText().toString().trim();

            if (!Utils.isNullOrEmpty(name)) {
                // Naming clashed?
                boolean clashed = false;
                for (TodoFolder t : filteredTodoFolders) {
                    if (name.equals(t.getName())) {
                        clashed = true;
                        break;
                    }
                }

                if (clashed) {
                    String string = todoFolderSettingsFragment.getString(R.string.another_tab_with_same_name_template, name);
                    showConflictSnackbar(string);
                    return;
                } else {
                    editedTodoFolder.setName(name);
                    needToUpdte = true;
                }
            }

            editText.clearFocus();
            Utils.hideKeyboard(todoFolderSettingsFragment);

            if (needToUpdte) {
                todoFolderSettingsFragment.setScrollToBottom(true);

                // The following is still a valid concern. But, since insertion is too slow, we are
                // forced to use UX optimization.
                //
                // Don't perform any UX optimization right here. This is because if we add a record
                // without id into our single source of truth, any sub-sequent operation like delete
                // , move, modification will become complicated. How can we handle delete, move,
                // modification on data without id? It is better to avoid UX optimization, and let
                // live data observer return a complete set data with ids.

                LiveData<List<TodoFolder>> todoFoldersLiveData = todoFolderSettingsFragment.getTodoFolderViewModel().getTodoFoldersLiveData();
                ready(todoFoldersLiveData, todoFolderSettingsFragment, todoFolders -> {
                    final long syncedTimestamp = System.currentTimeMillis();
                    editedTodoFolder.setSyncedTimestamp(syncedTimestamp);

                    todoFolders.add(editedTodoFolder);

                    // A dirty hacking way, to ensure we stay at Tab Settings page.
                    final int size = todoFolders.size();
                    WeTodoOptions.setSelectedTodoFolderIndex(size - 1);
                    WeTodoOptions.INSTANCE.setSelectedTodoFolder(todoFolders.get(size - 1).copy());

                    // Ensure Settings tab info is having correct position.
                    sortTodoFoldersBasedOnOriginal(todoFolders);

                    List<UpdateOrder> updateOrders = Utils.updateTodoFolderOrder(todoFolders);

                    todoFolderSettingsFragment.onChanged();
                    todoFolderSettingsFragment.postWaitForAnimationsToFinish(() -> todoFolderSettingsFragment.getTodoFolderViewModel().insertAsync(editedTodoFolder, updateOrders));
                });
            }
        }

        private void exitEditMode() {
            this.todoFolderItemSection.setVisibility(View.GONE);
            this.addImageButton.setVisibility(View.VISIBLE);
            this.addItemTextView.setVisibility(View.VISIBLE);
            this.editText.clearFocus();
            todoFolderSettingsFragment.setEditedTodoFolder(null);
        }

        private void enterEditMode() {
            final List<TodoFolder> filteredTodoFolders = todoFolderSettingsFragment.getFilteredTodoFolders();
            final TodoFolder lastFilteredTodoFolder = filteredTodoFolders.get(filteredTodoFolders.size() - 1);

            // We want standard color, not custom color for next color.
            final int lastFilteredTodoFolderColorIndex = lastFilteredTodoFolder.getColorIndex();
            final int colorIndex = isCustomColorIndex(lastFilteredTodoFolderColorIndex) ? 0 : (lastFilteredTodoFolderColorIndex + 1) % TodoFolder.getColorsAttrsLength();
            final int customColor = 0;

            todoFolderSettingsFragment.setEditedTodoFolder(TodoFolder.newInstance(
                    TodoFolder.Type.Custom,
                    null,
                    colorIndex,
                    customColor,
                    -1
            ));

            todoFolderItemSection.setVisibility(View.VISIBLE);
            setColor(todoFolderSettingsFragment.getEditedTodoFolder().getColor());
            addImageButton.setVisibility(View.GONE);
            addItemTextView.setVisibility(View.GONE);
            editText.setText(null);
            Utils.focusAndShowKeyboard(todoFolderSettingsFragment.getContext(), editText);
        }

        public void setColor(int color) {
            ((GradientDrawable)this.colorImageButton.getDrawable()).setColor(color);
        }
    }

    private boolean roomNotReady() {
        for (TodoFolder todoFolder : todoFolderSettingsFragment.getTodoFolderViewModel().getTodoFoldersLiveData().getValue()) {
            if (!Utils.isValidId(todoFolder.getId())) {
                return true;
            }
        }

        return false;
    }
}
