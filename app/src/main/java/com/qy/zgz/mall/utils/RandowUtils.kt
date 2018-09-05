package com.qy.zgz.mall.utils

/**
 * Created by LCB on 2018/4/26.
 */
class RandowUtils {
    companion object {
        /**
         * 创建指定数量的随机字符串
         * @param numberFlag 是否是数字
         * @param length
         * @return
         */
        fun createRandom(numberFlag: Boolean, length: Int): String {
            var retStr = ""
            val strTable = if (numberFlag) "1234567890" else "1234567890abcdefghijkmnpqrstuvwxyz"
            val len = strTable.length
            var bDone = true
            do {
                retStr = ""
                var count = 0
                for (i in 0 until length) {
                    val dblR = Math.random() * len
                    val intR = Math.floor(dblR).toInt()
                    val c = strTable[intR]
                    if (c in '0'..'9') {
                        count++
                    }
                    retStr += strTable[intR]
                }
                if (count >= 2) {
                    bDone = false
                }
            } while (bDone)

            return retStr
        }
    }

}