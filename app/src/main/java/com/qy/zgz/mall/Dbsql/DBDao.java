package com.qy.zgz.mall.Dbsql;

import android.util.Log;

import com.qy.zgz.mall.MyApplication;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.utils.DateUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by LCB on 2018/6/11.
 */

public class DBDao {

    private static DBDao dao;

    //清理数据的日期
    private final long dayTime=24*60*60*1000*15l;

    public static  DBDao getInstance(){
        if (null==dao){
            dao= new DBDao();
        }

        return dao;
    }

    /**
     * 保存出币记录
     * 存在数据市更新数据，不存在时添加数据
     *
     */
    public void saveOutCoinsRecord(DBOutCoinRecord outCoinRecord){
        try {
            List<DBOutCoinRecord> item= MyApplication.getInstance().db.selector(DBOutCoinRecord.class).where("StockBillID","=",outCoinRecord.getStockBillID())
                    .and("isError","=",1)
                    .findAll();

            if (item!=null&&!item.isEmpty()){
                item.get(item.size()-1).setIsError(outCoinRecord.getIsError());
                item.get(item.size()-1).setOutcount(outCoinRecord.getOutcount());
                MyApplication.getInstance().db.update(item,"isError","outcount");
            }else{
                outCoinRecord.setCreatetime(DateUtils.getDateToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
                outCoinRecord.setCreatetimelong(System.currentTimeMillis());
                MyApplication.getInstance().db.saveBindingId(outCoinRecord);
            }


        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新出币记录
     * 更新记录的状态(将异常更新为正常)
     *
     */
    public void updateStateOutCoinsRecord(List<String> StockBillIDList){
        try {
            List<DBOutCoinRecord> item= MyApplication.getInstance().db.selector(DBOutCoinRecord.class).where("StockBillID","in", StockBillIDList)
                    .and("isError","=",1).findAll();

            if (item!=null&&!item.isEmpty()){
                for (DBOutCoinRecord outCoinRecord:item){
                    outCoinRecord.setIsError(0);
                    MyApplication.getInstance().db.update(outCoinRecord,"isError");
                }

            }


        } catch (DbException e) {
           e.printStackTrace();
            Log.e("DB",e.getMessage());
        }
    }

    /**
     *
     * 查询异常单据
     *
     */
    public List<DBOutCoinRecord> queryErrorBill(){
        try {
            List<DBOutCoinRecord> item= MyApplication.getInstance().db.selector(DBOutCoinRecord.class)
                    .where("isError","=",1)
                    .findAll();


            return item;

        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     *
     * 查询现金异常单据
     *
     */
    public List<DBReceiveMoneyRecord> queryCashErrorBill(){
        try {
            String classId= SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString();
            String classTime= SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString();

            List<DBReceiveMoneyRecord> item= MyApplication.getInstance().db.selector(DBReceiveMoneyRecord.class)
                    .where("isError","=",1)
                    .and("ClassId","=",classId)
                    .and("ClassTime","=",classTime).findAll();

            return item;

        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     *
     * 查询现金异常单据(指定条数)
     *
     */
    public List<DBReceiveMoneyRecord> queryCashErrorBillByNum(int limit,int offset){
        try {
            String classId= SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString();
            String classTime= SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString();

            List<DBReceiveMoneyRecord> item= MyApplication.getInstance().db.selector(DBReceiveMoneyRecord.class)
                    .where("isError","=",1)
                    .and("ClassId","=",classId)
                    .and("ClassTime","=",classTime)
                    .limit(limit).offset(offset).orderBy("createtime",true).findAll();

            return item;

        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 保存现金记录
     * 存在数据市更新数据，不存在时添加数据
     *
     */
    public int saveOrUpdateCashRecord(DBReceiveMoneyRecord receiveMoneyRecord){
        try {
            List<DBReceiveMoneyRecord> item= MyApplication.getInstance().db.selector(DBReceiveMoneyRecord.class).where("id","=",receiveMoneyRecord.getId())
                    .and("isError","=",1).findAll();

            if (item!=null&&!item.isEmpty()){
                item.get(item.size()-1).setIsError(receiveMoneyRecord.getIsError());
                item.get(item.size()-1).setMoney(receiveMoneyRecord.getMoney());
                item.get(item.size()-1).setTaocanNum(receiveMoneyRecord.getTaocanNum());
                MyApplication.getInstance().db.update(item,"isError","taocanNum","money");

                return item.get(item.size()-1).getId();
            }else{
                receiveMoneyRecord.setCreatetime(DateUtils.getDateToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
                receiveMoneyRecord.setCreatetimelong(System.currentTimeMillis());
                MyApplication.getInstance().db.saveBindingId(receiveMoneyRecord);

                DBReceiveMoneyRecord findid= MyApplication.getInstance().db.selector(DBReceiveMoneyRecord.class)
                        .where("isError","=",1).orderBy("createtime",true).findFirst();

                return findid.getId();

            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return -1;
    }


    /**
     * 更新现金记录
     * 更新记录的状态(将异常更新为正常)
     *
     */
    public void updateStateReceiveMoneyRecord(List<Integer> id){
        try {
            List<DBReceiveMoneyRecord> item= MyApplication.getInstance().db.selector(DBReceiveMoneyRecord.class).where("id","in",id)
                    .and("isError","=",1).findAll();

            if (item!=null&&!item.isEmpty()){
                for (DBReceiveMoneyRecord receiveMoneyRecord:item){
                    receiveMoneyRecord.setIsError(0);
                    MyApplication.getInstance().db.update(receiveMoneyRecord,"isError");
                }

            }


        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * 查询本班次现金单据(指定班次)
     *
     */
    public List<DBReceiveMoneyRecord> queryCashBillByClass(){
        try {
            String classId= SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString();
            String classTime= SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString();

            List<DBReceiveMoneyRecord> item= MyApplication.getInstance().db.selector(DBReceiveMoneyRecord.class)
                    .where("ClassId","=",classId)
                    .and("ClassTime","=",classTime)
                    .findAll();

            return item;

        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }


    //清理数据(将15天后的数据清理掉)
    public void cleanNormalData(){
        try {

            MyApplication.getInstance().db
                    .delete(DBOutCoinRecord.class,  WhereBuilder.b().and("createtimelong","<=",System.currentTimeMillis()-dayTime));

            MyApplication.getInstance().db
                    .delete(DBReceiveMoneyRecord.class,  WhereBuilder.b().and("createtimelong","<=",System.currentTimeMillis()-dayTime));

        } catch (DbException e) {
            e.printStackTrace();
        }


    }
}
