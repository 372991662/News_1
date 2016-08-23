package com.atguigu.yangyuanyuan.news.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.yangyuanyuan.news.R;

public class NewsDetailActivity extends Activity implements View.OnClickListener {
    private TextView tv_basepager_title;
    private ImageButton ib_basepager_btn;
    private ImageButton ib_back;
    private ImageButton ib_textsize;
    private ImageButton ib_share;
    private WebView webView;
    private ProgressBar pb_loading;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initView();
        initListener();

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        WebSettings settings = webView.getSettings();
        //设置支持javascript
        settings.setJavaScriptEnabled(true);
        //设置页面加载页面结束监听
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pb_loading.setVisibility(View.GONE);

            }
        });
        webView.loadUrl(url);

    }


    private void initView() {
        tv_basepager_title = (TextView) findViewById(R.id.tv_basepager_title);
        ib_basepager_btn = (ImageButton) findViewById(R.id.ib_basepager_btn);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_textsize = (ImageButton) findViewById(R.id.ib_textsize);
        ib_share = (ImageButton) findViewById(R.id.ib_share);
        webView = (WebView) findViewById(R.id.webview);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);

        ib_back.setVisibility(View.VISIBLE);
        ib_textsize.setVisibility(View.VISIBLE);
        ib_share.setVisibility(View.VISIBLE);
    }

    private void initListener() {
        ib_back.setOnClickListener(this);
        ib_textsize.setOnClickListener(this);
        ib_share.setOnClickListener(this);
        tv_basepager_title.setVisibility(View.INVISIBLE);
    }

    public void onClick(View v) {
        if (v == ib_back) {
            finish();
        }
        if (v == ib_textsize) {
            Toast.makeText(NewsDetailActivity.this, "设置文字大小", Toast.LENGTH_SHORT).show();
        }
        if (v == ib_share) {
            Toast.makeText(NewsDetailActivity.this, "分享链接", Toast.LENGTH_SHORT).show();

        }
    }
}
