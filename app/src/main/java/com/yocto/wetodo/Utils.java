package com.yocto.wetodo;

import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Utils {
    private Utils() {
    }

    public static int dpToPixel(float dp) {
        DisplayMetrics displayMetrics = WeTodoApplication.instance().getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics) + 0.5);
    }
}
