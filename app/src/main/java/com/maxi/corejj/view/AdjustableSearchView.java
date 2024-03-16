package com.maxi.corejj.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.input.InputManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.maxi.corejj.R;

public class AdjustableSearchView extends LinearLayout {
    private final int CLOSE_WIDTH = 140;
    private final int EXPAND_WIDTH = 700;
    private View mRootView;
    private ImageView mIcon;
    private boolean mIsExpand = false;

    public AdjustableSearchView(Context context) {
        this(context, null);
    }

    public AdjustableSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdjustableSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater mInflater = LayoutInflater.from(context);
        mRootView = mInflater.inflate(R.layout.layout_ajustable_view, null);
        mIcon = mRootView.findViewById(R.id.iv_icon);
        addView(mRootView);
        LayoutParams params = new LayoutParams(CLOSE_WIDTH, mRootView.getLayoutParams().height);
        mRootView.setLayoutParams(params);
    }


    public void expand() {
        if (mIsExpand) {
            return;
        }
        mIsExpand = true;

        LayoutParams params = new LayoutParams(EXPAND_WIDTH, mRootView.getLayoutParams().height);
        mRootView.setLayoutParams(params);
        mRootView.setTranslationX(EXPAND_WIDTH);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(EXPAND_WIDTH, 0);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mRootView.setTranslationX(value);

            }
        });
        valueAnimator.start();
    }

    public void close() {
        if (!mIsExpand) {
            return;
        }
        mIsExpand = false;

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, EXPAND_WIDTH);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mRootView.setTranslationX(value);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                LayoutParams params = new LayoutParams(CLOSE_WIDTH, mRootView.getLayoutParams().height);
                mRootView.setLayoutParams(params);
                mRootView.setTranslationX(0);
            }
        });
        valueAnimator.start();
        InputMethodManager manager = (InputMethodManager) mRootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(mRootView.getWindowToken(), 0);
        }
    }
}
