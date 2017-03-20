package com.jueda.ndian.savedata;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.jueda.ndian.utils.Constants;


public class Configuration {

	/**
	 * 时间
	 * @param context
	 * @param data
	 */
	public void writeaDate(Context context,Long data) {
		if (null == context ) {
			return;
		}
		SharedPreferences pref = context.getSharedPreferences("date", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putLong("date", data);
		editor.commit();
	}
	public Long readaDate(Context context) {
		SharedPreferences pref = context.getSharedPreferences("date", Context.MODE_PRIVATE);
		Long data=pref.getLong("date", System.currentTimeMillis());
		return data;
	}
	/*
	 * Version 版本号
	 */
	 public void writeaVersion(Context context,String Version) {
	        if (null == context ) {
	            return;
	        }
	        SharedPreferences pref = context.getSharedPreferences(Constants.CONFIGURATION, Context.MODE_PRIVATE);
	        Editor editor = pref.edit();
	        editor.putString("Version", Version);
		 editor.commit();
	    }
	 public String readaVersion(Context context) {
	        SharedPreferences pref = context.getSharedPreferences(Constants.CONFIGURATION, Context.MODE_PRIVATE);
	        String Version=pref.getString("Version", "");
	        return Version;
	    }

	/**
	 * 判断是否登录
	 * @param context
	 * @param isLogin
	 */
	public void writeaIsLogin(Context context,boolean isLogin) {
		if (null == context ) {
			return;
		}
		SharedPreferences pref = context.getSharedPreferences(Constants.CONFIGURATION, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean("isLogin", isLogin);
		editor.commit();
	}
	public boolean readaIsLogin(Context context) {
		SharedPreferences pref = context.getSharedPreferences(Constants.CONFIGURATION, Context.MODE_PRIVATE);
		boolean isLogin=pref.getBoolean("isLogin", false);
		return isLogin;
	}
	/**
	 * 判断下载界面是否退出
	 * @param context
	 * @param isLogout
	 */
	public void writeaDownload(Context context, boolean isLogout) {
		if (null == context) {
			return;
		}
		SharedPreferences mySharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = mySharedPreferences.edit();
		edit.putBoolean("isLogout", isLogout);
		edit.commit();
	}

	//取出
	public boolean readaDownload(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		boolean isLogout = sharedPreferences.getBoolean("isLogout", true);
		return  isLogout;
	}
	/**
	 * 是否打开声音
	 * @param context
	 * @param sound
	 */
	public void writeaSound(Context context, boolean sound) {
		if (null == context) {
			return;
		}
		SharedPreferences mySharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = mySharedPreferences.edit();
		edit.putBoolean("sound", sound);
		edit.commit();
	}

	//取出
	public boolean readaSound(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		boolean isLogout = sharedPreferences.getBoolean("sound", true);
		return  isLogout;
	}
	/**
	 * 是否打开震动
	 * @param context
	 * @param vibration
	 */
	public void writeaVibration(Context context, boolean vibration) {
		if (null == context) {
			return;
		}
		SharedPreferences mySharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = mySharedPreferences.edit();
		edit.putBoolean("vibration", vibration);
		edit.commit();
	}

	//取出
	public boolean readaVibration(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		boolean isLogout = sharedPreferences.getBoolean("vibration", false);
		return  isLogout;
	}
	/**
	 * 是否打开评论提醒
	 * @param context
	 * @param comment
	 */
	public void writeaComment(Context context, boolean comment) {
		if (null == context) {
			return;
		}
		SharedPreferences mySharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = mySharedPreferences.edit();
		edit.putBoolean("comment", comment);
		edit.commit();
	}

	//取出
	public boolean readaComment(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		boolean isLogout = sharedPreferences.getBoolean("comment", true);
		return  isLogout;
	}
	/**
	 * 判断APP是否还在运行
	 * @param context
	 * @param comment
	 */
	public void writeaAppRunning(Context context, boolean isAppRunning) {
		if (null == context) {
			return;
		}
		SharedPreferences mySharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = mySharedPreferences.edit();
		edit.putBoolean("isAppRunning", isAppRunning);
		edit.commit();
	}

	//取出
	public boolean readaAppRunning(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		boolean isAppRunning = sharedPreferences.getBoolean("isAppRunning", false);
		return  isAppRunning;
	}

	/**
	 * 上一次签到时间
	 * @param context
	 * @param comment
	 */
	public void writeaSign_in(Context context, long time){
		if (null == context) {
			return;
		}
		SharedPreferences mySharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = mySharedPreferences.edit();
		edit.putLong("time", time);
		edit.commit();
	}

	//取出
	public long readaSign_in(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE);
		long time = sharedPreferences.getLong("time", 0);
		return  time;
	}
}
