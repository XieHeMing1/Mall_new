package com.qy.zgz.mall.utils;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by HiCode 12/3/15.
 */
public class FrescoUtils {

    private static boolean isInit = false;

    /**
     * 显示缩略图
     *
     * @param draweeView     draweeView
     * @param uri       url
     */
//    public static void showThumb(SimpleDraweeView draweeView, Uri uri, int resizeWidthXp, int resizeHeightXp) {
//        if (draweeView == null)
//            return;
//        initialize(draweeView.getContext());
//        ImageRequest build = null;
//        build = ImageRequestBuilder.newBuilderWithSource(uri)
//                .setResizeOptions(new ResizeOptions(resizeWidthXp, resizeHeightXp)).build();
//
//        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
//                .setImageRequest(build)
//                .build();
//        draweeView.setController(controller);
//    }

    /**
     * 显示缩略图
     *
     * @param draweeView     draweeView
     * @param resizeWidthDp  resizeWidth
     * @param resizeHeightDp resizeHeight
     */
    public static void showThumb(SimpleDraweeView draweeView, Uri uri, int resizeWidthDp, int resizeHeightDp) {
        if (draweeView == null)
            return;
        if (uri == null)
            return;
        initialize(draweeView.getContext());
        try {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(resizeWidthDp, resizeHeightDp))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(draweeView.getController())
                    .setControllerListener(new ImageListener(uri))
                    .build();
            draweeView.setController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * initialize
     *
     * @param context context
     */
    public static void initialize(Context context) {

        if (isInit)
            return;

        isInit = true;
    }

    public static void setControllerListener(final SimpleDraweeView simpleDraweeView, Uri imagePath, final int imageWidth) {
        final ViewGroup.LayoutParams layoutParams = simpleDraweeView.getLayoutParams();
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                int height = imageInfo.getHeight();
                int width = imageInfo.getWidth();
                layoutParams.width = imageWidth;
                layoutParams.height = (int) ((float) (imageWidth * height) / (float) width);
                simpleDraweeView.setLayoutParams(layoutParams);
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                throwable.printStackTrace();
            }
        };
        DraweeController controller = Fresco.newDraweeControllerBuilder().setControllerListener(controllerListener).setUri(imagePath).build();
        simpleDraweeView.setController(controller);
    }


    /**
     * @param simpleDraweeView
     * @param resId
     */
    public static void loadGifPicInApp(@NonNull SimpleDraweeView simpleDraweeView, @NonNull int resId) {
        if (simpleDraweeView == null) {
            return;
        }
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(resId))
                .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        simpleDraweeView.setController(draweeController);
    }


}