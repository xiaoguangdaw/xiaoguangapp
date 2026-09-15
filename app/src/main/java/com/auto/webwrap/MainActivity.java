package com.auto.webwrap;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 唯一界面：全屏 WebView，加载 assets/web/index.html
 *
 * 本地文件放在：app/src/main/assets/web/ 下
 * 入口文件名必须是 index.html
 */
public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);          // 允许网页里的 JS
        s.setDomStorageEnabled(true);          // localStorage
        s.setAllowFileAccess(true);            // 读本地文件
        s.setAllowContentAccess(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportZoom(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);

        // 让网页内部跳转仍留在 App 里，不弹外部浏览器
        webView.setWebViewClient(new WebViewClient());

        // ★ 加载你打包进去的入口页
        webView.loadUrl("file:///android_asset/web/index.html");
    }

    // 按返回键先让网页返回历史，没有历史才退出 App
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}