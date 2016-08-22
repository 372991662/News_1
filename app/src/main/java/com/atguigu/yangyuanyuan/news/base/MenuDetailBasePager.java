package com.atguigu.yangyuanyuan.news.base;

import android.content.Context;
import android.view.View;

/**
 * Created by 杨媛媛 on 2016/8/15 23:40.
 */
public abstract class MenuDetailBasePager {
    public final Context mContext;

    public View rootView;

    public MenuDetailBasePager(Context context) {
        this.mContext = context;
        rootView = initView();
    }

    public abstract View initView();

    public void initData() {

    }
}
