package com.qy.zgz.mall.lcb_game

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.Button
import com.example.mylock.DynamicLock.Prize.GamePrizeDialog
import com.qy.zgz.mall.Model.MemberInfo
import com.qy.zgz.mall.MyApplication
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseReadCardActivity
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.qy.zgz.mall.widget.TisDialog
import com.zhy.autolayout.utils.AutoUtils
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject


@ContentView(R.layout.activity_num_dance)
class NumDanceActivity : BaseReadCardActivity() {

    override fun initLoginView() {
    }

    override fun initUnLoginView() {
    }

    override fun onTickView(millisUntilFinished: Long) {
    }

    override fun onTickViewFinish() {
    }

    override fun showLoginQRcode(qrcode: String) {
    }

    @ViewInject(R.id.rntv_num_dance)
    lateinit var rntv: RiseNumberTextView

//    @ViewInject(R.id.snow_view)
//    lateinit var snow_view: SnowView

//    @ViewInject(R.id.tv_ready)
//    lateinit var tv_ready: TextView

    @ViewInject(R.id.wrv_prize)
    lateinit var wrv_prize: RecyclerView

    @ViewInject(R.id.btn_start)
    lateinit var btn_start: Button

    @ViewInject(R.id.btn_game_exit)
    lateinit var btn_exit: Button

    private var mainControl: MainControl? = null

    //上次选中的
    private var lastSelect = 0
    //总奖品数量
    private var sumPrizeNum = 40

    var game_prize_adapter: GamePrizeAdapter? = null;
    override fun onClick(v: View) {
        when (v!!.id) {
            R.id.btn_start -> {
//                if (isAbleStart()) {
                    if(MyApplication.getInstance().mIsConnectBox) {
                        if (rntv.isRunning) {
                            rntv.stop()
                            btn_start.text = "开始"
                        } else {
                            rntv.start()
                            btn_start.text = "停止"
                            cancelExit()
                        }
                    }else {
                        TisDialog(this).create().setMessage("柜子未连接").show()
                    }
//                }
            }

            R.id.btn_game_exit -> {
                finish()
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        setSOrECountDown(false);
        mainControl = MainControl.getControl();
//        snow_view.startSnowAnim(SnowUtils.SNOW_LEVEL_HEAVY)
        wrv_prize.layoutManager = GridLayoutManager(this, 4)
        wrv_prize.addItemDecoration(GridSpaceItemDecoration(AutoUtils.getPercentHeightSize(20), AutoUtils.getPercentWidthSize(5), AutoUtils.getPercentHeightSize(20), AutoUtils.getPercentWidthSize(5)))
        game_prize_adapter = GamePrizeAdapter(this, ArrayList<String>(), false, sumPrizeNum)
        wrv_prize.adapter = game_prize_adapter
        rntv.withNumber(1100)
        rntv.setOnEnd {
            btn_start.text = "开始"
//            game_prize_adapter!!.isShowSelect=false
//            game_prize_adapter!!.notifyDataSetChanged()
            var sel_pos = game_prize_adapter!!.getLastSelect()
            var row = sel_pos / 4 + 1//行数
            var col = sel_pos % 4 + 1//列数

            GamePrizeDialog(this).create()
                    .setMessage("第" + row + "行第" + col + "个柜子已开门...")
                    .setHandEventAfterDismiss(object : GamePrizeDialog.HandEventAfterDismiss {
                        override fun handEvent() {
                            startExit(45000)
                        }
                    })
                    .show()

            openDoor(culDoor(sel_pos))
        }
        rntv.setOnTimeTick {
            game_prize_adapter!!.apply {
                if (rntv.isRunning) {
                    if (this.getLastSelect() + 1 >= sumPrizeNum) {
                        this.setLastSelect(0)
                    } else {
                        this.setLastSelect(this.getLastSelect() + 1)
                    }
                    this.isShowSelect = true
                    this.notifyDataSetChanged()
                }
            }

        }


        rntv.postDelayed(Runnable {
            ID()
        }, 500)


        startExit(45000)
    }


    override fun ObjectMessage(msg: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //接受串口的数据
    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val bundle = msg.data
            var data: ByteArray? = null
            data = bundle.getByteArray("data")
            when (msg.what) {

            }
        }
    }


    //柜子
    fun POLL() {
        mainControl!!.POLL()
    }

    //柜子
    fun ACK() {
        mainControl!!.ACK()
    }

    //柜子开门
    fun RUN(position: Int) {
        mainControl!!.RUN(position)
    }

    //柜子查询ID
    fun ID() {
        mainControl!!.ID()
    }

    //开门操作
    fun openDoor(position: Int) {
        Thread(Runnable {
            POLL()
            Thread.sleep(500)
            RUN(position)
            Thread.sleep(500)
            ACK()
        }).start()
    }

    //重新计算需要打开门的下标
    fun culDoor(position: Int): Int {
        var h = position / 4 + 1
        var l = position % 4
        return (h * 4 - 1) - l
    }

    private val exitGameRunable = Runnable {
        //清除会员登录信息
        SharePerferenceUtil.getInstance()
                .setValue(Constance.member_Info, "")
        //清除商城会员登录accessToken
        SharePerferenceUtil.getInstance()
                .setValue(Constance.user_accessToken, "")
        //清除商城会员登录shop_id
        SharePerferenceUtil.getInstance()
                .setValue(Constance.shop_id, "")


        this@NumDanceActivity.finish()
    }

    private var isCancelExit = false
    private fun cancelExit() {
        isCancelExit = true
        handler.removeCallbacks(exitGameRunable)
    }

    private fun startExit(time: Long) {
        isCancelExit = false
        handler.postDelayed(exitGameRunable, time)
    }

    override fun onUserInteraction() {
        if (!isCancelExit) {
            cancelExit()
            startExit(45000)
        }
        super.onUserInteraction()

    }

    /**
     * 是否可以开始游戏
     */
    private fun isAbleStart(): Boolean {
        val memberInfo = GsonUtil.jsonToObject(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "")!!.toString(), MemberInfo::class.java)

        if (null == memberInfo) {
            TisDialog(this).create().setMessage("请登录").show()
            return false
        } else if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                        .getValue(Constance.user_accessToken, "")!!.toString())) {
            TisDialog(this).create().setMessage("请关注公众号绑卡或到前台添加手机号码,再重新登录!").show()
            return false
        } else {
            return true
        }

    }


}
