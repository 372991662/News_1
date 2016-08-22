package com.example.administrator.refreshlistview_sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.refreshlistview.RefreshListView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int PULL_DOWN = 1;
    private static final int LOAD_MORE = 2;
    private RefreshListView refreshListView;
    private ArrayList<String> datas;
    private MyAdapter adapter = new MyAdapter();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PULL_DOWN:
                    refreshListView.setOnRefreshFinish(true);
                    adapter = new MyAdapter();
                    refreshListView.setAdapter(adapter);
                    break;
                case LOAD_MORE:
                    refreshListView.setOnRefreshFinish(false);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshListView = (RefreshListView) findViewById(R.id.refreshListView);
        datas = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            datas.add(" data _" + i);
        }
        refreshListView.setAdapter(adapter);

        //设置刷新的监听
        refreshListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onPullDownlRefresh() {
                getDataFromNet();
            }

            @Override
            public void onLoadMore() {
                getMoreDataFromNet();
            }
        });
    }

    private void getMoreDataFromNet() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                SystemClock.sleep(2000);

                for (int i = 0; i < 50; i++) {
                    datas.add(" data _new more" + i);
                }

                handler.sendEmptyMessage(LOAD_MORE);
            }
        }.start();
    }

    private void getDataFromNet() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                SystemClock.sleep(2000);

                datas = new ArrayList<>();
                for (int i = 0; i < 50; i++) {
                    datas.add(" data _new" + i);
                }
                handler.sendEmptyMessage(PULL_DOWN);
            }
        }.start();
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView text = new TextView(MainActivity.this);
            text.setTextSize(25);
            text.setTextColor(Color.BLACK);
            text.setText(datas.get(position));
            return text;
        }
    }

}
