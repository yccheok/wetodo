package com.yocto.wetodo.folder;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.yocto.wetodo.R;
import com.yocto.wetodo.Utils;
import com.yocto.wetodo.color.ColorPickerDialogFragment;
import com.yocto.wetodo.color.ColorPickerDialogListener;
import com.yocto.wetodo.helper.SimpleItemTouchHelperCallback;
import com.yocto.wetodo.model.TodoFolder;
import com.yocto.wetodo.model.TodoFolderViewModel;
import com.yocto.wetodo.recyclerview.SimplePaddingSection;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import static com.yocto.wetodo.Utils.dpToPixel;
import static com.yocto.wetodo.ui.Utils.CUSTOM_COLOR_INDEX;
import static com.yocto.wetodo.ui.Utils.NIL_CUSTOM_COLOR;
import static com.yocto.wetodo.ui.Utils.isCustomColorIndex;

public class TodoFolderSettingsFragment extends Fragment implements ColorPickerDialogListener {
    private static final String TODO_FOLDER_CONFIRM_DELETE_DIALOG_FRAGMENT = "TODO_FOLDER_CONFIRM_DELETE_DIALOG_FRAGMENT";
    private static final String COLOR_PICKER_DIALOG_FRAGMENT = "COLOR_PICKER_DIALOG_FRAGMENT";

    private RecyclerView recyclerView;
    private TodoFolderViewModel todoFolderViewModel;
    private TodoFolderSection todoFolderSection;
    private SimplePaddingSection topPaddingSection;
    private SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;

    private final List<TodoFolder> filteredTodoFolders = new ArrayList<>();
    private TodoFolder editedTodoFolder = null;

    private boolean oldTopPaddingSectionVisible;
    private final List<TodoFolder> oldFilteredTodoFoldersCopy = new ArrayList<>();
    private TodoFolder oldEditedTodoFolderCopy = null;
    private boolean oldHasFooter;
    private Section.State oldState;

    private int[] colors;

    private boolean scrollToBottom = false;

    /***********************************************************************************************
     * START OF DRAG-N-MOVE
     **********************************************************************************************/

    private SimpleItemTouchHelperCallback simpleItemTouchHelperCallback;
    private ItemTouchHelper itemTouchHelper;
    private final OnStartDragListener onStartDragListener = new OnStartDragListener();

    // https://stackoverflow.com/questions/33710605/detect-animation-finish-in-androids-recyclerview
    private class WaitForAnimationsToFinishRunnable implements Runnable {
        private final Runnable defer;
        public WaitForAnimationsToFinishRunnable(Runnable defer) {
            this.defer = defer;
        }

        @Override
        public void run() {
            waitForAnimationsToFinish(defer);
        }
    }

    // Listener that is called whenever the recycler view have finished animating one view.
    private class ItemAnimatorFinishedListener implements RecyclerView.ItemAnimator.ItemAnimatorFinishedListener {
        private final Runnable defer;
        public ItemAnimatorFinishedListener(Runnable defer) {
            this.defer = defer;
        }

        @Override
        public void onAnimationsFinished() {
            // The current animation have finished and there is currently no animation running,
            // but there might still be more items that will be animated after this method returns.
            // Post a message to the message queue for checking if there are any more
            // animations running.
            postWaitForAnimationsToFinish(defer);
        }
    }

    public class OnStartDragListener implements com.yocto.wetodo.helper.OnStartDragListener {

        @Override
        public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
            itemTouchHelper.startDrag(viewHolder);
        }
    }

    /***********************************************************************************************
     * END OF DRAG-N-MOVE
     **********************************************************************************************/

    public static TodoFolderSettingsFragment newInstance() {
        return new TodoFolderSettingsFragment();
    }

    public void setScrollToBottom(boolean scrollToBottom) {
        this.scrollToBottom = scrollToBottom;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
        todoFolderViewModel = viewModelProvider.get(TodoFolderViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_folder_settings_fragment, container, false);

        // Special handling for view pager behavior. Swipe away and swipe back, will cause
        // onCreateView to be called again.
        final boolean firstTime = (this.recyclerView == null);

        this.recyclerView = view.findViewById(R.id.recycler_view);
        this.sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        this.topPaddingSection = new SimplePaddingSection(
                dpToPixel(16)
        );
        this.todoFolderSection = new TodoFolderSection(this);
        this.sectionedRecyclerViewAdapter.addSection(this.topPaddingSection);
        this.sectionedRecyclerViewAdapter.addSection(this.todoFolderSection);
        this.recyclerView.setAdapter(this.sectionedRecyclerViewAdapter);

        if (firstTime) {
            this.todoFolderSection.setState(Section.State.LOADING);
            this.todoFolderSection.setHasFooter(false);
        } else {
            ensureSectionHaveCorrectFooterAndState();
        }

        ensurePaddingSectionHaveCorrectVisibility();

        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ((SimpleItemAnimator) this.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        updateOldDatas();

        this.simpleItemTouchHelperCallback = new SimpleItemTouchHelperCallback(true, this.todoFolderSection);
        this.simpleItemTouchHelperCallback.setLongPressDragEnabled(false);
        this.itemTouchHelper = new ItemTouchHelper(this.simpleItemTouchHelperCallback);
        this.itemTouchHelper.attachToRecyclerView(this.recyclerView);

        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        todoFolderViewModel.getTodoFoldersLiveData().removeObservers(viewLifecycleOwner);
        todoFolderViewModel.getTodoFoldersLiveData().observe(viewLifecycleOwner, todoFolders -> {
            onChanged(todoFolders);
        });

        return view;
    }

    private static List<TodoFolder> getFilteredTodoFolders(List<TodoFolder> todoFolders) {
        List<TodoFolder> filteredTodoFolders = new ArrayList<>();
        for (TodoFolder todoFolder : todoFolders) {
            final TodoFolder.Type type = todoFolder.getType();
            if (type == TodoFolder.Type.Settings) {
                continue;
            }

            filteredTodoFolders.add(todoFolder);
        }
        return filteredTodoFolders;
    }

    public void onChanged() {
        Utils.ready(this.todoFolderViewModel.getTodoFoldersLiveData(), this, todoFolders -> onChanged(todoFolders));
    }

    private void ensureSectionHaveCorrectFooterAndState() {
        this.todoFolderSection.setState(Section.State.LOADED);
        this.todoFolderSection.setHasFooter(true);
    }

    // Depends on ensureSectionHaveCorrectFooterAndState.
    private void ensurePaddingSectionHaveCorrectVisibility() {
        if (todoFolderSection.getState() == Section.State.LOADED) {
            topPaddingSection.setVisible(true);
        } else {
            topPaddingSection.setVisible(false);
        }
    }

    private void onChanged(final List<TodoFolder> todoFolders) {
        this.filteredTodoFolders.clear();
        this.filteredTodoFolders.addAll(getFilteredTodoFolders(todoFolders));

        ensureSectionHaveCorrectFooterAndState();

        ensurePaddingSectionHaveCorrectVisibility();

        TodoFolderDiffUtilCallback todoFolderDiffUtilCallback = new TodoFolderDiffUtilCallback(
                this.topPaddingSection.isVisible(),
                this.oldTopPaddingSectionVisible,
                this.filteredTodoFolders,
                oldFilteredTodoFoldersCopy,
                editedTodoFolder,
                oldEditedTodoFolderCopy,
                todoFolderSection.hasFooter(),
                oldHasFooter,
                todoFolderSection.getState(),
                oldState
        );

        DiffUtil.calculateDiff(todoFolderDiffUtilCallback).dispatchUpdatesTo(sectionedRecyclerViewAdapter);

        updateOldDatas();

        if (scrollToBottom) {
            scrollToBottom = false;
            // Need not -1, as we have footer.
            int position = this.filteredTodoFolders.size();
            recyclerView.scrollToPosition(position);
        }

        // The code looks different than DashboardFragment, as the mutation operation happens
        // within this fragment.
    }

    private void updateOldDatas() {
        this.oldTopPaddingSectionVisible = topPaddingSection.isVisible();
        this.oldFilteredTodoFoldersCopy.clear();
        this.oldFilteredTodoFoldersCopy.addAll(TodoFolder.copy(this.filteredTodoFolders));
        this.oldEditedTodoFolderCopy = editedTodoFolder == null ? null : editedTodoFolder.copy();
        this.oldHasFooter = todoFolderSection.hasFooter();
        this.oldState = todoFolderSection.getState();
    }

    public List<TodoFolder> getFilteredTodoFolders() {
        return this.filteredTodoFolders;
    }

    public TodoFolder getEditedTodoFolder() {
        return this.editedTodoFolder;
    }

    public void setEditedTodoFolder(TodoFolder editedTodoFolder) {
        this.editedTodoFolder = editedTodoFolder;
    }

    public TodoFolderViewModel getTodoFolderViewModel() {
        return this.todoFolderViewModel;
    }

    public void showTodoFolderConfirmDeleteDialogFragment(TodoFolder todoFolder) {
        TodoFolderConfirmDeleteDialogFragment todoFolderConfirmDeleteDialogFragment = TodoFolderConfirmDeleteDialogFragment.newInstance(todoFolder);
        todoFolderConfirmDeleteDialogFragment.show(getParentFragmentManager(), TODO_FOLDER_CONFIRM_DELETE_DIALOG_FRAGMENT);
    }

    public void showColorPickerDialog(int row, int selectedColor) {
        if (colors == null) {
            colors = TodoFolder.getColors();
        }

        final boolean selectedColorIsValid = true;

        ColorPickerDialogFragment colorDialogFragment = ColorPickerDialogFragment.newInstance(
                ColorPickerDialogFragment.Type.Tab,
                row,
                colors,
                TodoFolder.getColorStringResourceIds(),
                null,
                selectedColor,
                selectedColorIsValid
        );

        colorDialogFragment.setTargetFragment(this, 0);

        FragmentManager fm = this.getFragmentManager();
        colorDialogFragment.show(fm, COLOR_PICKER_DIALOG_FRAGMENT);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        final int colorIndex = toColorIndex(color);
        final int customColor = isCustomColorIndex(colorIndex) ? color : NIL_CUSTOM_COLOR;

        if (dialogId == -1) {
            if (this.editedTodoFolder != null) {
                this.editedTodoFolder.setColorIndex(colorIndex);
                this.editedTodoFolder.setCustomColor(customColor);

                /** UX Optimization. Remove it if it causes trouble. **/
                onChanged();
            }
        } else {
            TodoFolder todoFolder = this.filteredTodoFolders.get(dialogId);

            final long syncedTimestamp = System.currentTimeMillis();
            todoFolder.setColorIndex(colorIndex);
            todoFolder.setCustomColor(customColor);
            todoFolder.setSyncedTimestamp(syncedTimestamp);
            onChanged();

            todoFolderViewModel.updateColorAsync(todoFolder.getId(), toColorIndex(color), customColor, syncedTimestamp);
        }
    }

    private int toColorIndex(int color) {
        if (colors == null) {
            colors = TodoFolder.getColors();
        }

        for (int i=0, ei=colors.length; i<ei; i++) {
            if (colors[i] == color) {
                return i;
            }
        }
        return CUSTOM_COLOR_INDEX;
    }

    public OnStartDragListener getOnStartDragListener() {
        return this.onStartDragListener;
    }

    // When the data in the recycler view is changed all views are animated. If the
    // recycler view is animating, this method sets up a listener that is called when the
    // current animation finishes. The listener will call this method again once the
    // animation is done.
    private void waitForAnimationsToFinish(Runnable defer) {
        if (recyclerView.isAnimating()) {
            // The recycler view is still animating, try again when the animation has finished.
            recyclerView.getItemAnimator().isRunning(new ItemAnimatorFinishedListener(defer));
            return;
        }

        // The recycler view have animated all it's views
        defer.run();
    }

    public void postWaitForAnimationsToFinish(Runnable defer) {
        new Handler().post(new WaitForAnimationsToFinishRunnable(defer));
    }

    public SectionedRecyclerViewAdapter getSectionedRecyclerViewAdapter() {
        return sectionedRecyclerViewAdapter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
