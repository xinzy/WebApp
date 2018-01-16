package com.xinzy.webapp.base;

import android.support.annotation.NonNull;

/**
 * Created by xinzy on 2017/11/16.
 */

public interface BaseContract {

    interface BaseView {

        void showLoading();

        void closeLoading();

        void toast(@NonNull String msg);

        boolean isActive();
    }

    interface BasePresenter {

    }

    interface BaseModel {

        interface ApiCallback<T> {
            void onSuccess(T data);
            void onFailure(int code, String msg);
        }
    }
}
