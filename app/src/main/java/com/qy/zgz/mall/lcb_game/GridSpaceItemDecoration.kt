package com.qy.zgz.mall.lcb_game

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by LCB on 2018/8/20.
 */

class GridSpaceItemDecoration(private val top: Int, private val right: Int, private val bottom: Int, private val left: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        outRect.top = top
        outRect.left = left
        outRect.right = right
        outRect.bottom = bottom

    }

}
