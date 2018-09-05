package com.qy.zgz.mall.Dbsql;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;


/**
 * Created by LCB on 2018/5/8.
 * 出币记录
 */

@Table(name = "OutCoinRecord")
public class DBOutCoinRecord  {

    @Column(name = "id", isId = true, autoGen = true)
    private int id;

    @Column(name = "createtime")
    private String createtime= "";

    @Column(name = "createtimelong")
    private long createtimelong= 0;


    @Column(name = "isError")
    private int isError=1;//0--代表正常,1--代表异常

    @Column(name = "outcount")
    private int outcount=1;

    @Column(name = "StockBillID")
    private String StockBillID="";//销售记录ID

    @Column(name = "CustID")
    private String CustID="";//会员ID

    @Column(name = "CustName")
    private String CustName="";//会员ID名称

    @Column(name = "ClassId")
    private String ClassId="";//班次ID

    @Column(name = "ClassTime")
    private String ClassTime="";//班次时间

    //默认的构造方法必须写出，如果没有，这张表是创建不成功的
    public DBOutCoinRecord() {
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getIsError() {
        return isError;
    }

    public void setIsError(int isError) {
        this.isError = isError;
    }

    public int getOutcount() {
        return outcount;
    }

    public void setOutcount(int outcount) {
        this.outcount = outcount;
    }

    public String getStockBillID() {
        return StockBillID;
    }

    public void setStockBillID(String stockBillID) {
        StockBillID = stockBillID;
    }

    public String getCustID() {
        return CustID;
    }

    public void setCustID(String custID) {
        CustID = custID;
    }

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getClassTime() {
        return ClassTime;
    }

    public void setClassTime(String classTime) {
        ClassTime = classTime;
    }

    public long getCreatetimelong() {
        return createtimelong;
    }

    public void setCreatetimelong(long createtimelong) {
        this.createtimelong = createtimelong;
    }
}
