package com.zyzxsp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class CellRectView extends View {

    public static final int HIDE_BORDER_WIDTH = 0;

    private Paint mPaint;

    private int mRectColor = VideoCell.NORMAL_PARTICIPANT_COLOR;
    private int mBorderWidth = 0;
    private int mDefaultBorderWidth = 1;
    private int mBorderRadius = 2;
    private RectF mBorderRect = new RectF();

    public CellRectView(Context context) {
        super(context);
        init();
    }

    public CellRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.STROKE);

        DisplayMetrics mDisplayMetrics = getContext().getResources().getDisplayMetrics();
        mBorderRadius = mBorderRadius * (int) mDisplayMetrics.density;
        mDefaultBorderWidth = mDefaultBorderWidth * (int) mDisplayMetrics.density;
        mBorderWidth = mDefaultBorderWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int r = getWidth();
        int b = getHeight();

        mPaint.setColor(mRectColor);
        mPaint.setStrokeWidth(mBorderWidth);
        mBorderRect.set(mDefaultBorderWidth, mDefaultBorderWidth, r - mDefaultBorderWidth, b - mDefaultBorderWidth);
        canvas.drawRoundRect(mBorderRect, mBorderRadius, mBorderRadius, mPaint);
    }

    public int getRectColor() {
        return mRectColor;
    }

    public void setRectColor(int mRectColor) {
        this.mRectColor = mRectColor;
        mPaint.setColor(mRectColor);
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    private void setBorderWidth(int mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    @Override
    public void setVisibility(int visibility) {
        setBorderWidth(visibility == VISIBLE ? mDefaultBorderWidth : HIDE_BORDER_WIDTH);
        super.setVisibility(visibility);
    }

}
