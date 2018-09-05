package com.qy.zgz.mall.entities;

public class CoinInfo {
    private int mPrice; //当值为-1时为自由购买类型
    private boolean mIsVip;

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int mPrice) {
        this.mPrice = mPrice;
    }

    public boolean isVip() {
        return mIsVip;
    }

    public void setmIsVip(boolean mIsVip) {
        this.mIsVip = mIsVip;
    }
}
