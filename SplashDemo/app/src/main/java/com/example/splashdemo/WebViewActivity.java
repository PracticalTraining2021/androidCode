package com.example.splashdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

/**
 * 仅用于测试用的Activity类
 */
public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private String gameId = null;
    private String url = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        //从intent里找有没有传gameId
        url = getIntent().getExtras().getString("url", null);
        webView = findViewById(R.id.tempWebView);
        WebUtil webUtil = new WebUtil(webView, getApplicationContext());
        webUtil.webViewSetting(url, 1);
    }
}