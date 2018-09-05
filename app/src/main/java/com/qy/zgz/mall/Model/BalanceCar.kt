package com.qy.zgz.mall.Model

import com.google.gson.JsonObject
import java.io.Serializable

/**
 * Created by LCB on 2018/3/28.
 */
class BalanceCar(var title:String="",
                 var tickets:String="",
                 var total_tickets:String="",
                 var image_default_id:String="",
                 var quantity:String="",
                 var cart_id:String="",
                 var price:JsonObject=JsonObject(),
                 var item_id:String="",
                 var item_type:String="default",
                 var productType:String="",
                 var skuCode:String=""

            ): Serializable