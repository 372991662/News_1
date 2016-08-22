package com.example.administrator.refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/20.
 */
public class RefreshListView extends ListView {
    private LinearLayout headerView;

    private View ll_pull_down;
    private ImageView iv_red_arrow;
    private ProgressBar pb_loading;
    private TextView tv_status;
    private TextView tv_time;
    private Animation upAnimation;
    private Animation downAnimation;
    private int headerViewHeight;
    private float startY = 0;
    //顶部轮播图部分
    private View viewHeader;
    private boolean displayTopNews;
    //listview在Y轴的坐标
    private int listViewOnScreenY = -1;
    //接口
    private OnRefreshListener mOnRefreshListener;
    /**
     * 下拉刷新状态
     **/
    private static final int PULL_DOWN_REFRESH = 1;
    /**
     * 手松刷新状态
     **/
    private static final int RELEASE_REFRESH = 2;
    /**
     * 正在刷新状态
     **/
    private static final int REFRESHING = 3;
    //当前状态
    private int currentState = PULL_DOWN_REFRESH;
    private boolean onRefreshFinish;
    private String systemTime;
    //加载更多布局
    private View footView;
    //上拉加载控件高
    private int footViewHeight;
    //是否加载更多
    private boolean isLoadMore = false;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView(context);
        initAnimation();
        initFootView(context);
    }

    private void initFootView(Context context) {
        footView = View.inflate(context, R.layout.refresh_footer, null);
        footView.measure(0, 0);
        footViewHeight = footView.getMeasuredHeight();

        footView.setPadding(0, -footViewHeight, 0, 0);
        addFooterView(footView);

        //监听滑动到listView的最后一个可见的item
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //静止或者是惯性滚动，并且是最后一个可见的时候
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    //到最后一个可见
                    if (getLastVisiblePosition() == getAdapter().getCount() - 1 && !isLoadMore) {
                        //显示加载更多空间
                        footView.setPadding(20, 20, 20, 20);
                        //设置状态
                        isLoadMore = true;
                        //回调接口
                        if (mOnRefreshListener != null) {
                            mOnRefreshListener.onLoadMore();
                        }
                    }
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    //初始化动画
    private void initAnimation() {
        upAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);
    }

    private void initHeaderView(Context context) {
        headerView = (LinearLayout) View.inflate(context, R.layout.refresh_head, null);

        ll_pull_down = headerView.findViewById(R.id.ll_pull_down);
        iv_red_arrow = (ImageView) headerView.findViewById(R.id.iv_red_arrow);
        pb_loading = (ProgressBar) headerView.findViewById(R.id.pb_loading);
        tv_status = (TextView) headerView.findViewById(R.id.tv_status);
        tv_time = (TextView) headerView.findViewById(R.id.tv_time);


        headerView.measure(0, 0);//调用测量方法
        headerViewHeight = ll_pull_down.getMeasuredHeight();
        //隐藏控件
        ll_pull_down.setPadding(0, -headerViewHeight, 0, 0);

        //以头的方式添加到listView中
        addHeaderView(headerView);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录起始坐标
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == 0) {
                    startY = ev.getY();
                }
                //判断顶部轮播图是否完全显示
                boolean isDisplayTopNews = isDisplayTopNews();//完全显示就是下拉刷新
                if (!isDisplayTopNews) {
                    //上拉刷新
                    break;
                }

                //记录结束坐标
                float endY = ev.getY();
                //计算偏移量
                float distanceY = endY - startY;

                if (distanceY > 0) {//向下滑动
                    int paddingTop = (int) (-headerViewHeight + distanceY);

                    if (paddingTop < 0 && currentState != PULL_DOWN_REFRESH) {
                        //下拉刷新
                        currentState = PULL_DOWN_REFRESH;
                        //更新状态
                        refreshStatus();
                    } else if (paddingTop > 0 && currentState != RELEASE_REFRESH) {
                        //手势刷新
                        currentState = RELEASE_REFRESH;
                        //更新状态
                        refreshStatus();
                    }

                    ll_pull_down.setPadding(0, paddingTop, 0, 0);

                }
                break;
            case MotionEvent.ACTION_UP:
                startY = 0;
                if (currentState == PULL_DOWN_REFRESH) {
                    //View.setPadding(0,-控件高，0,0）；//完成隐藏
                    ll_pull_down.setPadding(0, -headerViewHeight, 0, 0);
                } else if (currentState == RELEASE_REFRESH) {

                    currentState = REFRESHING;

                    ll_pull_down.setPadding(0, 0, 0, 0);//完成显示
                    //状态要更新
                    refreshStatus();
                    //回调接口
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onPullDownlRefresh();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void refreshStatus() {
        switch (currentState) {
            //下拉刷新
            case PULL_DOWN_REFRESH:
                tv_status.setText("下拉刷新...");
                iv_red_arrow.startAnimation(downAnimation);
                break;
            //手松刷新
            case RELEASE_REFRESH:
                tv_status.setText("手松刷新...");
                iv_red_arrow.startAnimation(upAnimation);
                break;
            //正在刷新
            case REFRESHING:
                pb_loading.setVisibility(VISIBLE);
                iv_red_arrow.setVisibility(GONE);
                iv_red_arrow.clearAnimation();
                tv_status.setText("正在刷新...");
                break;
        }

    }

    //顶部轮播图部分
    public void addTopNews(View viewHeader) {
        this.viewHeader = viewHeader;
        if (viewHeader != null && headerView != null) {
            //添加顶部轮播图
            headerView.addView(viewHeader);

        }

    }

    //判断顶部轮播是否完全显示
    public boolean isDisplayTopNews() {

        if (viewHeader != null) {

            //得到lisiview在屏幕上的Y轴坐标
            int[] location = new int[2];
            if (listViewOnScreenY == -1) {
                this.getLocationOnScreen(location);
                listViewOnScreenY = location[1];
            }

            //得到顶部轮播图在Y轴坐标
            viewHeader.getLocationOnScreen(location);
            int topnewsViewOnScreenY = location[1];

            return listViewOnScreenY <= topnewsViewOnScreenY;
        } else {
            return true;
        }
    }

    public void setOnRefreshFinish(boolean success) {
        if (isLoadMore) {
            //加载更多
            isLoadMore = false;
            footView.setPadding(0, -footViewHeight, 0, 0);
        } else {
            //下拉刷新
            iv_red_arrow.clearAnimation();
            iv_red_arrow.setVisibility(VISIBLE);
            pb_loading.setVisibility(GONE);
            tv_status.setText("下拉刷新...");
            ll_pull_down.setPadding(0, -headerViewHeight, 0, 0);
            currentState = PULL_DOWN_REFRESH;
            if (success) {
                //更新时间
                tv_time.setText("更新时间" + getSystemTime());
            }
        }
    }

    //获取系统时间
    public String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }


    /**
     * 监听视图刷新监听者
     **/
    public interface OnRefreshListener {
        /**
         * 当下拉刷新的时候回调这个方法
         */
        void onPullDownlRefresh();

        //加载更多时回调这个方法
        void onLoadMore();

    }

    /**
     * 设置视图刷新的监听
     */
    public void setOnRefreshListener(OnRefreshListener l) {
        this.mOnRefreshListener = l;
    }

}
