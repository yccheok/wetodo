package com.yocto.wetodo.todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yocto.wetodo.R;
import com.yocto.wetodo.model.TodoFolder;
import com.yocto.wetodo.model.TodoFolderViewModel;

import java.util.List;

public class TodoFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View tabLayoutBottomView;

    private TodoFolderViewModel todoFolderViewModel;

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

        this.viewPager = view.findViewById(R.id.view_pager);
        this.tabLayout = view.findViewById(R.id.tab_layout);
        this.tabLayoutBottomView = view.findViewById(R.id.tab_layout_bottom_view);

        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        todoFolderViewModel.getTabInfosLiveData().removeObservers(viewLifecycleOwner);
        todoFolderViewModel.getTabInfosLiveData().observe(
                viewLifecycleOwner,
                TodoFragment.this::onChanged
        );

        return view;
    }

    private void onChanged(List<TodoFolder> todoFolders) {

    }
}
