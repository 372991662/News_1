package com.atguigu.yangyuanyuan.news.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.atguigu.yangyuanyuan.news.base.BaseViewPager;

/**
 * Created by 杨媛媛 on 2016/8/15 18:45.
 */
public class GovaffairPager extends BaseViewPager {
    public GovaffairPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();

        tv_basepager_title.setText("政要页面");
        TextView tv = new TextView(mContext);
        tv.setText("政要内容");
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.RED);
        tv.setTextSize(25);

        //添加字数图
        fl_basepager.addView(tv);
    }
}
