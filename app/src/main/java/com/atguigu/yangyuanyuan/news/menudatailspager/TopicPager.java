package com.atguigu.yangyuanyuan.news.menudatailspager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.atguigu.yangyuanyuan.news.base.MenuDetailBasePager;

/**
 * Created by 杨媛媛 on 2016/8/15 23:59.
 */
public class TopicPager extends MenuDetailBasePager {
    private TextView textView;

    public TopicPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(mContext);

        TextView tv = new TextView(mContext);
        tv.setText("焦点页面");
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.RED);
        tv.setTextSize(25);

        //添加字数图
        return tv;
    }

    @Override
    public void initData() {
        super.initData();
    }
}
