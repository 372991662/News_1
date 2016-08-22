package com.atguigu.yangyuanyuan.news.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.atguigu.yangyuanyuan.news.activity.MainActivity;
import com.atguigu.yangyuanyuan.news.adapter.LeftListViewAdapter;
import com.atguigu.yangyuanyuan.news.base.BaseFragment;
import com.atguigu.yangyuanyuan.news.domain.NewsCenterPagerBean;
import com.atguigu.yangyuanyuan.news.pager.NewsCenterPager;
import com.atguigu.yangyuanyuan.news.utils.DensityUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/8/14.
 */
public class LeftmenuFragment extends BaseFragment {
    private List<NewsCenterPagerBean.DataBean> mLeftMenuData;
    private ListView listView;
    private int lastPosition;
    private LeftListViewAdapter adapter;


    //初始化视图
    @Override
    public View initView() {
        // 动态创建lv并初始化
        listView = new ListView(mContext);
        listView.setPadding(0, DensityUtil.dip2px(mContext, 60), 0, 0);
        listView.setDividerHeight(0);//设置分割线高度为0
        listView.setCacheColorHint(Color.TRANSPARENT);//设置默认透明
        listView.setSelector(android.R.color.transparent);//设置按下listViewItem不变色

        //初始化监听
        initListener();
        return listView;
    }

    //初始化数据
    @Override
    public void initData() {
        super.initData();
    }

    //设置菜单item条目监听
    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //记录点击位置，点击变红色
                lastPosition = position;
                adapter.setmLastPosition(position);
                adapter.notifyDataSetChanged();

                //关闭左侧菜单
                MainActivity mainActivity = (MainActivity) mContext;
                mainActivity.getSlidingMenu().toggle();

                //切换到详情页面
                switchPager(lastPosition);

            }
        });
    }

    //根据位置切换详情页面
    private void switchPager(int position) {
        MainActivity mainActivity = (MainActivity) mContext;
        MainFragment mainFragment = mainActivity.getMainFragment();
        NewsCenterPager newsCenterPager = mainFragment.getNewsCenterPager();
        newsCenterPager.switchPager(position);
    }


    //左侧菜单设置数据
    public void setData(List<NewsCenterPagerBean.DataBean> leftMenuData) {
        this.mLeftMenuData = leftMenuData;
        for (int i = 0; i < mLeftMenuData.size(); i++) {
            //---
            Log.e("TAG", mLeftMenuData.get(i).getTitle());
        }
        adapter = new LeftListViewAdapter(mContext, mLeftMenuData);
        //设置适配器
        listView.setAdapter(adapter);

        //设置默认页面为起始页面
        switchPager(0);
    }
}
