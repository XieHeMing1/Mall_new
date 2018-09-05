package com.qy.zgz.mall.Model;

import java.io.Serializable;
import java.util.List;

/**
 * 广告类
 */
public class Cinemadata implements Serializable {
    private String id;



    //缩略图
    private String image_default_id;
    //详情图
    private List<String> thumb;
    //二维码
    private String qcode;
    private String title;
    private String tickets;
    private String holidaytag;
    private String oldtickets;
    private String sku_id;
    private String item_type;
    private String productType;
    private String skuCode;

//    public String getImages() {
//        return images;
//    }
//
//    public void setImages(String images) {
//        this.images = images;
//    }

    public String getImage_default_id() {
        return image_default_id;
    }

    public void setImage_default_id(String image_default_id) {
        this.image_default_id = image_default_id;
    }

    public List<String> getThumb() {
        return thumb;
    }

    public void setThumb(List<String> thumb) {
        this.thumb = thumb;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQcode() {
        return qcode;
    }

    public void setQcode(String qcode) {
        this.qcode = qcode;
    }

//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }

    public String gettickets() {
        return tickets;
    }

    public void settickets(String tickets) {
        this.tickets = tickets;
    }

    public String getHolidaytag() {
        return holidaytag;
    }

    public void setHolidaytag(String holidaytag) {
        this.holidaytag = holidaytag;
    }


    public String getTickets() {
        return tickets;
    }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }

    public String getOldtickets() {
        return oldtickets;
    }

    public void setOldtickets(String oldtickets) {
        this.oldtickets = oldtickets;
    }

    public String getSku_id() {
        return sku_id;
    }

    public void setSku_id(String sku_id) {
        this.sku_id = sku_id;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
