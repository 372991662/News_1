package com.atguigu.yangyuanyuan.news.pager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.atguigu.yangyuanyuan.news.activity.MainActivity;
import com.atguigu.yangyuanyuan.news.base.BaseViewPager;
import com.atguigu.yangyuanyuan.news.base.MenuDetailBasePager;
import com.atguigu.yangyuanyuan.news.domain.NewsCenterPagerBean;
import com.atguigu.yangyuanyuan.news.fragment.LeftmenuFragment;
import com.atguigu.yangyuanyuan.news.menudatailspager.InteractPager;
import com.atguigu.yangyuanyuan.news.menudatailspager.NewsDetailsPager;
import com.atguigu.yangyuanyuan.news.menudatailspager.PhotosPager;
import com.atguigu.yangyuanyuan.news.menudatailspager.TopicPager;
import com.atguigu.yangyuanyuan.news.utils.CacheUtils;
import com.atguigu.yangyuanyuan.news.utils.Constants;
import com.atguigu.yangyuanyuan.news.volley.VolleyManager;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 杨媛媛 on 2016/8/15 18:45.
 */
public class NewsCenterPager extends BaseViewPager {


    private List<NewsCenterPagerBean.DataBean> menuData;
    private List<MenuDetailBasePager> detialBasePagers;

    public NewsCenterPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        ib_basepager_btn.setVisibility(View.VISIBLE);

        tv_basepager_title.setText("新闻页面");
        TextView tv = new TextView(mContext);
        tv.setText("新闻内容");
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.RED);
        tv.setTextSize(25);

        initListener();

        //添加子视图
        fl_basepager.addView(tv);

        //获取缓存数据
        String saveJson = CacheUtils.getString(mContext, Constants.NEWS_CENTER_PAGER_URL);
        //非空验证
        if (!TextUtils.isEmpty(saveJson)) {
            //从内存中解析数据
            processData(saveJson);
        }

        //联网请求数据
        //getDataFromNet();
        getDataFromNetByVolley();
    }

    //使用Volley联网请求
    private void getDataFromNetByVolley() {
        // RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest request = new StringRequest(Request.Method.GET, Constants.NEWS_CENTER_PAGER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                //从网络中解析数据
                processData(result);
                //缓存数据  key->value============================================
                CacheUtils.putString(mContext, Constants.NEWS_CENTER_PAGER_URL, result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("TAG", "请求错误" + volleyError.getMessage());
            }
        }) {
            //乱码问题
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String parsed = new String(response.data, "utf-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };

        //添加到队列
        VolleyManager.getRequestQueue().add(request);
    }


    //使用Xutils联网请求数据
    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NEWS_CENTER_PAGER_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "请求成功");
                //从网络中解析数据
                processData(result);
                //缓存数据  key->value======================================
                CacheUtils.putString(mContext, Constants.NEWS_CENTER_PAGER_URL, result);
                //设置ListView适配器

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "请求错误" + ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("TAG", "请求取消" + cex);
            }

            @Override
            public void onFinished() {
                Log.e("TAG", "请求结束");
            }
        });
    }

    private void initListener() {
        ib_basepager_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) mContext;
                SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
                slidingMenu.toggle();
            }
        });
    }

    //解析json数据,显示
    private void processData(String json) {
        NewsCenterPagerBean bean = parsedJson(json);
        String title = bean.getData().get(0).getChildren().get(1).getTitle();
        Log.e("TAG", title);

        //给左侧菜单传递数据
        menuData = bean.getData();
        MainActivity mainActivity = (MainActivity) mContext;
        LeftmenuFragment leftmenuFragment = (LeftmenuFragment) mainActivity.getLeftmenuFragment();

        //---------添加详情页面
        detialBasePagers = new ArrayList<>();
        //通过构造将第0条数据传过去
        detialBasePagers.add(new NewsDetailsPager(mContext, menuData.get(0)));
        detialBasePagers.add(new TopicPager(mContext, menuData.get(0)));
        detialBasePagers.add(new PhotosPager(mContext, menuData.get(2)));
        detialBasePagers.add(new InteractPager(mContext,menuData.get(2)));

        //传递数据给左侧菜单
        leftmenuFragment.setData(menuData);

    }

    //解析json数据
    private NewsCenterPagerBean parsedJson(String json) {
        Gson gson = new Gson();
        NewsCenterPagerBean bean = gson.fromJson(json, NewsCenterPagerBean.class);
        return bean;
    }

    //根据位置切换页面
    public void switchPager(int position) {
        //改变标题
        tv_basepager_title.setText(menuData.get(position).getTitle());
        //移除内容
        fl_basepager.removeAllViews();
        //添加新内容
        MenuDetailBasePager menuDetailBasePager = detialBasePagers.get(position);
        View rootView = menuDetailBasePager.rootView;
        menuDetailBasePager.initData();
        fl_basepager.addView(rootView);

        if (position == 2) {
            //图组详情页面
            ib_switch.setVisibility(View.VISIBLE);

            //设置点击事件
            ib_switch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //得到图组对象
                    PhotosPager pager = (PhotosPager) detialBasePagers.get(2);
                    //调用图组对象切换方法
                    pager.switchView(ib_switch);
                }
            });
        } else {
            ib_switch.setVisibility(View.GONE);
        }
    }
}
