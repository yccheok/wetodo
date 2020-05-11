package com.yocto.wetodo.color;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.yocto.wetodo.R;
import com.yocto.wetodo.Utils;

import static com.yocto.wetodo.Utils.dpToPixel;
import static com.yocto.wetodo.ui.Utils.getColor;
import static com.yocto.wetodo.ui.Utils.getContrastForegroundColor;

public class BrightnessPickerView extends View {
    interface ColorSelectedListener {
        void onColorSelected(int color);
    }

    private final Paint paint = new Paint();
    private final Paint shaderPaint = new Paint();

    private final Paint indicatorPaint = new Paint();
    private final float strokeWidth = dpToPixel(4);

    private int primaryTextColorLight;
    private int primaryTextColorDark;

    private int colorPickerBorderColor;

    private int borderWidthPx;

    private ColorSelectedListener colorSelectedListener;

    private int color;
    private int colorWithAdjustedV;
    private float v = 1.0f;
    private float[] hsv = new float[3];

    public BrightnessPickerView(Context context) {
        super(context);
        init(context);
    }

    public BrightnessPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BrightnessPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BrightnessPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.colorPickerBorderColor, typedValue, true);
        colorPickerBorderColor = typedValue.data;
        borderWidthPx = dpToPixel(1);

        shaderPaint.setAntiAlias(true);
        shaderPaint.setStyle(Paint.Style.FILL);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.STROKE);
        indicatorPaint.setStrokeWidth(this.strokeWidth);

        primaryTextColorLight = getColor(R.color.primaryTextColorLight);
        primaryTextColorDark = getColor(R.color.primaryTextColorDark);
    }

    private static void drawTopArc(Canvas canvas, Paint paint, float left, float top, float radius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(
                    left,
                    top,
                    left + (float)(radius * 2.0),
                    top + (float)(radius * 2.0),
                    180,
                    180,
                    true,
                    paint
            );
        } else {
            canvas.drawArc(
                    new RectF(
                            left,
                            top,
                            left + (float)(radius * 2.0),
                            top + (float)(radius * 2.0)
                    ),
                    180,
                    180,
                    true,
                    paint
            );
        }
    }

    private void drawBottomArc(Canvas canvas, Paint paint, float left, float top, float radius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(
                    left,
                    top,
                    left + (float)(radius * 2.0),
                    top + (float)(radius * 2.0),
                    0,
                    180,
                    true,
                    paint
            );
        } else {
            canvas.drawArc(
                    new RectF(
                            left,
                            top,
                            left + (float)(radius * 2.0),
                            top + (float)(radius * 2.0)
                    ),
                    0,
                    180,
                    true,
                    paint
            );
        }
    }

    public void setColorSelectedListener(ColorSelectedListener colorSelectedListener) {
        this.colorSelectedListener = colorSelectedListener;
    }

    public void setV(float v) {
        this.v = v;

        Color.colorToHSV(color, hsv);
        this.hsv[2] = v;

        this.colorWithAdjustedV = Color.HSVToColor(hsv);
        invalidate();

        colorSelectedListener.onColorSelected(colorWithAdjustedV);
    }

    public void setColor(int color) {
        this.color = color;

        Color.colorToHSV(color, hsv);
        this.hsv[2] = v;

        this.colorWithAdjustedV = Color.HSVToColor(hsv);
        invalidate();

        colorSelectedListener.onColorSelected(colorWithAdjustedV);
    }

    @Override protected void onDraw(Canvas canvas) {
        final float oldV = hsv[2];
        hsv[2] = 1;
        final int topColor = Color.HSVToColor(hsv);
        hsv[2] = 0;
        final int bottomColor = Color.HSVToColor(hsv);
        hsv[2] = oldV;

        final float radius = (float)getWidth() / 2.0f;

        paint.setColor(colorPickerBorderColor);
        drawTopArc(canvas, paint, 0f, 0f, radius);
        drawBottomArc(canvas, paint, 0f, getHeight()-radius*2.0f, radius);

        paint.setColor(topColor);
        drawTopArc(canvas, paint, borderWidthPx, borderWidthPx, radius-borderWidthPx);

        paint.setColor(bottomColor);
        drawBottomArc(canvas, paint, borderWidthPx, getHeight()-radius*2.0f + borderWidthPx, radius-borderWidthPx);

        paint.setColor(colorPickerBorderColor);
        canvas.drawRect(
                0,
                radius,
                getWidth(),
                getHeight()-radius,
                paint
        );

        Shader shader = new LinearGradient(
                (float)borderWidthPx,
                radius,
                (float)borderWidthPx,
                getHeight()-radius,
                topColor,
                bottomColor,
                Shader.TileMode.CLAMP
        );
        shaderPaint.setShader(shader);
        shaderPaint.setColor(topColor);

        canvas.drawRect(
                (float)borderWidthPx,
                radius,
                getWidth()-borderWidthPx,
                getHeight()-radius,
                shaderPaint
        );

        indicatorPaint.setColor(
                getContrastForegroundColor(
                        primaryTextColorLight,
                        primaryTextColorDark,
                        this.colorWithAdjustedV
                )
        );

        final float indicatorRadius = radius;

        // Having +1 is rather hacky. I have no idea why the circle doesn't blended perfectly with
        // the bottom border?!
        canvas.drawCircle(
                radius,
                radius + (1f-v) * (getHeight()-radius-radius + 1),
                indicatorRadius-borderWidthPx-strokeWidth/2.0f,
                indicatorPaint
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                return onTouchReceived(event);
            default:
                return false;
        }
    }

    private boolean onTouchReceived(MotionEvent event) {
        return onTouchReceived((int)event.getX(), (int)event.getY());
    }

    private boolean onTouchReceived(int x, int y) {
        final float radius = (float)getWidth() / 2.0f;

        float ratio = (y - radius) / (getHeight() - radius - radius);

        ratio = Math.min(1, Math.max(0, ratio));

        setV(1.0f - ratio);

        return true;
    }
}
