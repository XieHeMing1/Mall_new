package com.qy.zgz.mall.Model

/**
 * Created by LCB on 2018/3/28.
 * 订单信息
 */
class MyOrder(var status:String="",
              var cancel_status:String="",
              var user_id:String="",
              var tid:String="",
              var created_time:String="",
              var buyer_rate:String="",
              var shop_id:String="",
              var shop_name:String="",
              var total_tickets:String="",
              var total_num:String="",
              var order:ArrayList<MyOrderImg>?=ArrayList<MyOrderImg>()
            )