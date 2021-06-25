package com.example.moviebrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    String url;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        initComps();
    }

    private void initComps() {
        webView = findViewById(R.id.webView);
        Intent i = getIntent();
        String tmdb_id = i.getStringExtra("tmdb_id");

        url = "https://www.themoviedb.org/movie/" + tmdb_id;

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT < 18) {
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        }

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }
        else{ super.onBackPressed(); }
    }
}