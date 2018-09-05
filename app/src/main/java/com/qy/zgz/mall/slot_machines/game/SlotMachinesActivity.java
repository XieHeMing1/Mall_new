package com.qy.zgz.mall.slot_machines.game;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonObject;
import com.google.zxing.WriterException;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.Model.Prize;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.base.BaseRxActivity;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.utils.DownFileUtil;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HandlerUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.QRBitmapUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.UnityDialog;
import com.qy.zgz.mall.utils.Utils;
import com.qy.zgz.mall.vbar.VbarUtils;
import com.qy.zgz.mall.widget.NumberView;
import com.qy.zgz.mall.widget.SlotMachinView;
import com.qy.zgz.mall.widget.TisDialog;
import com.qy.zgz.mall.widget.TisGameExitDialog;
import com.qy.zgz.mall.widget.TisGameMsgDialog;
import com.qy.zgz.mall.widget.TisGameQRUrlDialog;
import com.qy.zgz.mall.widget.TisGameSuccessDialog;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 老虎机
 */
public class SlotMachinesActivity extends BaseRxActivity {
    @BindView(R.id.iv_rotate)
    ImageView ivRotate;
    @BindView(R.id.iv_background)
    SimpleDraweeView ivBackground;

    @BindView(R.id.iv_slot_top)
    SimpleDraweeView ivSlotTop;

    @BindView(R.id.rl_award)
    AutoRelativeLayout rlAeard;
    @BindView(R.id.iv_circle)
    SimpleDraweeView ivCircle;
    @BindView(R.id.iv_award)
    SimpleDraweeView ivAward;

    @BindView(R.id.ll_light)
    AutoLinearLayout llLight;
    @BindView(R.id.iv_qcode)
    SimpleDraweeView ivQcode;

    @BindView(R.id.rl_game)
    RelativeLayout rlGame;
    @BindView(R.id.rl_light)
    RelativeLayout rlLight;

    //游戏币
    @BindView(R.id.nv_num1)
    NumberView nvNum1;
    @BindView(R.id.nv_num2)
    NumberView nvNum2;

    @BindView(R.id.iv_an_award)
    SimpleDraweeView ivAnAward;
    @BindView(R.id.tv_award)
    TextView tvAward;

    @BindView(R.id.iv_start)
    ImageView iv_start;

    @BindView(R.id.iv_exit)
    ImageView iv_exit;

    @BindView(R.id.iv_game_myaward)
    ImageView iv_game_myaward;

    //是否正在转
    private boolean isRoteing=false;

    //消耗的票数提示
    private String needTickets="200";

    //检查微信登录handle
    private Handler wx_handle=new Handler();

    //奖品数据标识
    private final String jpdataTag="GAMEJP";

    //下载handle
    private Handler download_handle;

    //商品
    SlotMachinView[] slotMachinViews = new SlotMachinView[20];
    private List<Prize> prizeList;

    MediaPlayer mediaPlayer;         //音乐播放
    private HandlerUtil handlerUtil; //网络任务定时器
    private HandlerUtil rotateHandler; //转动定时器
    private HandlerUtil getGameMoneyHandler;//定时获取游戏币

    //最后10个前的时间间隔
    private int rotateSecond = 0;

    int[] addx = new int[20];
    int[] addy = new int[20];         //用于转动的位置判断
    private int index = 0;           //现在转中的下标
    private int num = 0;             //现在转到多少次
    private int nums = 80;           //总共需要转多少次

    //定时器有偏差，通过记录开始时间与打到最后10个的时间，来减少误差
    long startSecond = 0;
    int jiange;

    //扫码登录
    @BindView(R.id.iv_login_qcode)
    SimpleDraweeView ivLoginQcode;
    @BindView(R.id.ll_login_qcode)
    AutoLinearLayout llLoginQcode;
//    private LoginQcode loginQcode;
    private HandlerUtil getLoginCodeHandler;//获取扫码登录的二维码
    private HandlerUtil checkLoginHandler; //扫码登录定时器
//    private QcodeLoginUser qcodeLoginUser;
//    private Award award;
    private int money = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_slot_machines;
    }

    @Override
    protected void initView() {
        prizeList=new ArrayList<>();
        closeExit();
//        restartExitRun();
        ivBackground.setImageURI(Uri.parse("res://" + getPackageName() + "/" + R.drawable.bg_slot_machines));

        int height = AutoUtils.getPercentHeightSize(4049) - AutoUtils.getPercentWidthSize(4049) + AutoUtils.getPercentWidthSize(720);
        AutoRelativeLayout.LayoutParams layoutParams = new AutoRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        ivSlotTop.setLayoutParams(layoutParams);
        ivSlotTop.setImageURI(Uri.parse("res://" + getPackageName() + "/" + R.drawable.bg_slot_top));


        for (int i = 0; i < slotMachinViews.length; i++) {
            int view_id = getResources().getIdentifier("img" + (i + 1), "id", getPackageName());
            slotMachinViews[i] = findViewById(view_id);
            slotMachinViews[i].setEnabled(false);
            if (i < 6) {
                addx[i] = AutoUtils.getPercentWidthSize(250);
                addy[i] = AutoUtils.getPercentWidthSize(1050);
            } else if (i >= 6 && i <= 9) {
                addy[i] = AutoUtils.getPercentWidthSize(295 + 1050);
            } else if (i >= 10 && i <= 15) {
                addx[i] = AutoUtils.getPercentWidthSize(250);
                addy[i] = AutoUtils.getPercentWidthSize(1480 + 1050);
            } else if (i >= 16) {
                addx[i] = AutoUtils.getPercentWidthSize(250);
                addy[i] = AutoUtils.getPercentWidthSize(295 + 1050);
            }
        }



        mediaPlayer = MediaPlayer.create(SlotMachinesActivity.this, R.raw.vi_rotate);
        mediaPlayer.setLooping(true);

//        Glide.with(this).load(R.drawable.bg_iv_game_exit)
//                .asGif()
//                .into(iv_exit);

        ivBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
//                    Log.e("touch","gg");
                    if (!isCancelExit){
                        cancelExit();
                        startExit(45000);
                    }
                }
                return false;
            }
        });

        /**
         * 获取老虎机奖品
         */
        showProgressDialog("请稍等...");
        handlerUtil = new HandlerUtil(new HandlerUtil.NextListener() {
            @Override
            public void next(int number) {
                HashMap<String ,String> hashMap=new HashMap<>();
                hashMap.put("shop_id", SharePerferenceUtil.getInstance().getValue("typeId","").toString());
                NetworkRequest.getInstance().shopLotteryTicket(hashMap,new NetworkCallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject data) {
                        handlerUtil = null;
                        if (data==null){
                            return;
                        }

                        //缓存奖品数据
                        SharePerferenceUtil.getInstance().setValue(jpdataTag,data.toString());

                        showPrizeDataInfo(data);

                       List<Prize> prizeL= GsonUtil.Companion.jsonToList(data.getAsJsonArray("list").toString(),Prize.class) ;
                        ArrayList<String> imgList=new ArrayList<>();

                        for (Prize p:prizeL){
                            imgList.add(p.getImg());
                        }

                        //下载图片
                        HandlerThread handlerThread = new HandlerThread("DownLoadThread");
                        handlerThread.start();
                        download_handle=new Handler(handlerThread.getLooper());
                        download_handle.post(new Runnable() {
                            @Override
                            public void run() {
                                DownFileUtil downFileUtil = new DownFileUtil(imgList);
                                downFileUtil.startDown();
                            }
                        });

                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        if (number>1){
                            try{
                                showPrizeDataInfo(GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(jpdataTag,"").toString(),JsonObject.class));
                            }catch (Exception e){

                            }
                        }else{
                            handlerUtil.startNextInSecond(5000);
                        }

                    }

                    @Override
                    public void onNetWorkFailure(Exception e) {
                        if (number>1){
                            try{
                                showPrizeDataInfo(GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(jpdataTag,"").toString(),JsonObject.class));
                            }catch (Exception n){

                            }
                        }else{
                            handlerUtil.startNextInSecond(5000);
                        }
                        super.onNetWorkFailure(e);
                    }

                    @Override
                    public void onCompleted() {
                        dismissProgressDialog();
                        super.onCompleted();
                    }
                });
            }

        });
        handlerUtil.start();



        //定时退出
//        startExit(45000);
    }


    @OnClick({R.id.iv_game_myaward,R.id.iv_start, R.id.arl_back, R.id.iv_background, R.id.iv_award, R.id.iv_qcode, R.id.iv_exit,R.id.rl_award})
    public void onClick(View v) {

        if(Utils.isFastClick(1000)){
            return;
        }

        if (isRoteing){
            iv_start.setEnabled(false);
            return;
        }
        switch (v.getId()) {
            case R.id.iv_game_myaward:{
                cancelExit();
                new TisGameQRUrlDialog(this).create()
                        .setHandEventAfterDismiss(new TisGameQRUrlDialog.HandEventAfterDismiss() {
                            @Override
                            public void handEvent() {
                                startExit(45000);
                            }
                        })
                .show();
                break;
            }
            case R.id.iv_start:
//                if (qcodeLoginUser == null) {
//                    showHint("请先扫描二维码登录!");
//                    return;
//                }

//                if (rlAeard.getVisibility()!=View.GONE){
//                    return;
//                }

                if (prizeList == null || prizeList.size() == 0) {
                    showHint("正在获取商品列表");
                    return;
                }

                if (!isAbleStart()||Double.parseDouble(needTickets)<=0){
                    return;
                }

                isRoteing=true;
                iv_start.setEnabled(false);
                cancelExit();
                startGameSet();


                break;
            case R.id.arl_back:
            case R.id.iv_exit:
                if (isRoteing) {
                    return;
                }
                cancelExit();
                new TisGameExitDialog(this).create()
                        .setNegativeButtonListener(new TisGameExitDialog.NegativeButtonListener() {
                            @Override
                            public void onClick(View v) {

//                                if(ivBackground!=null)
//                                {
//                                    ivBackground.setVisibility(View.GONE);
//                                    ivBackground = null;
//                                }
                                SlotMachinesActivity.this.finish();
                                // 定义出入场动画
                                overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right);
//                                SlotMachinesActivity.this.startActivity(new Intent(SlotMachinesActivity.this, MallActivity.class));
                            }
                        })
                        .setPositiveButtonListener(new TisGameExitDialog.PositiveButtonListener() {
                            @Override
                            public void onClick(View v) {
                                //清除会员登录信息
                                SharePerferenceUtil.getInstance()
                                        .setValue(Constance.member_Info,"");
                                //清除商城会员登录accessToken
                                SharePerferenceUtil.getInstance()
                                        .setValue(Constance.user_accessToken,"");
                                //清除商城会员登录shop_id
                                SharePerferenceUtil.getInstance()
                                        .setValue(Constance.shop_id,"");

//                                if(ivBackground!=null)
//                                {
//                                    ivBackground.setVisibility(View.GONE);
//                                    ivBackground = null;
//                                }
                                SlotMachinesActivity.this.finish();
                                // 定义出入场动画
                                overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right);
//                                SlotMachinesActivity.this.startActivity(new Intent(SlotMachinesActivity.this, MallActivity.class));
                            }
                        })
                        .setCancelClickListener(new TisGameExitDialog.CancelClickListener() {
                            @Override
                            public void handEvent() {
                                startExit(45000);
                            }
                        })
                        .show();

//                if (money > 0) {
//                    exitHint();
//                    return;
//                }
                break;
            case R.id.iv_background:
            case R.id.rl_award:
                if (rlAeard.getVisibility() == View.VISIBLE) {
                    rlAeard.setVisibility(View.GONE);
//                    releasePlayer();
                }
                break;
        }

    }

    /**
     * 二维码登录
     */
    private void codeLogin() {
        getLoginCodeHandler = new HandlerUtil(new HandlerUtil.NextListener() {
            @Override
            public void next(int number) {

            }
        });
        getLoginCodeHandler.start();


        checkLoginHandler = new HandlerUtil(new HandlerUtil.NextListener() {
            @Override
            public void next(int number) {

            }
        });
    }

    /**
     * 获取游戏币
     */
    private void getGameMoney() {
        getGameMoneyHandler = new HandlerUtil(new HandlerUtil.NextListener() {
            @Override
            public void next(int number) {

            }
        });
        getGameMoneyHandler.start();
    }


    /**
     * 开始游戏
     */
    private void startGame(ArrayList<Integer> sel,boolean isAward,String curl,String selImgUrl) {

        money = 0;
        Random random=new Random();

        mediaPlayer.start();
        //选中奖品
//        int selectNum = selectIndex - index;

        int selectNum = sel.get(random.nextInt(sel.size()))- index;

        if (selectNum < 0) {
            selectNum = 20 + selectNum;
        }
        num = 0;
        nums = 80 + selectNum;
        rotateSecond = 2900 / (nums - 10);
        startSecond = new Date().getTime();
        System.out.println("开始前:" + startSecond+":"+rotateSecond);
        startRotate(isAward,curl,selImgUrl);
//        showProgressDialog("请稍等...");
//        NetworkRequest.getInstance().awardResult(loginQcode.getLoginid(), new NetworkCallback<Award>() {
//            @Override
//            public void onSuccess(Award data) {
//                dismissProgressDialog();
//                award = data;
//                if(data.getId()<=0){
//                    showHint("网络异常，请重新开始");
//                    return;
//                }
//                nvNum1.showNumber(data.getPlaytimeed());
//                nvNum2.showNumber(data.getPlaytimes());
//                money = data.getPlaytimes();
//                int selectIndex = -1;
//                for (int i = 0; i < prizeList.size(); i++) {
//                    if (prizeList.get(i).getId() == data.getId()) {
//                        selectIndex = i;
//                    }
//                }
//
//                if(selectIndex==-1){
//                    showHint("网络异常，请重新开始");
//                    return;
//                }
//                mediaPlayer = MediaPlayer.create(SlotMachinesActivity.this, R.raw.vi_rotate);
//                mediaPlayer.setLooping(true);
//                mediaPlayer.start();
//                int selectNum = selectIndex - index;
//                if (selectNum < 0) {
//                    selectNum = 20 + selectNum;
//                }
//                num = 0;
//                nums = 80 + selectNum;
//                rotateSecond = 2900 / (nums - 10);
//                startSecond = new Date().getTime();
//                System.out.println("开始前:" + startSecond);
//                startRotate();
//            }
//
//            @Override
//            public void onFailure(int code, String msg) {
//                dismissProgressDialog();
//                showHint(msg);
//            }
//        });

    }

    /**
     * 开始转动
     */
    private void startRotate(final boolean isAward,String curl,String selImgUrl) {
            rotateHandler = new HandlerUtil(new HandlerUtil.NextListener() {
                @Override
                public void next(int number) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivRotate.getLayoutParams();
                    int x = (int) (slotMachinViews[index].getX() + addx[index]);
                    int y = (int) (slotMachinViews[index].getY() + addy[index]);
                    layoutParams.setMargins(x, y, 0, 0);
                    ivRotate.setLayoutParams(layoutParams);
                    if (ivRotate.getVisibility() == View.GONE) {
                        ivRotate.setVisibility(View.VISIBLE);
                    }
                    num++;

                    if (nums - num == 9) {
                        long second = new Date().getTime();
                        jiange = (int) ((4500 - (second - startSecond) - 360 - 252) / 7);
                    }
                    if (num <= nums) {
                        if (nums - num <= 9) {
                            rotateHandler.startNextInSecond(jiange);
                        } else if (nums - num <= 2) {
                            rotateHandler.startNextInSecond(180);
                        } else if (nums - num <= 0) {
                            rotateHandler.startNextInSecond(252);
                        } else {
                            rotateHandler.startNextInSecond(50);
                        }
                        index++;
                        if (index >= slotMachinViews.length) {
                            index = 0;
                        }
                    } else {

                        //显示中奖结果
                        if (isAward){
                            new TisGameSuccessDialog(SlotMachinesActivity.this)
                                    .create()
                                    .setQrURL(curl)
                                    .setAwardImg(selImgUrl)
                                    .setNegativeButtonListener(new TisGameSuccessDialog.NegativeButtonListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    })
                                    .setPositiveButtonListener(new TisGameSuccessDialog.PositiveButtonListener() {
                                        @Override
                                        public void onClick(View v) {
                                            isRoteing=false;
                                            iv_start.setEnabled(true);
                                            iv_start.callOnClick();
                                        }
                                    })
                                    .setHandEventAfterDismiss(new TisGameSuccessDialog.HandEventAfterDismiss() {
                                        @Override
                                        public void handEvent() {
                                            isRoteing=false;
                                            iv_start.setEnabled(true);
                                            mediaPlayer.pause();
                                            startExit(45000);
                                        }
                                    })
                                    .show();


                        }else{

                            new TisGameMsgDialog(SlotMachinesActivity.this)
                                    .create()
                                    .setImageBg(R.drawable.bg_game_unprize)
                                    .setHandEventAfterDismiss(new TisGameMsgDialog.HandEventAfterDismiss() {
                                        @Override
                                        public void handEvent() {
                                            isRoteing=false;
                                            iv_start.setEnabled(true);
                                            mediaPlayer.pause();
                                            startExit(45000);
                                        }
                                    })
                                    .show();

                        }

//                        handler.postDelayed(awardRunnable, 400);
                    }
                    System.out.println("进行中:" + num + "," + nums + "," + index);
                }
            });
            rotateHandler.start();
    }

    /**
     * 出现奖品
     */
    private HandlerUtil receiveAwardHandler;
    private Runnable awardRunnable = new Runnable() {
        @Override
        public void run() {
            rlAeard.setVisibility(View.VISIBLE);
            ivAward.setImageURI(Uri.parse("res://" + getPackageName() + "/" + R.drawable.ic_award));
            AnimationDrawable frameAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.an_slot_circle);
            ivCircle.setBackground(frameAnim);
//            DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
//                    .setAutoPlayAnimations(true)
//                    //加载drawable里的一张gif图
//                    .setUri(Uri.parse("res://" + getPackageName() + "/" + R.drawable.gf_award))//设置uri
//                    .build();
            //设置Controller
//            ivAnAward.setController(mDraweeController);
            frameAnim.start();

//            ivQcode.setImageURI(Uri.parse(award.getQcode()));
            try {
                ivQcode.setImageBitmap(QRBitmapUtils.createQRCode("领取成功!",100));
            } catch (WriterException e) {
                e.printStackTrace();
            }
            tvAward.setText(prizeList.get(index).getBonus_desc());
//            String loginid = loginQcode.getLoginid();
            receiveAwardHandler = new HandlerUtil(new HandlerUtil.NextListener() {
                @Override
                public void next(int number) {
                }
            });
            receiveAwardHandler.start();
//            restartExitRun();
            if (money == 0) {

//                codeLogin();
//                qcodeLoginUser = null;
//                loginQcode = null;
            }
        }
    };

    @Override
    protected void onDestroy() {
        releasePlayer();
        cancelExit();
        wx_handle.removeCallbacksAndMessages(null);
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (isRoteing) {
            return;
        }
        cancelExit();
        new TisGameExitDialog(this).create()
                .setNegativeButtonListener(new TisGameExitDialog.NegativeButtonListener() {
                    @Override
                    public void onClick(View v) {
                        SlotMachinesActivity.this.finish();
                        // 定义出入场动画
                        overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right);
                    }
                })
                .setPositiveButtonListener(new TisGameExitDialog.PositiveButtonListener() {
                    @Override
                    public void onClick(View v) {
                        //清除会员登录信息
                        SharePerferenceUtil.getInstance().setValue(Constance.member_Info,"");
                        //清除商城会员登录accessToken
                        SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken,"");
                        //清除商城会员登录shop_id
                        SharePerferenceUtil.getInstance().setValue(Constance.shop_id,"");

                        SlotMachinesActivity.this.finish();
                        // 定义出入场动画
                        overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right);
                    }
                })
                .setCancelClickListener(new TisGameExitDialog.CancelClickListener() {
                    @Override
                    public void handEvent() {
                        startExit(45000);
                    }
                })
                .show();
//        super.onBackPressed();
    }

    private Runnable exitRun = new Runnable() {
        @Override
        public void run() {
//            SlotMachinesActivity.this.finish();
        }
    };

    private void restartExitRun() {
        handler.removeCallbacks(exitRun);
        if (money > 0) {
            handler.postDelayed(exitRun, 6000000);
        } else {
            handler.postDelayed(exitRun, 180000);
        }
//        else if (qcodeLoginUser != null) {
//            handler.postDelayed(exitRun, 300000);
//        }
    }

    private void exitHint() {
        new UnityDialog(this)
                .setTitle("提示")
                .setHint("游戏进行中，是否退出，退出币清零")
                .setCancel("取消", null)
                .setConfirm("确定", new UnityDialog.OnConfirmDialogListener() {
                    @Override
                    public void confirm(UnityDialog unityDialog, String content) {
                        SlotMachinesActivity.this.finish();
                    }
                }).show();
    }

    private void showHint(String msg) {
        if(ivRotate==null)
        {
            return;
        }
        new UnityDialog(this)
                .setTitle("提示")
                .setHint(msg)
                .setCancel("确定", null).show();
    }

    /**
     * 是否可以开始游戏
     */
    private boolean isAbleStart(){
        MemberInfo memberInfo=GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info,"").toString(),MemberInfo.class);

        if (null==memberInfo){
            new TisDialog(this).create().setMessage("请登录").show();
            return false;
        }
        else if(TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.user_accessToken,"").toString())){
            new TisDialog(this).create().setMessage("请关注公众号绑卡或到前台添加手机号码,再重新登录!").show();
            return false;
        }
        else{
            return true;
        }

    }

    /**
     * 开始游戏
     */
    private  void startGameSet(){
        //请求接口获取奖品和扣去费用
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("shop_id", SharePerferenceUtil.getInstance().getValue("typeId","").toString());
        hashMap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString());
        showProgressDialog("请稍后");
        NetworkRequest.getInstance().lotteryPaytheLottery(hashMap, new NetworkCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject data) {
               if (null==data){
                   isRoteing=false;
                   iv_start.setEnabled(true);
                   startExit(45000);
                   return;
               }
                isRoteing=true;
                try{
                    nvNum1.showNumber(nvNum1.getNumber()-Integer.parseInt(needTickets));
                }catch (Exception e){

                }
                String curl=data.get("curl").getAsString();
                String type=data.get("status").getAsString().trim();
                int sel=data.get("rules_id").getAsInt();
                ArrayList<Integer> selList=new ArrayList<>();
                String selImgUrl="";
                for (int i=0;i<prizeList.size();i++){
                    if (prizeList.get(i).getId()==sel){
                        selList.clear();
                        selImgUrl=prizeList.get(i).getImg();
                        try {
                            List<String> seatList=prizeList.get(i).getSeatList();
                            for (String seat:seatList){
                                selList.add(Integer.parseInt(seat));
                            }

                        }catch (Exception e){

                        }

//                        int num=slotMachinViews.length/prizeList.size();
//                        if (num<1){
//                            num=1;
//                        }
//                        for (int j=0;j<num;j++){
//                            selList.add(i+prizeList.size()*j);
//                        }
                        break;
                    }
                }

                if (selList.size()>0){
                    if (type.equals("not")){
                        startGame(selList,false,curl,selImgUrl);
                    }else{
                        startGame(selList,true,curl,selImgUrl);

                    }

                }else{
                    isRoteing=false;
                    iv_start.setEnabled(true);
                    startExit(45000);
                }


            }

            @Override
            public void onFailure(int code, String msg) {
                isRoteing=false;
                iv_start.setEnabled(true);
                startExit(45000);
                if (msg.contains("彩票不足")){
                new TisGameMsgDialog(SlotMachinesActivity.this)
                        .create()
                        .setImageBg(R.drawable.bg_game_ticket_whitout)
                        .setHandEventAfterDismiss(new TisGameMsgDialog.HandEventAfterDismiss() {
                            @Override
                            public void handEvent() {
                            }
                        }).show();
                }else{
                    new TisDialog(SlotMachinesActivity.this)
                            .create().setMessage("扣票失败").show();
                }

            }

            @Override
            public void onNetWorkFailure(Exception e) {
                isRoteing=false;
                iv_start.setEnabled(true);
                startExit(45000);
                super.onNetWorkFailure(e);
            }

            @Override
            public void onCompleted() {
                dismissProgressDialog();
                super.onCompleted();
            }
        });
    }



    /**
     * 释放播放器资源
     */
    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    private  Runnable exitGameRunable=new Runnable() {
        @Override
        public void run() {
            //清除会员登录信息
            SharePerferenceUtil.getInstance()
                    .setValue(Constance.member_Info,"");
            //清除商城会员登录accessToken
            SharePerferenceUtil.getInstance()
                    .setValue(Constance.user_accessToken,"");
            //清除商城会员登录shop_id
            SharePerferenceUtil.getInstance()
                    .setValue(Constance.shop_id,"");

//            if(ivBackground!=null)
//            {
//                ivBackground.setVisibility(View.GONE);
//                ivBackground = null;
//            }
            SlotMachinesActivity.this.finish();
            // 定义出入场动画
            overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right);


        }
    };

    private boolean isCancelExit=false;
    private void cancelExit(){
        isCancelExit=true;
        handler.removeCallbacks(exitGameRunable);
    }

    private void startExit(long time){
        isCancelExit=false;
        handler.postDelayed(exitGameRunable,time);
    }

    /**
     * 开启登录识别扫描器
     */
    private void startLoginRecognitionScan(){
        //开启扫描器识别
        VbarUtils.getInstance(this)
                .setScanResultExecListener(new VbarUtils.ScanResultExecListener(){
                    @Override
                    public void scanResultExec(String result) {
                        if (!TextUtils.isEmpty(result)
                                && TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString())){
                            scanCardLogin(result);
                        }
                    }

                }).getScanResult();
    }


    /**
     * 会员扫卡登录
     */
    public void scanCardLogin(String scan_result){
        HashMap<String,String> hashmap=new HashMap<String,String>();
        hashmap.put("CardSN",scan_result);
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        KProgressHUD dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show();
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.GetMemberInfoByCardNo,hashmap,new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonResult= GsonUtil.Companion.jsonToObject(result,JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200")){
                    JsonObject data=jsonResult.getAsJsonObject("Data");

                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info,data.toString());

                    String Wid=data.get("WechatID").getAsString();
                    String Bid=SharePerferenceUtil.getInstance().getValue(Constance.BranchID,"").toString();
                    String Vpn=SharePerferenceUtil.getInstance().getValue(Constance.Vpn,"").toString();

                    //显示登录信息

                    isLogining();

                    //登录商城
                    if (!TextUtils.isEmpty(Bid)
                           &&!TextUtils.isEmpty(Vpn)){
                        //执行商城会员登录
                        userLogin(Wid,Bid,Vpn);
                    }

                    new TisDialog(SlotMachinesActivity.this)
                            .create().setMessage("登录成功,请点击PLAY按钮进行抽奖").show();

                }else{
                    new TisDialog(SlotMachinesActivity.this)
                            .create().setMessage("登录失败").show();
                    startLoginRecognitionScan();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                new TisDialog(SlotMachinesActivity.this)
                        .create().setMessage("登录失败").show();
                startLoginRecognitionScan();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (dia!=null&&dia.isShowing()){
                    dia.dismiss();
                }
            }


        });

    }

    /**
     * 商城会员登录接口
     */
    private void  userLogin( String wxopen_id,String branch_id,String vpn){
        String MacineId=SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString();
        String Bname=SharePerferenceUtil.getInstance().getValue(Constance.BranchName,"").toString();
        if (TextUtils.isEmpty(MacineId)||TextUtils.isEmpty(Bname)){
            return;
        }
        HashMap<String,String> map=new HashMap();
        map.put("open_id",wxopen_id);
        map.put("branch_id",branch_id);
        map.put("child_url",vpn);
        map.put("deviceid",MacineId);
        map.put("branch_name",Bname);
        MemberInfo memberInfo=GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info,"").toString(),MemberInfo.class);

        if (null!=memberInfo){
            map.put("cust_id",memberInfo.getId());
            map.put("mobile",memberInfo.getPhone());
        }else{
            map.put("cust_id","");
            map.put("mobile","");
        }
        NetworkRequest.getInstance().userLogin(map,new NetworkCallback<JsonObject>(){

            @Override
            public void onSuccess(JsonObject data) {
                SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken,data.get("accessToken").getAsString());
                SharePerferenceUtil.getInstance().setValue(Constance.shop_id,data.get("shop_id").getAsString());

            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    /**
     * 生成授权微信登陆二维码
     */
    private void CreateScanCode(String MachineID){
        if (TextUtils.isEmpty(MachineID)){
            return;
        }
        HashMap<String,String> hashmap=new HashMap<String,String>();
        hashmap.put("MachineID",MachineID);
        hashmap.put("MenuName","扫码登录");
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson( Constance.MEMBER_HOST+Constance.CreateScanCode,hashmap,new XutilsCallback<String>()
        {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonResult=GsonUtil.Companion.jsonToObject(result,JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200")){

                    //登录二维码
                    try {
                        ivLoginQcode.setImageBitmap(QRBitmapUtils.createQRCode(jsonResult.get("Data").getAsString(),300));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    String TmpGuid=jsonResult.get("Data2").getAsString();
                    wx_handle.removeCallbacksAndMessages(null);
                    //开始循环接口，查看是否登录
                    wx_handle.post(new Runnable(){
                        @Override
                        public void run() {
                            if (TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString())){
                                authorizedLogin(TmpGuid);
                            }
                        }

                    });

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * 微信授权扫码登录
     */
    private void authorizedLogin(String TmpGuid){
        if (!TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString())){
            return;
        }
        HashMap<String,String> hashmap=new HashMap<String,String>();
        hashmap.put("TempGuid",TmpGuid);
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.GetCustomerScanData,hashmap,new XutilsCallback<String>(){
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonResult=GsonUtil.Companion.jsonToObject(result,JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200")){

                    JsonObject data=jsonResult.getAsJsonObject("Data");
                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info,data.toString());

                    String Wid=data.get("WechatID").getAsString();
                    String Bid=SharePerferenceUtil.getInstance().getValue(Constance.BranchID,"").toString();
                    String Vpn=SharePerferenceUtil.getInstance().getValue(Constance.Vpn,"").toString();

                    //显示登录信息
                    isLogining();

                    //登录商城
//                    userLogin(Wid,Bid,Vpn);
                    if (!TextUtils.isEmpty(Bid)
                            &&!TextUtils.isEmpty(Vpn)){
                        //执行商城会员登录
                        userLogin(Wid,Bid,Vpn);
                    }

                    new TisDialog(SlotMachinesActivity.this)
                            .create().setMessage("登录成功").show();

                }else{
                    wx_handle.postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            authorizedLogin(TmpGuid);
                        }

                    },2500);

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                wx_handle.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        authorizedLogin(TmpGuid);
                    }

                },2500);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //显示或隐藏登录(登录了机台信息)
    private void isLogining(){
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info,"").toString())){

            llLoginQcode.setBackgroundResource(R.drawable.bg_iv_slot_unlogin);
            ivLoginQcode.setVisibility(View.VISIBLE);

            //开启登录识别扫描器
            startLoginRecognitionScan();
            //创建新的微信授权二维码
            CreateScanCode(SharePerferenceUtil.getInstance()
                    .getValue(Constance.MachineID,"").toString());
        }else{

            llLoginQcode.setBackgroundResource(R.drawable.bg_iv_slot_login);
            ivLoginQcode.setVisibility(View.GONE);

            try{
                MemberInfo memberInfo=GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance()
                        .getValue(Constance.member_Info,"").toString(),MemberInfo.class);

                nvNum1.showNumber((int)Double.parseDouble(memberInfo.getTickets()));

            }catch (Exception e){

            }

            if (!isCancelExit){
                cancelExit();
                startExit(45000);
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        wx_handle.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cancelExit();
        startExit(45000);

        isLogining();
    }

    //显示抽奖商品信息
    private void showPrizeDataInfo(JsonObject data){
        if (data==null){
            return;
        }
        try{
            //每次消耗票数
            needTickets=data.get("tickets").getAsString();
            try{
                nvNum2.showNumber(Integer.parseInt(needTickets));
            }catch (Exception e){

            }


            prizeList= GsonUtil.Companion.jsonToList(data.getAsJsonArray("list").toString(),Prize.class) ;

            for (Prize p:prizeList){
                List<String> plist= Arrays.asList(p.getSeat().split(","));
                p.setSeatList(plist);
                for (String seat:plist){
                    int i= Integer.parseInt(seat);
                    if (i>=0 &&i<20){
                        //判断是否已经使用了
                        if(slotMachinViews[i].isEnabled()){
                            p.getSeatList().remove(seat);
                        }else{
                            slotMachinViews[i].showData(p);
                        }

                    }else{
                        p.getSeatList().remove(seat);
                    }
                }
            }

        }catch (Exception e){
            //防止没数据显示
//            isRoteing=true;
        }
    }


}
