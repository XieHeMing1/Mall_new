package com.qy.zgz.mall.Dbsql;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;


/**
 * Created by LCB on 2018/5/8.
 * 出币记录
 */

@Table(name = "ReceiveMoneyRecord")
public class DBReceiveMoneyRecord {

    @Column(name = "id", isId = true, autoGen = true)
    private int id;

    @Column(name = "createtime")
    private String createtime= "";

    @Column(name = "isError")
    private int isError=1;//0--代表正常,1--代表异常

    @Column(name = "money")
    private double money=0.00;

    @Column(name = "taocanId")
    private String taocanId;//套餐ID

    @Column(name = "taocanNum")
    private int taocanNum;//套餐数量

    @Column(name = "CustID")
    private String CustID="";//会员ID

    @Column(name = "CustName")
    private String CustName="";//会员ID名称

    @Column(name = "ClassId")
    private String ClassId="";//班次ID

    @Column(name = "ClassTime")
    private String ClassTime="";//班次时间

    @Column(name = "createtimelong")
    private long createtimelong= 0;

    //默认的构造方法必须写出，如果没有，这张表是创建不成功的
    public DBReceiveMoneyRecord() {
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

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getTaocanId() {
        return taocanId;
    }

    public void setTaocanId(String taocanId) {
        this.taocanId = taocanId;
    }

    public int getTaocanNum() {
        return taocanNum;
    }

    public void setTaocanNum(int taocanNum) {
        this.taocanNum = taocanNum;
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
