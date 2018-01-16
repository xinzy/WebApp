package com.vip.cordova.callback;

import android.graphics.Bitmap;
import android.webkit.JsResult;

/**
 * Created by xinzy on 2017/11/1.
 */

public interface ClientCallback {

    void onPageStart(String url);

    void onPageFinished(String url);

    void onProgressChanged(String url, int progress);

    void onReceivedTitle(String url, String title);

    void onReceivedFavicon(Bitmap icon);

    void onReceivedError(String description, String url);

    void setRefreshable(boolean enable);

    void switchProgressDialog(boolean isShown);

    boolean onJsAlert(String message, JsResult result);

    boolean onJsConfirm(String message, JsResult result);
}
