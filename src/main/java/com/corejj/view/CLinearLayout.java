package com.corejj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.corejj.callback.PositionCallback;
import com.corejj.utils.ZLog;

public class CLinearLayout extends LinearLayout {
    private PositionCallback mCallback;

    public CLinearLayout(Context context) {
        super(context);
    }

    public CLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mCallback != null) {
            mCallback.onResponse(ev.getX(), ev.getY());
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setCallback(PositionCallback callback) {
        mCallback = callback;
    }


}
