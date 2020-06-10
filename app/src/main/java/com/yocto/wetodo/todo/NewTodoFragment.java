package com.yocto.wetodo.todo;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.yocto.wetodo.R;

import static com.yocto.wetodo.Utils.TypefaceLazyHolder.ROBOTO_MEDIUM_TYPE_FACE;
import static com.yocto.wetodo.Utils.setCustomTypeFace;

public class NewTodoFragment extends Fragment {
    private ImageButton checkedImageButton;
    private EditText titleEditText;

    // TODO: Replace with Todo object.
    private boolean checked = false;

    private int bigCheckedIconResourceId;
    private int bigUncheckedIconResourceId;
    private int config_mediumAnimTime;

    private final CheckedImageButtonOnClickListener checkedImageButtonOnClickListener = new CheckedImageButtonOnClickListener();

    private class CheckedImageButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (checked) {
                unCheckedImageButton();
            } else {
                checkedImageButton();
            }
            checked = !checked;
        }
    }
    public static NewTodoFragment newInstance(Bundle bundle) {
        NewTodoFragment newTodoFragment = new NewTodoFragment();
        newTodoFragment.setArguments(bundle);
        return newTodoFragment;
    }

    private void initResource() {
        Context context = getContext();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        Resources resources = getResources();

        theme.resolveAttribute(R.attr.bigCheckedIcon, typedValue, true);
        bigCheckedIconResourceId = typedValue.resourceId;
        theme.resolveAttribute(R.attr.bigUncheckedIcon, typedValue, true);
        bigUncheckedIconResourceId = typedValue.resourceId;
        config_mediumAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initResource();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_todo_fragment, container, false);

        checkedImageButton = view.findViewById(R.id.checked_image_button);
        titleEditText = view.findViewById(R.id.title_edit_text);

        setCustomTypeFace(titleEditText, ROBOTO_MEDIUM_TYPE_FACE);

        checkedImageButton.setOnClickListener(checkedImageButtonOnClickListener);

        return view;
    }

    private void unCheckedImageButton() {
        checkedImageButton.setImageResource(bigUncheckedIconResourceId);
    }

    private void checkedImageButton() {
        ScaleAnimation scaleDownAnimation = new ScaleAnimation(
                1.0f,
                0.0f,
                1.0f,
                0.0f,
                checkedImageButton.getWidth() / 2.0f,
                checkedImageButton.getHeight() / 2.0f
        );
        scaleDownAnimation.setFillAfter(true);
        scaleDownAnimation.setDuration(config_mediumAnimTime);

        scaleDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                checkedImageButton.setImageResource(bigCheckedIconResourceId);

                ScaleAnimation scaleUpAnimation = new ScaleAnimation(
                        0.0f,
                        1.0f,
                        0.0f,
                        1.0f,
                        checkedImageButton.getWidth() / 2.0f,
                        checkedImageButton.getHeight() / 2.0f
                );
                scaleUpAnimation.setFillAfter(true);
                scaleUpAnimation.setDuration(config_mediumAnimTime);
                checkedImageButton.startAnimation(scaleUpAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        checkedImageButton.startAnimation(scaleDownAnimation);
    }
}
