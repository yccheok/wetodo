package com.yocto.wetodo.todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yocto.wetodo.FragmentType;
import com.yocto.wetodo.MainActivity;
import com.yocto.wetodo.R;
import com.yocto.wetodo.WeTodoOptions;
import com.yocto.wetodo.model.TodoFolder;
import com.yocto.wetodo.model.TodoFolderViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.yocto.wetodo.Utils.ensureTodoFoldersAreValid;

public class TodoFragment extends Fragment {

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private View tabLayoutBottomView;
    private TodoFragmentStateAdapter todoFragmentStateAdapter;

    private TodoFolderViewModel todoFolderViewModel;
    private final List<TodoFolder> todoFolders = new ArrayList<>();
    private final TabConfigurationStrategy tabConfigurationStrategy = new TabConfigurationStrategy();

    private class TabConfigurationStrategy implements TabLayoutMediator.TabConfigurationStrategy {

        @Override
        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

        }
    }

    public static TodoFragment newInstance() {
        TodoFragment projectFragment = new TodoFragment();
        return projectFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
        todoFolderViewModel = viewModelProvider.get(TodoFolderViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_fragment, container, false);

        this.viewPager2 = view.findViewById(R.id.view_pager2);
        this.tabLayout = view.findViewById(R.id.tab_layout);
        this.tabLayoutBottomView = view.findViewById(R.id.tab_layout_bottom_view);

        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        todoFolderViewModel.getTabInfosLiveData().removeObservers(viewLifecycleOwner);
        todoFolderViewModel.getTabInfosLiveData().observe(
                viewLifecycleOwner,
                TodoFragment.this::onChanged
        );

        getMainActivity().refresh(
                FragmentType.Todo,
                null
        );

        return view;
    }

    private MainActivity getMainActivity() {
        return ((MainActivity)getActivity());
    }

    private void ensureSelectedTodoFolderIndexIsValid() {
        int selectedTodoFolderIndex = WeTodoOptions.getSelectedTodoFolderIndex();
        if (selectedTodoFolderIndex < 0 || selectedTodoFolderIndex >= this.todoFolders.size()) {
            selectedTodoFolderIndex = 0;
            WeTodoOptions.setSelectedTodoFolderIndex(selectedTodoFolderIndex);
        }
    }

    private void onChanged(List<TodoFolder> todoFolders) {
        boolean valid = ensureTodoFoldersAreValid(todoFolders, true);

        if (valid) {
            this.todoFolders.clear();
            this.todoFolders.addAll(todoFolders);

            ensureSelectedTodoFolderIndexIsValid();

            initTabs();
/*
            updateTabsIcon();
            updateTabsColor();

            Utils.onGlobalLayout(tabLayout, () -> {
                updateLabelImageButtonVisibility();
            });*/
        }
    }

    private void initTabs() {
        if (todoFragmentStateAdapter == null) {
            todoFragmentStateAdapter = new TodoFragmentStateAdapter(
                    this,
                    todoFolders
            );
            viewPager2.setAdapter(todoFragmentStateAdapter);

            final TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy);
            tabLayoutMediator.attach();
        } else {
            todoFragmentStateAdapter.notifyDataSetChanged();
        }

        // http://stackoverflow.com/questions/9857420/viewpager-fragments-getting-destroyed-over-time
        viewPager2.setOffscreenPageLimit(1);

        //int selectedNoteTabIndex = WeNoteOptions.getSelectedNoteTabIndex();
        //setSelectedNoteTab(selectedNoteTabIndex);
    }
}
