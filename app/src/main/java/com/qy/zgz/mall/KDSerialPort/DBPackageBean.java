package com.qy.zgz.mall.KDSerialPort;

/**
 * 根据数据表pay_cash来定义类的属性
 */
public class DBPackageBean {

    private int id = 0;
    private String packageID = "";
    private String packageName = "";
    private String creatTime = "";
    private String creatKind = "";//创建类型，1是从后台获取，2是本地自动生成
    private int packageCoins = 0;
    private double packagePrice = 0.00;
    private String state = "";//判断是否显示在可购买套餐列表,1是显示，0不显示

    public DBPackageBean() {

    }

    public DBPackageBean(String packageID, String packageName, String creatTime, String creatKind, int packageCoins, double packagePrice, String state) {
        this.packageID = packageID;
        this.packageName = packageName;
        this.creatTime = creatTime;
        this.creatKind = creatKind;
        this.packageCoins = packageCoins;
        this.packagePrice = packagePrice;
        this.state = state;
    }

    public DBPackageBean(int id, String packageID, String packageName, String creatTime, String creatKind, int packageCoins, double packagePrice, String state) {
        this.id = id;
        this.packageID = packageID;
        this.packageName = packageName;
        this.creatTime = creatTime;
        this.creatKind = creatKind;
        this.packageCoins = packageCoins;
        this.packagePrice = packagePrice;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getCreatKind() {
        return creatKind;
    }

    public void setCreatKind(String creatKind) {
        this.creatKind = creatKind;
    }

    public int getPackageCoins() {
        return packageCoins;
    }

    public void setPackageCoins(int packageCoins) {
        this.packageCoins = packageCoins;
    }

    public double getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(double packagePrice) {
        this.packagePrice = packagePrice;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
