package com.yocto.wetodo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;

public class WeTodoApplication extends MultiDexApplication implements DefaultLifecycleObserver {
    @Override
    public void onResume(LifecycleOwner owner) {
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        try {
            WeTodoOptions.INSTANCE.saveToSharedPreferences();
        } finally {
        }
    }

    private static WeTodoApplication me;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        android.util.Log.i("CHEOK", "WARNING: You should not see this message if you're in release build");

        me = this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initLifecycleObserver();
    }

    private void initLifecycleObserver() {
        Lifecycle lifecycle = ProcessLifecycleOwner.get().getLifecycle();
        lifecycle.removeObserver(this);
        lifecycle.addObserver(this);
    }

    public static WeTodoApplication instance() {
        return me;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
