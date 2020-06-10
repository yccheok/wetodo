package com.yocto.wetodo.todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.yocto.wetodo.R;

import static com.yocto.wetodo.Utils.TypefaceLazyHolder.ROBOTO_MEDIUM_TYPE_FACE;
import static com.yocto.wetodo.Utils.setCustomTypeFace;

public class NewTodoFragment extends Fragment {
    private EditText titleEditText;

    public static NewTodoFragment newInstance(Bundle bundle) {
        NewTodoFragment newTodoFragment = new NewTodoFragment();
        newTodoFragment.setArguments(bundle);
        return newTodoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_todo_fragment, container, false);

        titleEditText = view.findViewById(R.id.title_edit_text);

        setCustomTypeFace(titleEditText, ROBOTO_MEDIUM_TYPE_FACE);

        return view;
    }
}
