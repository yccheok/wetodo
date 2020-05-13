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

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import static com.yocto.wetodo.ui.Utils.CUSTOM_COLOR_INDEX;
import static com.yocto.wetodo.ui.Utils.NIL_CUSTOM_COLOR;
import static com.yocto.wetodo.ui.Utils.isCustomColorIndex;
import static com.yocto.wetodo.ui.Utils.scroll;

public class TodoFolderSettingsFragment extends Fragment implements ColorPickerDialogListener {
    private static final String TODO_FOLDER_CONFIRM_DELETE_DIALOG_FRAGMENT = "TODO_FOLDER_CONFIRM_DELETE_DIALOG_FRAGMENT";
    private static final String COLOR_PICKER_DIALOG_FRAGMENT = "COLOR_PICKER_DIALOG_FRAGMENT";

    private RecyclerView recyclerView;
    private TodoFolderViewModel todoFolderViewModel;
    private TodoFolderSection todoFolderSection;
    private SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;

    private final List<TodoFolder> filteredTodoFolders = new ArrayList<>();
    private TodoFolder editedTodoFolder = null;
    private final List<TodoFolder> oldFilteredTodoFoldersCopy = new ArrayList<>();
    private TodoFolder oldEditedTodoFolderCopy = null;

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
        this.todoFolderSection = new TodoFolderSection(this);
        this.sectionedRecyclerViewAdapter.addSection(this.todoFolderSection);
        this.recyclerView.setAdapter(this.sectionedRecyclerViewAdapter);

        this.todoFolderSection.setHasFooter(true);
        if (firstTime) {
            this.todoFolderSection.setState(Section.State.LOADING);
        } else {
            ensureSectionHasCorrectState();
        }

        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ((SimpleItemAnimator) this.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

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

    private void ensureSectionHasCorrectState() {
        this.todoFolderSection.setState(Section.State.LOADED);
    }

    private void onChanged(final List<TodoFolder> todoFolders) {
        this.filteredTodoFolders.clear();
        this.filteredTodoFolders.addAll(getFilteredTodoFolders(todoFolders));

        final boolean firstTime = this.oldFilteredTodoFoldersCopy.isEmpty() && false == this.filteredTodoFolders.isEmpty();

        ensureSectionHasCorrectState();

        TodoFolderDiffUtilCallback todoFolderDiffUtilCallback = new TodoFolderDiffUtilCallback(
                this.filteredTodoFolders,
                oldFilteredTodoFoldersCopy,
                editedTodoFolder,
                oldEditedTodoFolderCopy
        );

        DiffUtil.calculateDiff(todoFolderDiffUtilCallback).dispatchUpdatesTo(sectionedRecyclerViewAdapter);

        this.oldFilteredTodoFoldersCopy.clear();
        this.oldFilteredTodoFoldersCopy.addAll(TodoFolder.copy(this.filteredTodoFolders));
        this.oldEditedTodoFolderCopy = editedTodoFolder == null ? null : editedTodoFolder.copy();

        if (scrollToBottom) {
            scrollToBottom = false;
            // Need not -1, as we have footer.
            int position = this.filteredTodoFolders.size();
            recyclerView.scrollToPosition(position);
        } else {
            // When viewModelProvider.getTabInfosLiveData() is being initialized for the first time
            // (This only happen in Drawer navigation, as viewModelProvider.getTabInfosLiveData()
            // will be initialized in other tab pages), recycler view will scroll to bottom. We not
            // sure why this happens. The following is a hack to prevent so.
            if (firstTime) {
                initRecycleViewScrollPosition();
            }
        }

        // The code looks different than DashboardFragment, as the mutation operation happens
        // within this fragment.
    }

    private void initRecycleViewScrollPosition() {
        recyclerView.post(() -> scroll(recyclerView, 0));
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
