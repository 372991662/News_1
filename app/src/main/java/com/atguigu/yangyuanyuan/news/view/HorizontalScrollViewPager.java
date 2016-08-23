package com.atguigu.yangyuanyuan.news.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/8/23.
 */
public class HorizontalScrollViewPager extends ViewPager {
    public HorizontalScrollViewPager(Context context) {
        super(context);
    }

    public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float startX, startY;
    private float endX, endY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();

                //请求父视图不拦截此事件
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                endX = ev.getX();
                endY = ev.getY();
                float dx = endX - startX;
                float dy = endY - startY;
                //判断滑动方向
                //水平放下滑动

                if (Math.abs(dx) > Math.abs(dy)) {
                    if (getCurrentItem() == 0 && dx > 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else if ((getCurrentItem() == (getAdapter().getCount() - 1)) && dx < 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);

                }

                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
