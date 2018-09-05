package com.qy.zgz.mall.utils;

import android.graphics.drawable.Animatable;
import android.net.Uri;

import com.facebook.drawee.controller.ControllerListener;

import java.io.File;

import javax.annotation.Nullable;

/**
 * Created by zgz on 2017/12/22.
 */

public class ImageListener implements ControllerListener {
    private Uri path;
    ImageListener(Uri path)
    {
        this.path=path;
    }
    @Override
    public void onSubmit(String id, Object callerContext) {

    }

    @Override
    public void onFinalImageSet(String id, @Nullable Object imageInfo, @Nullable Animatable animatable) {

    }

    @Override
    public void onIntermediateImageSet(String id, @Nullable Object imageInfo) {

    }

    @Override
    public void onIntermediateImageFailed(String id, Throwable throwable) {

    }
    /**
     * 图片加载失败时调用的方法
     * @param id
     * @param throwable
     */
    @Override
    public void onFailure(String id, Throwable throwable) {
        if(path.getPath().indexOf("http:")==-1)
        {
            File file=new File(path.getPath());
            if(file!=null && file.exists())
            {
                file.delete();
            }
        }
        System.out.println("加载失败:"+id +","+ this.path.getPath());
    }

    @Override
    public void onRelease(String id) {

    }
}
