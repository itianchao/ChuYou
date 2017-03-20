package com.jueda.ndian.listener;

import android.util.Log;

import io.rong.imlib.RongIMClient;
import io.rong.notification.PushNotificationMessage;


/**
 * 收到 push 消息的处理。
 *
 * @param pushNotificationMessage push 消息实体。
 * @return true 自己来弹通知栏提示，false 融云 SDK 来弹通知栏提示。
 */
public class MyReceivePushMessageListener implements RongIMClient.OnReceivePushMessageListener {
    @Override
    public boolean onReceivePushMessage(PushNotificationMessage pushNotificationMessage) {
        Log.i("----------push","push");
        return false;
    }
}
