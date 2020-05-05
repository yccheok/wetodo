package com.yocto.wetodo.folder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.yocto.wetodo.R;

public class TodoFolderSettingsFragment extends Fragment {
    public static TodoFolderSettingsFragment newInstance() {
        return new TodoFolderSettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_folder_settings_fragment, container, false);

        return view;
    }
}
