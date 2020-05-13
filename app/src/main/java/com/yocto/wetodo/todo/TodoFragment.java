package com.yocto.wetodo.todo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yocto.wetodo.FragmentType;
import com.yocto.wetodo.MainActivity;
import com.yocto.wetodo.R;
import com.yocto.wetodo.Utils;
import com.yocto.wetodo.WeTodoOptions;
import com.yocto.wetodo.model.TodoFolder;
import com.yocto.wetodo.model.TodoFolderViewModel;

import java.util.ArrayList;
import java.util.List;

import static androidx.viewpager2.widget.ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT;
import static com.yocto.wetodo.Utils.ensureTodoFoldersAreValid;
import static com.yocto.wetodo.Utils.getPageTitle;
import static com.yocto.wetodo.Utils.trackEvent;
import static com.yocto.wetodo.ui.Utils.getOptimizedPrimaryTextColor;
import static com.yocto.wetodo.ui.Utils.getViewOfType;

public class TodoFragment extends Fragment {

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private View tabLayoutBottomView;
    private TodoFragmentStateAdapter todoFragmentStateAdapter;

    private int normalTabColor;
    private int colorPrimary;
    private int tabTextColor;
    private int tabIconColor;
    private int defaultTabMinWidth;

    private TodoFolderViewModel todoFolderViewModel;
    private final List<TodoFolder> todoFolders = new ArrayList<>();
    private final OnPageChangeCallback onPageChangeCallback = new OnPageChangeCallback();
    private final TabConfigurationStrategy tabConfigurationStrategy = new TabConfigurationStrategy();

    private class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        public void onPageSelected(int position) {
            WeTodoOptions.setSelectedTodoFolderIndex(position);

            // updateTabsIcon needs to call before updateTabsColor. Not sure why.
            updateTabsIcon();
            updateTabsColor();
/*
            ((MainActivity)getActivity()).finishSupportActionMode();
            hideSnackbar();
            hideKeyboard(NoteFragment.this);*/
        }
    }

    private class TabConfigurationStrategy implements TabLayoutMediator.TabConfigurationStrategy {

        @Override
        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
            final TodoFolder todoFolder = todoFolders.get(position);

            tab.setText(getPageTitle(todoFolder));
        }
    }

    public static TodoFragment newInstance() {
        TodoFragment projectFragment = new TodoFragment();
        return projectFragment;
    }

    private void initResource() {
        Context context = getContext();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.normalTabColor, typedValue, true);
        normalTabColor = typedValue.data;
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colorPrimary = typedValue.data;
        theme.resolveAttribute(R.attr.tabTextColor, typedValue, true);
        tabTextColor = typedValue.data;
        theme.resolveAttribute(R.attr.tabIconColor, typedValue, true);
        tabIconColor = typedValue.data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initResource();

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
        todoFolderViewModel.getTodoFoldersLiveData().removeObservers(viewLifecycleOwner);
        todoFolderViewModel.getTodoFoldersLiveData().observe(
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

            updateTabsIcon();

            updateTabsColor();
/*
            Utils.onGlobalLayout(tabLayout, () -> {
                updateLabelImageButtonVisibility();
            });*/
        }
    }

    private void updateTabsColor() {
        final int tabCount = tabLayout.getTabCount();
        if (tabCount != todoFolders.size()) {
            // UI and data structure not tally yet. Should tally eventually.
            return;
        }

        final int selectedTodoFolderIndex = WeTodoOptions.getSelectedTodoFolderIndex();

        LinearLayout tabsContainer = (LinearLayout) tabLayout.getChildAt(0);
        for (int i = 0, ei = tabCount; i < ei; i++) {
            LinearLayout item = (LinearLayout) tabsContainer.getChildAt(i);
            item.setBackgroundResource(R.drawable.tab_background);

            LayerDrawable layerDrawable = (LayerDrawable)item.getBackground();
            GradientDrawable tabBackgroundDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.tab_background);
            final int selectedColor;
            if (i == selectedTodoFolderIndex) {
                selectedColor = todoFolders.get(i).getColor();
                tabBackgroundDrawable.setColor(selectedColor);
                tabLayoutBottomView.setBackgroundColor(selectedColor);
            } else {
                selectedColor = normalTabColor;
                tabBackgroundDrawable.setColor(normalTabColor);
            }

            GradientDrawable tabSpaceDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.tab_space);
            tabSpaceDrawable.setStroke(Utils.dpToPixel(1), colorPrimary);

            TextView tv = null;

            if (item.getChildCount() >= 1) {
                View child = item.getChildAt(1);
                if (child instanceof TextView) {
                    tv = (TextView) child;
                } else {
                    tv = getViewOfType(item, TextView.class);
                }
            } else {
                tv = getViewOfType(item, TextView.class);
            }


            if (tv != null) {
                if (i == selectedTodoFolderIndex) {
                    tv.setTextColor(getOptimizedPrimaryTextColor(selectedColor));
                } else {
                    tv.setTextColor(tabTextColor);
                }
            }
        }
    }

    private void updateTabsIcon() {
        final int tabCount = tabLayout.getTabCount();
        if (tabCount != todoFolders.size()) {
            // UI and data structure not tally yet. Should tally eventually.
            return;
        }

        LinearLayout tabsContainer = (LinearLayout) tabLayout.getChildAt(0);

        if (this.defaultTabMinWidth <= 0) {
            for (int i = 0, ei = tabCount; i < ei; i++) {
                LinearLayout item = (LinearLayout) tabsContainer.getChildAt(i);
                int minimumWidth = ViewCompat.getMinimumWidth(item);
                if (minimumWidth > 0) {
                    this.defaultTabMinWidth = minimumWidth;
                    break;
                }
            }
        }

        final int selectedTodoFolderIndex = WeTodoOptions.getSelectedTodoFolderIndex();

        for (int i = 0, ei = tabCount; i < ei; i++) {
            LinearLayout item = (LinearLayout) tabsContainer.getChildAt(i);

            final TodoFolder todoFolder = todoFolders.get(i);

            int iconResourceId = todoFolder.getIconResourceId();
            if (iconResourceId == 0) {
                tabLayout.getTabAt(i).setIcon(null);
                item.setMinimumWidth(this.defaultTabMinWidth);
            } else {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                tab.setIcon(iconResourceId);

                Drawable drawable = tab.getIcon();

                if (i == selectedTodoFolderIndex) {
                    int selectedColor = todoFolder.getColor();
                    DrawableCompat.setTint(drawable, getOptimizedPrimaryTextColor(selectedColor));
                } else {
                    DrawableCompat.setTint(drawable, tabIconColor);
                }

                if (todoFolder.getName() == null) {
                    item.setMinimumWidth(Math.min(Utils.dpToPixel(48), this.defaultTabMinWidth));
                } else {
                    item.setMinimumWidth(this.defaultTabMinWidth);
                }
            }
        }
    }

    private void initTabs() {
        if (todoFragmentStateAdapter == null) {
            todoFragmentStateAdapter = new TodoFragmentStateAdapter(
                    this,
                    todoFolders
            );
            viewPager2.setAdapter(todoFragmentStateAdapter);

            viewPager2.registerOnPageChangeCallback(onPageChangeCallback);

            final TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy);
            tabLayoutMediator.attach();
        } else {
            todoFragmentStateAdapter.notifyDataSetChanged();
        }

        // http://stackoverflow.com/questions/9857420/viewpager-fragments-getting-destroyed-over-time
        viewPager2.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT_DEFAULT);

        int selectedTodoFolderIndex = WeTodoOptions.getSelectedTodoFolderIndex();
        setSelectedTodoFolder(selectedTodoFolderIndex);
    }

    private void setSelectedTodoFolder(int selectedTodoFolderIndex) {
        selectedTodoFolderIndex = Math.min(selectedTodoFolderIndex, todoFragmentStateAdapter.getItemCount()-1);
        WeTodoOptions.setSelectedTodoFolderIndex(selectedTodoFolderIndex);

        // Avoid unnecessary animation.
        final int s = selectedTodoFolderIndex;
        viewPager2.setCurrentItem(s, false);
        viewPager2.post(() -> {
            TabLayout.Tab tab = tabLayout.getTabAt(s);
            if (tab != null) {
                tab.select();
            } else {
                trackEvent("setSelectedTodoFolder", "fatal", Integer.toString(s));
            }
        });
    }
}
