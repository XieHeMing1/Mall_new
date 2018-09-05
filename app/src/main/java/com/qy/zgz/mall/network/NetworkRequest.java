package com.qy.zgz.mall.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qy.zgz.mall.Model.Cranemaapi;
import com.qy.zgz.mall.Model.MyOrderDetail;
import com.qy.zgz.mall.Model.User;
import com.qy.zgz.mall.Model.Version;

import java.util.HashMap;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 事件绑定
 */
public class NetworkRequest {
    private NetworkRequest()
    {
    }

    //在顶一次调用时创建单利
    private static class SingletonHolder{
        private static final NetworkRequest INSTANCE=new NetworkRequest();
    }

    public static NetworkRequest getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 登录
     */
    public void login(String accout,String pwd,Subscriber<User> subscribers)
    {
        Observable<User> observable = NetworkClient.getInstance().mService
                .login(accout,pwd)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 获取广告信息
     */
    public void getCranemaapi(String typeid,String cinemaid,Subscriber<Cranemaapi> subscriber) {
        Observable<Cranemaapi> observable = NetworkClient.getInstance().mService
                .getCranemaapi(typeid)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscriber);
    }

//    /**
//     * 获取广告信息2
//     */
//    public void getCranemaapi2(String typeid,String cinemaid,Subscriber<String> subscriber) {
//        Observable<String> observable = NetworkClient.getInstance().mService
//                .getCranemaapi(typeid,cinemaid)
//                .map(new NetworkResultFun<>());
//        toSubscribe(observable, subscriber);
//    }

    /**
     * 获取版本信息
     */
    public void getVersion(Subscriber<Version> subscriber)
    {
        Observable<Version> observable=NetworkClient.getInstance().mService
                .getVersion()
                .map(new NetworkResultFun<>());
        toSubscribe(observable,subscriber);
    }

    /**
     * 搜索商品接口
     */
    public void getSearch(HashMap<String, String> map, Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .getSearch(map)
                .map(new NetworkResultFun<>());
                toSubscribe(observable, subscribers);
    }


    /**
     * 会员登录接口
     */
    public void userLogin(HashMap<String, String> map, Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .userLogin(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员加入购物车
     */
    public void addCart(HashMap<String, String> map, Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .addCart(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员查看购物车
     */
    public void getCart(HashMap<String, String> map, Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .getCart(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员删除购物车
     */
    public void delCart(HashMap<String, String> map, Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .delCart(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员更新购物车
     */
    public void updateCart(HashMap<String, String> map, Subscriber<JsonArray> subscribers)
    {
        Observable<JsonArray> observable = NetworkClient.getInstance().mService
                .updateCart(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员查看结算的购物车
     */
    public void checkOutCart(HashMap<String, String> map, Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .checkOutCart(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员查看地址
     */
    public void memberAddressList(HashMap<String, String> map, Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .memberAddressList(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员添加地址
     */
    public void memberAddressCreate(HashMap<String, String> map, Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .memberAddressCreate(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员删除地址
     */
    public void memberAddressDelete(HashMap<String, String> map, Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .memberAddressDelete(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员修改地址
     */
    public void memberAddressUpdate(HashMap<String, String> map, Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .memberAddressUpdate(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员设置默认地址
     */
    public void memberAddressSetdefault(HashMap<String, String> map, Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .memberAddressSetdefault(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 地区JSON数据
     */
    public void regionJson(Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .regionJson()
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 购物车结算
     */
    public void cartCartPay(HashMap<String, String> map,Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .cartCartPay(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员订单列表
     */
    public void tradeList(HashMap<String, String> map,Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .tradeList(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 会员订单列表详情
     */
    public void tradeGet(HashMap<String, String> map,Subscriber<MyOrderDetail> subscribers)
    {
        Observable<MyOrderDetail> observable = NetworkClient.getInstance().mService
                .tradeGet(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 商城视频轮播
     */
    public void itemVideo(HashMap<String, String> map,Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .itemVideo(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 转转盘抽奖彩票和奖品列表
     */
    public void shopLotteryTicket(HashMap<String, String> map,Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .shopLotteryTicket(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }

    /**
     * 转转盘抽奖彩票和奖品列表
     */
    public void lotteryPaytheLottery(HashMap<String, String> map,Subscriber<JsonObject> subscribers)
    {
        Observable<JsonObject> observable = NetworkClient.getInstance().mService
                .lotteryPaytheLottery(map)
                .map(new NetworkResultFun<>());
        toSubscribe(observable, subscribers);
    }


    /**
     * 根据返回的状态,抛出异常或将返回数据的固定格式中的data值剥取出来
     *
     * @param <T> 具体业务所需的数据类型
     */
    private class NetworkResultFun<T> implements Func1<NetworkResult<T>, T> {

        @Override
        public T call(NetworkResult<T> tNetworkResult) {
            if (tNetworkResult.getCode() != 0) {
                throw new ApiException(tNetworkResult.getCode(),tNetworkResult.getMessage());
            }
            return tNetworkResult.getData();
        }
    }

    /**
     * 处理http请求——RX
     * 切换线程,绑定观察者
     *
     * @param o   被观察者
     * @param s   观察者
     * @param <T> 泛型
     */
    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        Scheduler io= Schedulers.io();
        o.subscribeOn(io)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(io)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
}
