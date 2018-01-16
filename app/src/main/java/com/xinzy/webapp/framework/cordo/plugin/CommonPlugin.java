package com.xinzy.webapp.framework.cordo.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.xinzy.webapp.framework.webview.WebViewFragment;
import com.xinzy.webapp.util.Utils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by xinzy on 2017/11/1.
 */

public class CommonPlugin extends CordovaPlugin {
    private static final String CONSOLE = "console";
    private static final String REFRESHABLE = "refreshable";
    private static final String LOADING = "loading";
    private static final String OPEN = "open";
    private static final String CLOSE = "close";
    private static final String DEVICE_INFO = "deviceInfo";
    private static final String OPEN_PAGE_FOR_RESULT = "openPageForResult";
    private static final String CLOSE_PAGE_FOR_RESULT = "closePageForResult";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (CONSOLE.equals(action)) {
            console(args.getString(0), args.getJSONArray(1));
            return true;
        } else if (REFRESHABLE.equals(action)) {
            refresh(args.getBoolean(0));
            return true;
        } else if (OPEN.equals(action)) {
            String title = args.length() > 1 ? args.getString(1) : "";
            open(args.getString(0), title);
            return true;
        } else if (CLOSE.equals(action)) {
            close();
            return true;
        } else if (LOADING.equals(action)) {
            loading(args.getBoolean(0));
            return true;
        } else if (DEVICE_INFO.equals(action)) {
            callbackContext.success(deviceInfo());
            return true;
        } else if (OPEN_PAGE_FOR_RESULT.equals(action)) {
            openPageForResult(args.getJSONObject(0));
            return true;
        } else if (CLOSE_PAGE_FOR_RESULT.equals(action)) {
            closePageForResult(args.length() == 0 ? null : args.getJSONObject(0));
            return true;
        }
        return false;
    }

    private void open(final String url, final String title) {
        cordova.getActivity().runOnUiThread(() -> {
            if (!TextUtils.isEmpty(url)) {
                new WebViewFragment.Builder().url(url).title(title).start(cordova.getActivity());
            }
        });
    }

    private void close() {
        cordova.getActivity().runOnUiThread(() -> cordova.getActivity().finish());
    }

    private void refresh(final boolean enable) {
        cordova.getActivity().runOnUiThread(() -> webView.setRefreshable(enable));
    }

    private void loading(final boolean enable) {
        cordova.getActivity().runOnUiThread(() -> webView.switchProgressDialog(enable));
    }

    private void console(String level, JSONArray array) {
        String tag = "Common";
        String message = "";
        final int length = array.length();
        if (length >= 2) {
            tag = array.optString(0);
            message = array.optString(1);
        } else if (length == 1) {
            message = array.optString(0);
        }
        switch (level) {
            case "info":
                Log.i(tag, message);
                break;
            case "debug":
                Log.d(tag, message);
                break;
            case "warn":
                Log.w(tag, message);
                break;
            case "error":
                Log.e(tag, message);
                break;
        }
    }

    private JSONObject deviceInfo() throws JSONException {
        Context context = cordova.getActivity();
        JSONObject json = new JSONObject();
        json.put("deviceId", Utils.getDeviceId(context));
        json.put("ip", Utils.getIpAddress(context));
        json.put("appVersion", Utils.getVersionName(context));

        return json;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 以下是实验性协议
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * cordova.exec(null, null, "Common", "openPageForResult", [{"requestCode": 1, "url":"http://10.107.70.41/cordova/redirect/second.html"}]);
     * @param json
     * @throws JSONException
     */
    private void openPageForResult(JSONObject json) throws JSONException {
        final int requestCode = json.getInt("requestCode");
        final String url = json.getString("url");
        cordova.getActivity().runOnUiThread(() -> {
            Intent intent = new WebViewFragment.Builder().url(url).intent();
            cordova.startActivityForResult(null, intent, requestCode);
        });
    }

    /**
     * cordova.exec(null, null, "Common", "closePageForResult", [{"success": "true"}]);
     * @param json
     * @throws JSONException
     */
    private void closePageForResult(JSONObject json) throws JSONException {
        final Bundle data = new Bundle();
        if (json != null) {
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                data.putString(key, json.getString(key));
            }
        }
        cordova.getActivity().runOnUiThread(() -> cordova.onMessage("closePageForResult", data));
    }
}
