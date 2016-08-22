package com.atguigu.yangyuanyuan.news.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.atguigu.yangyuanyuan.news.R;
import com.atguigu.yangyuanyuan.news.activity.MainActivity;
import com.atguigu.yangyuanyuan.news.base.BaseFragment;
import com.atguigu.yangyuanyuan.news.base.BaseViewPager;
import com.atguigu.yangyuanyuan.news.pager.GovaffairPager;
import com.atguigu.yangyuanyuan.news.pager.HomePager;
import com.atguigu.yangyuanyuan.news.pager.NewsCenterPager;
import com.atguigu.yangyuanyuan.news.pager.SettingPager;
import com.atguigu.yangyuanyuan.news.pager.SmartServicePager;
import com.atguigu.yangyuanyuan.news.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/8/14.
 */
public class MainFragment extends BaseFragment {

    //xUtils初始控件
    @ViewInject(R.id.vp_main)
    private NoScrollViewPager vp_main;

    @ViewInject(R.id.rg_main)
    private RadioGroup rg_main;

    private List<BaseViewPager> basePagers;

    private BaseViewPager viewPager;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.main_fragment, null);
        //vp_main = (ViewPager) view.findViewById(R.id.vp_main);
        //rg_main = (RadioGroup) view.findViewById(R.id.rg_main);

        //把视图注入框架中
        x.view().inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();

        //初始化页面并且放入集合中
        basePagers = new ArrayList<>();
        basePagers.add(new HomePager(mContext));
        basePagers.add(new NewsCenterPager(mContext));
        basePagers.add(new SmartServicePager(mContext));
        basePagers.add(new GovaffairPager(mContext));
        basePagers.add(new SettingPager(mContext));

        //设置适配器
        vp_main.setAdapter(new BasePagerAdapter());


        //设置radioGroup的监听
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_main_home:
                        vp_main.setCurrentItem(0, false);
                        isScrollSildingMenu(false);
                        break;
                    case R.id.rb_main_news:
                        vp_main.setCurrentItem(1, false);
                        isScrollSildingMenu(true);
                        break;
                    case R.id.rb_main_smart:
                        vp_main.setCurrentItem(2, false);
                        isScrollSildingMenu(false);
                        break;
                    case R.id.rb_main_goverment:
                        vp_main.setCurrentItem(3, false);
                        isScrollSildingMenu(false);
                        break;
                    case R.id.rb_main_setting:
                        vp_main.setCurrentItem(4, false);
                        isScrollSildingMenu(false);
                        break;
                }
            }
        });

        //设置默认选中的页面
        rg_main.check(R.id.rb_main_home);
        basePagers.get(0).initData();

        //监听页面改变并初始化数据
        vp_main.addOnPageChangeListener(new MyOnPageChangeListener());

    }

    //左侧菜单是否可以滚动
    private void isScrollSildingMenu(boolean b) {
        MainActivity mainActivity = (MainActivity) mContext;
        SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
        if (b) {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    //得到新闻中心的方法
    public NewsCenterPager getNewsCenterPager() {
        return (NewsCenterPager) basePagers.get(1);
    }

    //##############################
    //页面改变监听
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //初始化数据
            basePagers.get(position).initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    //适配器
    class BasePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return basePagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseViewPager viewPager = basePagers.get(position);

            View rootView = viewPager.rootView;
            //--------------------------
            container.addView(rootView);
            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
