package com.yocto.wetodo.color;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.yocto.wetodo.R;

import java.util.Arrays;

import static com.yocto.wetodo.Utils.Assert;
import static com.yocto.wetodo.Utils.TypefaceLazyHolder.ROBOTO_MEDIUM_TYPE_FACE;
import static com.yocto.wetodo.Utils.TypefaceLazyHolder.ROBOTO_MONO_REGULAR_TYPE_FACE;
import static com.yocto.wetodo.Utils.setCustomTypeFace;
import static com.yocto.wetodo.color.Utils.hasAlpha;
import static com.yocto.wetodo.ui.Utils.getOptimizedPrimaryTextColor;

public class HexColorStringInputDialogFragment extends DialogFragment {
    private static final String COLOR_CODES_KEY = "COLOR_CODES_KEY";

    private static final int COLOR_CODE_LENGTH = 6;

    private final char[] colorCodes = new char[COLOR_CODE_LENGTH];

    private View view;

    private TextView displayTextView;
    private ImageButton confirmInputImageButton;

    private int primaryTextColor;

    private static boolean isValidColorCode(char c) {
        if (Character.isDigit(c)) {
            return true;
        }
        if (c >= 'A' && c <= 'F') {
            return true;
        }
        return false;
    }

    private boolean isValidColorCodes() {
        for (char c : colorCodes) {
            if (false == isValidColorCode(c)) {
                return false;
            }
        }
        return true;
    }

    private String getHexColorString() {
        StringBuilder sb = new StringBuilder("#");
        for (char c : colorCodes) {
            if (isValidColorCode(c)) {
                sb.append(c);
            } else {
                sb.append('-');
            }
        }
        return sb.toString();
    }

    private int getDisplayTextViewBackgroundColor() {
        if (isValidColorCodes()) {
            return Color.parseColor(getHexColorString());
        } else {
            return Color.TRANSPARENT;
        }
    }

    private int getDisplayTextViewTextColor() {
        int backgroundColor = getDisplayTextViewBackgroundColor();
        if (hasAlpha(backgroundColor)) {
            return primaryTextColor;
        } else {
            return getOptimizedPrimaryTextColor(backgroundColor);
        }
    }

    private void initResource() {
        Activity activity = getActivity();

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activity.getTheme();
        theme.resolveAttribute(R.attr.primaryTextColor, typedValue, true);
        primaryTextColor = typedValue.data;
    }

    public static HexColorStringInputDialogFragment newInstance() {
        return new HexColorStringInputDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initResource();

        if (savedInstanceState != null) {
            System.arraycopy(
                    savedInstanceState.getCharArray(COLOR_CODES_KEY),
                    0,
                    colorCodes,
                    0,
                    colorCodes.length
            );
        }
    }

    private void updateDisplayTextView() {
        displayTextView.setText(getHexColorString());
        displayTextView.setBackgroundColor(getDisplayTextViewBackgroundColor());
        displayTextView.setTextColor(getDisplayTextViewTextColor());
    }

    private void updateConfirmInputImageButton() {
        if (isValidColorCodes()) {
            confirmInputImageButton.setEnabled(true);
        } else {
            confirmInputImageButton.setEnabled(false);
        }
    }

    private View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.hex_color_string_input_dialog_fragment, container, false);

        this.displayTextView = view.findViewById(R.id.display_text_view);
        this.confirmInputImageButton = view.findViewById(R.id.confirm_input_image_button);

        setCustomTypeFace(this.displayTextView,  ROBOTO_MONO_REGULAR_TYPE_FACE);

        this.displayTextView.setOnClickListener(view1 -> {
            if (isValidColorCodes()) {
                confirmInput();
            }
        });

        initButtons(view);
        updateDisplayTextView();
        updateConfirmInputImageButton();

        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        this.view = onCreateView(layoutInflater, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.input_a_color)
                .setView(this.view);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        return alertDialog;
    }

    private void clearInput() {
        Arrays.fill(colorCodes, (char)0);
        updateDisplayTextView();
        updateConfirmInputImageButton();
    }

    private void backspace() {
        int index = colorCodes.length-1;
        do {
            if (isValidColorCode(colorCodes[index])) {
                break;
            }
            index--;
        } while (index >= 0);

        if (index >= 0) {
            colorCodes[index] = 0;
            updateDisplayTextView();
            updateConfirmInputImageButton();
        }
    }

    private void confirmInput() {
        Assert (isValidColorCodes());

        dismiss();

        Fragment fragment = getTargetFragment();
        if (fragment instanceof HexColorStringInputDialogListener) {
            ((HexColorStringInputDialogListener)fragment).onColorSelected(
                    Color.parseColor(getHexColorString())
            );
        }
    }

    private void initButtons(final View view) {
        ViewGroup viewGroup;

        if (view instanceof ViewGroup) {
            viewGroup = (ViewGroup)view;
        } else {
            return;
        }

        for (int i=0, ei=viewGroup.getChildCount(); i<ei; i++) {
            View child = viewGroup.getChildAt(i);

            initButtons(child);

            if (child instanceof Button) {
                Button button = (Button)child;

                setCustomTypeFace(button, ROBOTO_MEDIUM_TYPE_FACE);

                button.setOnClickListener(v -> {
                    int index = 0;
                    do {
                        if (!isValidColorCode(colorCodes[index])) {
                            break;
                        }
                        index++;
                    } while (index < colorCodes.length);

                    if (index < colorCodes.length && !isValidColorCode(colorCodes[index])) {
                        colorCodes[index] = button.getText().charAt(0);
                        updateDisplayTextView();
                        updateConfirmInputImageButton();
                    }
                });
            } else if (child instanceof ImageButton) {
                final int id = child.getId();

                if (id == R.id.backspace_image_button) {
                    child.setOnClickListener(view1 -> {
                        backspace();
                    });
                    child.setOnLongClickListener(view12 -> {
                        clearInput();
                        return true;
                    });
                } else if (id == R.id.confirm_input_image_button) {
                    child.setOnClickListener(view1 -> {
                        confirmInput();
                    });
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putCharArray(COLOR_CODES_KEY, this.colorCodes);
    }
}
