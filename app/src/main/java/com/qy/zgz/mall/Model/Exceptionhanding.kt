package com.qy.zgz.mall.Model

/**
 * Created by LCB on 2018/3/28.
 */
class Exceptionhanding(
        var Date:String="--",
        var BussType:String="--",
        var ErrType:String="--",//错误类型:CashErr-现金，MobileErr-移动支付，OutCoinErr-出币
        var CustomerName:String="--",
        var Status:String="--",
        var POS:String="--",//移动订单商户单号
        var Amount:String="--",
        var RecCoin:String="--",
        var ActCoin:String="--",
        var DiffCoin:String="--",
        var ID:String="--",//记录ID
        var EntryID:String="--",//分录ID
        var ischeck:Boolean=false,
        var CustomerID:String="--",//会员ID
        var CashErrorRecordId:Int=-1//本地现金异常记录ID

            )