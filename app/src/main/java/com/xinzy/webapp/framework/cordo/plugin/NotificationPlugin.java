package com.xinzy.webapp.framework.cordo.plugin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.xinzy.webapp.widget.ConfirmDialog;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by xinzy on 2017/11/2.
 */

public class NotificationPlugin extends CordovaPlugin {

    private static final String BEEP = "beep";
    private static final String VIBRATOR = "vibrator";
    private static final String ALERT = "alert";
    private static final String CONFIRM = "confirm";
    private static final String TOAST = "toast";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (BEEP.equals(action)) {
            beep();
            return true;
        } else if (ALERT.equals(action)) {
            String label = args.length() > 2 ? args.getString(2) : "OK";
            alert(args.getString(0), args.getString(1), label, callbackContext);
            return true;
        } else if (CONFIRM.equals(action)) {
            confirm(args.getString(0), args.getString(1), args.getJSONArray(2), callbackContext);
            return true;
        } else if (TOAST.equals(action)) {
            toast(args.getString(0));
            return true;
        } else if (VIBRATOR.equals(action)) {
            vibrator();
            return true;
        }

        return false;
    }

    private void beep() {
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone notification = RingtoneManager.getRingtone(cordova.getActivity(), ringtone);
        if (notification != null) {
            notification.play();
        }
    }

    private void vibrator() {
        Vibrator vibrator = (Vibrator) cordova.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && ContextCompat.checkSelfPermission(cordova.getActivity(), Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            vibrator.vibrate(new long[]{200, 300, 200, 300,}, -1);
        }
    }

    private synchronized void alert(final String title, final String message, final String buttonLabel, final CallbackContext callbackContext) {
        Runnable runnable = () -> new ConfirmDialog.Builder(cordova.getActivity()).title(title).message(message).ok(buttonLabel).listener(new ConfirmDialog.OnButtonClickListener() {
            @Override
            public void onClick(ConfirmDialog dialog, int which) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, "ok");
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
            }
        }).create().show();
        cordova.getActivity().runOnUiThread(runnable);
    }

    private synchronized void confirm(final String title, final String message, final JSONArray buttonLabels, final CallbackContext callbackContext) {

        Runnable runnable = () -> {
            final int size = buttonLabels.length();
            String ok = "OK", cancel = "CANCEL";
            if (size >= 2) {
                ok = buttonLabels.optString(0);
                cancel = buttonLabels.optString(1);
            } else if (size == 1) {
                ok = buttonLabels.optString(0);
            }
            new ConfirmDialog.Builder(cordova.getActivity()).title(title).message(message).ok(ok).cancel(cancel).listener(new ConfirmDialog.OnButtonClickListener() {
                @Override
                public void onClick(ConfirmDialog dialog, int which) {
                    if (which == ConfirmDialog.BUTTON_CANCEL) {
                        PluginResult result = new PluginResult(PluginResult.Status.OK, "cancel");
                        result.setKeepCallback(true);
                        callbackContext.sendPluginResult(result);
                    } else if (which == ConfirmDialog.BUTTON_OK) {
                        PluginResult result = new PluginResult(PluginResult.Status.OK, "ok");
                        result.setKeepCallback(true);
                        callbackContext.sendPluginResult(result);
                    }
                }
            }).create().show();
        };
        cordova.getActivity().runOnUiThread(runnable);
    }

    private void toast(String message) {
        Toast.makeText(cordova.getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
