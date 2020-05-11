package com.yocto.wetodo.color;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.yocto.wetodo.Utils;

import static com.yocto.wetodo.Constants.SELECTOR_RADIUS_DP;

/**
 * HSV color wheel
 */
public class ColorWheelView extends FrameLayout {
    public interface ColorWheelViewListener {
        void onColor(int color);
    }

    private float radius;
    private float centerX;
    private float centerY;

    private float selectorRadiusPx;

    private PointF currentPoint = new PointF();
    private int currentColor = Color.MAGENTA;

    private ColorWheelViewListener colorWheelViewListener;
    private ColorWheelSelector selector;

    public ColorWheelView(Context context) {
        this(context, null);
    }

    public ColorWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        selectorRadiusPx = Utils.dpToPixel(SELECTOR_RADIUS_DP);

        {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ColorWheelPalette palette = new ColorWheelPalette(context);
            int padding = (int) selectorRadiusPx;
            palette.setPadding(padding, padding, padding, padding);
            addView(palette, layoutParams);
        }

        {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            selector = new ColorWheelSelector(context);
            addView(selector, layoutParams);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        width = height = Math.min(maxWidth, maxHeight);
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int netWidth = w - getPaddingLeft() - getPaddingRight();
        int netHeight = h - getPaddingTop() - getPaddingBottom();
        radius = Math.min(netWidth, netHeight) * 0.5f - selectorRadiusPx;
        if (radius < 0) return;
        centerX = netWidth * 0.5f;
        centerY = netHeight * 0.5f;
        setColor(currentColor);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                update(event);
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void update(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (colorWheelViewListener != null) {
            colorWheelViewListener.onColor(getColorAtPoint(x, y));
        }
        updateSelector(x, y);
    }

    private int getColorAtPoint(float eventX, float eventY) {
        float x = eventX - centerX;
        float y = eventY - centerY;
        double r = Math.sqrt(x * x + y * y);
        float[] hsv = {0, 0, 1};
        hsv[0] = (float) (Math.atan2(y, -x) / Math.PI * 180f) + 180;
        hsv[1] = Math.max(0f, Math.min(1f, (float) (r / radius)));
        return Color.HSVToColor(hsv);
    }

    public void setColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        float r = hsv[1] * radius;
        float radian = (float) (hsv[0] / 180f * Math.PI);
        updateSelector((float) (r * Math.cos(radian) + centerX), (float) (-r * Math.sin(radian) + centerY));
        currentColor = color;
        if (colorWheelViewListener != null) {
            colorWheelViewListener.onColor(color);
        }
    }

    private void updateSelector(float eventX, float eventY) {
        float x = eventX - centerX;
        float y = eventY - centerY;
        double r = Math.sqrt(x * x + y * y);
        if (r > radius) {
            x *= radius / r;
            y *= radius / r;
        }
        currentPoint.x = x + centerX;
        currentPoint.y = y + centerY;
        selector.setCurrentPoint(currentPoint);
    }

    public void setColorWheelViewListener(ColorWheelViewListener colorWheelViewListener) {
        this.colorWheelViewListener = colorWheelViewListener;
    }
}

