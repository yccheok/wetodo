package com.yocto.wetodo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import static com.yocto.wetodo.Utils.trackEvent;

public class FixedViewPager extends ViewPager {
    private final String TAG = "FixedViewPager";

    public FixedViewPager(@NonNull Context context) {
        super(context);
    }

    public FixedViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "", e);
            trackEvent("onTouchEvent", "fatal", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "", e);
            trackEvent("onInterceptTouchEvent", "fatal", e.getMessage());
        }
        return false;
    }
}
