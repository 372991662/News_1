package com.atguigu.yangyuanyuan.news;

import android.app.Application;

import org.xutils.x;

/**
 * Created by 杨媛媛 on 2016/8/15 17:01.
 */
public class NewsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
