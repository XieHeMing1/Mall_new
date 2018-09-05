package com.qy.zgz.mall;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment基类
 * TODO: BaseFragment 封装，复用代码
 * Created by admin on 2018/1/16.
 */

public abstract class BaseFragment extends Fragment{


    protected Activity mAttachActivity;
    private Unbinder mUnbinder;
    protected Handler mBaseFragmentHandler;

    /**
     * 设置 Fragment 的View，除了可以通过布局文件来初始化，还可以直接new View 然后添加到 container中
     * @param inflater
     * @param container
     * @return
     */
    public abstract View getLayoutView(LayoutInflater inflater, @Nullable ViewGroup container);

//    public abstract View doInOnCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
//                                          @Nullable Bundle savedInstanceState);

//    public abstract int getLayoutId();

    /**
     * 初始化View 和 一些监听器设置
     * @param view
     */
    public abstract void initViews(View view);


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(this.getClass().getSimpleName(), " onAttach invoke");
        mAttachActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(this.getClass().getSimpleName(), " onCreate invoke");
        mBaseFragmentHandler = new BaseFragmentHandler(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), " onCreateView invoke");
//        return super.onCreateView(inflater, container, savedInstanceState);
//        View view = inflater.inflate(getLayoutId(), container, false);
        View view = getLayoutView(inflater, container);
        if (view == null) {
            throw new IllegalArgumentException("getLayoutView can not return null");
        }
        mUnbinder = ButterKnife.bind(this, view);
        initViews(view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(this.getClass().getSimpleName(), " onViewCreated invoke");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(this.getClass().getSimpleName(), " onStart invoke");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(this.getClass().getSimpleName(), " onResume invoke");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(this.getClass().getSimpleName(), " onPause invoke");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(this.getClass().getSimpleName(), " onStop invoke");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(this.getClass().getSimpleName(), " onDestroyView invoke");
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(this.getClass().getSimpleName(), " onDestroy invoke");
        if (mBaseFragmentHandler != null) {
            mBaseFragmentHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(this.getClass().getSimpleName(), " onDetach invoke");
    }


    //
    private static class BaseFragmentHandler extends Handler {
        private WeakReference<BaseFragment> mFragmentWeakReference;

        public BaseFragmentHandler(BaseFragment fragment) {
            mFragmentWeakReference = new WeakReference<BaseFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseFragment baseFragment = mFragmentWeakReference.get();
            if (baseFragment != null && baseFragment.isVisible()) { // TODO: 这个是fragment.isVisible() 是否恰当?
                // 可见的状态去处理信息应该没有问题
                baseFragment.handleMessage(msg);
            }
        }
    }

    /**
     * 处理 Handler 发送过来的消息
     * @param msg
     */
    protected void handleMessage(Message msg) {
    }

}

