package com.atguigu.yangyuanyuan.news.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.atguigu.yangyuanyuan.news.R;
import com.atguigu.yangyuanyuan.news.utils.CacheUtils;
import com.atguigu.yangyuanyuan.news.utils.DensityUtil;

import java.util.ArrayList;

public class GuideActivity extends Activity {
    private ViewPager vp_guide;
    private Button btn_guide_start;
    private LinearLayout ll_guide_points;
    private ArrayList<ImageView> imageViews;
    private ImageView iv_guide_red;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
        initData();
        initListener();
    }

    //设置按钮的点击事件
    private void initListener() {
        btn_guide_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //存到SP中
                CacheUtils.putBoolean(GuideActivity.this, SplashActivity.START_MAIN, true);

                //跳转到Main
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initData() {
        int[] pic = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
        imageViews = new ArrayList<>();
        for (int i = 0; i < pic.length; i++) {
            ImageView imageView = new ImageView(this);
            //设置背景
            imageView.setBackgroundResource(pic[i]);
            imageViews.add(imageView);

            //创建点
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_normal);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(this, 10), DensityUtil.dip2px(this, 10));
            if (i != 0) {
                params.leftMargin = DensityUtil.dip2px(this, 10);
            }
            point.setLayoutParams(params);

            ll_guide_points.addView(point);
        }

        vp_guide.setAdapter(new ViewPagerAdapter());

        //处理红点的移动
        redPointMove();
    }

    //处理红点移动的方法
    private void redPointMove() {
        iv_guide_red.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //执行一次后移除监听
                iv_guide_red.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                //计算出每个红点的之间间距
                final int everySpace = ll_guide_points.getChildAt(1).getLeft() - ll_guide_points.getChildAt(0).getLeft();

                //得到屏幕滑动百分比
                //viewPager的监听器
                vp_guide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        //两点之间移动到距离 = 屏幕的百分比*间距
                        int moveDistance = (int) ((position * everySpace) + (positionOffset * everySpace));
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_guide_red.getLayoutParams();
                        params.leftMargin = moveDistance;
                        //设置到红点上
                        iv_guide_red.setLayoutParams(params);

                    }

                    //界面选中回调的方法
                    @Override
                    public void onPageSelected(int position) {
                        if (position == (imageViews.size() - 1)) {
                            btn_guide_start.setVisibility(View.VISIBLE);
                        } else {
                            btn_guide_start.setVisibility(View.INVISIBLE);
                        }
                    }


                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });

    }

    private void initView() {
        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        btn_guide_start = (Button) findViewById(R.id.btn_guide_start);
        ll_guide_points = (LinearLayout) findViewById(R.id.ll_guide_points);
        iv_guide_red = (ImageView) findViewById(R.id.iv_guide_red);
    }

    //ViewPagerAdapter
    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //container 是ViewPager的容器
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageViews.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position));
        }
    }
}
