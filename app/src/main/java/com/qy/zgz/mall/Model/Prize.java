package com.qy.zgz.mall.Model;

import java.util.List;

/**
 * Created by Administrator on 2018/1/24 0024.
 */

public class Prize {
    //类型:cash现金，goods商品，ticket电影票，coupons优惠券
    private String type;
    private String money;
    private String url;
    private int id;
    private String num;
    private String img;
    private String bonus_desc;//奖品名
    private String seat;//奖品位置
    private List<String> seatList;//奖品位置列表


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getBonus_desc() {
        return bonus_desc;
    }

    public void setBonus_desc(String bonus_desc) {
        this.bonus_desc = bonus_desc;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public List<String> getSeatList() {
        return seatList;
    }

    public void setSeatList(List<String> seatList) {
        this.seatList = seatList;
    }
}
