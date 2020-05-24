package com.yocto.wetodo.color;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.yocto.wetodo.Utils;

import static com.yocto.wetodo.Constants.SELECTOR_RADIUS_DP;

public class ColorWheelSelector extends View {
    private float STROKE_WIDTH_DP = 2;

    private final Paint paint = new Paint();
    private final float whiteRadius = Utils.dpToPixel(SELECTOR_RADIUS_DP - STROKE_WIDTH_DP);
    private final float blackRadius = Utils.dpToPixel(SELECTOR_RADIUS_DP - STROKE_WIDTH_DP - STROKE_WIDTH_DP);
    private final float strokeWidth = Utils.dpToPixel(STROKE_WIDTH_DP);

    private PointF currentPoint = new PointF();

    public ColorWheelSelector(Context context) {
        this(context, null);
    }

    public ColorWheelSelector(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorWheelSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        canvas.drawCircle(currentPoint.x, currentPoint.y, blackRadius, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(currentPoint.x, currentPoint.y, whiteRadius, paint);
    }

    public void setCurrentPoint(PointF currentPoint) {
        this.currentPoint = currentPoint;
        invalidate();
    }
}
