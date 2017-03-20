package com.jueda.ndian.listener;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.activity.home.biz.LocationBiz;
import com.jueda.ndian.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 百度地图定位
 * Created by Administrator on 2016/6/17.
 */
public class MyLocationListener implements BDLocationListener {

    @Override
    public void onReceiveLocation(BDLocation location) {
        JSONObject jsonObject=new JSONObject();
        if(location.getLocType()==61||location.getLocType()==161){
            try {
                jsonObject.put("latitude",location.getLatitude()+"");
                jsonObject.put("lontitude",location.getLongitude()+"");
                jsonObject.put("addr",location.getAddrStr()+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new LocationBiz(NdianApplication.instance,jsonObject.toString());
            NdianApplication.instance.mLocationClient.stop();
        }
        new LogUtil("MyLocationListener",jsonObject.toString());
    }
}