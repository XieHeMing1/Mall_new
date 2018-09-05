package com.qy.zgz.mall.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 广告实体
 */
public class Cranemaapi implements Serializable {
    //大的广告图
    private ArrayList<Cinemadata> cinemabigdata;
    //小的广告图
//    private ArrayList<Cinemadata> cinemasmalldata;
    //中的广告图
    private ArrayList<Cinemadata> cinemamediumdata;
    private ArrayList<CinemadataCategory> cinemasmalldata;
    //获取京东数据
    private ArrayList<Cinemadata> cinemajddata;
    //获取卡券数据
    private ArrayList<Cinemadata> cinemakadata;
    //视频
    private ArrayList<Video> videodata;
    //滚动文本
    private List<String> textdata;
    //红包二维码
    private String cinemapacketdata;
    //定制链接页面
    private String giftCust;
    //定制链接页面
    private String home_page="1";

    //滚动横幅
    private ArrayList<String> images;


    public ArrayList<Cinemadata> getCinemabigdata() {
        return cinemabigdata;
    }

    public void setCinemabigdata(ArrayList<Cinemadata> cinemabigdata) {
        this.cinemabigdata = cinemabigdata;
    }

    public ArrayList<CinemadataCategory> getCinemasmalldata() {
        return cinemasmalldata;
    }

    public void setCinemasmalldata(ArrayList<CinemadataCategory> cinemasmalldata) {
        this.cinemasmalldata = cinemasmalldata;
    }

    public ArrayList<Cinemadata> getCinemamediumdata() {
        return cinemamediumdata;
    }

    public void setCinemamediumdata(ArrayList<Cinemadata> cinemamediumdata) {
        this.cinemamediumdata = cinemamediumdata;
    }

    public ArrayList<Video> getVideodata() {
        return videodata;
    }

    public void setVideodata(ArrayList<Video> videodata) {
        this.videodata = videodata;
    }

    public List<String> getTextdata() {
        return textdata;
    }

    public void setTextdata(List<String> textdata) {
        this.textdata = textdata;
    }

    public String getCinemapacketdata() {
        return cinemapacketdata;
    }

    public void setCinemapacketdata(String cinemapacketdata) {
        this.cinemapacketdata = cinemapacketdata;
    }

    public ArrayList<Cinemadata> getCinemajddata() {
        return cinemajddata;
    }

    public void setCinemajddata(ArrayList<Cinemadata> cinemajddata) {
        this.cinemajddata = cinemajddata;
    }

    public String getGiftCust() {
        return giftCust;
    }

    public void setGiftCust(String giftCust) {
        this.giftCust = giftCust;
    }

    public ArrayList<Cinemadata> getCinemakadata() {
        return cinemakadata;
    }

    public void setCinemakadata(ArrayList<Cinemadata> cinemakadata) {
        this.cinemakadata = cinemakadata;
    }

    public String getHome_page() {
        return home_page;
    }

    public void setHome_page(String home_page) {
        this.home_page = home_page;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getImages() {
        return images;
    }
}
