package com.qy.zgz.mall.Model;

import java.io.Serializable;
import java.util.List;

/**
 * 广告视频
 */
public class Video implements Serializable {
    //视频地址
    private String playurl;
    //类型；ad为有广告页
    private String type;
    private List<String> images;
    private String qcode;
    private String test;

    public String getPlayurl() {
        return playurl;
    }

    public void setPlayurl(String playurl) {
        this.playurl = playurl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getQcode() {
        return qcode;
    }

    public void setQcode(String qcode) {
        this.qcode = qcode;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
