package com.yocto.wetodo.ui;

import com.yocto.wetodo.R;
import com.yocto.wetodo.Theme;
import com.yocto.wetodo.ThemeType;
import com.yocto.wetodo.WeTodoOptions;

import static com.yocto.wetodo.Utils.Assert;

public class Utils {
    public static final int CUSTOM_COLOR_INDEX = -1;

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

}
