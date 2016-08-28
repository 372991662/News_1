package com.atguigu.yangyuanyuan.news.menudatailspager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.atguigu.yangyuanyuan.news.R;
import com.atguigu.yangyuanyuan.news.activity.ShowImageActivity;
import com.atguigu.yangyuanyuan.news.base.MenuDetailBasePager;
import com.atguigu.yangyuanyuan.news.domain.NewsCenterPagerBean;
import com.atguigu.yangyuanyuan.news.domain.PhotosMenuDetailPagerBean;
import com.atguigu.yangyuanyuan.news.utils.CacheUtils;
import com.atguigu.yangyuanyuan.news.utils.Constants;
import com.atguigu.yangyuanyuan.news.volley.VolleyManager;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by 杨媛媛 on 2016/8/15 23:59.
 */
public class PhotosPager extends MenuDetailBasePager {
    private ListView listview;
    private GridView gridview;
    private String url;
    private NewsCenterPagerBean.DataBean dataBean;
    private List<PhotosMenuDetailPagerBean.DataEntity.NewsEntity> news;
    private boolean isListView = true;
    private ListViewAdapter adapter;


    public PhotosPager(Context context, NewsCenterPagerBean.DataBean dataBean) {
        super(context);
        this.dataBean = dataBean;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.photos_menudetail_pager, null);
        listview = (ListView) view.findViewById(R.id.listview);
        gridview = (GridView) view.findViewById(R.id.gridview);

        //设置listView的点击事件
        listview.setOnItemClickListener(new MyItemClickListener());
        return view;
    }

    class MyItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //得到图片的路径
            PhotosMenuDetailPagerBean.DataEntity.NewsEntity newsEntity = news.get(position);
            String imageUrl = Constants.BASE_URL + newsEntity.getLargeimage();

            //跳转到ShowImage界面
            Intent intent = new Intent(mContext, ShowImageActivity.class);
            intent.putExtra("url", imageUrl);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL + dataBean.getUrl();

        //创建队列
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //缓存数据
                CacheUtils.putString(mContext, url, s);

                //解析数据
                processData(s);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("TAG", "请求失败" + volleyError.getMessage());
            }
        }) {
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

        VolleyManager.getRequestQueue().add(request);
    }

    private void processData(String s) {
        if (!TextUtils.isEmpty(s)) {
            Gson gson = new Gson();
            PhotosMenuDetailPagerBean photosMenuDetailPagerBean = gson.fromJson(s, PhotosMenuDetailPagerBean.class);
            news = photosMenuDetailPagerBean.getData().getNews();

            //设置适配器
            listview.setAdapter(new ListViewAdapter());
        }
    }

    public void switchView(ImageButton ib_switch) {
        if (isListView) {
            isListView = false;
            gridview.setVisibility(View.VISIBLE);
            adapter = new ListViewAdapter();
            gridview.setAdapter(adapter);
            listview.setVisibility(View.GONE);
            ib_switch.setImageResource(R.drawable.icon_pic_list_type);
        } else {
            isListView = true;
            listview.setVisibility(View.VISIBLE);
            adapter = new ListViewAdapter();
            listview.setAdapter(adapter);
            gridview.setVisibility(View.GONE);
            ib_switch.setImageResource(R.drawable.icon_pic_grid_type);
        }
    }

    class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return news.size();
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

                convertView = View.inflate(mContext, R.layout.item_photos_menudetail_pager, null);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            PhotosMenuDetailPagerBean.DataEntity.NewsEntity newsEntity = news.get(position);
            holder.tv_title.setText(newsEntity.getTitle());

            String imageUrl = Constants.BASE_URL + newsEntity.getSmallimage();
            //使用Volley请求图片-设置图片了
            loaderImager(holder, imageUrl);
            return convertView;
        }

        private void loaderImager(final ViewHolder holder, String imageUrl) {
            ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    //如果出错，则说明都不显示（简单处理)
                    holder.iv_icon.setImageResource(R.drawable.home_scroll_default);
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (imageContainer != null) {
                        if (holder.iv_icon != null) {
                            if (imageContainer.getBitmap() != null) {
                                //设置图片
                                holder.iv_icon.setImageBitmap(imageContainer.getBitmap());
                            } else {
                                //设置默认图片
                                holder.iv_icon.setImageResource(R.drawable.home_scroll_default);
                            }
                        }
                    }
                }
            };
            VolleyManager.getImageLoader().get(imageUrl, imageListener);
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
    }
}
