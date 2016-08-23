package com.atguigu.yangyuanyuan.news.menudatailspager.tabdetailpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.yangyuanyuan.news.R;
import com.atguigu.yangyuanyuan.news.base.MenuDetailBasePager;
import com.atguigu.yangyuanyuan.news.domain.NewsCenterPagerBean;
import com.atguigu.yangyuanyuan.news.domain.TableDetailPagerBean;
import com.atguigu.yangyuanyuan.news.utils.CacheUtils;
import com.atguigu.yangyuanyuan.news.utils.Constants;
import com.atguigu.yangyuanyuan.news.utils.DensityUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by 杨媛媛 on 2016/8/16 19:20.
 */

//页签详情页面
public class TopicTabDetailPager extends MenuDetailBasePager {
    private ViewPager vp_newsdetails;
    private TextView tv_title;
    private LinearLayout ll_newsdetails;
    private PullToRefreshListView pull_refresh_listview;

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
    private ListView listView;

    public TopicTabDetailPager(Context context, NewsCenterPagerBean.DataBean.ChildrenBean childrenBean) {
        super(context);
        this.mChilderBean = childrenBean;
    }


    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.topic_tabdetail_pager, null);
        pull_refresh_listview = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_listview);
        //从控件中得到listview
        listView = pull_refresh_listview.getRefreshableView();

        View viewHeader = View.inflate(mContext, R.layout.news_list_header, null);
        vp_newsdetails = (ViewPager) viewHeader.findViewById(R.id.vp_newsdetails);
        tv_title = (TextView) viewHeader.findViewById(R.id.tv_title);
        ll_newsdetails = (LinearLayout) viewHeader.findViewById(R.id.ll_newsdetails);


        /**
         * Add Sound Event Listener
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(mContext);
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        pull_refresh_listview.setOnPullEventListener(soundListener);

        //添加listview的头部
        listView.addHeaderView(viewHeader);

        //监听控件刷新
        pull_refresh_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getDataFromNet();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (TextUtils.isEmpty(moreUrl)) {
                    //没有加载更多
                    Toast.makeText(mContext, "没有更多数据", Toast.LENGTH_SHORT).show();
                    //隐藏
                    pull_refresh_listview.onRefreshComplete();
                } else {
                    //有加载更多
                    getMoreDataFromNet();
                }
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
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //缓存数据
                CacheUtils.putString(mContext, url, result);
                //解析数据
                processData(result);
                pull_refresh_listview.onRefreshComplete();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "请求失败" + ex);
                pull_refresh_listview.onRefreshComplete();


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
            listView.setAdapter(adapter);
        } else {
            //加载更多
            isLoadeMore = false;
            news.addAll(tableDetailPagerBean.getData().getNews());

            //刷新适配器
            adapter.notifyDataSetChanged();
        }


    }

    private void setPointData() {
        //监听页面变化
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

            @Override
            public void onPageScrollStateChanged(int state) {

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
                pull_refresh_listview.onRefreshComplete();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "请求失败" + ex);
                pull_refresh_listview.onRefreshComplete();


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
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}