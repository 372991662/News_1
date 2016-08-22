package com.atguigu.yangyuanyuan.news.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.atguigu.yangyuanyuan.news.R;
import com.atguigu.yangyuanyuan.news.utils.CacheUtils;

public class SplashActivity extends Activity {

    private ImageView iv_splash;
    public static final String START_MAIN = "start_main";
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                //是否为第一次登陆过此程序
                boolean isAccessMain = CacheUtils.getBoolean(SplashActivity.this, START_MAIN);
                if (isAccessMain) {

                    //进入主页面
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    //引导页面
                    Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initAnimation();
    }


    private void initAnimation() {
        iv_splash = (ImageView) findViewById(R.id.iv_splash);
        //渐变动画
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        //缩放动画
        ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        //动画集合
        AnimationSet set = new AnimationSet(false);
        set.setDuration(1500);
        set.setFillAfter(true);
        set.addAnimation(aa);
        set.addAnimation(sa);

        iv_splash.setAnimation(set);

        set.setAnimationListener(new Animation());
    }

    //为动画设置监听
    class Animation implements android.view.animation.Animation.AnimationListener {


        @Override
        public void onAnimationStart(android.view.animation.Animation animation) {

        }

        @Override
        public void onAnimationEnd(android.view.animation.Animation animation) {

            handler.sendEmptyMessageDelayed(0, 2000);

        }

        @Override
        public void onAnimationRepeat(android.view.animation.Animation animation) {

        }
    }
}
