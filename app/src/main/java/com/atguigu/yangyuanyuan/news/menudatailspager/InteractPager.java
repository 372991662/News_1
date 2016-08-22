package com.atguigu.yangyuanyuan.news.menudatailspager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.atguigu.yangyuanyuan.news.base.MenuDetailBasePager;

/**
 * Created by 杨媛媛 on 2016/8/15 23:59.
 */
public class InteractPager extends MenuDetailBasePager {
    private TextView textView;

    public InteractPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(mContext);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();

        Log.e("TAG", "互动页面数据被初始化了..");
        textView.setText("互动页面内容");
    }
}
