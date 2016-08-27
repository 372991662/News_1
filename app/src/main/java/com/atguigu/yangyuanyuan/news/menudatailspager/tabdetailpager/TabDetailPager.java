package com.atguigu.yangyuanyuan.news.menudatailspager.tabdetailpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.yangyuanyuan.news.R;
import com.atguigu.yangyuanyuan.news.activity.NewsDetailActivity;
import com.atguigu.yangyuanyuan.news.base.MenuDetailBasePager;
import com.atguigu.yangyuanyuan.news.domain.NewsCenterPagerBean;
import com.atguigu.yangyuanyuan.news.domain.TableDetailPagerBean;
import com.atguigu.yangyuanyuan.news.utils.CacheUtils;
import com.atguigu.yangyuanyuan.news.utils.Constants;
import com.atguigu.yangyuanyuan.news.utils.DensityUtil;
import com.atguigu.yangyuanyuan.news.view.HorizontalScrollViewPager;
import com.example.administrator.refreshlistview.RefreshListView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;


/**
 * Created by 杨媛媛 on 2016/8/16 19:20.
 */

//页签详情页面
public class TabDetailPager extends MenuDetailBasePager {
    public static final String READ_ARRAY_ID = "read_array_id";
    private HorizontalScrollViewPager vp_newsdetails;
    private TextView tv_title;
    private LinearLayout ll_newsdetails;
    private RefreshListView lv_newsdetails;


    private int lastPosition;
    private NewsCenterPagerBean.DataBean.ChildrenBean mChilderBean = new NewsCenterPagerBean
            .DataBean.ChildrenBean();
    private String url;
    //顶部新闻数据
    private List<TableDetailPagerBean.DataBean.TopnewsBean> topnews;
    //新闻列表数据集合
    private List<TableDetailPagerBean.DataBean.NewsBean> news;
    private String moreUrl;

    //加载更多数据成功了
    private boolean isLoadeMore = false;
    private NewsListViewAdapter adapter;
    private MyHandler handler;


    public TabDetailPager(Context context, NewsCenterPagerBean.DataBean.ChildrenBean childrenBean) {
        super(context);
        this.mChilderBean = childrenBean;
    }


    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.tabdetail_pager, null);
        lv_newsdetails = (RefreshListView) view.findViewById(R.id.lv_newsdetails);

        View viewHeader = View.inflate(mContext, R.layout.news_list_header, null);
        vp_newsdetails = (HorizontalScrollViewPager) viewHeader.findViewById(R.id.vp_newsdetails);
        tv_title = (TextView) viewHeader.findViewById(R.id.tv_title);
        ll_newsdetails = (LinearLayout) viewHeader.findViewById(R.id.ll_newsdetails);

        //添加listview的头部
        //lv_newsdetails.addHeaderView(viewHeader);
        lv_newsdetails.addTopNews(viewHeader);

        //监听控件刷新
        lv_newsdetails.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            //上拉刷新
            @Override
            public void onPullDownlRefresh() {
                getDataFromNet();
            }

            //在加载更多
            @Override
            public void onLoadMore() {
                if (TextUtils.isEmpty(moreUrl)) {
                    Toast.makeText(mContext, "没有更多数据", Toast.LENGTH_SHORT).show();
                    lv_newsdetails.setOnRefreshFinish(false);
                } else {
                    getMoreDataFromNet();

                }
            }
        });

        //实现点击变灰功能，设置监听点击某一条
        lv_newsdetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int realPosition = position - 1;
                TableDetailPagerBean.DataBean.NewsBean newsBean = news.get(realPosition);

                //存成字符串
                String readArrayId = CacheUtils.getString(mContext, READ_ARRAY_ID);
                //如果不包含保存
                if (!readArrayId.contains(newsBean.getId() + "")) {
                    //保存到sp中
                    CacheUtils.putString(mContext, READ_ARRAY_ID, readArrayId + newsBean.getId() + ",");
                    //刷新适配器
                    adapter.notifyDataSetChanged();
                }
                //点击跳转新闻详情页面
                Intent intent = new Intent(mContext, NewsDetailActivity.class);
                String url = Constants.BASE_URL + newsBean.getUrl();
                intent.putExtra("url", url);
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void initData() {
        super.initData();

        //设置ViewPager的数据

        //获取联网请求
        url = Constants.BASE_URL + mChilderBean.getUrl();

        String saveJson = CacheUtils.getString(mContext, url);
        //解析数据
        if (!TextUtils.isEmpty(saveJson)) {
            //解析数据
            processData(saveJson);
        }

        getDataFromNet();
    }


    public void getDataFromNet() {
        //--------------解决小红点bug问题
        lastPosition = 0;
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //缓存数据
                CacheUtils.putString(mContext, url, result);
                //解析数据
                processData(result);
                lv_newsdetails.setOnRefreshFinish(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "请求失败" + ex);
                lv_newsdetails.setOnRefreshFinish(false);

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

    //解析数据
    private void processData(String saveJson) {
        //jason解析的数据
        TableDetailPagerBean tableDetailPagerBean = parsedJson(saveJson);
        //获取加载更多的数据
        String more = tableDetailPagerBean.getData().getMore();
        if (TextUtils.isEmpty(more)) {
            moreUrl = "";
        } else {
            moreUrl = Constants.BASE_URL + more;
        }

        if (!isLoadeMore) {
            //原来的加载
            //获取顶部轮播图数据集合
            topnews = tableDetailPagerBean.getData().getTopnews();
            //设置ViewPager数据
            vp_newsdetails.setAdapter(new NewsTopViewPagerAdapter());
            //添加小红点
            addPoint();
            //设置小红点移动和文本变化
            setPointData();

            //准备listview数据
            news = tableDetailPagerBean.getData().getNews();
            //设置listView的数据
            adapter = new NewsListViewAdapter();
            lv_newsdetails.setAdapter(adapter);

        } else {
            //加载更多
            isLoadeMore = false;
            news.addAll(tableDetailPagerBean.getData().getNews());

            //刷新适配器
            adapter.notifyDataSetChanged();
        }


        //---------------------------------设置轮播图4000切换
        if (handler == null) {
            handler = new MyHandler();
        }

        //移除上次消息
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(1, 4000);
        Log.e("TAG", "发送延迟消息");
    }

    //监听页面变化
    private void setPointData() {
        vp_newsdetails.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //设置文本
                tv_title.setText(topnews.get(position).getTitle());
                //红点高亮
                //从线性布局取红点
                ImageView lastIv = (ImageView) ll_newsdetails.getChildAt(lastPosition);
                lastIv.setEnabled(false);

                ImageView iv = (ImageView) ll_newsdetails.getChildAt(position);
                iv.setEnabled(true);

                lastPosition = position;
            }

            private boolean isDragging = false;

            @Override
            public void onPageScrollStateChanged(int state) {
              /*  switch (state) {
                    //拖拽
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        handler.removeCallbacksAndMessages(null);
                        break;
                    //空闲
                    case ViewPager.SCROLL_STATE_IDLE:
                        handler.removeCallbacksAndMessages(null);
                        handler.sendEmptyMessageDelayed(1, 4000);
                        break;
                    //惯性
                    case ViewPager.SCROLL_STATE_SETTLING:
                        handler.removeCallbacksAndMessages(null);
                        handler.sendEmptyMessageDelayed(1, 4000);
                        break;
                }*/
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    isDragging = true;
                    handler.removeCallbacksAndMessages(null);
                }
                if (state == ViewPager.SCROLL_STATE_SETTLING && isDragging) {
                    isDragging = false;
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(1, 4000);
                }
                if (state == ViewPager.SCROLL_STATE_IDLE && isDragging) {
                    isDragging = false;
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(1, 4000);
                }
            }
        });

        //默认显示第一条顶部新闻
        tv_title.setText(topnews.get(0).getTitle());
    }

    //添加小红点放发
    private void addPoint() {
        //-------必须移除小红点先
        ll_newsdetails.removeAllViews();
        //添加小点
        for (int i = 0; i < topnews.size(); i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.drawable.point_selector);

            //设置点和点之间间距
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px
                    (mContext, 5), DensityUtil.dip2px(mContext, 5));
            params.rightMargin = 8;
            iv.setLayoutParams(params);

            //默认选中首个红点
            if (i == 0) {
                iv.setEnabled(true);
            } else {
                iv.setEnabled(false);
            }

            ll_newsdetails.addView(iv);
        }
    }

    private TableDetailPagerBean parsedJson(String saveJson) {
        return new Gson().fromJson(saveJson, TableDetailPagerBean.class);
    }

    //加载更多联网请求
    public void getMoreDataFromNet() {
        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                isLoadeMore = true;
                //解析数据
                processData(result);
                lv_newsdetails.setOnRefreshFinish(false);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "请求失败" + ex);
                lv_newsdetails.setOnRefreshFinish(false);

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

    //ListView的适配器
    class NewsListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return news == null ? 0 : news.size();
        }

        @Override
        public Object getItem(int position) {
            return news.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_newslistview, null);

                holder.iv_newslist = (ImageView) convertView.findViewById(R.id.iv_newslist);
                holder.tv_newslist_title = (TextView) convertView.findViewById(R.id
                        .tv_newslist_title);
                holder.tv_newslist_time = (TextView) convertView.findViewById(R.id
                        .tv_newslist_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到数据
            TableDetailPagerBean.DataBean.NewsBean newsBean = news.get(position);
            String url = Constants.BASE_URL + newsBean.getListimage();
            //联网请求图片
            x.image().bind(holder.iv_newslist, url);
            holder.tv_newslist_title.setText(newsBean.getTitle());
            holder.tv_newslist_time.setText(newsBean.getPubdate());

            //从sp中取出字符串
            String readArrayId = CacheUtils.getString(mContext, READ_ARRAY_ID);
            if (readArrayId.contains(newsBean.getId() + "")) {
                //变灰色
                holder.tv_newslist_title.setTextColor(Color.GRAY);
                holder.tv_newslist_time.setTextColor(Color.GRAY);
            } else {
                holder.tv_newslist_title.setTextColor(Color.BLACK);
                holder.tv_newslist_time.setTextColor(Color.BLACK);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_newslist;
        TextView tv_newslist_title;
        TextView tv_newslist_time;
    }

    //ViewAdapter适配器
    class NewsTopViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return topnews == null ? 0 : topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = new ImageView(mContext);
            iv.setBackgroundResource(R.drawable.home_scroll_default);
            container.addView(iv);
            //联网请求图片
            x.image().bind(iv, Constants.BASE_URL + topnews.get(position).getTopimage());

            //iv的触摸事件(发送延迟消息)
            iv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            handler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP:
                            handler.removeCallbacksAndMessages(null);
                            handler.sendEmptyMessageDelayed(1, 4000);
                            break;
                    }
                    return true;
                }
            });
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                vp_newsdetails.setCurrentItem((vp_newsdetails.getCurrentItem() + 1) % topnews.size());
                handler.sendEmptyMessageDelayed(1, 4000);
            }
        }
    }
}