package com.qy.zgz.mall.Model

import java.io.Serializable

/**
 * Created by LCB on 2018/3/28.
 */
class BuyCoins(
        var Id:String="",
        var Name:String="",
        var PackagePrice:String="",
        var StandardCoins:String="",
        var IsMember:Boolean=false,
        var Coins1:String="",
        var BuyQty:String="1",//匹配购买数量(自由购买使用)
        var BuyAmount:String=""//匹配购买金额(自由购买使用)
            ):Serializable