package us.xingkong.study.ui.activity.web;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.xingkong.study.R;
import us.xingkong.study.R2;

import static us.xingkong.study.utils.Utils.log;

public class WebActivity extends AppCompatActivity {
    @BindView(R2.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        initView();
    }

    @JavascriptInterface
    public void change(String result) {
        if (result != null && result.contains("rgb(")) {
            log(result);
            String[] rgb = result.replace("\"", "").replace("rgb(", "").replace(")", "").split(", ");
            System.out.print(Arrays.toString(rgb));
            int color = Color.argb(255, Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim()));
            runOnUiThread(() -> {
                getWindow().setStatusBarColor(color);
                getWindow().setNavigationBarColor(color);
            });
        } else {
            log("result");
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        webView.addJavascriptInterface(this, "app");
        webView.setWebChromeClient(new WebChromeClient() {

        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                log("run js.");
                view.evaluateJavascript("(function () {" +
                        "if(window.location.href.match('http://sc-lapp01.gcu.edu.cn/php/educate/table')){var tmp;" +
                        "if(tmp = document.getElementsByClassName('mdui-card-content')[0])" +
                        "tmp.innerText = '登录状态已过期';" +
                        "if(tmp = document.getElementsByClassName('mdui-btn mdui-ripple mdui-btn-block mdui-m-l-1 mdui-m-r-1')[0])" +
                        "{tmp.innerText = '该功能来自智慧校园';tmp.removeAttribute('href');}" +
                        "if(tmp = document.getElementsByClassName('mdui-btn mdui-btn-icon')[1])" +
                        "tmp.onclick = function(){change_style();var result = window.getComputedStyle(document.getElementsByClassName('mdui-toolbar mdui-color-theme')[0])['backgroundColor'];app.change(result);};" +
                        "var result = window.getComputedStyle(document.getElementsByClassName('mdui-toolbar mdui-color-theme')[0])['backgroundColor'];" +
                        "if(result) return result;" +
                        "}})();", result -> change(result));
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        webView.loadUrl(getIntent().getStringExtra("url"));
    }
}
