package com.yocto.wetodo.font;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yocto.wetodo.WeTodoOptions;

import static com.yocto.wetodo.Constants.PREFERRED_FONT_TYPE;
import static com.yocto.wetodo.Utils.TypefaceLazyHolder.NOTO_SANS_REGULAR_TYPE_FACE;
import static com.yocto.wetodo.Utils.TypefaceLazyHolder.NOTO_SANS_SEMI_BOLD_TYPE_FACE;
import static com.yocto.wetodo.Utils.TypefaceLazyHolder.ROBOTO_CONDENSED_BOLD_TYPE_FACE;
import static com.yocto.wetodo.Utils.TypefaceLazyHolder.ROBOTO_CONDENSED_REGULAR_TYPE_FACE;
import static com.yocto.wetodo.Utils.TypefaceLazyHolder.ROBOTO_REGULAR_TYPE_FACE;
import static com.yocto.wetodo.Utils.TypefaceLazyHolder.ROBOTO_SLAB_REGULAR_TYPE_FACE;
import static com.yocto.wetodo.Utils.weTodoApplicationIsNull;
import static com.yocto.wetodo.WeTodoOptions.FONT_TYPE;

public class Utils {
    private Utils() {
    }

    private static FontType getFontType(SharedPreferences sharedPreferences) {
        String json_font_type = sharedPreferences.getString(FONT_TYPE, null);
        if (!com.yocto.wetodo.Utils.isNullOrEmpty(json_font_type)) {
            final Gson gson = new GsonBuilder().create();
            FontType fontType = gson.fromJson(json_font_type, FontType.class);
            if (fontType != null) {
                return fontType;
            }
        }
        return PREFERRED_FONT_TYPE;
    }

    public static Typeface getTitleEditTextTypeFace(Context context) {
        final FontType fontType;

        if (weTodoApplicationIsNull()) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            fontType = getFontType(sharedPreferences);
        } else {
            fontType = WeTodoOptions.INSTANCE.getFontType();
        }

        switch (fontType) {
            case NotoSans:
                return NOTO_SANS_REGULAR_TYPE_FACE;

            case SlabSerif:
                return ROBOTO_CONDENSED_REGULAR_TYPE_FACE;

            default:
                return null;
        }
    }

    public static Typeface getTitleTypeFace() {
        FontType fontType = WeTodoOptions.INSTANCE.getFontType();
        switch (fontType) {
            case NotoSans:
                return NOTO_SANS_SEMI_BOLD_TYPE_FACE;

            case SlabSerif:
                return ROBOTO_CONDENSED_BOLD_TYPE_FACE;

            default:
                return null;
        }
    }

    public static Typeface getBodyTypeFace() {
        FontType fontType = WeTodoOptions.INSTANCE.getFontType();
        switch (fontType) {
            case NotoSans:
                return ROBOTO_REGULAR_TYPE_FACE;

            case SlabSerif:
                return ROBOTO_SLAB_REGULAR_TYPE_FACE;

            default:
                return null;
        }
    }
}
