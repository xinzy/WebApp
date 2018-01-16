package com.xinzy.webapp.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.xinzy.webapp.R;

/**
 * Created by xinzy on 2017/11/3.
 */

public class ProgressDialog extends Dialog {

    private ImageView mImageView;

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        setCanceledOnTouchOutside(false);

        mImageView = findViewById(R.id.progressImage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RotateAnimation animation  = new RotateAnimation(0f, -360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(Integer.MAX_VALUE);
        animation.setInterpolator(new LinearInterpolator());
        mImageView.startAnimation(animation);
    }
}
