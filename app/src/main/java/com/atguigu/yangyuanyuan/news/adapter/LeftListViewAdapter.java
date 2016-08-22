package com.atguigu.yangyuanyuan.news.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.atguigu.yangyuanyuan.news.R;
import com.atguigu.yangyuanyuan.news.domain.NewsCenterPagerBean;

import java.util.List;


/**
 * Created by 杨媛媛 on 2016/8/15 21:47.
 */
public class LeftListViewAdapter extends BaseAdapter {
    private List<NewsCenterPagerBean.DataBean> mLeftMenuData;
    private Context mContext;
    private int mLastPosition;

    public LeftListViewAdapter(Context context, List<NewsCenterPagerBean.DataBean> leftMenuData) {
        this.mContext = context;
        this.mLeftMenuData = leftMenuData;

    }

    public void setmLastPosition(int mLastPosition) {
        this.mLastPosition = mLastPosition;
    }

    @Override
    public int getCount() {
        return mLeftMenuData == null ? 0 : mLeftMenuData.size();
    }

    @Override
    public Object getItem(int position) {
        return mLeftMenuData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(mContext, R.layout.left_menu_item, null);
        //设置内容
        textView.setText(mLeftMenuData.get(position).getTitle());
        if (position == mLastPosition) {
            textView.setEnabled(true);
        } else {
            textView.setEnabled(false);
        }

        return textView;
    }


}
