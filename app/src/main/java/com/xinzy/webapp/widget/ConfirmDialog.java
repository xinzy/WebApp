package com.xinzy.webapp.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xinzy.webapp.R;

/**
 * Created by xinzy on 2017/11/2.
 */

public class ConfirmDialog extends Dialog implements View.OnClickListener {
    public static final int BUTTON_OK = 1;
    public static final int BUTTON_CANCEL = 0;

    private String title;
    private String message;
    private String cancel;
    private String ok;

    private boolean cancelable;

    private OnButtonClickListener mClickListener;

    private ConfirmDialog(@NonNull Context context, String title, String content, String ok, String cancel, boolean cancelable, OnButtonClickListener l) {
        super(context);

        this.title = title;
        this.message = content;
        this.ok = ok;
        this.cancel = cancel;
        this.cancelable = cancelable;
        this.mClickListener = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);

        final Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(0));
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.72);
            window.setAttributes(lp);
        }
        setCanceledOnTouchOutside(false);
        if (!cancelable) {
            setCancelable(false);
        }

        TextView titleText = findViewById(R.id.dialogTitle);
        TextView contentText = findViewById(R.id.dialogContent);
        TextView okText = findViewById(R.id.dialogOk);
        TextView cancelText = findViewById(R.id.dialogCancel);

        if (!TextUtils.isEmpty(title)) {
            titleText.setText(title);
        } else {
            titleText.setVisibility(View.GONE);
        }
        contentText.setText(message);
        okText.setText(ok);
        if (TextUtils.isEmpty(cancel)) {
            cancelText.setVisibility(View.GONE);
            findViewById(R.id.dialogDivide).setVisibility(View.GONE);
        } else {
            cancelText.setText(cancel);
        }

        okText.setOnClickListener(this);
        cancelText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.dialogOk) {
            dismiss();
            if (mClickListener != null) mClickListener.onClick(this, BUTTON_OK);
        } else if (id == R.id.dialogCancel) {
            dismiss();
            if (mClickListener != null) mClickListener.onClick(this, BUTTON_CANCEL);
        }
    }

    public interface OnButtonClickListener {
        void onClick(ConfirmDialog dialog, @Type int which);

        @IntDef(value={BUTTON_OK, BUTTON_CANCEL})
        @interface Type {}
    }

    public static class Builder {

        private String title;
        private String message;
        private String cancel;
        private String ok;
        private boolean cancelable = true;
        private OnButtonClickListener listener;

        private Context mContext;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder cancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder ok(String ok) {
            this.ok = ok;
            return this;
        }

        public Builder cancelable(boolean b) {
            this.cancelable = b;
            return this;
        }

        public Builder listener(OnButtonClickListener listener) {
            this.listener = listener;
            return this;
        }

        public ConfirmDialog create() {
            if (TextUtils.isEmpty(title)) {
                title = "";
            }
            if (TextUtils.isEmpty(message)) {
                message = "";
            }
            if (TextUtils.isEmpty(ok)) {
                ok = "确认";
            }

            return new ConfirmDialog(mContext, title, message, ok, cancel, cancelable, listener);
        }
    }
}
