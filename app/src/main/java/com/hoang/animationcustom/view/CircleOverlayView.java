package com.hoang.animationcustom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.hoang.animationcustom.R;

/**
 * Created by Hoang-PC on 12/23/2016.
 */

public class CircleOverlayView extends FrameLayout {
    private int RADIUS = 200;

    private Paint mBackgroundPaint;
    private float mCx = -1;
    private float mCy = -1;

    private int mTutorialColor = Color.parseColor("#D20E0F02");

    public CircleOverlayView(Context context) {
        super(context);
        init();
    }

    public CircleOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleOverlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_HARDWARE, null);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setCircle(Point p, int radius){
        mCx = p.x;
        mCy = p.y;
        RADIUS = radius;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mTutorialColor);
        if (mCx >= 0 && mCy >= 0) {
            canvas.drawCircle(mCx, mCy, RADIUS, mBackgroundPaint);
        }
    }
}

