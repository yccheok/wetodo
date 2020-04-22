package com.yocto.wetodo;

import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.UUID;

public class Utils {
    private Utils() {
    }

    public static int dpToPixel(float dp) {
        DisplayMetrics displayMetrics = WeTodoApplication.instance().getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics) + 0.5);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static void Assert(boolean condition) {
        if (!condition) {
            throw new java.lang.RuntimeException();
        }
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }
}
