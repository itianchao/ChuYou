package com.jueda.ndian.utils;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * 获取机器码
 * Created by Administrator on 2016/4/28.
 */
public class GetDeviceId {
    public static String GetDeviceIds(Activity activity) {
        final TelephonyManager tm = (TelephonyManager) activity.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(activity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }
}
