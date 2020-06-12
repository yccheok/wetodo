package com.yocto.wetodo.todo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.yocto.wetodo.R;

import static com.yocto.wetodo.Utils.TypefaceLazyHolder.ROBOTO_MEDIUM_TYPE_FACE;
import static com.yocto.wetodo.Utils.TypefaceLazyHolder.ROBOTO_REGULAR_TYPE_FACE;
import static com.yocto.wetodo.Utils.setCustomTypeFace;

public class NewTodoFragment extends Fragment {
    private ImageButton checkedImageButton;
    private EditText titleEditText;
    private TextView dueDateTextView;
    private TextView reminderTextView;

    // TODO: Replace with Todo object.
    private boolean checked = false;

    private int primaryTextColor;
    private int secondaryTextColor;
    private int bigCheckedIconResourceId;
    private int bigUncheckedIconResourceId;
    private int config_shortAnimTime;

    private final CheckedImageButtonOnClickListener checkedImageButtonOnClickListener = new CheckedImageButtonOnClickListener();

    private class CheckedImageButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            checked = !checked;

            updateCheckedImageButtonWithAnimation();
            updateTitleEditText();
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

        theme.resolveAttribute(R.attr.primaryTextColor, typedValue, true);
        primaryTextColor = typedValue.data;
        theme.resolveAttribute(R.attr.secondaryTextColor, typedValue, true);
        secondaryTextColor = typedValue.data;
        theme.resolveAttribute(R.attr.bigCheckedIcon, typedValue, true);
        bigCheckedIconResourceId = typedValue.resourceId;
        theme.resolveAttribute(R.attr.bigUncheckedIcon, typedValue, true);
        bigUncheckedIconResourceId = typedValue.resourceId;
        config_shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime);
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
        dueDateTextView = view.findViewById(R.id.due_date_text_view);
        reminderTextView = view.findViewById(R.id.reminder_text_view);

        setCustomTypeFace(titleEditText, ROBOTO_MEDIUM_TYPE_FACE);
        setCustomTypeFace(dueDateTextView, ROBOTO_REGULAR_TYPE_FACE);
        setCustomTypeFace(reminderTextView, ROBOTO_REGULAR_TYPE_FACE);

        checkedImageButton.setOnClickListener(checkedImageButtonOnClickListener);

        updateCheckedImageButton();
        updateTitleEditText();

        return view;
    }

    private void updateTitleEditText() {
        if (checked) {
            titleEditText.setTextColor(secondaryTextColor);
            titleEditText.setPaintFlags(titleEditText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            titleEditText.setTextColor(primaryTextColor);
            titleEditText.setPaintFlags(titleEditText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    private void updateCheckedImageButton() {
        if (checked) {
            checkedImageButton();
        } else {
            unCheckedImageButton();
        }
    }

    private void updateCheckedImageButtonWithAnimation() {
        if (checked) {
            checkedImageButtonWithAnimation();
        } else {
            unCheckedImageButton();
        }
    }

    private void unCheckedImageButton() {
        checkedImageButton.setImageResource(bigUncheckedIconResourceId);
    }

    private void checkedImageButton() {
        checkedImageButton.setImageResource(bigCheckedIconResourceId);
    }

    private void checkedImageButtonWithAnimation() {
        ScaleAnimation scaleDownAnimation = new ScaleAnimation(
                1.0f,
                0.0f,
                1.0f,
                0.0f,
                checkedImageButton.getWidth() / 2.0f,
                checkedImageButton.getHeight() / 2.0f
        );
        scaleDownAnimation.setFillAfter(true);
        scaleDownAnimation.setDuration(config_shortAnimTime);

        scaleDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                checkedImageButton();

                ScaleAnimation scaleUpAnimation = new ScaleAnimation(
                        0.0f,
                        1.0f,
                        0.0f,
                        1.0f,
                        checkedImageButton.getWidth() / 2.0f,
                        checkedImageButton.getHeight() / 2.0f
                );
                scaleUpAnimation.setFillAfter(true);
                scaleUpAnimation.setDuration(config_shortAnimTime);
                checkedImageButton.startAnimation(scaleUpAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        checkedImageButton.startAnimation(scaleDownAnimation);
    }
}
