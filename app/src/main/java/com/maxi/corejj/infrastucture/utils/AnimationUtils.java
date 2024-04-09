package com.maxi.corejj.infrastucture.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class AnimationUtils {
    public static void rotation(ImageView imageView) {
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(imageView,
                "rotation", 0f, 360f);
        // 旋转动画持续时间为2秒
        rotateAnimator.setDuration(4000);
        // 设置插值器，使动画匀速进行
        rotateAnimator.setInterpolator(new LinearInterpolator());
        // 设置重复次数为无限次
        rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(imageView,
                "scaleX", 1f, 2f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(imageView,
                "scaleY", 1f, 2f, 1f);
        scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleXAnimator.setDuration(1000);
        scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleYAnimator.setDuration(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotateAnimator, scaleXAnimator, scaleYAnimator);
        animatorSet.start();
    }
}
