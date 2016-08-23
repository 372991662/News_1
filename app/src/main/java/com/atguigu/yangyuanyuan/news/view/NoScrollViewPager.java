package com.atguigu.yangyuanyuan.news.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 杨媛媛 on 2016/8/15 20:05.
 */

//创建
public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //里面轮播图和外面viewpager冲突
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    //解决最后一个Viewpager外划
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
