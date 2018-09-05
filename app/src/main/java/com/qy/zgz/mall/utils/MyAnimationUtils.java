package com.qy.zgz.mall.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.zhy.autolayout.utils.AutoUtils;

/**
 * 动画工具类
 */
public class MyAnimationUtils {
    private static MyAnimationUtils animationUtils;

    public static MyAnimationUtils getInstance() {
        if (animationUtils == null) {
            synchronized (FileManager.class) {
                if (animationUtils == null) {
                    animationUtils = new MyAnimationUtils();
                }
            }
        }
        return animationUtils;
    }

    /**
     * 全屏移动
     */
    int isAddX = 1;
    int isAddY = 1;
    int addX = AutoUtils.getPercentWidthSize(30);
    int addY = AutoUtils.getPercentHeightSize(30);
    private View view;
    ObjectAnimator animatorator;

    public void screenTranslation(View view) {
        this.view = view;
        float curTranslationX = view.getTranslationX();
        float curTranslationY = view.getTranslationY();

        if (animatorator != null) {
            animatorator.pause();
            animatorator = null;
        }
        float tranx;
        float trany;
        if(isAddX==-1)
        {
            tranx=curTranslationX*-1;
        }
        else{
            tranx= AutoUtils.getPercentWidthSize(2278)-curTranslationX- AutoUtils.getPercentWidthSize(324);
        }
        if(isAddY==-1)
        {
            trany=curTranslationY*-1;
        }
        else
        {
            trany= AutoUtils.getPercentHeightSize(4049-1200-368)-curTranslationY;
        }

        if(Math.abs(tranx)<Math.abs(trany))
        {
            isAddX=isAddX*-1;
            trany=Math.abs(tranx)*isAddY;
        }
        else if(Math.abs(tranx) >Math.abs(trany))
        {
            isAddY=isAddY*-1;
            tranx=Math.abs(trany)*isAddX;
        }
        else
        {
            isAddX=isAddX*-1;
            isAddY=isAddY*-1;
        }

        int second= (int) (Math.abs(tranx)/30*200);


        animatorator = ObjectAnimator.ofFloat(view, "translationX", curTranslationX, curTranslationX+tranx).setDuration(second);
        ObjectAnimator animator2=ObjectAnimator.ofFloat(view, "translationY", curTranslationY, curTranslationY+trany).setDuration(second);

        animatorator.setInterpolator(new LinearInterpolator());
        animator2.setInterpolator(new LinearInterpolator());
        animatorator.start();
        animator2.start();
        animatorator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                if (view.getX() < 0) {
//                    isAddX = isAddX * -1;
//                }
//                if (view.getY() < AutoUtils.getPercentHeightSize(1100)) {
//                    isAddY = isAddY * -1;
//                }
//                if ((view.getX() + AutoUtils.getPercentWidthSize(324)) >= AutoUtils.getPercentWidthSize(2278)) {
//                    isAddX = isAddX * -1;
//                }
//                if ((view.getY() + AutoUtils.getPercentHeightSize(368)) >= AutoUtils.getPercentWidthSize(4049)) {
//                    isAddY = isAddY * -1;
//                }
                screenTranslation(view);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    /**
     * 放大缩小动画
     * @param view
     * @param fromx x从多大
     * @param tox    到多大
     * @param fromy  y从多大
     * @param toy    到多大
     * @param startx 从x哪个点开始
     * @param starty 从y哪个点开始
     * @param duration 毫秒
     * @param offset 延迟
     * @param addListener
     */
    public void scale(View view, float fromx, float tox, float fromy, float toy, float startx, float starty, int duration, int offset, AddListener addListener) {
        ScaleAnimation animation = new ScaleAnimation(fromx, tox, fromy, toy,
                Animation.RELATIVE_TO_SELF, startx, Animation.RELATIVE_TO_SELF, starty);
        animation.setDuration(duration);
        animation.setStartOffset(offset);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (addListener != null) {
                    addListener.end();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 移动动画
     * @param view
     * @param fromx 从x轴哪个点
     * @param tox   到x轴哪个点
     * @param fromy 从y轴哪个点
     * @param toy   到y轴哪个点
     * @param duration 毫秒数
     * @param offset    延迟
     * @param addListener
     */
    public void tran(View view,float fromx,float tox,float fromy,float toy,int duration,int offset,AddListener addListener)
    {
        TranslateAnimation animation= new TranslateAnimation(fromx,tox, fromy,toy);
        animation.setDuration(duration);
        animation.setStartOffset(offset);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (addListener != null) {
                    addListener.end();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 属性移动
     * @param view
     * @param type translationX与translationY
     * @param from 从哪个点
     * @param to    到哪个点
     * @param duration 毫秒数
     * @param offset   延迟
     * @param addListener
     */
    public void tranObjectAnimation(View view,String type,float from,float to,int duration,int offset,AddListener addListener)
    {
        ObjectAnimator animatorator = ObjectAnimator.ofFloat(view, type, from, to);
        animatorator.setDuration(duration);
        animatorator.setStartDelay(offset);
        animatorator.start();
        animatorator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(addListener!=null)
                {
                    addListener.end();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void rotation(View view,float from,float to,int duration,int offset,int repeatCount,AddListener addListener)
    {
        ObjectAnimator animatorator = ObjectAnimator.ofFloat(view, "rotation", from, to);
        animatorator.setDuration(duration);
        animatorator.setStartDelay(offset);
        animatorator.setRepeatCount(repeatCount);
        animatorator.start();
        animatorator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(addListener!=null)
                {
                    addListener.end();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    public interface AddListener {
        public void end();
    }

}
