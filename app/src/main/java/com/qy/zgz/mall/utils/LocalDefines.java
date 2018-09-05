package com.qy.zgz.mall.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qy.zgz.mall.Model.ShopCar;

import java.util.ArrayList;

public class LocalDefines {

    /**
     * 获取img包名信息
     * @param context
     * @return
     *
     */
    public static String getImgUriHead(Context context) {
        return "res://"+context.getPackageName() + "/";
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                  int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
//        Log.d(TAG, "origin, w= " + width + " h=" + height);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

//        Log.d(TAG, "sampleSize:" + inSampleSize);
        return inSampleSize;
    }


    /**
     * 判断是否已经登录
     */
    public static boolean sIsLogin = false;

    /**
     * 获取具体订单类型字符串
     */
    public static final String WAIT_BUYER_PAY="WAIT_BUYER_PAY"; //代收款
    public static final String WAIT_SELLER_SEND_GOODS="WAIT_SELLER_SEND_GOODS"; //待发货
    public static final String WAIT_BUYER_CONFIRM_GOODS="WAIT_BUYER_CONFIRM_GOODS"; //待收货
    public static final String WAIT_RATE="WAIT_RATE";  //待评价

    //是否获取滚动横幅数据SP值
    public static final String LAST_GET_BANNER_TIME = "LAST_GET_BANNER_TIME";

//    public static ArrayList<ShopCar> sShopCarArrayList = new ArrayList<>();

}
