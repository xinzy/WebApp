package com.xinzy.webapp.base;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.xinzy.webapp.widget.ProgressDialog;

/**
 * Created by xinzy on 2017/11/1.
 */

public abstract class BaseFragment extends Fragment implements BaseContract.BaseView {

    private ProgressDialog mProgressDialog;

    @Override
    public void showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
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
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void toast(@NonNull String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }
}
