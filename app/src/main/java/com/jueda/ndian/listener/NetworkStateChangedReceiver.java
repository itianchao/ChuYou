package com.jueda.ndian.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.utils.Constants;

public class NetworkStateChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//判断用户是打开还是关闭
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		if (activeNetworkInfo == null) {
			//无网络则设置为false；
			Constants.currentNetworkType = Constants.TYPE_NONE;
			Toast.makeText(context, "网络断开", Toast.LENGTH_SHORT).show();
		} else {
			/**有网络则注册友盟信息*/
			if(Constants.currentNetworkType!=Constants.TYPE_NONE){
				if(NdianApplication.instance!=null){
					NdianApplication.instance.isFist=false;
					NdianApplication.instance.OnStartUmeng();
				}
				if(CharityCircleFragment.instance!=null){
					MainActivity.instance.addTag();
				}
			}
			//wifi ,mobile(gprs,3g,4g)
			//判断网络类型

			//用户开了wifi和移动网络，操作系统使用的是wifi
			NetworkInfo wifiNetworkInfo = manager.getNetworkInfo(manager.TYPE_WIFI);
			if (wifiNetworkInfo != null
					&& wifiNetworkInfo.isConnected())
				Constants.currentNetworkType = Constants.TYPE_WIFI;

			NetworkInfo mobileNetworkInfo = manager.getNetworkInfo(manager.TYPE_MOBILE);
			if (mobileNetworkInfo != null
					&& mobileNetworkInfo.isConnected()) {
				Constants.currentNetworkType = Constants.TYPE_MOBILE;
			}
		}

	}
		
}
	

