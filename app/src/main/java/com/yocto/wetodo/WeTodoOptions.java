package com.yocto.wetodo;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.yocto.wetodo.Constants.PREFERRED_THEME;

public enum WeTodoOptions {
    INSTANCE;

    private static final String TAG = "WeTodoOptions";

    public static final String THEME = "THEME";

    @NonNull
    private Theme theme = PREFERRED_THEME;

    WeTodoOptions() {
        final Gson gson = getGsonForRead();
        final SharedPreferences sharedPreferences = WeTodoApplication.instance().getSharedPreferences();

        try {
            String json_theme = sharedPreferences.getString(THEME, null);

            if (!Utils.isNullOrEmpty(json_theme)) {
                Theme theme = gson.fromJson(json_theme, Theme.class);
                if (theme != null) {
                    this.theme = theme;
                }
            }
        } catch (Exception | AssertionError e) {
            Log.e(TAG, "", e);
        }
    }

    public Theme getTheme() {
        return this.theme;
    }

    private static Gson getGsonForRead() {
        final Gson gson = new GsonBuilder()
                .create();
        return gson;
    }
}
