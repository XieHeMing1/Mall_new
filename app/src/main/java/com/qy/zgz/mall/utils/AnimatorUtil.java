package com.qy.zgz.mall.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;

import com.zhy.autolayout.utils.AutoUtils;

/**
 * 动画工具类
 */
public class AnimatorUtil {
    /**
     * 全屏移动
     */
    int isAddX = -1;
    int isAddY = -1;
    int addX = AutoUtils.getPercentWidthSize(5);
    int addY = AutoUtils.getPercentHeightSize(5);
    private Handler handler = new Handler();
    private View view;
    ObjectAnimator animatorator;
    public void screenTranslation(View view) {
        this.view = view;
        float curTranslationX = view.getTranslationX();
        float curTranslationX2 = curTranslationX + addX * isAddX;
        float curTranslationY = view.getTranslationY();
        float curTranslationY2 = curTranslationY + addY * isAddY;

        if(animatorator!=null)
        {
            animatorator.pause();
            animatorator=null;
        }
        animatorator = ObjectAnimator.ofFloat(view, "translationX", curTranslationX, curTranslationX2).setDuration(100);
        ObjectAnimator.ofFloat(view, "translationY", curTranslationY, curTranslationY2).setDuration(100).start();
        animatorator.start();
        animatorator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (view.getX() <= 0) {
                    isAddX = isAddX * -1;
                }
                if (view.getY() <= 0) {
                    isAddY = isAddY * -1;
                }
                if ((view.getX() + AutoUtils.getPercentWidthSize(327)) >= AutoUtils.getPercentWidthSize(2278)) {
                    isAddX = isAddX * -1;
                }
                if ((view.getY() + AutoUtils.getPercentHeightSize(368)) >= AutoUtils.getPercentWidthSize(4049)) {
                    isAddY = isAddY * -1;
                }
                screenTranslation(view);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
//        handler.postDelayed(runnable,90);

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (view.getX() <= 0) {
                isAddX = isAddX * -1;
            }
            if (view.getY() <= 0) {
                isAddY = isAddY * -1;
            }
            if ((view.getX() + AutoUtils.getPercentWidthSize(327)) >= AutoUtils.getPercentWidthSize(2278)) {
                isAddX = isAddX * -1;
            }
            if ((view.getY() + AutoUtils.getPercentHeightSize(368)) >= AutoUtils.getPercentWidthSize(4049)) {
                isAddY = isAddY * -1;
            }
            screenTranslation(view);
        }
    };
}
