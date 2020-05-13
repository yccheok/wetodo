package com.yocto.wetodo.color;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yocto.wetodo.R;
import com.yocto.wetodo.Utils;
import com.yocto.wetodo.WeTodoOptions;
import com.yocto.wetodo.billing.Feature;
import com.yocto.wetodo.billing.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.yocto.wetodo.Constants.MOST_RECENT_COLOR_LIST_SIZE;
import static com.yocto.wetodo.Utils.Assert;
import static com.yocto.wetodo.Utils.TypefaceLazyHolder.ROBOTO_MONO_REGULAR_TYPE_FACE;
import static com.yocto.wetodo.Utils.toArray;
import static com.yocto.wetodo.Utils.toSet;
import static com.yocto.wetodo.billing.Utils.isAllow;
import static com.yocto.wetodo.billing.Utils.shop;
import static com.yocto.wetodo.color.Utils.colorToV;
import static com.yocto.wetodo.color.Utils.toHexColorString;
import static com.yocto.wetodo.ui.Utils.getOptimizedPrimaryTextColor;

public class ColorPickerDialogFragment extends DialogFragment implements HexColorStringInputDialogListener {

    public enum Type implements Parcelable {
        Note,
        Tab;

        public static final Creator<Type> CREATOR = new Creator<Type>() {
            public Type createFromParcel(Parcel in) {
                return Type.valueOf(in.readString());
            }

            public Type[] newArray(int size) {
                return new Type[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeString(this.name());
        }
    }

    private static final String INTENT_EXTRA_TYPE = "INTENT_EXTRA_TYPE";
    private static final String INTENT_EXTRA_DIALOG_ID = "INTENT_EXTRA_DIALOG_ID";
    private static final String INTENT_EXTRA_COLORS = "INTENT_EXTRA_COLORS";
    private static final String INTENT_EXTRA_COLOR_STRING_RESOURCE_IDS = "INTENT_EXTRA_COLOR_STRING_RESOURCE_IDS";
    private static final String INTENT_EXTRA_EXTRA_COLOR = "INTENT_EXTRA_EXTRA_COLOR";
    private static final String INTENT_EXTRA_SELECTED_COLOR = "INTENT_EXTRA_SELECTED_COLOR";
    private static final String INTENT_EXTRA_SELECTED_COLOR_IS_VALID = "INTENT_EXTRA_SELECTED_COLOR_IS_VALID";

    private static final String SELECTED_COLOR_KEY = "SELECTED_COLOR_KEY";
    private static final String SELECTED_COLOR_IS_VALID_KEY = "SELECTED_COLOR_IS_VALID_KEY";
    private static final String SELECTED_V_KEY = "SELECTED_V_KEY";
    private static final String SELECTED_PAGE_INDEX_KEY = "SELECTED_PAGE_INDEX_KEY";

    private static final String HEX_COLOR_STRING_INPUT_DIALOG_FRAGMENT = "HEX_COLOR_STRING_INPUT_DIALOG_FRAGMENT";

    private Type type;
    private int dialogId;
    private int[] colors;
    private int[] colorStringResourceIds;
    private Integer extraColor;

    private int selectedColor;
    private boolean selectedColorIsValid;
    private float selectedV;
    private int selectedPageIndex;

    private ColorPaletteAdapter colorPaletteAdapter;
    private ColorPaletteAdapter shadesColorPaletteAdapter;
    private final ColorSelectedListener colorSelectedListener = new ColorSelectedListener();
    private final ColorWheelViewListener colorWheelViewListener = new ColorWheelViewListener();
    private final CustomColorSelectedListener customColorSelectedListener = new CustomColorSelectedListener();

    private ViewAnimator viewAnimator;
    private View view;
    private GridView gridView;
    private View shadesDivider;
    private GridView shadesGridView;

    private ColorWheelView customColorWheelView;
    private LinearLayout customSelectedColorLinearLayout;
    private BrightnessPickerView customBrightnessPickerView;
    private TextView customTextView;
    private Button selectButton;

    private Animation slideInRightFast;
    private Animation slideOutLeftSlow;
    private Animation slideInLeftFast;
    private Animation slideOutRightSlow;
    private int smallLockedIconResourceId;
    private int dialogPositiveButtonTextColor;
    private int dialogPositiveButtonSelectorResourceId;

    private Integer selectedCustomColor = null;

    private class CustomColorSelectedListener implements BrightnessPickerView.ColorSelectedListener {

        @Override
        public void onColorSelected(int color) {
            // Prepare to savedInstanceState restore.
            selectedV = colorToV(color);

            selectedCustomColor = color;

            customTextView.setBackgroundColor(color);
            customTextView.setTextColor(getOptimizedPrimaryTextColor(color));

            customTextView.setText(toHexColorString(color));
        }
    }

    private class ColorWheelViewListener implements ColorWheelView.ColorWheelViewListener {
        @Override
        public void onColor(int color) {
            // Prepare to savedInstanceState restore.
            selectedColor = color;
            selectedColorIsValid = true;

            customBrightnessPickerView.setColor(color);

            customBrightnessPickerView.setVisibility(View.VISIBLE);
            customSelectedColorLinearLayout.setVisibility(View.VISIBLE);

            if (selectButton != null) {
                selectButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private class ColorSelectedListener implements ColorPaletteAdapter.ColorSelectedListener {

        @Override
        public void onColorSelected(int color) {
            WeTodoOptions.INSTANCE.setSelectedColorPickerDialogPageIndex(type, selectedPageIndex);

            Fragment fragment = getTargetFragment();
            if (fragment instanceof ColorPickerDialogListener) {
                ((ColorPickerDialogListener) fragment).onColorSelected(dialogId, color);
            }

            dismiss();
        }
    }

    // Callback from HexColorStringInputDialogFragment
    @Override
    public void onColorSelected(int color) {
        updateCustomColorUI(color, colorToV(color));
    }

    private void initResource() {
        Context context = getContext();
        this.slideInRightFast = AnimationUtils.loadAnimation(context, R.anim.slide_in_right_fast);
        this.slideOutLeftSlow = AnimationUtils.loadAnimation(context, R.anim.slide_out_left_slow);
        this.slideInLeftFast = AnimationUtils.loadAnimation(context, R.anim.slide_in_left_fast);
        this.slideOutRightSlow = AnimationUtils.loadAnimation(context, R.anim.slide_out_right_slow);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.smallLockedIcon, typedValue, true);
        this.smallLockedIconResourceId = typedValue.resourceId;
        theme.resolveAttribute(R.attr.dialogPositiveButtonTextColor, typedValue, true);
        this.dialogPositiveButtonTextColor = typedValue.data;
        theme.resolveAttribute(R.attr.dialogPositiveButtonSelector, typedValue, true);
        this.dialogPositiveButtonSelectorResourceId = typedValue.resourceId;
    }

    public static ColorPickerDialogFragment newInstance(Type type, int dialogId, int[] colors, int[] colorStringResourceIds, Integer extraColor, int selectedColor, boolean selectedColorIsValid) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(INTENT_EXTRA_TYPE, type);
        arguments.putInt(INTENT_EXTRA_DIALOG_ID, dialogId);
        arguments.putIntArray(INTENT_EXTRA_COLORS, colors);
        arguments.putIntArray(INTENT_EXTRA_COLOR_STRING_RESOURCE_IDS, colorStringResourceIds);
        if (extraColor != null) {
            arguments.putInt(INTENT_EXTRA_EXTRA_COLOR, extraColor);
        }
        arguments.putInt(INTENT_EXTRA_SELECTED_COLOR, selectedColor);
        arguments.putBoolean(INTENT_EXTRA_SELECTED_COLOR_IS_VALID, selectedColorIsValid);
        ColorPickerDialogFragment colorDialogFragment = new ColorPickerDialogFragment();
        colorDialogFragment.setArguments(arguments);
        return colorDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        this.type = arguments.getParcelable(INTENT_EXTRA_TYPE);
        this.dialogId = arguments.getInt(INTENT_EXTRA_DIALOG_ID);
        this.colors = arguments.getIntArray(INTENT_EXTRA_COLORS);
        this.colorStringResourceIds = arguments.getIntArray(INTENT_EXTRA_COLOR_STRING_RESOURCE_IDS);
        if (arguments.containsKey(INTENT_EXTRA_EXTRA_COLOR)) {
            extraColor = arguments.getInt(INTENT_EXTRA_EXTRA_COLOR);
        } else {
            extraColor = null;
        }

        if (savedInstanceState == null) {
            this.selectedColorIsValid = arguments.getBoolean(INTENT_EXTRA_SELECTED_COLOR_IS_VALID);
            this.selectedColor = arguments.getInt(INTENT_EXTRA_SELECTED_COLOR);
            if (this.selectedColorIsValid) {
                this.selectedV = colorToV(this.selectedColor);
            } else {
                this.selectedV = 1.0f;
            }
            this.selectedPageIndex = WeTodoOptions.INSTANCE.getSelectedColorPickerDialogPageIndex(this.type);
        } else {
            this.selectedColorIsValid = savedInstanceState.getBoolean(SELECTED_COLOR_IS_VALID_KEY);
            this.selectedColor = savedInstanceState.getInt(SELECTED_COLOR_KEY);
            this.selectedV = savedInstanceState.getFloat(SELECTED_V_KEY);
            this.selectedPageIndex = savedInstanceState.getInt(SELECTED_PAGE_INDEX_KEY);
        }

        initResource();
    }

    @Override
    public void onResume() {
        super.onResume();

        AlertDialog alertDialog = ((AlertDialog)getDialog());

        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        super.onResume();

        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(view -> {
            if (viewAnimator.getDisplayedChild() == 0) {
                goToPage(1, true);
            } else {
                goToPage(0, true);
            }
        });

        this.selectButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        this.selectButton.setOnClickListener(view -> onCustomColorSelectedThenDismiss());
        this.customTextView.setOnClickListener(view -> showHexColorStringInputDialogFragment());

        selectButton.setTextColor(dialogPositiveButtonTextColor);
        selectButton.setBackgroundResource(dialogPositiveButtonSelectorResourceId);

        if (false == isAllow(Feature.Color)) {
            selectButton.setCompoundDrawablesWithIntrinsicBounds(
                    smallLockedIconResourceId,
                    0,
                    0,
                    0
            );
            selectButton.setCompoundDrawablePadding(Utils.dpToPixel(8));
            DrawableCompat.setTint(selectButton.getCompoundDrawables()[0].mutate(), dialogPositiveButtonTextColor);
        }

        goToPage(this.selectedPageIndex, false);
    }

    private void showHexColorStringInputDialogFragment() {
        HexColorStringInputDialogFragment hexColorStringInputDialogFragment = HexColorStringInputDialogFragment.newInstance();

        hexColorStringInputDialogFragment.setTargetFragment(this, 0);

        FragmentManager fm = this.getFragmentManager();
        hexColorStringInputDialogFragment.show(fm, HEX_COLOR_STRING_INPUT_DIALOG_FRAGMENT);
    }

    private void onCustomColorSelectedThenDismiss() {
        if (this.selectedCustomColor != null) {
            if (isAllow(Feature.Color)) {
                WeTodoOptions.INSTANCE.setSelectedColorPickerDialogPageIndex(type, selectedPageIndex);
                WeTodoOptions.INSTANCE.setAndClearMostRecentSelectedColorList(type, selectedCustomColor);

                Fragment fragment = getTargetFragment();
                if (fragment instanceof ColorPickerDialogListener) {
                    ((ColorPickerDialogListener) fragment).onColorSelected(this.dialogId, this.selectedCustomColor);
                }
            } else {
                shop(getFragmentManager(), Shop.ColorLite);
            }

            dismiss();
        } else {
            Assert (false);
        }
    }

    private void updateCustomColorUI(int color, float v) {
        this.customColorWheelView.setColor(color);

        // Make customBrightnessPickerView has correct 'v'.
        this.customBrightnessPickerView.setV(v);
    }

    private View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.color_dialog_fragment, container, false);

        this.gridView = view.findViewById(R.id.gridView);
        this.shadesDivider = view.findViewById(R.id.shades_divider);
        this.shadesGridView = view.findViewById(R.id.shade_grid_view);

        this.viewAnimator = view.findViewById(R.id.view_animator);

        this.customColorWheelView = view.findViewById(R.id.custom_color_wheel_view);
        this.customSelectedColorLinearLayout = view.findViewById(R.id.custom_selected_color_linear_layout);
        this.customBrightnessPickerView = view.findViewById(R.id.custom_brightness_picker_view);
        this.customTextView = view.findViewById(R.id.custom_text_view);

        this.customColorWheelView.setColorWheelViewListener(this.colorWheelViewListener);
        this.customBrightnessPickerView.setColorSelectedListener(this.customColorSelectedListener);

        Utils.setCustomTypeFace(this.customTextView, ROBOTO_MONO_REGULAR_TYPE_FACE);

        int selectedPosition = -1;
        if (this.selectedColorIsValid) {
            for (int i=0; i<colors.length; i++) {
                final int color = colors[i];
                if (color == selectedColor && colorToV(color) == selectedV) {
                    selectedPosition = i;
                    break;
                }
            }

            updateCustomColorUI(selectedColor, selectedV);
        }

        this.colorPaletteAdapter = new ColorPaletteAdapter(
                colorSelectedListener,
                this.colors,
                this.colorStringResourceIds,
                selectedPosition,
                ColorShape.CIRCLE
        );

        this.gridView.setAdapter(this.colorPaletteAdapter);

        initShades();

        return view;
    }

    private void initShades() {
        List<Integer> mostRecentSelectedColors = new ArrayList<>(WeTodoOptions.INSTANCE.getMostRecentSelectedColorLists(this.type));

        if (extraColor != null) {
            mostRecentSelectedColors.add(0, extraColor);
            if (mostRecentSelectedColors.size() > MOST_RECENT_COLOR_LIST_SIZE) {
                mostRecentSelectedColors = mostRecentSelectedColors.subList(0, MOST_RECENT_COLOR_LIST_SIZE);
            }
        }

        if (mostRecentSelectedColors.isEmpty()) {
            this.shadesDivider.setVisibility(View.GONE);
            this.shadesGridView.setVisibility(View.GONE);
        } else {
            final Set<Integer> presetsColors = toSet(colors);

            List<Integer> goodColors = new ArrayList<>();

            int selectedPosition = -1;

            for (int color : mostRecentSelectedColors) {
                if (presetsColors.contains(color)) {
                    continue;
                }

                if (this.selectedColorIsValid && this.selectedColor == color) {
                    selectedPosition = goodColors.size();
                }

                goodColors.add(color);
            }

            if (goodColors.isEmpty()) {
                this.shadesDivider.setVisibility(View.GONE);
                this.shadesGridView.setVisibility(View.GONE);
            } else {
                this.shadesColorPaletteAdapter = new ColorPaletteAdapter(
                        colorSelectedListener,
                        toArray(goodColors),
                        null,
                        selectedPosition,
                        ColorShape.CIRCLE
                );

                this.shadesGridView.setAdapter(this.shadesColorPaletteAdapter);

                this.shadesDivider.setVisibility(View.VISIBLE);
                this.shadesGridView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void goToPage(int position, boolean animate) {
        this.selectedPageIndex = position;

        AlertDialog alertDialog = ((AlertDialog)getDialog());

        if (position == 0) {
            if (animate) {
                viewAnimator.setInAnimation(slideInLeftFast);
                viewAnimator.setOutAnimation(slideOutRightSlow);
            } else {
                viewAnimator.setInAnimation(null);
                viewAnimator.setOutAnimation(null);
            }
            viewAnimator.setDisplayedChild(0);

            selectButton.setVisibility(View.INVISIBLE);
            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setText(R.string.custom_color);

        } else {
            if (animate) {
                viewAnimator.setInAnimation(slideInRightFast);
                viewAnimator.setOutAnimation(slideOutLeftSlow);
            } else {
                viewAnimator.setInAnimation(null);
                viewAnimator.setOutAnimation(null);
            }
            viewAnimator.setDisplayedChild(1);

            if (selectedCustomColor == null) {
                customSelectedColorLinearLayout.setVisibility(View.INVISIBLE);
                customBrightnessPickerView.setVisibility(View.INVISIBLE);
                selectButton.setVisibility(View.INVISIBLE);
            } else {
                customSelectedColorLinearLayout.setVisibility(View.VISIBLE);
                customBrightnessPickerView.setVisibility(View.VISIBLE);
                selectButton.setVisibility(View.VISIBLE);
            }

            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setText(R.string.presets_color);
        }
    }

    // https://stackoverflow.com/questions/20303865/viewpager-in-dialogfragment-illegalstateexception-fragment-does-not-have-a-vi
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return this.view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        this.view = onCreateView(layoutInflater, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.select_a_color)
                .setView(this.view)
                .setPositiveButton(R.string.select_color, (dialogInterface, i) -> {

                })
                .setNeutralButton(R.string.custom_color, (dialogInterface, i) -> {

                });

        final AlertDialog alertDialog = alertDialogBuilder.create();

        return alertDialog;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(SELECTED_COLOR_IS_VALID_KEY, this.selectedColorIsValid);
        savedInstanceState.putInt(SELECTED_COLOR_KEY, this.selectedColor);
        savedInstanceState.putFloat(SELECTED_V_KEY, this.selectedV);
        savedInstanceState.putInt(SELECTED_PAGE_INDEX_KEY, this.selectedPageIndex);
    }
}
