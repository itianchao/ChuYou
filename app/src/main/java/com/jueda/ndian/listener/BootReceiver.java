package com.jueda.ndian.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.entity.serverAppEntity;
import com.jueda.ndian.savedata.Configuration;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 安装与卸载
 * Created by Administrator on 2016/3/12.
 */
public class BootReceiver extends BroadcastReceiver {
    Map<String, String> map_value = new HashMap<>();
    private boolean isLogout;
    Configuration configuration;
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getDataString();
        packageName = packageName.substring(8, packageName.length());
        //取出值判断是否软件退出
        configuration=new Configuration();
        isLogout=configuration.readaDownload(context);

        //接收安装广播
        if(!isLogout) {
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")&&MainActivity.instance!=null) {
                //// TODO: 2016/3/17 需要保存本地一个数据然后再判断应用是否关闭
                if (MainActivity.instance.apkList.size() > 0) {
                    for (int i = 0; i < MainActivity.instance.apkList.size(); i++) {
                        serverAppEntity entity = MainActivity.instance.apkList.get(i);
                        String val = entity.getName();
                        if (packageName.equals((String) val)) {
                            MainActivity.instance.vals = "";
                            MainActivity.instance.keys = 1024;
                            //添加名称统计
                            Name(MainActivity.instance.apkList.get(i).getName());
                            //安装成功移除
                            MainActivity.instance.apkList.remove(i);
                            //安装成功调用image类中方法
                            MainActivity.instance.Installation(entity.getKey(),entity.getWho());
                            MobclickAgent.onEventValue(context, "install", map_value, 200);
                            return;
                        }
                    }
                    if (MainActivity.instance.vals.equals(packageName)) {
                        MainActivity.instance.Installation(MainActivity.instance.keys,MainActivity.instance.who);
                        MainActivity.instance.vals = "";
                        MainActivity.instance.keys = 1024;
                        Name(MainActivity.instance.name);
                        MobclickAgent.onEventValue(context, "install", map_value, 200);
                    }
                } else if (MainActivity.instance.vals != "" && MainActivity.instance.keys != 1024) {
                    MainActivity.instance.Installation(MainActivity.instance.keys,MainActivity.instance.who);
                    MainActivity.instance.vals = "";
                    MainActivity.instance.keys = 1024;
                    Name(MainActivity.instance.name);
                    MobclickAgent.onEventValue(context, "install", map_value, 200);
                }
            }
            //接收卸载广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")&&MainActivity.instance!=null) {
                //// TODO: 2016/3/14 真实数据时还差一步，另外建一个hashmap存储下载的的包名addhashmap。当卸载时，addhashmap中有值直接调用。否则调用当前的hashmap
                if (MainActivity.instance.apkList != null && MainActivity.instance.apkList.size() > 0) {
                    for (int i = 0; i < MainActivity.instance.apkList.size(); i++) {
                        serverAppEntity entity = MainActivity.instance.apkList.get(i);
                        String val = entity.getName();
                        if (packageName.equals(val)) {
                            MainActivity.instance.vals = val;
                            MainActivity.instance.keys = entity.getKey();
                            MainActivity.instance.name = entity.getName();
                            MainActivity.instance.who=entity.getWho();
                            MainActivity.instance.apkList.remove(i);
                            MainActivity.instance.Uninstall(entity.getKey(),entity.getWho());
                            return;
                        }
                    }
                }
                if (MainActivity.instance.addList.size() > 0) {
                    for (int i = 0; i < MainActivity.instance.addList.size(); i++) {
                        serverAppEntity entity = MainActivity.instance.addList.get(i);
                        String val = entity.getName();
                        if (packageName.equals(val)) {
                            MainActivity.instance.Uninstall(entity.getKey(),entity.getWho());
                            return;
                        }
                    }
                }
            }
        }
    }
    public void Name(String name){
        map_value.put("type" , "install" );
        map_value.put("name" , name );
    }

}
