package com.xinzy.webapp.framework.cordo.plugin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.xinzy.webapp.framework.webview.WebViewFragment;

import org.apache.cordova.CordovaPlugin;

import java.util.Set;

/**
 * Created by xinzy on 2017/12/15.
 */

public class LinkProtocolPlugin extends CordovaPlugin {
    private static final String OPEN = "open";
    private static final String CLOSE = "close";
    private static final String OPEN_PAGE_FOR_RESULT = "openPageForResult";
    private static final String CLOSE_PAGE_FOR_RESULT = "closePageForResult";

    @Override
    public boolean onOverrideUrlLoading(String url) {
        Uri uri = Uri.parse(url);
        String schema = uri.getScheme();
        if ("xinzy".equals(schema)) {
            String host = uri.getHost();
            String path = uri.getPath();

            if ("view".equals(host)) {
                if (TextUtils.isEmpty(path)) {
                    return false;
                } else {
                    int start = path.startsWith("/") ? 1 : 0;
                    int end = path.endsWith("/") ? path.length() - 1 : path.length();
                    return handleView(uri, path.substring(start, end));
                }
            }
        }

        return false;
    }

    private boolean handleView(Uri uri, String path) {
        switch (path) {
            case OPEN:
                String url = uri.getQueryParameter("url");
                open(url);
                return true;
            case CLOSE:
                close();
                return true;
            case OPEN_PAGE_FOR_RESULT:
                openPageForResult(uri);
                return true;
            case CLOSE_PAGE_FOR_RESULT:
                closePageForResult(uri);
                return true;
        }

        return false;
    }

    private void open(final String url) {
        cordova.getActivity().runOnUiThread(() -> {
            if (!TextUtils.isEmpty(url)) {
                new WebViewFragment.Builder().url(url).start(cordova.getActivity());
            }
        });
    }

    private void close() {
        cordova.getActivity().runOnUiThread(() -> cordova.getActivity().finish());
    }

    private void openPageForResult(Uri uri) {
        String code = uri.getQueryParameter("requestCode");
        if (TextUtils.isEmpty(code) || !TextUtils.isDigitsOnly(code)) return ;
        final int requestCode = Integer.parseInt(code);
        final String url = uri.getQueryParameter("url");
        cordova.getActivity().runOnUiThread(() -> {
            Intent intent = new WebViewFragment.Builder().url(url).intent();
            cordova.startActivityForResult(null, intent, requestCode);
        });
    }

    private void closePageForResult(Uri uri) {
        final Bundle data = new Bundle();
        Set<String> paramNames = uri.getQueryParameterNames();
        if (paramNames != null && paramNames.size() > 0) {
            for (String param : paramNames) {
                data.putString(param, uri.getQueryParameter(param));
            }
        }
        cordova.getActivity().runOnUiThread(() -> cordova.onMessage("closePageForResult", data));
    }
}
