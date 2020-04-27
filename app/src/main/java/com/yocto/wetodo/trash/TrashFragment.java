package com.yocto.wetodo.trash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.yocto.wetodo.FragmentType;
import com.yocto.wetodo.MainActivity;
import com.yocto.wetodo.R;

public class TrashFragment extends Fragment {
    public static TrashFragment newInstance() {
        return new TrashFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trash_fragment, container, false);

        getMainActivity().refresh(
                FragmentType.Trash,
                null
        );

        return view;
    }

    private MainActivity getMainActivity() {
        return ((MainActivity)getActivity());
    }
}
