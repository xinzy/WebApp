package com.xinzy.webapp.biz.splash;

import android.os.Bundle;

import com.xinzy.webapp.base.BaseActivity;
import com.xinzy.webapp.biz.web.WebViewActivity;
import com.xinzy.webapp.framework.webview.WebViewFragment;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = new WebViewFragment.Builder().hideBackButton(true).bundle();
        WebViewActivity.start(this, data);
        finish();
    }
}
