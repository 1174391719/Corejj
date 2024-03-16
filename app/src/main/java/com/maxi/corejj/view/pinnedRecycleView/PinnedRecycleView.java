package com.maxi.corejj.view.pinnedRecycleView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.maxi.corejj.callback.IntCallback;

public class PinnedRecycleView extends RecyclerView {
    private int mPinnedViewHeight = 0;
    private IntCallback mIntCallback;
    private boolean mHasPinnedTitle;

    public PinnedRecycleView(@NonNull Context context) {
        super(context);
    }

    public PinnedRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PinnedRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!mHasPinnedTitle || mIntCallback == null || mPinnedViewHeight == 0) {
            return super.onInterceptTouchEvent(e);
        }
        if (e.getY() < mPinnedViewHeight) {
            mIntCallback.onResponse(1);
            return true;
        }
        return super.onInterceptTouchEvent(e);
    }

    public void setPinnedViewHeight(int height) {
        mPinnedViewHeight = height;
    }

    public void setPinnedViewOnClickListener(IntCallback callback) {
        mIntCallback = callback;
    }

    public void setHasPinnedTitle(boolean has) {
        mHasPinnedTitle = has;
    }
}
