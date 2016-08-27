package com.atguigu.yangyuanyuan.news.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Administrator on 2016/8/26.
 */
public class NetCacheUtils {
    public static final int SUCCESS = 1;
    public static final int FAIL = 2;
    private Handler handler;
    private int position;
    private ExecutorService service;
    private LocalCacheUtils localCacheUtils;
    private MemoryCacheUtils memoryCacheUtils;

    public NetCacheUtils(Handler handler, LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        this.handler = handler;
        this.localCacheUtils = localCacheUtils;
        this.memoryCacheUtils = memoryCacheUtils;
        //全局线程池，初始化一次
        service = Executors.newFixedThreadPool(10);

    }

    //###########################$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5
    public void getBitmapFromNet(String imageUrl, int position) {
        //获取线程池
        service.execute(new MyRunnable(imageUrl));
        this.position = position;
    }

    class MyRunnable implements Runnable {
        private String imageUrl;

        public MyRunnable(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);
                int code = connection.getResponseCode();
                if (code == 200) {
                    InputStream is = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    //显示控件上
                    Message msg = Message.obtain();
                    msg.what = SUCCESS;
                    msg.arg1 = position;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                    //内存缓存
                    memoryCacheUtils.putBitmap(imageUrl, bitmap);
                    //本地缓存
                    localCacheUtils.putBitmapToLocal(imageUrl, bitmap);
                }
            } catch (IOException e) {
                Message msg = Message.obtain();
                msg.what = FAIL;
                msg.arg1 = position;
                handler.sendMessage(msg);

            }
        }
    }
}
