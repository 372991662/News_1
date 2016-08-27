package com.atguigu.yangyuanyuan.news.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;


/**
 * Created by Administrator on 2016/8/26.
 */
public class BitmapCacheUtils {
    //网络缓存工具
    private NetCacheUtils netCacheUtils;
    private LocalCacheUtils localCacheUtils;
    private MemoryCacheUtils memoryCacheUtils;

    public BitmapCacheUtils(Handler handler) {
        memoryCacheUtils = new MemoryCacheUtils();
        localCacheUtils = new LocalCacheUtils(memoryCacheUtils);
        netCacheUtils = new NetCacheUtils(handler, localCacheUtils, memoryCacheUtils);
    }

    public Bitmap getBitmap(String imageUrl, int position) {
        //1.内存获取
        if (memoryCacheUtils != null) {
            Bitmap bitmap = memoryCacheUtils.getBitmapFromUrl(imageUrl);
            if (bitmap != null) {
                Log.e("TAG", "内存加载图片成功" + position);
                return bitmap;
            }
        }
        //2.本地获取
        if (localCacheUtils != null) {
            Bitmap bitmap = localCacheUtils.getBitmapFromLocal(imageUrl);
            if (bitmap != null) {
                Log.e("TAG", "本地加载图片成功" + position);
                return bitmap;
            }
        }
        //3.网络获取
        netCacheUtils.getBitmapFromNet(imageUrl, position);
        return null;
    }
}
