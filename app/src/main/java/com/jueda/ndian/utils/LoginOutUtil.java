package com.jueda.ndian.utils;

import android.app.Activity;
import android.content.Intent;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.savedata.Configuration;

/**
 * 退出登录
 * Created by Administrator on 2016/7/15.
 */
public class LoginOutUtil {
    public LoginOutUtil(Activity activity){
        new Configuration().writeaIsLogin(activity, false);
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        if(MainActivity.instance!=null){
            MainActivity.instance.addTag();
        }
        if(CharityCircleFragment.instance!=null){
            CharityCircleFragment.instance.Reconnection();
        }
    }
}
