package com.qy.zgz.mall.network;

import android.os.Environment;

import com.qy.zgz.mall.utils.SharePerferenceUtil;

/**
 * Created by Administrator on 2017/11/21.
 */

public class Constance {
//    public final static String HOST = "http://api.9117fun.com"; //上线地址

//    public final static String CRANEMAAPI = "/cranemaapi.html";  //广告接口
//    public final static String LOGIN="cranemaapi-logins.html";   //登录接口

    public final static String VERSION="/index.php/topapi?method=version.new";//版本接口

//    public final static String HOST = "http://jkjoo.cn";
    public final static String HOST = "http://hyppmm.com";
    public final static String LOGIN = "/index.php/topapi?method=shop.login";
    public final static String CRANEMAAPI = "/index.php/topapi?method=item.app";
    //搜索
    public final static String GETSEARCH = "/index.php/topapi?method=item.search.app";

    //礼品定制
    public final static String PREDEFINEGIFT = "/index.php/topapi?method=shop.gift";

    //获取首页红包图片
    public final static String RED_PACKET = "/index.php/topapi?method=shop.red.packet";

    //商城会员登录
    public final static String USER_APPLOGIN= "/index.php/topapi?method=user.laveLogin";

    //商城会员加入购物车
    public final static String CAR_ADD= "/index.php/topapi?method=cart.add";

    //商城会员查看购物车
    public final static String CAR_GET= "/index.php/topapi?method=cart.get.Appbasic";

    //商城会员删除购物车
    public final static String CAR_DEL="/index.php/topapi?method=cart.del";

    //商城会员更新购物车
    public final static String CART_UPDATE="/index.php/topapi?method=cart.update";

    //商城会员查看需要结算的购物车
    public final static String CART_CHECKOUT="/index.php/topapi?method=cart.checkout.app";

    //商城会员查看地址
    public final static String MEMBER_ADDRESS_LIST="/index.php/topapi?method=member.address.list";

    //商城会员添加地址
    public final static String MEMBER_ADDRESS_CREATE="/index.php/topapi?method=member.address.create";

    //商城会员修改地址
    public final static String MEMBER_ADDRESS_UPDATE="/index.php/topapi?method=member.address.update";

    //商城会员删除地址
    public final static String MEMBER_ADDRESS_DELETE="/index.php/topapi?method=member.address.delete";

    //商城会员设置默认地址
    public final static String MEMBER_ADDRESS_SETDEFAULT="/index.php/topapi?method=member.address.setDefault";

    //地区JSON数据
    public final static String REGION_JSON="/index.php/topapi?method=region.json";

    //购物车结算
    public final static String CART_CARTPAY="/index.php/topapi?method=cart.cartPay";

    //购物车订单列表
    public final static String TRADE_LIST="/index.php/topapi?method=trade.list";

    //购物车订详情
    public final static String TRADE_GET="/index.php/topapi?method=trade.get";

    //商城视频轮播
    public final static String ITEM_VIDEO="/index.php/topapi?method=item.video";

    //转转盘抽奖彩票和奖品列表
    public final static String SHOP_LOTTERY_TICKET="/index.php/topapi?method=shop.lottery.ticket";

    //支付抽奖接口
    public final static String LOTTERY_PAY_THELOTTERY="/index.php/topapi?method=lottery.pay.thelottery";


    //商城视频轮播链接标识
    public static String itemVideoUrl="ITEMVIDEOURL";

    //商城会员登录accessToken
    public static String user_accessToken="USER_ACCESSTOKEN";

    //商城会员登录店铺ID
    public static String shop_id="SHOP_ID";


    //用户无操作时间
    public static long lastTouchTime=0l;

    //会员登录后的信息
    public final static String member_Info="MEMBER_INFO";

    //系统MAC物理地址
    public final static String mac_Address="MAC_ADDRESS";

    //机器场地BranchID
    public final static String BranchID="BranchID";

    //机器场地BranchName
    public final static String BranchName="BranchName";


    //机器ID
    public final static String MachineID="MachineID";

    //机器班次时间标识
    public final static String MachineClassTime="MachineClassTime";

    //机器班次ID标识
    public final static String MachineClassID="MachineClassID";

    //机器班次名字
    public final static String MachineClassNAME="MachineClassNAME";


    //机器用户ID
    public static String machineUserID="22222222-2222-2222-2222-222222222222";

    //机器散客会员ID
    public static String machineFLTUserID="00000000-0000-0000-0000-000000000000";

    //机器经理卡级别
    public static String machineMangerLevel="11111111-1111-1111-1111-111111111111";

    //机器清币记录标识
    public static String MachineClearID="MachineClearID";

    //机器清币数量
    public static String MachineClearNum="MachineClearNum";


    //机器场地VPN
    public final static String Vpn="Vpn";

    //机器场地最大出币数
    public static int maxOutCoinValue=200;

    //智能安装标识
    public static String auto_Install="AUTO_INSTALL";
    //智能安装的目录(删除下载文件时使用)
    public static String auto_Installpageage_path=Environment.getExternalStorageDirectory().getPath() + "/Download/together";


    //会员信息接口域名
    public final static String MEMBER_HOST_TAG="MEMBER_HOST_TAG";
    public static String MEMBER_HOST = SharePerferenceUtil.getInstance().getValue(MEMBER_HOST_TAG,"").toString();
//    public final static String MEMBER_HOST ="http://120.76.72.68:8085/";

    //会员信息接口域名(刷卡,扫码)
//    public final static String MEMBER_HOST ="http://192.168.6.252:29800/";

    //会员信息接口签名KEY
    public final static String member_Host_Key="zhikehuake1212";

    //获取机器班次信息
    public final static String MacGetMachineClassInfo="api/Login/GetLoginMachineInfo";
    //查询授权登陆的会员信息(微信扫码登录)
    public final static String GetCustomerScanData="api/Customer/GetQrcodeCustInfo";
    //查询会员信息(扫卡登录)
    public final static String GetMemberInfoByCardNo="api/Customer/GetCustomersSingle";
    //生成授权登陆二维码
    public final static String CreateScanCode="api/Customer/CreateLoginQrCode";

    //会员提币
    public final static String WriteGetCoin="api/Customer/WriteGetCoin";

    //会员修改密码
    public final static String UpdateCustomerPwd="api/Customer/UpdateCustomerPwd";

    //会员套餐销售
    public final static String GetPackageList="api/Package/GetPackageList";

    //会员套餐销售详情
    public final static String GetPackageSaleInfo="api/Package/GetPackageSaleInfo";

    //会员获取打印码
    public final static String GetBarCode="api/Common/GetBarCode";

    //会员生成支付二维码
    public final static String GetQrCode="api/Order/GetQrCode";

    //验证移动支付是否已付款
    public final static String CheckIsPayStatus="api/Order/CheckIsPayStatus";

    //更新数据库出币记录
    public final static String UpdateOutCoinLog="api/Common/UpdateOutCoinLog";

    //销售套餐记录
    public final static String SalePackage="api/Package/SalePackage";

    //获取一体机参数
    public final static String GetGMSSettingsInfoList="api/Query/GetGMSSettingsInfoList";

    //机器验证会员密码
    public final static String CheckCustomerPassword="api/Customer/CheckCustomerPassword";

    //机器获取会员消费记录
    public final static String GetCustomerConsume="api/Query/GetCustomerConsume";

    //机器获取交班信息
    public final static String GetHand2Check="api/ChangeClass/GetHand2Check";

    //机器获取交班清币
    public final static String WriteClearCoin="api/ChangeClass/WriteClearCoin";

    //机器交班
    public final static String Hand2Check="api/ChangeClass/Hand2Check";

    //机器移动订单同步
    public final static String OrderUpdate="api/Order/OrderUpdate";

    //机器获取异常记录
    public final static String GetAbnormityList="api/Query/GetAbnormityList";

    //重新处理出币记录
    public final static String GetNoFinishCoins="api/Common/GetNoFinishCoins";

    //移动订单重新处理
    public final static String OrderReOperate="api/Package/OrderReOperate";

    //移动订单退款
    public final static String OrderRefund="api/Order/OrderRefund";

    //更改机器登陆状态
    public final static String MachineLogout="api/Login/MachineLogout";

    //套餐自动匹配
    public final static String AutoMathPackageList="api/Package/AutoMathPackageList";

    //检查套餐是否可购买
    public final static String CheckSalePackage="api/Package/CheckSalePackage";

    //本地出币记录更新数据库记录
    public final static String UpdateServerByLocalData="api/RepairCoin/UpdateServerByLocalData";



    //-----------------------------------

    //ttyS1 纸币机，ttyS4 出币机
//    public static String[][] devices_array = {{"/dev/ttyS4","19200","0","8","1","3"},{"/dev/ttyS1","9600","0","8","1","1"}};
    public static String[][] devices_array = {{"/dev/ttyS1","9600","0","8","1","1"},{"/dev/ttyS3","9600","0","8","1","2"},{"/dev/ttyS4","19200","0","8","1","3"}};
}
