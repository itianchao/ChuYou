package com.jueda.ndian.utils;

import android.util.Log;

/**
 * Created by Administrator on 2016/4/25.
 */
public class LogUtil {
    public LogUtil(String title,String content){
        if(Constants.isOpenLog){
            Log.i("--------fengjian-->"+title,content+"");
        }
    }
}
