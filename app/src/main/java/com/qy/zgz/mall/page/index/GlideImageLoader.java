package com.qy.zgz.mall.page.index;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.qy.zgz.mall.R;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by LCB on 2018/2/2.
 */


public class GlideImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        try {
            Glide.with(context).load(path.toString()).
                    listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            imageView.setImageDrawable(resource);
                            return false;
                        }
                    }).placeholder(R.drawable.index_banner).
                    into(imageView);
        }catch (Exception e){

        }

    }
}
