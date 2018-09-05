package com.qy.zgz.mall.page.max;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qy.zgz.mall.R;
import com.qy.zgz.mall.utils.FileManager;
import com.qy.zgz.mall.utils.FrescoUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * 详情页holder
 */
public class MaxImageHolder extends RecyclerView.ViewHolder {
    private SimpleDraweeView ivItem;

    public MaxImageHolder(View itemView) {
        super(itemView);
        AutoUtils.autoSize(itemView);
        ivItem = (SimpleDraweeView) itemView.findViewById(R.id.iv_item);
    }

    public void showData(String str) {
        String fileName = FileManager.getInstance().getFileName(str);
        Uri uri=null;
        if (FileManager.getInstance().isFileExists(fileName)) {
            String url = "file://" + FileManager.getInstance().getDestFileDir() + fileName;
            uri=Uri.parse(url);
        } else {
            uri=Uri.parse(str);
        }
        FrescoUtils.setControllerListener(ivItem,uri,AutoUtils.getPercentWidthSize(1858));
//        FrescoUtils.showThumb(ivItem,uri,AutoUtils.getPercentWidthSize(1858),AutoUtils.getPercentHeightSize(1170));

    }

}
