package com.zyzxsp.view;

import android.graphics.Rect;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

public class ResizeMoveAnimation extends Animation {
    View view;
    int fromLeft;
    int fromTop;
    int fromRight;
    int fromBottom;
    int toLeft;
    int toTop;
    int toRight;
    int toBottom;

    public ResizeMoveAnimation(View fromView, View toView, int duration) {
        this(fromView, fromView.getLeft(), fromView.getTop(), fromView.getRight(), fromView.getBottom(), toView.getLeft(), toView.getTop(), toView.getRight(),
                toView.getBottom(), duration);
    }

    public ResizeMoveAnimation(View sourceView, Rect fromRect, Rect toRect, int duration) {
        this(sourceView, fromRect.left, fromRect.top, fromRect.right, fromRect.bottom, toRect.left, toRect.top, toRect.right,
                toRect.bottom, duration);
    }

    public ResizeMoveAnimation(View targetView, int fromLeft, int fromTop, int fromRight, int fromBottom, int toLeft, int toTop, int toRight, int toBottom,
                               int duration) {
        this.view = targetView;
        this.toLeft = toLeft;
        this.toTop = toTop;
        this.toRight = toRight;
        this.toBottom = toBottom;

        this.fromLeft = fromLeft;
        this.fromTop = fromTop;
        this.fromRight = fromRight;
        this.fromBottom = fromBottom;

        setDuration(duration);
        setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        float left = fromLeft + (toLeft - fromLeft) * interpolatedTime;
        float top = fromTop + (toTop - fromTop) * interpolatedTime;
        float right = fromRight + (toRight - fromRight) * interpolatedTime;
        float bottom = fromBottom + (toBottom - fromBottom) * interpolatedTime;

        view.layout((int) left, (int) top, (int) right, (int) bottom);
    }
}
