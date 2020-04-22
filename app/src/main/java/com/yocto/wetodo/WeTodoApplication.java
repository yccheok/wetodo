package com.yocto.wetodo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.multidex.MultiDexApplication;

public class WeTodoApplication extends MultiDexApplication {
    private static WeTodoApplication me;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        android.util.Log.i("CHEOK", "WARNING: You should not see this message if you're in release build");

        me = this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static WeTodoApplication instance() {
        return me;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
