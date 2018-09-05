package com.qy.zgz.mall.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by LCB on 2018/2/3.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        space= AutoUtils.getPercentWidthSize(18);
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

    }

}
