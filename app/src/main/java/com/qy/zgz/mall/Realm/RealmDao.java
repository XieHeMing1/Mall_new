//package com.qy.zgz.kamiduonew.Realm;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import io.realm.Realm;
//import io.realm.RealmResults;
//
///**
// * Created by LCB on 2018/5/8.
// */
//
//public class RealmDao {
//    //添加一条记录(出币记录)
//    public void addOutCoinRecord(OutCoinRecord outCoinRecord) {
//        Realm mRealm = Realm.getDefaultInstance();
//        mRealm.beginTransaction();
//        mRealm.copyToRealm(outCoinRecord);
//        mRealm.commitTransaction();
//    }
//
//
//    //删除正常记录(出币记录)
//    public void delOutCoinRecordByWhere() {
//        Realm mRealm = Realm.getDefaultInstance();
//        //先查找到数据
//        final RealmResults<OutCoinRecord> outList = mRealm.where(OutCoinRecord.class).equalTo("isError",0).findAll();
//        mRealm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                outList.deleteAllFromRealm();
//            }
//        });
//    }
//
//
//    //查询异常记录(出币记录)
//    public  ArrayList<HashMap<String,Object>> findOutRecordByError() {
//        Realm mRealm = Realm.getDefaultInstance();
//        ArrayList<String> billList=new ArrayList<>();
//        ArrayList<HashMap<String,Object>> recordArrayList=new ArrayList<>();
//        //先查找到数据
//        final RealmResults<OutCoinRecord> outList = mRealm.where(OutCoinRecord.class).equalTo("isError",1).findAll();
//        for (OutCoinRecord record:outList){
//            if(!billList.contains(record.getStockBillID())){
//                billList.add(record.getStockBillID());
//            }
//
//        }
//
//        for (String StockBillID:billList){
//            HashMap<String,Object> map=new HashMap<>();
//            map.put("StockBillID",StockBillID);
//            int num=  outList.where().equalTo("StockBillID",StockBillID).findAll().sum("outcount").intValue();
//            map.put("coinsnum",num);
//            recordArrayList.add(map);
//        }
//
//        return recordArrayList;
//    }
//
//
//
//
//
//}
