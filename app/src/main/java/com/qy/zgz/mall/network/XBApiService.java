package com.qy.zgz.mall.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qy.zgz.mall.Model.Cranemaapi;
import com.qy.zgz.mall.Model.MyOrderDetail;
import com.qy.zgz.mall.Model.User;
import com.qy.zgz.mall.Model.Version;

import java.util.HashMap;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * api接口
 */
public interface XBApiService {
    /**
     * 获取广告信息
     *
     * @return
     */
//    @GET(Constance.CRANEMAAPI)
//    Observable<NetworkResult<Cranemaapi>> getCranemaapi(@Query("typeid") String typeid, @Query("cinemaid") String cinemaid);


    @GET(Constance.CRANEMAAPI)//(Constance.CRANEMAAPI)
    Observable<NetworkResult<Cranemaapi>> getCranemaapi(@Query("shop_id") String typeid);

    @FormUrlEncoded
    @POST(Constance.LOGIN)
    Observable<NetworkResult<User>> login(@Field("account") String account, @Field("password") String password);

    @GET(Constance.VERSION)
    Observable<NetworkResult<Version>> getVersion();

    //搜索商品
    @FormUrlEncoded
    @POST(Constance.GETSEARCH)
    Observable<NetworkResult<JsonObject>> getSearch(@FieldMap HashMap<String,String> map);

    //会员登录
    @FormUrlEncoded
    @POST(Constance.USER_APPLOGIN)
    Observable<NetworkResult<JsonObject>> userLogin(@FieldMap HashMap<String,String> map);

    //加入购物车
    @FormUrlEncoded
    @POST(Constance.CAR_ADD)
    Observable<NetworkResult<JsonObject>> addCart(@FieldMap HashMap<String,String> map);

    //会员查看购物车信息
    @FormUrlEncoded
    @POST(Constance.CAR_GET)
    Observable<NetworkResult<JsonObject>> getCart(@FieldMap HashMap<String,String> map);

    //会员删除购物车信息
    @FormUrlEncoded
    @POST(Constance.CAR_DEL)
    Observable<NetworkResult<JsonObject>> delCart(@FieldMap HashMap<String,String> map);

    //会员更新购物车信息
    @FormUrlEncoded
    @POST(Constance.CART_UPDATE)
    Observable<NetworkResult<JsonArray>> updateCart(@FieldMap HashMap<String,String> map);

    //会员查看结算的购物车信息
    @FormUrlEncoded
    @POST(Constance.CART_CHECKOUT)
    Observable<NetworkResult<JsonObject>> checkOutCart(@FieldMap HashMap<String,String> map);

    //会员查看地址
    @FormUrlEncoded
    @POST(Constance.MEMBER_ADDRESS_LIST)
    Observable<NetworkResult<JsonObject>> memberAddressList(@FieldMap HashMap<String,String> map);

    //会员添加地址
    @FormUrlEncoded
    @POST(Constance.MEMBER_ADDRESS_CREATE)
    Observable<NetworkResult<JsonObject>> memberAddressCreate(@FieldMap HashMap<String,String> map);

    //会员修改地址
    @FormUrlEncoded
    @POST(Constance.MEMBER_ADDRESS_UPDATE)
    Observable<NetworkResult<JsonObject>> memberAddressUpdate(@FieldMap HashMap<String,String> map);

    //会员删除地址
    @FormUrlEncoded
    @POST(Constance.MEMBER_ADDRESS_DELETE)
    Observable<NetworkResult<JsonObject>> memberAddressDelete(@FieldMap HashMap<String,String> map);

    //会员设置默认地址
    @FormUrlEncoded
    @POST(Constance.MEMBER_ADDRESS_SETDEFAULT)
    Observable<NetworkResult<JsonObject>> memberAddressSetdefault(@FieldMap HashMap<String,String> map);

    //地区
    @GET(Constance.REGION_JSON)
    Observable<NetworkResult<JsonObject>> regionJson();

    //购物车结算
    @FormUrlEncoded
    @POST(Constance.CART_CARTPAY)
    Observable<NetworkResult<JsonObject>> cartCartPay(@FieldMap HashMap<String,String> map);

    //订单列表
    @FormUrlEncoded
    @POST(Constance.TRADE_LIST)
    Observable<NetworkResult<JsonObject>> tradeList(@FieldMap HashMap<String,String> map);

    //订单详情
    @FormUrlEncoded
    @POST(Constance.TRADE_GET)
    Observable<NetworkResult<MyOrderDetail>> tradeGet(@FieldMap HashMap<String,String> map);

    //轮播视频
    @FormUrlEncoded
    @POST(Constance.ITEM_VIDEO)
    Observable<NetworkResult<JsonObject>> itemVideo(@FieldMap HashMap<String,String> map);


    //转转盘抽奖彩票和奖品列表
    @FormUrlEncoded
    @POST(Constance.SHOP_LOTTERY_TICKET)
    Observable<NetworkResult<JsonObject>> shopLotteryTicket(@FieldMap HashMap<String,String> map);


    //支付抽奖接口
    @FormUrlEncoded
    @POST(Constance.LOTTERY_PAY_THELOTTERY)
    Observable<NetworkResult<JsonObject>> lotteryPaytheLottery(@FieldMap HashMap<String,String> map);

}
