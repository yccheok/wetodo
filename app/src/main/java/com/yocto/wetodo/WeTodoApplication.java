package com.yocto.wetodo;

import androidx.multidex.MultiDexApplication;

public class WeTodoApplication extends MultiDexApplication {
    private static WeTodoApplication me;

    @Override
    public void onCreate() {
        super.onCreate();

        android.util.Log.i("CHEOK", "WARNING: You should not see this message if you're in release build");

        me = this;
    }

    public static WeTodoApplication instance() {
        return me;
    }
}
