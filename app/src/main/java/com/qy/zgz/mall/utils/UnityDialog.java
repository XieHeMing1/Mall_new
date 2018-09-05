package com.qy.zgz.mall.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qy.zgz.mall.R;


/**
 * 统一 Dialog
 *
 * @author 树魂
 */
public class UnityDialog implements TextWatcher{


    //定义回调事件，用于dialog的点击事件
    public interface OnConfirmDialogListener {
        public void confirm(UnityDialog unityDialog, String content);

    }

    //关闭回调事件
    public interface OnCancelDialogListener {
        public void cancel(UnityDialog unityDialog);
    }

    //自定义view回调
    public interface GetViewListener {
        void getView(View view);
    }

    private OnConfirmDialogListener onConfirmDialogListener;
    private OnCancelDialogListener onCancelDialogListener;
    private LinearLayout llLoading;

    //标题
    private TextView tvTitle;

    //提示
    private TextView tvHint;

    //输入内容
    private EditText etContent;

    //自定义view
    private FrameLayout mFlContainer;
    private View mView;

    //取消按钮
    private TextView tvCancel;

    //确定按钮
    private TextView tvConfirm;
    private View vBtn;

    //操作布局
    private LinearLayout llHandle;
    private LinearLayout llContent;

    //输入框可输入最大行数,0为不限制
    private int inputMaxLines = 0;

    private Activity context;
    private Dialog mDialog;
    private Handler handler;

    public UnityDialog(Activity context) {
        this.context = context;
        init();
    }

    private void init()
    {
        handler=new Handler();
        mDialog = new Dialog(context, R.style.dialog);
        mDialog.show();
        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = mDialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.9);    //宽度设置为屏幕的0.80
        //p.height = (int) (d.getHeight() * 0.3);   //高度设置为屏幕的0.3
        mDialog.getWindow().setAttributes(p);     //设置生效
        mDialog.setContentView(R.layout.view_uniuty_dialog);

        mDialog.setCanceledOnTouchOutside(false);
        tvTitle = (TextView) mDialog.findViewById(R.id.tv_title);
        tvHint = (TextView) mDialog.findViewById(R.id.tv_hint);
        etContent = (EditText) mDialog.findViewById(R.id.et_content);
        tvCancel = (TextView) mDialog.findViewById(R.id.tv_cancel);
        tvConfirm = (TextView) mDialog.findViewById(R.id.tv_confirm);
        llHandle = (LinearLayout) mDialog.findViewById(R.id.ll_handle);
        llLoading = (LinearLayout) mDialog.findViewById(R.id.lin_loading);

        mFlContainer = (FrameLayout) mDialog.findViewById(R.id.fl_content_view);
        vBtn=(View)mDialog.findViewById(R.id.v_btn);
        llContent=(LinearLayout)mDialog.findViewById(R.id.ll_content);

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (onDismissListener != null) {
                    onDismissListener.dismiss();
                }
            }
        });
    }

    public void show() {
        if(tvCancel.getVisibility()==View.VISIBLE && tvConfirm.getVisibility()==View.VISIBLE)
        {
            vBtn.setVisibility(View.VISIBLE);
        }
        if(tvCancel.getVisibility()==View.VISIBLE && tvConfirm.getVisibility()==View.GONE)
        {
//            tvCancel.setBackgroundResource(R.drawable.dialog_button_single_style);
        }
        else if(tvCancel.getVisibility()==View.GONE && tvConfirm.getVisibility()==View.VISIBLE)
        {
//            tvConfirm.setBackgroundResource(R.drawable.dialog_button_single_style);
        }
        mDialog.show();
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public UnityDialog setTitle(String title) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
        return this;
    }

    /**
     * 设置提示信息
     *
     * @param hint 提示信息
     */
    public UnityDialog setHint(String hint) {
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(hint);
        return this;
    }

    public UnityDialog setHint(String hint, int gravity) {
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(hint);
        tvHint.setGravity(gravity);
        return this;
    }

    /**
     * 设置内容输入框
     *
     * @param hint    提示框信息
     * @param content 内容
     * @param isDelete       是否显示删除按钮
     * @param inputType      输入方式（-1为不设置,默认文本）
     */
    public UnityDialog setContent(String hint, String content,boolean isDelete,int inputType) {
        etContent.setVisibility(View.VISIBLE);
        etContent.setHint(hint);
        etContent.setText(content);
        if(inputType!=-1)
        {
            etContent.setInputType(inputType);
        }

        editFocus(content);
        return this;
    }

    /**
     * 设置内容输入框
     *
     * @param hint           提示框信息
     * @param content        内容
     * @param showMinLines   输入框显示的最小行数
     * @param showMaxLines   输入框显示的最大行数
     * @param inputMaxLines  输入框支持的最大行数
     * @param inputMaxLength 输入框支持的最大长度
     * @param isDelete       是否显示删除按钮
     * @param inputType      输入方式（-1为不设置,默认文本）
     */
    public UnityDialog setContent(String hint, String content,
                                  int showMinLines, int showMaxLines, int inputMaxLines, int inputMaxLength,
                                  boolean isDelete,int inputType) {
        etContent.setVisibility(View.VISIBLE);
        etContent.setHint(hint);
        etContent.setText(content);
        if (showMinLines != 0) {
            etContent.setMinLines(showMinLines);
        }

        if (showMaxLines != 0) {
            etContent.setMaxLines(showMaxLines);
        }


        if (inputMaxLines != 0) {
            etContent.addTextChangedListener(this);
        }
        this.inputMaxLines = inputMaxLines;
        if(inputType!=-1)
        {
            etContent.setInputType(inputType);
        }
       editFocus(content);
        return this;
    }

    /**
     * 添加自定义布局
     * @param resId
     * @return
     */
    public UnityDialog setView(@LayoutRes int resId) {
        mView = LayoutInflater.from(context).inflate(resId, null);
        mFlContainer.setVisibility(View.VISIBLE);
        mFlContainer.addView(mView);
        return this;
    }

    //设置自定义布局
    public UnityDialog setView(@LayoutRes int resId, GetViewListener listener) {
        mView = LayoutInflater.from(context).inflate(resId, null);
        mFlContainer.setVisibility(View.VISIBLE);
        mFlContainer.addView(mView);
        listener.getView(mView);
        return this;
    }

    private void editFocus(String content)
    {
        etContent.setSelection(content.length());//将光标移至文字末尾
        etContent.requestFocus();
        InputMethodManager imm2 = (InputMethodManager)etContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.showSoftInput(etContent, InputMethodManager.RESULT_SHOWN);
        imm2.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 设置关闭按钮
     *
     * @param cancelStr              按钮文本
     * @param onCancelDialogListener 回调接口
     */
    public UnityDialog setCancel(String cancelStr, OnCancelDialogListener onCancelDialogListener) {
        tvCancel.setVisibility(View.VISIBLE);
        tvCancel.setText(cancelStr);
        tvCancel.setOnClickListener(clickListener);
        this.onCancelDialogListener = onCancelDialogListener;
        return this;
    }

    /**
     * 设置确定按钮
     *
     * @param confirmStr              按钮文本
     * @param onConfirmDialogListener 回调接口
     */
    public UnityDialog setConfirm(String confirmStr, OnConfirmDialogListener onConfirmDialogListener) {
        tvConfirm.setVisibility(View.VISIBLE);
        tvConfirm.setText(confirmStr);
        tvConfirm.setOnClickListener(clickListener);
        this.onConfirmDialogListener = onConfirmDialogListener;
        return this;
    }

    public void dismiss() {
        InputMethodManager imm2 = (InputMethodManager)etContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.hideSoftInputFromWindow(etContent.getWindowToken(),0);
        mDialog.dismiss();
    }

    /**
     * 设置触摸外部不隐藏
     */
    public UnityDialog isCancelable(boolean cancelable) {
        mDialog.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == tvCancel) {
                if (onCancelDialogListener == null) {
                    dismiss();
                } else {
                    onCancelDialogListener.cancel(UnityDialog.this);
                }
            } else if (v == tvConfirm) {
                if (onConfirmDialogListener != null) {
                    onConfirmDialogListener.confirm(UnityDialog.this, etContent.getText().toString().trim());
                }
            }

        }
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int lines = etContent.getLineCount();
        // 限制最大输入行数
        if (lines > inputMaxLines) {
            String str = s.toString();
            int cursorStart = etContent.getSelectionStart();
            int cursorEnd = etContent.getSelectionEnd();
            if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                str = str.substring(0, cursorStart - 1) + str.substring(cursorStart);
            } else {
                str = str.substring(0, s.length() - 1);
            }
            // setText会触发afterTextChanged的递归
            etContent.setText(str);
            // setSelection用的索引不能使用str.length()否则会越界
            etContent.setSelection(etContent.getText().length());
            return;
        }
        String summary = etContent.getText().toString();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private OnDismissListener onDismissListener;

    public UnityDialog setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    /**
     * 监听dialog关闭
     */
    public interface OnDismissListener {
        void dismiss();
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(TextView tvTitle) {
        this.tvTitle = tvTitle;
    }

    public TextView getTvHint() {
        return tvHint;
    }

    public void setTvHint(TextView tvHint) {
        this.tvHint = tvHint;
    }

    public EditText getEtContent() {
        return etContent;
    }


    public TextView getTvCancel() {
        return tvCancel;
    }

    public void setTvCancel(TextView tvCancel) {
        this.tvCancel = tvCancel;
    }

    public TextView getTvConfirm() {
        return tvConfirm;
    }

    public void setTvConfirm(TextView tvConfirm) {
        this.tvConfirm = tvConfirm;
    }

    public LinearLayout getLlHandle() {
        return llHandle;
    }

    public void setLlHandle(LinearLayout llHandle) {
        this.llHandle = llHandle;
    }

    public View getmView() {
        return mView;
    }

    public void setmView(View mView) {
        this.mView = mView;
    }
}
