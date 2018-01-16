package com.xinzy.webapp.util;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by xinzy on 2017/11/6.
 *
 */

public class Utils {

    private static String sDeviceId = null;

    public static String md5(@NonNull String input) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] data = md.digest();
            int j = data.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte b : data) {
                str[k++] = hexDigits[b >>> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static String getDeviceId(Context context) {
        if (!TextUtils.isEmpty(sDeviceId)) {
            return sDeviceId;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String uuid = sp.getString("UUID", "");
        if (!TextUtils.isEmpty(uuid)) {
            sDeviceId = uuid;
            return sDeviceId;
        }
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            if (!TextUtils.isEmpty(androidId) && !"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
            } else {
                final String deviceId = getImei(context);
                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
            }
        } catch (Throwable t) {
            uuid = UUID.randomUUID().toString();
        }
        sDeviceId = uuid;
        sp.edit().putString("UUID", uuid).apply();

        return sDeviceId;
    }

    private static String getImei(@NonNull Context context) {
        String imei = "";
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                imei = manager != null ? manager.getDeviceId() : null;
                if (imei == null) imei = "";
            } catch (Exception e) {
            }
        }
        return imei;
    }

    public static String getIpAddress(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {

            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                final int type = info.getType();
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    return getMobileIp();
                } else if (type == ConnectivityManager.TYPE_WIFI) {
                    return getWifiIp(context);
                }
            }
        }

        return "";
    }

    private static String getMobileIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    private static String getWifiIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            return  (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
        }
        return "";
    }
}
