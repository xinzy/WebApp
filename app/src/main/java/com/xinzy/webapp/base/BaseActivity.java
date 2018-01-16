package com.xinzy.webapp.base;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.xinzy.webapp.widget.ProgressDialog;

/**
 * Created by xinzy on 2017/10/31.
 *
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseContract.BaseView {

    private ProgressDialog mProgressDialog;

    @Override
    public boolean isActive() {
        return !isFinishing();
    }

    @Override
    public void showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    @Override
    public void closeLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
