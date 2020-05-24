package com.yocto.wetodo.color;

import android.graphics.Color;

public class Utils {
    private Utils() {
    }

    static float colorToV(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[2];
    }

    static String toHexColorString(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        // https://stackoverflow.com/questions/3607858/convert-a-rgb-color-value-to-a-hexadecimal-string
        String hex = String.format("#%02X%02X%02X", r, g, b);
        return hex;
    }

    public static boolean hasAlpha(int color) {
        return Color.alpha(color) != 255;
    }
}
