package com.qy.zgz.mall.KDSerialPort;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill.T on 2018/3/13.
 */
public class CMDUtils {

    private static final String TAG = "BaseSerialPortActivity";
    public static byte ITL_SEQ =  -128;

    public static byte ITL_7F = 127;

    //存放不完整数据集
    public static List<Byte> ItlNotCompleteCmd = new ArrayList<Byte>();
    public static List<Byte> IctNotCompleteCmd = new ArrayList<Byte>();
    public static List<Byte> BdNotCompleteCmd = new ArrayList<Byte>();

    private static List<Byte> ItlCompleteCmd = new ArrayList<>();
    private static List<Byte> IctCompleteCmd = new ArrayList<>();
    private static List<Byte> BdCompleteCmd = new ArrayList<>();

    private static List<Byte> SendedCmd = new ArrayList<>();

    // 无用命令过长时清空，一般有效命令不会超过120个
    private static final int DEL_CMD = 200;
    /**
     * 安卓版状态命令
     *
     * @return
     */
    public static byte[] command_jj(String status) {
        int i = 0;
        byte[] b = new byte[15];
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("j"));
        b[i++] = Common.HexToByte(Common.string2HexString("j"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(7), 16);
        b[i++] = Common.HexToByte(status);
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 机台同步命令
     *
     * @return
     */
    public static byte[] command_kk() {
        int i = 0;
        byte[] b = new byte[15];
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("k"));
        b[i++] = Common.HexToByte(Common.string2HexString("k"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(7), 16);
        b[i++] = 0x00;
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    public static byte[] command_aa(int qty) {
        int i = 0;
        byte[] b = new byte[15];
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("a"));
        b[i++] = Common.HexToByte(Common.string2HexString("a"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(7), 16);
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(qty), 16);
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 平板同步命令
     *
     * @param p1 纸币机1类型  0：ICT  1：ITL
     * @param p2 纸币机2类型  0：ICT  1：ITL
     * @param p3 是否支持检测读卡器  0：支持 1：不支持
     * @param p4 出币类型  0：直接出币  1：按键出币
     */
    public static byte[] command_fa(int p1, int p2, int p3, int p4) {
        int i = 0;
        byte[] b = new byte[20];
        b[i++] = Common.HexToByte(Common.string2HexString("%"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("F"));
        b[i++] = Common.HexToByte(Common.string2HexString("A"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(12), 16);
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(p1), 16);
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(p2), 16);
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(p3), 16);
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(p4), 16);
        b[i++] = 0x00;
        b[i++] = 0x00;
        b[i++] = 0x00;
        b[i++] = 0x00;
        b[i++] = 0x00;
        b[i++] = 0x00;
        b[i++] = 0x00;
        b[i++] = 0x00;
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 响应接收现金命令
     *
     * @param td      收取现金渠道
     * @param money   收到的现金数
     * @param can     是否接收
     * @param cmd_num 命令唯一码
     * @return
     */
    public static byte[] command_fc(int td, int money, int can, String cmd_num) {
        int i = 0;
        byte[] b = new byte[17];
        b[i++] = Common.HexToByte(Common.string2HexString("%"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("F"));
        b[i++] = Common.HexToByte(Common.string2HexString("C"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(9), 16);
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(td), 16);
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(money), 16);
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(can), 16);
        b[i++] = Common.HexToByte(cmd_num.substring(0, 2));
        b[i++] = Common.HexToByte(cmd_num.substring(2, 4));
        b[i++] = Common.HexToByte(cmd_num.substring(4, 6));
        b[i++] = Common.HexToByte(cmd_num.substring(6, 8));
        b[i++] = Common.HexToByte(cmd_num.substring(8, 10));
        b[i++] = Common.HexToByte(cmd_num.substring(10, 12));
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 发送出币命令
     *
     * @param coins_count 出币数
     * @return
     */
    public static byte[] command_fd(int coins_count) {
        String hex = Integer.toHexString(coins_count);
        String str = Common.addZeroForNum(hex, 4);
        String H = str.substring(0, 2);
        String L = str.substring(2, 4);
        int i = 0;
        byte[] b = new byte[16];
        b[i++] = Common.HexToByte(Common.string2HexString("%"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("F"));
        b[i++] = Common.HexToByte(Common.string2HexString("D"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(8), 16);
        b[i++] = (byte) Integer.parseInt(H, 16);
        b[i++] = (byte) Integer.parseInt(L, 16);
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;

    }

    /**
     * 兑币机握手命令
     *
     * @return
     */
    public static byte[] command_bd_ma() {
        byte[] b = new byte[9];
        int i = 0;
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("M"));
        b[i++] = Common.HexToByte(Common.string2HexString("A"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(1), 16);
        b[i++] = Common.HexToByte("00");
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 售币命令(电脑    ---   单片机)
     * 注：上位机发送此命令后5秒钟之内没有接收到：CB、CM、OF、MB等命令时应视为本次“售币故障”。
     * @param coins_count 出币个数
     * @param device ：出币器选择标志；=0 时为选择左出币器售币，=1 时为
    选择双出币器售币，=2 时为选择右出币器售币。
     * @return
     */
    public static byte[] command_qe(int coins_count,int device) {
        String hex = Integer.toHexString(coins_count);
        String str = Common.addZeroForNum(hex, 6);
        String str1 = str.substring(0, 2);
        String str2 = str.substring(2, 4);
        String str3 = str.substring(4, 6);
        byte[] b = new byte[13];
        int i = 0;
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("Q"));
        b[i++] = Common.HexToByte(Common.string2HexString("E"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(5), 16);
        b[i++] = Common.HexToByte("00");
        b[i++] = (byte) Integer.parseInt(str1, 16);
        b[i++] = (byte) Integer.parseInt(str2, 16);
        b[i++] = (byte) Integer.parseInt(str3, 16);
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(device), 16);
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 暂停出币命令(电脑      单片机)
     * 注：上位机发送此命令时单片机应返回OK命令（OK标志＝0x06）。
     *
     * @return
     */
    public static byte[] command_zt() {
        byte[] b = new byte[9];
        int i = 0;
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("Z"));
        b[i++] = Common.HexToByte(Common.string2HexString("T"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(1), 16);
        b[i++] = Common.HexToByte("00");
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 电脑发往售币机继续售币命令（电脑     单片机）
     * 注：上位机发送此命令时单片机应返回OK命令（OK标志＝0x07）。
     *
     * @return
     */
    public static byte[] command_bd_vu() {
        byte[] b = new byte[9];
        int i = 0;
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("V"));
        b[i++] = Common.HexToByte(Common.string2HexString("U"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(1), 16);
        b[i++] = Common.HexToByte("00");
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 结束本轮售币任务(电脑       单片机)
     * 注：上位机发送此命令时单片机应返回OE命令(命令中的购币数量数据为结束前本轮已售出的总币数)。
     *
     * @return
     */
    public static byte[] command_bd_jw() {
        byte[] b = new byte[9];
        int i = 0;
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("J"));
        b[i++] = Common.HexToByte(Common.string2HexString("W"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(1), 16);
        b[i++] = Common.HexToByte("00");
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 清点币机所有币(电脑       单片机)
     * 注：上位机发送此命令后5秒钟之内没有接收到：CB、CM、KX、MB等命令时应视为本次“清币故障”
     *
     * @return
     */
    public static byte[] command_pi() {
        byte[] b = new byte[9];
        int i = 0;
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("P"));
        b[i++] = Common.HexToByte(Common.string2HexString("I"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(1), 16);
        b[i++] = Common.HexToByte("00");
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 打开警报指示灯  (电脑       单片机)
     * 注：上位机发送此命令时单片机应返回OK命令（OK标志＝0x04）。
     *
     * @return
     */
    public static byte[] command_ld() {
        byte[] b = new byte[8];
        int i = 0;
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("L"));
        b[i++] = Common.HexToByte(Common.string2HexString("D"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(1), 16);
        b[i++] = Common.HexToByte(Common.string2HexString(Common.getRandomString(1)));
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 关闭警报指示灯 (电脑       单片机)
     * 注：上位机发送此命令时单片机应返回OK命令（OK标志＝0x05）。
     *
     * @return
     */
    public static byte[] command_gd() {
        byte[] b = new byte[8];
        int i = 0;
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("G"));
        b[i++] = Common.HexToByte(Common.string2HexString("D"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(1), 16);
        b[i++] = Common.HexToByte("00");
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * 清除出币器状态(电脑       单片机) 此命令支持双出币器控制板
     * 注：上位机发送此命令时单片机应返回OK命令（OK标志＝0x08）。
     *
     * @return
     */
    public static byte[] command_bd_yv() {
        byte[] b = new byte[9];
        int i = 0;
        b[i++] = Common.HexToByte(Common.string2HexString("$"));
        b[i++] = Common.HexToByte(Common.string2HexString("@"));
        b[i++] = Common.HexToByte(Common.string2HexString("Y"));
        b[i++] = Common.HexToByte(Common.string2HexString("V"));
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(1), 16);
        b[i++] = (byte) Integer.parseInt(Integer.toHexString(3), 16);
        b[i++] = (byte) Common.ArrXor(b);
        b[i++] = Common.HexToByte("0D");
        b[i++] = Common.HexToByte("0A");
        return b;
    }

    /**
     * ICT纸币器 接收纸币，初始化
     *
     * @return
     */
    public static byte[] command_ict_02() {
        return new byte[]{Common.HexToByte("02")};
    }

    /**
     *  初始化
     * @return
     */
    public static byte[] command_ict_0c(){
        return new byte[]{Common.HexToByte("0C")};
    }

    /**
     * ICT纸币器 设置纸币机可收钱
     *
     * @return
     */
    public static byte[] command_ict_3e() {
        return new byte[]{Common.HexToByte("3E")};
    }

    /**
     * ICT纸币器 设置纸币机不可收钱
     *
     * @return
     */
    public static byte[] command_ict_5e() {
        return new byte[]{Common.HexToByte("5E")};
    }

    /**
     * ICT纸币器 拒绝收钱(退回纸币)
     *
     * @return
     */
    public static byte[] command_ict_0f() {
        return new byte[]{Common.HexToByte("0F")};
    }

    public static byte[] command_itl_init() {
        byte[] b = new byte[6];
        int i = 0;
        b[i++]= ITL_7F;
        b[i++]= ITL_SEQ;
        b[i++]= Common.HexToByte("01");
        b[i++]= Common.HexToByte("01");
        char crc =  Common.CalculateCRC(b,1,i-1);
        b[i++]= (byte)crc;
        b[i++]= (byte)(crc >> 8);
        return b;
    }

    /**
     * 发送 0x11 号命令查找纸币器是否连接
     * 同步命令: 将 SEQ/Slave ID 重设为 0x80(NV9、NV0、NV200) 或 0x90 (Hopper)
     * @return
     */
    public static byte[] command_itl_is_connect(){
        byte[] b = new byte[6];
        int i = 0;
        b[i++]= ITL_7F;
        b[i++]= Common.HexToByte("80");// ITL_SEQ;
        b[i++]= Common.HexToByte("01");
        b[i++]= Common.HexToByte("11");
        char crc =  Common.CalculateCRC(b,1,i-1);
        b[i++]= (byte) crc;
        b[i++]= (byte) (crc >> 8);
        return b;
    }

    /**
     * Poll 命令
     * @return
     */
    public static  byte[] command_itl_07(){
        byte[] b = new byte[6];
        int i = 0;
        b[i++]= ITL_7F;
        b[i++]= ITL_SEQ;
        b[i++]= Common.HexToByte("01");
        b[i++]= Common.HexToByte("07");
        char crc =  Common.CalculateCRC(b,1,i-1);
        b[i++]= (byte)crc;
        b[i++]= (byte)(crc >> 8);
        return b;
    }


    /**
     *  发送 0x0A 号命令允许纸币器识别纸币（使能）:
     * @return
     */
    public  static  byte[] command_itl_0a(){
        byte[] b = new byte[6];
        int i = 0;
        b[i++]= ITL_7F;
        b[i++]= ITL_SEQ;
        b[i++]= Common.HexToByte("01");
        b[i++]= Common.HexToByte("0A");
        char crc =  Common.CalculateCRC(b,1,i-1);
        b[i++]= (byte)crc;
        b[i++]= (byte)(crc >> 8);
        return b;
    }

    /**
     *  发送--0x09禁能纸币器命令
     * @return
     */
    public  static  byte[] command_itl_09(){
        byte[] b = new byte[6];
        int i = 0;
        b[i++]= ITL_7F;
        b[i++]= ITL_SEQ;
        b[i++]= Common.HexToByte("01");
        b[i++]= Common.HexToByte("09");
        char crc =  Common.CalculateCRC(b,1,i-1);
        b[i++]= (byte)crc;
        b[i++]= (byte)(crc >> 8);
        return b;
    }

    /**
     *  发送 0x05 号命令读取纸币器通道配置情况
     * @return
     */
    public static byte[] command_itl_05(){
        byte[] b = new byte[6];
        int i = 0;
        b[i++]= ITL_7F;
        b[i++]= ITL_SEQ;
        b[i++]= Common.HexToByte("01");
        b[i++]= Common.HexToByte("05");
        char crc =  Common.CalculateCRC(b,1,i-1);
        b[i++]= (byte)crc;
        b[i++]= (byte)(crc >> 8);
        return b;
    }

    /**
     *  发送 0x02号命令设置允许识别哪几种纸币
     * @return
     */
    public static byte[] command_itl_02(String newHex){
        byte[] b = new byte[8];
        int i = 0;
        b[i++]= ITL_7F;
        b[i++]= ITL_SEQ;
        b[i++]= Common.HexToByte("03");
        b[i++]= Common.HexToByte("02");
        b[i++]=Common.HexToByte(newHex);
        b[i++]= Common.HexToByte("00");
        char crc =  Common.CalculateCRC(b,1,i-1);
        b[i++]= (byte)crc;
        b[i++]= (byte)(crc >> 8);
        return b;
    }

    /**
     * 获取ITL支付通道
     * @param bytes
     * @return
     */
    public static byte[] get_itl_pay_channel(byte[] bytes){
        byte b = bytes[15];
        byte[] bytes1 = new byte[b];
        int j = 0;
        for(int i= 16;i <= 15+b;i++){
            bytes1[j] = bytes[i];
            j++;
        }
        return  bytes1;
    }

    /**
     *  ITL 纸币拒收命令
     * @return
     */
    public static byte[] command_itl_08(){
        byte[] b = new byte[6];
        int i = 0;
        b[i++]= ITL_7F;
        b[i++]= ITL_SEQ;
        b[i++]= Common.HexToByte("01");
        b[i++]= Common.HexToByte("08");
        char crc =  Common.CalculateCRC(b,1,i-1);
        b[i++]= (byte)crc;
        b[i++]= (byte)(crc >> 8);
        return b;
    }

    /**
     * 纸币暂存指令
     */
    public static byte[] command_itl_18(){
        byte[] b = new byte[6];
        int i = 0;
        b[i++]= ITL_7F;
        b[i++]= ITL_SEQ;
        b[i++]= Common.HexToByte("01");
        b[i++]= Common.HexToByte("18");
        char crc =  Common.CalculateCRC(b,1,i-1);
        b[i++]= (byte)crc;
        b[i++]= (byte)(crc >> 8);
        return b;
    }

    /**
     *  检测是否返回完整的ITL命令
     * @return
     */
    public static boolean check_itl_cmd(){
        if(ItlNotCompleteCmd.size() > 3){
            byte[] cmds = new byte[ItlNotCompleteCmd.size()];
            int i = 0;
            for(Byte b: ItlNotCompleteCmd){
                cmds[i] = b;
                i++;
            }
            char crc = Common.CalculateCRC(cmds,1,cmds.length-3);
            if(((byte) crc) == cmds[cmds.length-2] && ((byte) (crc >> 8)) == cmds[cmds.length-1]){
                ITL_SEQ = (byte)(ITL_SEQ == (byte)-128 ? 0 : -128);
                ItlCompleteCmd.clear();
                ItlCompleteCmd.addAll(ItlNotCompleteCmd);
                ItlNotCompleteCmd.clear();
                return true;
            }
            else if(ItlNotCompleteCmd.size() > DEL_CMD){
                ItlNotCompleteCmd.clear();
            }
        }
        return false;
    }



    public static byte[] get_itl_omplete_cmd(){
        byte[] bytes = new byte[ItlCompleteCmd.size()];
        int i = 0;
        for(Byte b: ItlCompleteCmd){
            bytes[i++] = b;
        }
        return  bytes;
    }

    public static byte[] get_ict_omplete_cmd(){
        byte[] bytes = new byte[IctCompleteCmd.size()];
        int i = 0;
        for(Byte b: IctCompleteCmd){
            bytes[i++] = b;
        }
        return  bytes;
    }

    public static byte[] get_bd_omplete_cmd(){
        byte[] bytes = new byte[BdCompleteCmd.size()];
        int i = 0;
        for(Byte b: BdCompleteCmd){
            bytes[i++] = b;
        }
        return  bytes;
    }

    /**
     *   检测是否返回完整的售币机命令
     */
    public static boolean check_bd_cmd() {
        if (BdNotCompleteCmd .size() > 6) {
            String Cmd24 = Common.Byte2Hex(BdNotCompleteCmd.get(0));
            String Cmd40 = Common.Byte2Hex(BdNotCompleteCmd.get(1));
            String Cmd0D = Common.Byte2Hex(BdNotCompleteCmd.get(BdNotCompleteCmd.size() - 2));
            String Cmd0A = Common.Byte2Hex(BdNotCompleteCmd.get(BdNotCompleteCmd.size() - 1));
            if (Cmd24.equals("24") && Cmd40.equals("40") && Cmd0A.equals("0A") && Cmd0D.equals("0D") ) {
                BdCompleteCmd.clear();
                BdCompleteCmd.addAll(BdNotCompleteCmd);
                BdNotCompleteCmd.clear();
                return true;
            } else if (BdNotCompleteCmd.size() > DEL_CMD) {
                BdNotCompleteCmd.clear();
            }
        }
        return false;
    }

    public static boolean check_ict_cmd(){
        if(IctNotCompleteCmd.size() > 0){
            String Cmd80 = Common.Byte2Hex(IctNotCompleteCmd.get(0));
            String Cmd8F = IctNotCompleteCmd.size() > 1 ? Common.Byte2Hex(IctNotCompleteCmd.get(1)) : "";
            if(Cmd80.equals("80") && Cmd8F.equals("8F")){
                IctCompleteCmd.clear();
                IctCompleteCmd.addAll(IctNotCompleteCmd);
                IctNotCompleteCmd.clear();
                return true;
            }
            else if(Cmd80.equals("81") && Cmd8F.equals("3D") && IctNotCompleteCmd.size() == 4){
                IctCompleteCmd.clear();
                IctCompleteCmd.addAll(IctNotCompleteCmd);
                IctNotCompleteCmd.clear();
                return true;
            }
            else if(Cmd80.equals("10")){
                IctCompleteCmd.clear();
                IctCompleteCmd.addAll(IctNotCompleteCmd);
                IctNotCompleteCmd.clear();
                return true;
            }
            else if(Cmd80.equals("5E")){
                IctCompleteCmd.clear();
                IctCompleteCmd.addAll(IctNotCompleteCmd);
                IctNotCompleteCmd.clear();
                return true;
            }
            else if(Cmd80.equals("3E")){
                IctCompleteCmd.clear();
                IctCompleteCmd.addAll(IctNotCompleteCmd);
                IctNotCompleteCmd.clear();
                return true;
            }
            else if(Cmd80.equals("A1")){
                IctCompleteCmd.clear();
                IctCompleteCmd.addAll(IctNotCompleteCmd);
                IctNotCompleteCmd.clear();
                return true;
            }
            else if(Cmd80.equals("29")&& Cmd8F.equals("2F")){
                IctCompleteCmd.clear();
                IctCompleteCmd.addAll(IctNotCompleteCmd);
                IctNotCompleteCmd.clear();
                return true;
            }
            else if (IctNotCompleteCmd.size() > DEL_CMD) {
                IctNotCompleteCmd.clear();
            }
            Log.i(TAG,"IctCompleteCmd ===" +Cmd80+"==="+Cmd8F);
        }

        return false;
    }

    public static void add_not_complete_cmd(byte[] bytes,int kend){
        switch (kend){
            case 1 :
                if (bytes.length > 0) {
                    for (byte b : bytes) {
                        IctNotCompleteCmd.add(b);
                    }
                }
                break;
            case 2 :
                if (bytes.length > 0) {
                    for (byte b : bytes) {
                        ItlNotCompleteCmd.add(b);
                    }
                }
                break;
            case 3:
                if (bytes.length > 0) {
                    for (byte b : bytes) {
                        BdNotCompleteCmd.add(b);
                    }
                }
                break;
        }

    }

    /**
     * 获取ITL需要解析的指令
     */
    public static byte[] get_itl_cmd_analysis(byte[] bytes){
        if(bytes.length > 3){
            int i = Common.HexToInt(Common.Byte2Hex(bytes[2]));
            byte[] cmd = new byte[i];
            for(int j = 3;j <  3 +i;j++){
                cmd[j-3] = bytes[j];
            }
            return cmd;
        }
        return null;
    }

    /**
     * 纸币器是否 拒绝接收纸币执行完毕，钞票已经被拒收退出
     * @param bytes
     * @return
     */
    public  static  boolean itl_is_refuse(byte[] bytes){
        byte[] bCmd = get_itl_cmd_analysis(bytes);
        String sCmd = Common.ByteArrToHex(bCmd,false);
        return sCmd.contains("EC");
    }

    /**
     * 纸币正在拒钞中
     * @param bytes
     * @return
     */
    public static boolean itl_is_refuse_ed(byte[] bytes){
        byte[] bCmd = get_itl_cmd_analysis(bytes);
        String sCmd = Common.ByteArrToHex(bCmd,false);
        return sCmd.contains("ED");
    }

    /**
     *  判断纸币器是否处于关闭状态
     * @param bytes
     * @return
     */
    public static  boolean itl_is_disabled(byte[] bytes){
        byte[] bCmd = get_itl_cmd_analysis(bytes);
        String sCmd = Common.ByteArrToHex(bCmd,false);
        return sCmd.contains("E8");
    }

    /**
     * 判断纸币器是否收钱成功
     */
    public static boolean itl_is_get_momey_success(byte[] bytes){
        byte[] bCmd = get_itl_cmd_analysis(bytes);
        String sCmd = Common.ByteArrToHex(bCmd,false);
        return sCmd.contains("EE") || sCmd.contains("E6");
    }

    /**
     *  纸币器收钱完成
     * @param bytes
     * @return
     */
    public static boolean itl_is_get_momey_end(byte[] bytes){
        byte[] bCmd = get_itl_cmd_analysis(bytes);
        String sCmd = Common.ByteArrToHex(bCmd,false);
        return sCmd.contains("EB");
    }

    //判断是否ITL OK指令
    public  static  boolean itl_is_ok_cmd(byte[] bytes){
        if(bytes.length == 6 ? Common.Byte2Hex(bytes[3]).equals("F0") : false ){
            String cmd = Common.ByteArrToHex(bytes,false);
            return (cmd.equals("7F8001F02380") || cmd.equals("7F0001F0200A"));
        }
        return false;
    }

    /**
     *  支付通道设置
     * @param curVal
     * @return
     */
    public static String itl_get_inhibits(List<Integer> curVal,byte[] bytes){
        String cmd = null;
        if(bytes != null){
            cmd =Common.ByteArrToHex(get_itl_cmd_analysis(bytes),false);
        }
        int[] hex = new int[]{1,0,0,0,0,0,0,0};
        for(Integer i:curVal){
            if(cmd == null){
                if (i == 100) {
                    hex[7] = 1;
                } else if (i == 50) {
                    hex[6] = 1;
                } else if (i == 20) {
                    hex[5] = 1;
                } else if (i == 10) {
                    hex[4] = 1;
                } else if (i == 5) {
                    hex[3] = 1;
                } else if (i == 1) {
                    hex[2] = 1;
                }
            }
            else {
                if (i == 100 && cmd.contains("64")) {
                    hex[7] = 1;
                } else if (i == 50 && cmd.contains("32")) {
                    hex[6] = 1;
                } else if (i == 20 && cmd.contains("14")) {
                    hex[5] = 1;
                } else if (i == 10 && cmd.contains("0A")) {
                    hex[4] = 1;
                } else if (i == 5 && cmd.contains("05")) {
                    hex[3] = 1;
                } else if (i == 1 && cmd.contains("01")) {
                    hex[2] = 1;
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        for(int y:hex){
            sb.append(y);
        }
        return Common.hexString2binaryString(sb.toString());
    }

    /**
     *  获取纸币器对应通道的币值
     * @param bytes
     * @return
     */
    public static int itl_get_momey(byte[] bytes){
        int price = Common.HexToInt(Common.Byte2Hex(bytes[5]));
        int sPrice = 0;
        switch (price){
            case 1 :
                sPrice = 1;
                break;
            case 2 :
                sPrice = 5;
                break;
            case 3:
                sPrice = 10;
                break;
            case 4 :
                sPrice = 20;
                break;
            case 5 :
                sPrice = 50;
                break;
            case 6 :
                sPrice = 100;
                break;
        }
        return sPrice;
    }

    /** 币斗相关指令---------------------------**/

    /**
     *  是否币斗返回OK指令
     * @param bytes
     * @return
     */
    public static boolean bd_is_ok_cmd(byte[] bytes){
        String cmd1 = Common.Byte2Hex(bytes[0]),
                cmd2 = Common.Byte2Hex(bytes[1]),
                cmd3 = Common.Byte2Hex(bytes[2]),
                cmd4 = Common.Byte2Hex(bytes[3]);
        return (cmd1.equals("24") && cmd2.equals("40") && cmd3.equals("4F") && cmd4.equals("4B"));
    }

    /**
     * 判断是否出币中指令
     * @param bytes
     * @return
     */
    public static boolean bd_is_out_coining(byte[] bytes){
        String cmd1 = Common.Byte2Hex(bytes[0]),
                cmd2 = Common.Byte2Hex(bytes[1]),
                cmd3 = Common.Byte2Hex(bytes[2]),
                cmd4 = Common.Byte2Hex(bytes[3]);
        return (cmd1.equals("24") && cmd2.equals("40") && cmd3.equals("43") && cmd4.equals("4D"));
    }

    /**
     * 币斗存在多条指令一起发送的情况，所以需要拆分成多条
     * @param cmd
     * @return
     */
    public static List<byte[]> bd_is_cmds(byte[] cmd){
        List<byte[]> listCmds = new ArrayList<byte[]>();
        String sCmds = Common.ByteArrToHex(cmd,true).trim();
        String[] ssCmds = sCmds.split("0D 0A");
        if(ssCmds.length <= 1){
            listCmds.add(cmd);
            return listCmds;
        }
        for(String c:ssCmds) {
            if (c.length() <= 0) {
                continue;
            }
            String[] sssCmds = c.trim().split(" ");
            byte[] hex = new byte[sssCmds.length + 2];
            int i = 0;
            for (String s : sssCmds) {
                hex[i] = Common.HexToByte(s);
                i++;
            }
            hex[hex.length - 2] = Common.HexToByte("0D");
            hex[hex.length - 1] = Common.HexToByte("0A");
            listCmds.add(hex);
        }
        return listCmds;
    }

    /**
     * 本轮操作已出的币数命令,接收到此命令时表示出币电机已经停止运转
     * @return 大于0表示出币已停止
     */
    public static int bd_is_stop(byte[] bytes){
        String cmd1 = Common.Byte2Hex(bytes[0]),
                cmd2 = Common.Byte2Hex(bytes[1]),
                cmd3 = Common.Byte2Hex(bytes[2]),
                cmd4 = Common.Byte2Hex(bytes[3]);
        int i = -1;
        if(cmd1.equals("24") && cmd2.equals("40") && cmd3.equals("51") && cmd4.equals("45")){
           i = Common.HexToInt(Common.Byte2Hex(bytes[6])+Common.Byte2Hex(bytes[7])+Common.Byte2Hex(bytes[8]));
        }
        return i;
    }

    /**
     *  判断出币是否完成
     * @param bytes
     * @return
     */
    public static boolean bd_is_out_end(byte[] bytes){
        String cmd1 = Common.Byte2Hex(bytes[0]),
                cmd2 = Common.Byte2Hex(bytes[1]),
                cmd3 = Common.Byte2Hex(bytes[2]),
                cmd4 = Common.Byte2Hex(bytes[3]);
        return (cmd1.equals("24") && cmd2.equals("40") && cmd3.equals("51") && cmd4.equals("46"));
    }

    /**
     * 判断是否出故障
     * @param bytes
     * @return
     */
    public static boolean bd_is_out_error(byte[] bytes){
        String cmd1 = Common.Byte2Hex(bytes[0]),
                cmd2 = Common.Byte2Hex(bytes[1]),
                cmd3 = Common.Byte2Hex(bytes[2]),
                cmd4 = Common.Byte2Hex(bytes[3]),
                cmd5 = Common.Byte2Hex(bytes[4]),
                cmd6 = Common.Byte2Hex(bytes[5]);
        return (cmd1.equals("24") && cmd2.equals("40") && cmd3.equals("58") && cmd4.equals("57") && cmd5.equals("01")&&cmd6.equals("03"));
    }

}
