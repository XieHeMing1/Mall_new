package com.qy.zgz.mall.KDSerialPort;

import java.io.FileInputStream;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Random;

/**
 * Created by Bill.T on 2018/3/13.
 */

public class Common {

    /**
     * @param num 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
     */

    public static int isOdd(int num) {
        return num & 0x1;
    }

    /**
     * @param inHex
     * @return Hex字符串转int
     */
    static public int HexToInt(String inHex) {
        return Integer.parseInt(inHex, 16);
    }

    /**
     * @param inHex
     * @return Hex字符串转byte
     */
    public static byte HexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * @param inByte
     * @return 1字节转2个Hex字符
     */
    public static String Byte2Hex(Byte inByte) {
        return String.format("%02x", inByte).toUpperCase();
    }

    /**
     * @param inBytArr
     * @return 字节数组转转hex字符串
     */
    public static String ByteArrToHex(byte[] inBytArr, boolean isAddSpace) {
        StringBuilder strBuilder = new StringBuilder();
        if (inBytArr != null && inBytArr.length > 0) {
            int j = inBytArr.length;
            for (int i = 0; i < j; i++) {
                strBuilder.append(Byte2Hex(inBytArr[i]));
                if (isAddSpace) {
                    strBuilder.append(" ");
                }
            }
            return strBuilder.toString();
        } else {
            return "";
        }
    }

    /**
     * @param inBytArr
     * @return 字节数组转转hex字符串
     */
    public static String ByteArrToHex(Byte[] inBytArr, boolean isAddSpace) {
        StringBuilder strBuilder = new StringBuilder();
        int j = inBytArr.length;
        for (int i = 0; i < j; i++) {
            strBuilder.append(Byte2Hex(inBytArr[i]));
            if (isAddSpace) {
                strBuilder.append(" ");
            }
        }
        return strBuilder.toString();
    }

    /**
     * @param inBytArr
     * @param offset
     * @param byteCount
     * @return //字节数组转转hex字符串，可选长度
     */
    public static String ByteArrToHex(byte[] inBytArr, int offset, int byteCount) {
        StringBuilder strBuilder = new StringBuilder();
        int j = byteCount;
        for (int i = offset; i < j; i++) {
            strBuilder.append(Byte2Hex(inBytArr[i]));
        }
        return strBuilder.toString();
    }

    /**
     * @param inHex
     * @return //hex字符串转字节数组
     */
    public static byte[] HexToByteArr(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (isOdd(hexlen) == 1) {//奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {//偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * @param
     * @return 生成随机字符串
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


    /**
     * @param byte_1
     * @param byte_2
     * @return //java 合并两个byte数组
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * @param src 16进制字符串
     * @return 字节数组
     * @throws
     * @Title:hexString2String
     * @Description:16进制字符串转字符串
     */
    public static String hexString2String(String src) {
        String temp = "";
        for (int i = 0; i < src.length() / 2; i++) {
            temp = temp
                    + (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2),
                    16).byteValue();
        }
        return temp;
    }


    /**
     * @param strPart 字符串
     * @return 16进制字符串
     * @throws
     * @Title:string2HexString
     * @Description:字符串转16进制字符串
     */
    public static String string2HexString(String strPart) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);
        }
        return hexString.toString();
    }

    /**
     * 求异或和
     *
     * @param byCmd
     * @return
     */
    public static int ArrXor(byte[] byCmd) {
        int a = 0;
        a = byCmd[2];
        for (int i = 3; i < byCmd.length; i++) {
            a = a ^ byCmd[i];
        }
        return a;
    }

    /**
     * 低八位补零
     *
     * @param str
     * @param strLength
     * @return
     */
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);// 左补0
                //sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }

        return str;
    }

    /**
     * ITL加密方法
     *
     * @param data
     * @param offset
     * @param length
     * @return
     */
    public static char CalculateCRC(byte[] data, int offset, int length) {
        char crc = '\uffff';
        if (offset + length >= data.length) {
            return '\u0000';
        } else {
            for (int i = 0; i < length; ++i) {
                crc = (char) (crc ^ data[i + offset] << 8);

                for (int j = 0; j < 8; ++j) {
                    if ((crc & '耀') > 0) {
                        crc = (char) (crc << 1 ^ '者');
                    } else {
                        crc = (char) (crc << 1);
                    }
                }
            }

            return crc;
        }
    }

    public static char getChar(byte bytes) {
        Charset cs = Charset.forName("GBK");
        ByteBuffer bb = ByteBuffer.allocate(1);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);

        char[] tmp = cb.array();

        return tmp[0];
    }

    public static String hexString2binaryString(String bString) {

        bString = bString.replace(" ", "");//去掉直接从word表格内复制出来的空格
        bString = bString.replace(" ", "");//去掉英文空格
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {

            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));

        }
        return tmp.toString();
    }

    /**
     * 获取设备的MAC地址，支持Android 5.0以下版本
     *
     * @return
     */
    public static String getMacUUID() {
        String mac = null;
        try {
            String path = "sys/class/net/eth0/address";
            FileInputStream fis_name = new FileInputStream(path);
            byte[] buffer_name = new byte[8192];
            int byteCount_name = fis_name.read(buffer_name);
            if (byteCount_name > 0) {
                mac = new String(buffer_name, 0, byteCount_name, "utf-8");
            }


            if (mac == null) {
                fis_name.close();
                return "";
            }
            fis_name.close();
        } catch (Exception io) {
            String path = "sys/class/net/wlan0/address";
            FileInputStream fis_name;
            try {
                fis_name = new FileInputStream(path);
                byte[] buffer_name = new byte[8192];
                int byteCount_name = fis_name.read(buffer_name);
                if (byteCount_name > 0) {
                    mac = new String(buffer_name, 0, byteCount_name, "utf-8");
                }
                fis_name.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //针对android7.0获取mac
        if (mac == null) {
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iF = interfaces.nextElement();
                    byte[] addr = iF.getHardwareAddress();
                    if (addr == null || addr.length == 0) {
                        continue;
                    }
                    StringBuilder buf = new StringBuilder();
                    for (byte b : addr) {
                        buf.append(String.format("%02X:", b));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    String mac7 = buf.toString();
                    if (iF.getName().equalsIgnoreCase("wlan0")) {
                        mac = mac7;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mac == null) {
            return "";
        } else {
            return mac.trim();
        }
    }
}
