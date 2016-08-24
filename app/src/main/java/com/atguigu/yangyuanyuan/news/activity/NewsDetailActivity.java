package com.atguigu.yangyuanyuan.news.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private int temp = 2;
    private int textSize = temp;
    private WebSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initView();
        initListener();

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        settings = webView.getSettings();
        //设置支持javascript
        settings.setJavaScriptEnabled(true);
        //设置双击变大
        settings.setUseWideViewPort(true);
        //设置增大减小按钮
        settings.setBuiltInZoomControls(true);
        //设置字体大小
        settings.setTextZoom(100);
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
            showChangTextSizeDialog();
        }
        if (v == ib_share) {
            Toast.makeText(NewsDetailActivity.this, "分享链接", Toast.LENGTH_SHORT).show();

        }
    }


    private void showChangTextSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置文字大小");
        String[] items = new String[]{"超大字体", "大字体", "正常", "小字体", "超小字体"};
        builder.setSingleChoiceItems(items, textSize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                temp = which;
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textSize = temp;
                //改变字体大小
                changeTextSize();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void changeTextSize() {
        switch (textSize) {
            case 0:
                settings.setTextZoom(200);
                break;
            case 1:
                settings.setTextZoom(150);
                break;
            case 2:
                settings.setTextZoom(100);
                break;
            case 3:
                settings.setTextZoom(75);
                break;
            case 4:
                settings.setTextZoom(50);
                break;
            case 5:
                settings.setTextZoom(25);
                break;
        }

    }
}
