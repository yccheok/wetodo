package com.yocto.wetodo;

import android.annotation.SuppressLint;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.UUID;

public class Utils {
    private Utils() {
    }

    public static void trackEvent(String category, String action, String label) {
    }

    public static float spToPixelInFloat(float sp) {
        DisplayMetrics displayMetrics = WeTodoApplication.instance().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, displayMetrics);
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

    public static void ensureToolbarTextViewBestTextSize(final TextView toolbarTextView, final float originalToolbarTextViewTextSize) {

        if (toolbarTextView != null && originalToolbarTextViewTextSize > 0) {
            final ViewTreeObserver viewTreeObserver = toolbarTextView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @SuppressLint("NewApi")
                @Override
                public void onGlobalLayout() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        toolbarTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        toolbarTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    // Sound strange at the first place. But, this is a hack to make getLineCount
                    // more accurate.
                    final ViewTreeObserver viewTreeObserver = toolbarTextView.getViewTreeObserver();
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onGlobalLayout() {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                toolbarTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            } else {
                                toolbarTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }

                            final int lineCount = toolbarTextView.getLineCount();

                            if (lineCount > 1) {
                                float minPixel = Utils.spToPixelInFloat(14);
                                toolbarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.max(minPixel, originalToolbarTextViewTextSize * 3.0f / 5.0f));
                            } else if (lineCount == 1) {
                                toolbarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalToolbarTextViewTextSize);
                            }
                        }
                    });
                }
            });
        }
    }
}
