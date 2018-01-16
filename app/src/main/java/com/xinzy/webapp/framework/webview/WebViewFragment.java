package com.xinzy.webapp.framework.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.vip.cordova.callback.ClientCallback;
import com.xinzy.webapp.BuildConfig;
import com.xinzy.webapp.R;
import com.xinzy.webapp.base.BaseFragment;
import com.xinzy.webapp.widget.ConfirmDialog;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginEntry;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by xinzy on 2017/11/1.
 */

public class WebViewFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        ClientCallback {
    private static final String TAG = "WebViewFragment";
    private static final boolean OVERRIDE_JS_DIALOG = true;

    public static final int REQUEST_CODE_UPLOAD = 0xFFFF;

    public static final String KEY_URL = "URL";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_HIDE_BACK = "HIDE_BACK";
    public static final String KEY_ALWAYS_CLEAR_HISTORY = "ALWAYS_CLEAR_HISTORY";

    private final int mWebViewId = View.generateViewId();

    private ImageView mBackImageView;
    private ImageView mIconImageView;
    private TextView mTitleText;
    private ProgressBar mProgressBar;
    private ViewFlipper mViewFlipper;
    private SwipeRefreshLayout mRefreshLayout;

    protected CordovaWebView mAppView;
    protected CordovaPreferences preferences;
    protected ArrayList<PluginEntry> pluginEntries;
    protected CordovaInterfaceImpl cordovaInterface;
    protected String launchUrl;

    protected boolean keepRunning = true;

    private Activity mActivity;
    private String mTitle;
    private boolean mHideBack;
    private boolean mAlwaysClearHistory;
    private WebHandler mHandler = new WebHandler();

    private boolean mLoadUrlFailure;

    public static WebViewFragment newInstance(Bundle args) {
        WebViewFragment fragment = new WebViewFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = (Activity) context;
        mHandler.setActivity(mActivity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            launchUrl = bundle.getString(KEY_URL, "");
            mTitle = bundle.getString(KEY_TITLE, "");
            mHideBack = bundle.getBoolean(KEY_HIDE_BACK, false);
            mAlwaysClearHistory = bundle.getBoolean(KEY_ALWAYS_CLEAR_HISTORY, false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        loadConfig();
        super.onCreate(savedInstanceState);

        cordovaInterface = new WebCordovaInterfaceImpl(mActivity);
        if (savedInstanceState != null) {
            cordovaInterface.restoreInstanceState(savedInstanceState);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mBackImageView = view.findViewById(R.id.backImg);
        mIconImageView = view.findViewById(R.id.iconImg);
        mTitleText = view.findViewById(R.id.titleText);
        mProgressBar = view.findViewById(R.id.progressBar);
        mViewFlipper = view.findViewById(R.id.viewFlipper);
        mRefreshLayout = view.findViewById(R.id.webViewContainer);

        mRefreshLayout.setOnRefreshListener(this);
        mBackImageView.setOnClickListener(this);
        mTitleText.setOnClickListener(this);
        view.findViewById(R.id.errorContent).setOnClickListener(this);

        mBackImageView.setVisibility(mHideBack ? View.GONE : View.VISIBLE);
        if (!TextUtils.isEmpty(mTitle)) {
            mTitleText.setText(mTitle);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!TextUtils.isEmpty(launchUrl)) {
            loadUrl(launchUrl);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAppView != null) {
            mAppView.handleStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAppView != null) {
            mAppView.handleResume(keepRunning);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAppView != null) {
            keepRunning = keepRunning || this.cordovaInterface.activityResultCallback != null;
            mAppView.handlePause(keepRunning);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAppView != null) {
            mAppView.handleStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAppView != null) {
            mAppView.handleDestroy();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cordovaInterface.onActivityResult(requestCode, resultCode, data);

        if (requestCode < REQUEST_CODE_UPLOAD) {
            JSONObject json = new JSONObject();
            if (data != null && data.getExtras() != null) {
                final Bundle bundle = data.getExtras();
                for (String key : bundle.keySet()) {
                    try {
                        json.put(key, bundle.getString(key));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            String jsCommand = String.format(Locale.getDefault(), "javascript:onPageResult(%1$d, %2$d, %3$s);",
                    requestCode, resultCode, json.toString());
            mAppView.loadUrl(jsCommand);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        cordovaInterface.setActivityResultRequestCode(requestCode);
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        cordovaInterface.onSaveInstanceState(outState);
    }

    private void loadConfig() {
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(mActivity);
        preferences = parser.getPreferences();
        preferences.setPreferencesBundle(getArguments());
        pluginEntries = parser.getPluginEntries();

        if (TextUtils.isEmpty(launchUrl)) {
            launchUrl = parser.getLaunchUrl();
        }
        String logLevel = preferences.getString("loglevel", "ERROR");
        LOG.setLogLevel(logLevel);
    }

    public void loadUrl(String url) {
        mLoadUrlFailure = false;
        if (mAppView == null) {
            initAppView();
        }
        this.keepRunning = preferences.getBoolean("KeepRunning", true);
        mAppView.loadUrlIntoView(url, true);
    }

    private void initAppView() {
        mAppView = new CordovaWebViewImpl(CordovaWebViewImpl.createEngine(mActivity, preferences), this);
        mAppView.getView().setId(mWebViewId);
        mAppView.getView().setLayoutParams(new SwipeRefreshLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mRefreshLayout.addView(mAppView.getView());
        mAppView.getView().requestFocusFromTouch();

        if (!mAppView.isInitialized()) {
            mAppView.init(cordovaInterface, pluginEntries, preferences);
        }
        cordovaInterface.onCordovaInit(mAppView.getPluginManager());

        String volumePref = preferences.getString("DefaultVolumeStream", "");
        if ("media".equals(volumePref.toLowerCase(Locale.ENGLISH))) {
            mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    private void goBack() {
        mHandler.sendEmptyMessage(WebHandler.KEY_BACK_DOWN);
        mHandler.sendEmptyMessageDelayed(WebHandler.KEY_BACK_UP, 50);
    }

    public void reload() {
        mLoadUrlFailure = false;
        if (mAppView != null) mAppView.reload();
    }

    public void clearHistory() {
        if (mAppView != null) mAppView.clearHistory();
    }

    @Override
    public void onRefresh() {
        reload();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImg:
                goBack();
                break;
            case R.id.titleText:
                break;
            case R.id.errorContent:
                reload();
                break;
        }
    }

    @Override
    public void onPageStart(String url) {
        mRefreshLayout.setEnabled(true);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);
    }

    @Override
    public void onPageFinished(String url) {
        mProgressBar.setVisibility(View.GONE);
        if (mRefreshLayout.isRefreshing()) mRefreshLayout.setRefreshing(false);
        if (!mLoadUrlFailure && mViewFlipper.getDisplayedChild() != 0) mViewFlipper.setDisplayedChild(0);
        if (mAlwaysClearHistory) mAppView.clearHistory();
    }

    @Override
    public void onProgressChanged(String url, int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onReceivedTitle(String url, String title) {
        if (!TextUtils.isEmpty(title) && !title.startsWith("http")) mTitleText.setText(title);
    }

    @Override
    public void onReceivedFavicon(Bitmap icon) {
        if (icon != null) mIconImageView.setImageBitmap(icon);
    }

    @Override
    public void onReceivedError(String description, String url) {
        mLoadUrlFailure = true;
        mViewFlipper.setDisplayedChild(1);
    }

    @Override
    public void setRefreshable(boolean enable) {
        mRefreshLayout.setEnabled(enable);
    }

    @Override
    public void switchProgressDialog(boolean isShown) {
        if (isShown) {
            showLoading();
        } else {
            closeLoading();
        }
    }

    @Override
    public boolean onJsAlert(String message, final JsResult result) {
        if (!OVERRIDE_JS_DIALOG) return false;
        new ConfirmDialog.Builder(mActivity).message(message).ok(getString(R.string.ok)).cancelable(false)
                .listener((dialog, which) -> result.confirm()).create().show();
        return true;
    }

    @Override
    public boolean onJsConfirm(String message, final JsResult result) {
        if (!OVERRIDE_JS_DIALOG) return false;
        new ConfirmDialog.Builder(mActivity).message(message).ok(getString(R.string.ok)).cancel(getString(R.string.cancel))
                .cancelable(false).listener((dialog, which) -> {
                    if (which == ConfirmDialog.BUTTON_OK) {
                        result.confirm();
                    } else {
                        result.cancel();
                    }
                }).create().show();
        return true;
    }

    private static class WebHandler extends Handler {
        private static final int KEY_BACK_DOWN = 1;
        private static final int KEY_BACK_UP = 2;
        private WeakReference<Activity> activityReference;

        void setActivity(Activity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case KEY_BACK_DOWN:
                    if (activityReference != null && activityReference.get() != null) {
                        activityReference.get().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                    }
                    break;
                case KEY_BACK_UP:
                    if (activityReference != null && activityReference.get() != null) {
                        activityReference.get().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                    }
                    break;
            }
        }
    }

    private class WebCordovaInterfaceImpl extends CordovaInterfaceImpl {
        WebCordovaInterfaceImpl(Activity activity) {
            super(activity);
        }

        @Override
        public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {
            setActivityResultCallback(command);
            try {
                WebViewFragment.this.startActivityForResult(intent, requestCode);
            } catch (Throwable e) {
                activityResultCallback = null;
            }
        }

        @Override
        public Object onMessage(String id, Object data) {
            switch (id) {
                case "closePageForResult":
                    Intent intent = new Intent();
                    if (data instanceof Bundle) {
                        Bundle bundle = (Bundle) data;
                        intent.putExtras(bundle);
                    }
                    mActivity.setResult(Activity.RESULT_OK, intent);
                    mActivity.finish();
                    return null;
            }

            return super.onMessage(id, data);
        }
    }

    public static class Builder {
        private String url;
        private String title;
        private boolean hideBackButton;
        private boolean alwaysClearHistory;

        public Builder url(String url) {
            this.url = url;
            return this;
        }
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        public Builder hideBackButton(boolean hideBackButton) {
            this.hideBackButton = hideBackButton;
            return this;
        }
        public Builder alwaysClearHistory(boolean alwaysClearHistory) {
            this.alwaysClearHistory = alwaysClearHistory;
            return this;
        }

        public Bundle bundle() {
            Bundle data = new Bundle();
            if (!TextUtils.isEmpty(url)) {
                data.putString(KEY_URL, url);
            }
            if (!TextUtils.isEmpty(title)) {
                data.putString(KEY_TITLE, title);
            }
            if (hideBackButton) {
                data.putBoolean(KEY_HIDE_BACK, true);
            }
            if (alwaysClearHistory) {
                data.putBoolean(KEY_ALWAYS_CLEAR_HISTORY, true);
            }
            return data;
        }

        public Intent intent() {
            Intent intent = new Intent(BuildConfig.ACTION_BROWSER);
            intent.putExtras(bundle());
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            return intent;
        }

        public void start(Context context) {
            Intent intent = intent();
            context.startActivity(intent);
        }
    }
}
