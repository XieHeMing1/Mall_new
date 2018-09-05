//package com.qy.zgz.kamiduonew.Realm;
//
//import com.qy.zgz.kamiduonew.utils.DateUtils;
//
//import io.realm.RealmModel;
//import io.realm.annotations.RealmClass;
//import io.realm.annotations.Required;
//
///**
// * Created by LCB on 2018/5/8.
// * 出币记录
// */
//
//@RealmClass
//public class OutCoinRecord implements RealmModel {
//    @Required
//    private String createtime= DateUtils.getDateToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss");
//    @Required
//    private int isError=1;//0--代表正常,1--代表异常
//    @Required
//    private int outcount=1;
//    @Required
//    private String StockBillID="";//销售记录ID
//    @Required
//    private String CustID="";//会员ID
//    @Required
//    private String CustN="";//会员ID名称
//
//    public String getCreatetime() {
//        return createtime;
//    }
//
//    public void setCreatetime(String createtime) {
//        this.createtime = createtime;
//    }
//
//    public int getIsError() {
//        return isError;
//    }
//
//    public void setIsError(int isError) {
//        this.isError = isError;
//    }
//
//    public int getOutcount() {
//        return outcount;
//    }
//
//    public void setOutcount(int outcount) {
//        this.outcount = outcount;
//    }
//
//    public String getStockBillID() {
//        return StockBillID;
//    }
//
//    public void setStockBillID(String stockBillID) {
//        StockBillID = stockBillID;
//    }
//
//    public String getCustID() {
//        return CustID;
//    }
//
//    public void setCustID(String custID) {
//        CustID = custID;
//    }
//
//    public String getCustN() {
//        return CustN;
//    }
//
//    public void setCustN(String custN) {
//        CustN = custN;
//    }
//}
