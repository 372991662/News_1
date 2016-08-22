package com.atguigu.yangyuanyuan.news.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.atguigu.yangyuanyuan.news.R;

/**
 * Created by 杨媛媛 on 2016/8/15 18:20.
 */
public class BaseViewPager {

    public final Context mContext;
    public View rootView;

    public TextView tv_basepager_title;
    public ImageButton ib_basepager_btn;
    public FrameLayout fl_basepager;

    public BaseViewPager(Context context) {
        this.mContext = context;
        this.rootView = initView();
    }

    //用于初始化公共视图
    private View initView() {
        View view = View.inflate(mContext, R.layout.base_pager, null);
        tv_basepager_title = (TextView) view.findViewById(R.id.tv_basepager_title);
        ib_basepager_btn = (ImageButton) view.findViewById(R.id.ib_basepager_btn);
        fl_basepager = (FrameLayout) view.findViewById(R.id.fl_basepager);
        return view;
    }

    //子类初始化数据
    public void initData() {

    }
}
