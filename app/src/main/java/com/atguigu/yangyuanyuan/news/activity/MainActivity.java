package com.atguigu.yangyuanyuan.news.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;

import com.atguigu.yangyuanyuan.news.R;
import com.atguigu.yangyuanyuan.news.fragment.LeftmenuFragment;
import com.atguigu.yangyuanyuan.news.fragment.MainFragment;
import com.atguigu.yangyuanyuan.news.utils.DensityUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

    public static final String MAIN_TAG = "main_tag";
    public static final String LEFT_TAG = "left_tag";
    private Object leftmenuFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置主页面
        setContentView(R.layout.activity_main);
        //设置左侧菜单
        setBehindContentView(R.layout.activity_leftmenu);

        SlidingMenu sm = getSlidingMenu();
        //sm.setSecondaryMenu(R.layout.activity_rightmenu);
        //设置显示模式
        sm.setMode(SlidingMenu.LEFT);
        //设置滑动模式
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置占据主页宽度
        sm.setBehindOffset(DensityUtil.dip2px(this, 200));

        initFragment();
        //-----------------------------设置虚拟按键
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    //初始化Fragment
    private void initFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_main, new MainFragment(), MAIN_TAG);
        transaction.replace(R.id.fl_left, new LeftmenuFragment(), LEFT_TAG);
        transaction.commit();

    }

    public LeftmenuFragment getLeftmenuFragment() {
        FragmentManager manager = getSupportFragmentManager();
        return (LeftmenuFragment) manager.findFragmentByTag(LEFT_TAG);
    }

    public MainFragment getMainFragment() {
        FragmentManager manager = getSupportFragmentManager();
        return (MainFragment) manager.findFragmentByTag(MAIN_TAG);
    }
}
