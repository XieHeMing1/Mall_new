package com.qy.zgz.mall.Model

/**
 * Created by LCB on 2018/3/28.
 * 订单详情订单信息
 */
class MyOrderDetail(var status:String="",
                    var cancel_status:String="",
                    var user_id:String="",
                    var tid:String="",
                    var created_time:String="",
                    var buyer_rate:String="",
                    var shop_id:String="",
                    var shop_name:String="",
                    var total_tickets_fee:String="",
                    var total_num:String="",
                    var receiver_state:String="",
                    var receiver_city:String="",
                    var receiver_district:String?="",
                    var receiver_address:String="",
                    var receiver_name:String="",
                    var receiver_mobile:String="",
                    var orders:ArrayList<MyOrderDetailImg>?=ArrayList<MyOrderDetailImg>()
            )