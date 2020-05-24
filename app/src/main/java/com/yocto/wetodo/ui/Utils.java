package com.yocto.wetodo.ui;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.yocto.wetodo.R;
import com.yocto.wetodo.Theme;
import com.yocto.wetodo.ThemeType;
import com.yocto.wetodo.WeTodoApplication;
import com.yocto.wetodo.WeTodoOptions;

import static com.yocto.wetodo.Utils.Assert;

public class Utils {
    public static final int NIL_CUSTOM_COLOR = 0;
    public static final int CUSTOM_COLOR_INDEX = -1;

    private static final class ColorLazyHolder {
        private static final int primaryTextColorLight;
        private static final int primaryTextColorDark;
        private static final int secondaryTextColorLight;
        private static final int secondaryTextColorDark;
        private static final int noteLineColorLight;
        private static final int noteLineColorDark;

        private static final SparseIntArray OPTIMIZED_PRIMARY_TEXT_COLORS = new SparseIntArray();
        private static final SparseIntArray OPTIMIZED_SECONDARY_TEXT_COLORS = new SparseIntArray();
        private static final SparseBooleanArray IS_LIGHT_COLOR = new SparseBooleanArray();

        static {
            primaryTextColorLight = getColor(R.color.primaryTextColorLight);
            primaryTextColorDark = getColor(R.color.primaryTextColorDark);
            secondaryTextColorLight = getColor(R.color.secondaryTextColorLight);
            secondaryTextColorDark = getColor(R.color.secondaryTextColorDark);
            noteLineColorLight = getColor(R.color.noteLineColorLight);
            noteLineColorDark = getColor(R.color.noteLineColorDark);
        }

        static {
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.whiteNoteColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.redNoteColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.orangeNoteColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.yellowNoteColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.darkBlueNoteColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.blueNoteColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.tealNoteColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.greenNoteColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.purpleNoteColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.pinkNoteColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.brownNoteColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.greyNoteColorLight), primaryTextColorLight);

            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.whiteNoteSchemeColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.redNoteSchemeColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.orangeNoteSchemeColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.yellowNoteSchemeColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.darkBlueNoteSchemeColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.blueNoteSchemeColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.tealNoteSchemeColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.greenNoteSchemeColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.purpleNoteSchemeColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.pinkNoteSchemeColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.brownNoteSchemeColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.greyNoteSchemeColorLight), primaryTextColorDark);

            // Special case for white note.
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.whiteNoteColorDark), primaryTextColorDark);

            // Special case for selected color.
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.cardSelectedColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.cardSelectedColorDark), primaryTextColorDark);

            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.blueTabColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.greenTabColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.redTabColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.orangeTabColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.purpleTabColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.yellowTabColorLight), primaryTextColorLight);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.cyanTabColorLight), primaryTextColorDark);
            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.greyTabColorLight), primaryTextColorDark);

            OPTIMIZED_PRIMARY_TEXT_COLORS.append(getColor(R.color.greyTabColorDark), primaryTextColorDark);
        }

        static {
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.whiteNoteColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.redNoteColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.orangeNoteColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.yellowNoteColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.darkBlueNoteColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.blueNoteColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.tealNoteColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.greenNoteColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.purpleNoteColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.pinkNoteColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.brownNoteColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.greyNoteColorLight), secondaryTextColorLight);

            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.whiteNoteSchemeColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.redNoteSchemeColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.orangeNoteSchemeColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.yellowNoteSchemeColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.darkBlueNoteSchemeColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.blueNoteSchemeColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.tealNoteSchemeColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.greenNoteSchemeColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.purpleNoteSchemeColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.pinkNoteSchemeColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.brownNoteSchemeColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.greyNoteSchemeColorLight), secondaryTextColorDark);

            // Special case for white note.
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.whiteNoteColorDark), secondaryTextColorDark);

            // Special case for selected color.
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.cardSelectedColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.cardSelectedColorDark), secondaryTextColorDark);

            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.blueTabColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.greenTabColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.redTabColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.orangeTabColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.purpleTabColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.yellowTabColorLight), secondaryTextColorLight);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.cyanTabColorLight), secondaryTextColorDark);
            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.greyTabColorLight), secondaryTextColorDark);

            OPTIMIZED_SECONDARY_TEXT_COLORS.append(getColor(R.color.greyTabColorDark), secondaryTextColorDark);
        }

        static {
            IS_LIGHT_COLOR.append(getColor(R.color.whiteNoteColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.redNoteColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.orangeNoteColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.yellowNoteColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.darkBlueNoteColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.blueNoteColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.tealNoteColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.greenNoteColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.purpleNoteColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.pinkNoteColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.brownNoteColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.greyNoteColorLight), true);

            IS_LIGHT_COLOR.append(getColor(R.color.whiteNoteSchemeColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.redNoteSchemeColorLight), false);
            IS_LIGHT_COLOR.append(getColor(R.color.orangeNoteSchemeColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.yellowNoteSchemeColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.darkBlueNoteSchemeColorLight), false);
            IS_LIGHT_COLOR.append(getColor(R.color.blueNoteSchemeColorLight), false);
            IS_LIGHT_COLOR.append(getColor(R.color.tealNoteSchemeColorLight), false);
            IS_LIGHT_COLOR.append(getColor(R.color.greenNoteSchemeColorLight), false);
            IS_LIGHT_COLOR.append(getColor(R.color.purpleNoteSchemeColorLight), false);
            IS_LIGHT_COLOR.append(getColor(R.color.pinkNoteSchemeColorLight), false);
            IS_LIGHT_COLOR.append(getColor(R.color.brownNoteSchemeColorLight), false);
            IS_LIGHT_COLOR.append(getColor(R.color.greyNoteSchemeColorLight), false);

            // Special case for white note.
            IS_LIGHT_COLOR.append(getColor(R.color.whiteNoteColorDark), false);

            // Special case for selected color.
            IS_LIGHT_COLOR.append(getColor(R.color.cardSelectedColorLight), true);
            IS_LIGHT_COLOR.append(getColor(R.color.cardSelectedColorDark), false);
        }
    }

    private Utils() {
    }

    public static boolean isCustomColorIndex(int colorIndex) {
        return CUSTOM_COLOR_INDEX == colorIndex;
    }

    public static int getThemeResourceId(ThemeType themeType) {
        return getThemeResourceId(themeType, WeTodoOptions.INSTANCE.getTheme());
    }

    public static int getThemeResourceId(ThemeType themeType, Theme theme) {
        if (themeType == ThemeType.Main) {
            switch (theme) {
                case Black:
                    return R.style.Theme_WeTodo_Black;
                default:
                    return R.style.Theme_WeTodo_Black;
            }
        }

        Assert (false);
        return -1;
    }

    public static int getContrastForegroundColor(int foregroundColor0, int foregroundColor1, int backgroundColor) {
        if (ColorUtils.calculateContrast(foregroundColor0, backgroundColor) > ColorUtils.calculateContrast(foregroundColor1, backgroundColor)) {
            return foregroundColor0;
        } else {
            return foregroundColor1;
        }
    }

    public static <T extends android.view.View> T getViewOfType(View view, Class<? extends T> clazz) {
        if (clazz.isInstance(view)) {
            return (T)view;
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup)view;
            for (int i=0, ei=viewGroup.getChildCount(); i<ei; i++) {
                View child = viewGroup.getChildAt(i);
                View result = getViewOfType(child, clazz);
                if (result != null) {
                    return (T)result;
                }
            }
        }

        return null;
    }

    public static @ColorInt
    int getColor(@ColorRes int colorRes) {
        return ContextCompat.getColor(WeTodoApplication.instance(), colorRes);
    }

    public static @ColorInt int getOptimizedPrimaryTextColor(int backgroundColor) {
        int optimizedTextColor = ColorLazyHolder.OPTIMIZED_PRIMARY_TEXT_COLORS.get(backgroundColor, backgroundColor);
        if (optimizedTextColor != backgroundColor) {
            return optimizedTextColor;
        }

        return getContrastForegroundColor(
                ColorLazyHolder.primaryTextColorLight,
                ColorLazyHolder.primaryTextColorDark,
                backgroundColor
        );
    }

    public static void scroll(RecyclerView recyclerView, int row) {
        // If the row is not visible, scroll to that row.
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager)layoutManager);
            final int firstCompletelyVisibleItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            final int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

            if (row < firstCompletelyVisibleItemPosition || row > lastCompletelyVisibleItemPosition) {
                ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(row, 0);
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            // I would like to have similar scrolling logic as LinearLayoutManager, by using
            // findFirstCompletelyVisibleItemPosition and findLastCompletelyVisibleItemPosition.
            // However, I'm not exactly sure how to do that for StaggeredGridLayoutManager. Since,
            // not much users are using StaggeredGridLayoutManager right now, we can omit it at
            // this moment.
            ((StaggeredGridLayoutManager)layoutManager).scrollToPositionWithOffset(row, 0);
        } else {
            Assert(false);
        }
    }

    public static boolean isPureDarkTheme() {
        return Theme.PureDark == WeTodoOptions.INSTANCE.getTheme();
    }

    public static boolean isDarkTheme() {
        return Theme.Dark == WeTodoOptions.INSTANCE.getTheme();
    }

    public static boolean isLightBackgroundColor(int backgroundColor) {
        if (ColorLazyHolder.IS_LIGHT_COLOR.indexOfKey(backgroundColor) >= 0) {
            return ColorLazyHolder.IS_LIGHT_COLOR.get(backgroundColor, true);
        }

        if (Color.BLACK == getContrastForegroundColor(Color.BLACK, Color.WHITE, backgroundColor)) {
            return true;
        }

        return false;
    }
}
