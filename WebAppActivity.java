package com.ysc.BookPreview0518_ysc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebAppActivity extends AppCompatActivity {


    private WebView webView;        // 액티비티에 붙인 WebView
    private String mUrl = "";       // URL 주소


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_app);

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new TestBrowser());
        //unityIntent() 메서드에서 보낸 URL 주소값을 가진 Intent
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("INPUT_URL");

        String url = mUrl;
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
    }


    private class TestBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }

}
