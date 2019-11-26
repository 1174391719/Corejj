package com.corejj.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.corejj.R;

public class CTriangleView extends View {
    private int mWidth = 52;
    private int mHeight = 30;
    private int mDirection = 1;

    public CTriangleView(Context context) {
        this(context, null);
    }

    public CTriangleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CTriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CTriangleView, defStyleAttr, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.CTriangleView_triangle_direction) {
                mDirection = array.getInteger(attr, 0);
            } else if (attr == R.styleable.CTriangleView_triangle_width) {
                mWidth = array.getInteger(attr, 0);
            } else if (attr == R.styleable.CTriangleView_triangle_height) {
                mHeight = array.getInteger(attr, 0);
            }
        }
        array.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDirection == 1) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(3);
            Path path = new Path();
            path.moveTo(0, mHeight);
            path.lineTo(mWidth / 2, 0);
            path.lineTo(mWidth, mHeight);
            path.close();
            canvas.drawPath(path, paint);
        } else if (mDirection == 2) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.parseColor("#b0000000"));
            paint.setStrokeWidth(3);
            Path path = new Path();
            path.moveTo(0, 0);
            path.lineTo(mWidth, mHeight / 2);
            path.lineTo(0, mHeight);
            path.close();
            canvas.drawPath(path, paint);
        }
    }
}
