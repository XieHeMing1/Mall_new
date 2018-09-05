package com.qy.zgz.mall.page.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kevin.wraprecyclerview.WrapRecyclerView;
import com.qy.zgz.mall.Model.Cinemadata;
import com.qy.zgz.mall.MyApplication;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.adapter.SmallImageAdapter;
import com.qy.zgz.mall.page.max.MaxImageActivity;
import com.qy.zgz.mall.utils.FileManager;
import com.qy.zgz.mall.utils.FrescoUtils;
import com.qy.zgz.mall.widget.MyGridLayoutManager;
import com.qy.zgz.mall.widget.SpaceItemDecoration;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 小图fragment
 */
public class SmallImageFragment extends Fragment {
    @BindView(R.id.img4)
    public SimpleDraweeView img4;
    @BindView(R.id.img5)
    public SimpleDraweeView img5;
    @BindView(R.id.rlv_image)
    public WrapRecyclerView rlvImage;
    @BindView(R.id.ll_mediu2)
    public AutoLinearLayout llMediu2;

    SmallImageAdapter listAdapter;

    private List<Cinemadata> mediuList;
    private List<Cinemadata> smallList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_small, container, false);
        ButterKnife.bind(this, view);
        initView();
        showImageList();
        return view;
    }

    private void initView() {
        String cinemaType = getActivity().getIntent().getStringExtra("cinemaType");
        int pageLengNum = 4;
//        switch (cinemaType) {
//            case MainActivity.CINEMA3:
//                pageLengNum = 5;
//                updateSmallRightLayout();
//                break;
//            case MainActivity.CINEMA4:
//                pageLengNum = 5;
//                updateSmallLeftLayout();
//                break;
//        }

        listAdapter = new SmallImageAdapter(new ArrayList<>());
        rlvImage.setLayoutManager(new MyGridLayoutManager(getActivity(), pageLengNum));
        rlvImage.addItemDecoration(new SpaceItemDecoration(AutoUtils.getPercentHeightSize(20)));
        rlvImage.setAdapter(listAdapter);
        rlvImage.addFooterView(LayoutInflater.from(getContext()).inflate(R.layout.item_recycle_bottom,null));
    }

    /**
     * 小图在左边的风格
     */
    private void updateSmallLeftLayout() {
        llMediu2.setVisibility(View.VISIBLE);
        AutoRelativeLayout.LayoutParams params = (AutoRelativeLayout.LayoutParams) llMediu2.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF, R.id.rlv_image);
        params.setMargins(AutoUtils.getPercentWidthSize(10), 0, 0, 0);
        AutoRelativeLayout.LayoutParams params1 = new AutoRelativeLayout.LayoutParams(AutoUtils.getPercentWidthSize(1400), ViewGroup.LayoutParams.WRAP_CONTENT);
        rlvImage.setLayoutParams(params1);
        llMediu2.setLayoutParams(params);
    }

    /**
     * 小图在右边的风格
     */
    private void updateSmallRightLayout() {
        llMediu2.setVisibility(View.VISIBLE);
        AutoRelativeLayout.LayoutParams params = (AutoRelativeLayout.LayoutParams) rlvImage.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF, R.id.ll_mediu2);
        params.setMargins(AutoUtils.getPercentWidthSize(10), 0, 0, 0);
        rlvImage.setLayoutParams(params);
    }

    public void setImageList(List<Cinemadata> mediuList, List<Cinemadata> cinemadataList) {
        this.mediuList = mediuList;
        this.smallList = cinemadataList;

    }

    private void showImageList() {
        if (mediuList != null && mediuList.size() > 0) {
            showMediuImage(img4, mediuList.get(0));
        }
        if (mediuList != null && mediuList.size() > 1) {
            showMediuImage(img5, mediuList.get(1));
        }
        listAdapter.setListStr(smallList);
        listAdapter.notifyDataSetChanged();
    }

    /**
     * 显示中图信息
     *
     * @param image
     */
    private void showMediuImage(SimpleDraweeView image, Cinemadata cinemadata) {
        String fileName = FileManager.getInstance().getFileName(cinemadata.getImage_default_id());
        Uri uri=null;
        if (FileManager.getInstance().isFileExists(fileName)) {
            uri=Uri.parse("file://" + FileManager.getInstance().getDestFileDir() + fileName);
        } else {
            uri=Uri.parse(cinemadata.getImage_default_id());
        }
        FrescoUtils.showThumb(image,uri,AutoUtils.getPercentWidthSize(826),AutoUtils.getPercentHeightSize(690));


    }

    @OnClick({R.id.img4, R.id.img5})
    public void onClick(View v) {
        Intent intent = new Intent(MyApplication.getInstance(), MaxImageActivity.class);
        switch (v.getId()) {
            case R.id.img4:
                if (mediuList != null && mediuList.size() > 0) {
                    intent.putExtra("cinema", mediuList.get(0));
                } else {
                    return;
                }
                break;
            case R.id.img5:
                if (mediuList != null && mediuList.size() > 1) {
                    intent.putExtra("cinema", mediuList.get(1));
                } else {
                    return;
                }
                break;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getInstance().startActivity(intent);
    }
}
