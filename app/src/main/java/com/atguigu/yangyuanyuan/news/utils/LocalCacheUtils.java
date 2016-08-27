package com.atguigu.yangyuanyuan.news.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.xutils.common.util.MD5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/8/26.
 */
public class LocalCacheUtils {

    private static FileOutputStream fos;
    private static InputStream is;
    private MemoryCacheUtils memoryCacheUtils;

    public LocalCacheUtils(MemoryCacheUtils memoryCacheUtils) {
        this.memoryCacheUtils = memoryCacheUtils;
    }

    public Bitmap getBitmapFromLocal(String imageUrl) {
        //保存图片到SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //MD5加密
            String fileName = MD5.md5(imageUrl);
            File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews", fileName);

            //取出图片
            try {
                if (file.exists()) {
                    is = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    //缓存内存
                    if (bitmap != null) {
                        memoryCacheUtils.putBitmap(imageUrl, bitmap);
                        Log.e("TAG", "从本地保存到内存中");
                    }
                    return bitmap;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("TAG", "图片本地获取失败");

            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
        return null;
    }


    //根据url获存入本地
    public  void putBitmapToLocal(String imageUrl, Bitmap bitmap) {
        //保存图片到SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //MD5加密
            String fileName = MD5.md5(imageUrl);
            File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews", fileName);
            File parentFile = file.getParentFile();
            try {
                //没有SD卡创建目录
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                //保存图片
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAG", "图片本地缓存失败" + e.getMessage());
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
