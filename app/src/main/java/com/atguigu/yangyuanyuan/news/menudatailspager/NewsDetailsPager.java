package com.atguigu.yangyuanyuan.news.menudatailspager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.atguigu.yangyuanyuan.news.R;
import com.atguigu.yangyuanyuan.news.activity.MainActivity;
import com.atguigu.yangyuanyuan.news.base.MenuDetailBasePager;
import com.atguigu.yangyuanyuan.news.domain.NewsCenterPagerBean;
import com.atguigu.yangyuanyuan.news.menudatailspager.tabdetailpager.TabDetailPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 杨媛媛 on 2016/8/15 23:59.
 */
public class NewsDetailsPager extends MenuDetailBasePager {
    private ViewPager vp_newsmenu_detail;
    private TabPageIndicator tpi_newsmenu_datail_pager;
    //页签页面的数据
    private List<NewsCenterPagerBean.DataBean.ChildrenBean> newsDataChildren = new ArrayList<>();
    //页签页面的集合
    private List<TabDetailPager> tabDetailPagers = new ArrayList<>();
    private ImageButton ib_tabnext;

    public NewsDetailsPager(Context context, NewsCenterPagerBean.DataBean newsData) {
        super(context);
        newsDataChildren = newsData.getChildren();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.newsmenu_detail_pager, null);

        vp_newsmenu_detail = (ViewPager) view.findViewById(R.id.vp_newsmenu_detail);
        tpi_newsmenu_datail_pager = (TabPageIndicator) view.findViewById(R.id
                .tpi_newsmenu_datail_pager);
        ib_tabnext = (ImageButton) view.findViewById(R.id.ib_tabnext);

        //设置ImageView的点击事件
        ib_tabnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_newsmenu_detail.setCurrentItem(vp_newsmenu_detail.getCurrentItem() + 1);
            }
        });
        return view;
    }

    //给ViewPager设置数据
    //1.准备数据 集合
    //2.设置适配器
    @Override
    public void initData() {
        super.initData();

        Log.e("TAG", "新闻详情页面数据被初始化了..");

        //创建页面并添加集合，将新闻数据传递
        for (int i = 0; i < newsDataChildren.size(); i++) {
            tabDetailPagers.add(new TabDetailPager(mContext, newsDataChildren.get(i)));
        }

        //设置适配器
        vp_newsmenu_detail.setAdapter(new NewsMenuDetailAdapter());


        //关联ViewPager和TabPagerIndicator
        tpi_newsmenu_datail_pager.setViewPager(vp_newsmenu_detail);
        //监听页面变化需要用TabPagerIndicator
        tpi_newsmenu_datail_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    isScrollSildingMenu(true);
                } else {
                    isScrollSildingMenu(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //左侧菜单是否可以滑动
    private void isScrollSildingMenu(boolean b) {
        MainActivity mainActivity = (MainActivity) mContext;
        SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
        if (b) {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    //viewPager的适配器
    class NewsMenuDetailAdapter extends PagerAdapter {

        //设置TabPagerIndicator标题,必须重写此方法
        @Override
        public CharSequence getPageTitle(int position) {
            return newsDataChildren.get(position).getTitle();
        }

        @Override
        public int getCount() {
            return tabDetailPagers == null ? 0 : tabDetailPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager detailPager = tabDetailPagers.get(position);
            //初始化数据
            detailPager.initData();
            View rootView = detailPager.rootView;
            container.addView(rootView);
            return rootView;
        }
    }
}
