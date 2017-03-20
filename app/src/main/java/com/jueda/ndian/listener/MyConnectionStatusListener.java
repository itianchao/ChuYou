package com.jueda.ndian.listener;

import android.os.Handler;


import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.activity.me.biz.LoginOutBiz;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoginOutUtil;

import io.rong.imlib.RongIMClient;

/**
 * 融云消息
 * Created by Administrator on 2016/3/26.
 */
public class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {

    Handler handler=new Handler(){};

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        switch (connectionStatus){

            case CONNECTED://连接成功。

                break;
            case DISCONNECTED://断开连接。

                break;
            case CONNECTING://连接中。

                break;
            case NETWORK_UNAVAILABLE://网络不可用。

                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                if(new Configuration().readaIsLogin(NdianApplication.instance)&&new Configuration().readaAppRunning(NdianApplication.instance)){
                    new LoginOutBiz(NdianApplication.instance,handler, false);
                    Constants.isOut=true;
                   new LoginOutUtil(MainActivity.instance);
                }
                new Configuration().writeaIsLogin(NdianApplication.instance, false);
                break;
        }
    }
}
