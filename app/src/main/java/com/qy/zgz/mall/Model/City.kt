package com.qy.zgz.mall.Model

import java.io.Serializable

/**
 * Created by LCB on 2018/3/28.
 * 省,市，区
 */
class City(var id:String="",
           var value:String="",
           var parentId:String="",
           var children:ArrayList<District>?=ArrayList()
            ): Serializable