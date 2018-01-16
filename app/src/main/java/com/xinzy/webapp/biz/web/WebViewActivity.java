package com.xinzy.webapp.biz.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xinzy.webapp.base.BaseActivity;
import com.xinzy.webapp.framework.webview.WebViewFragment;

public class WebViewActivity extends BaseActivity {
    private static final String TAG = "WebViewActivity";

    public static void start(Context context, Bundle data) {
        Intent starter = new Intent(context, WebViewActivity.class);
        if (data != null) {
            starter.putExtras(data);
        }
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebViewFragment fragment = WebViewFragment.newInstance(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment, TAG).commit();
    }
}
