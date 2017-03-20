package com.jueda.ndian.utils;

import android.app.Activity;
import android.content.Context;

import com.jueda.ndian.R;


public class ChangeTitle {
	/**
	 * 改变导航栏背景
	 * @param context
	 */
	public ChangeTitle(Context context){
		// 创建状态栏的管理实例
		  SystemBarTintManager tintManager = new SystemBarTintManager((Activity) context);
		  // 激活状态栏设置
		  tintManager.setStatusBarTintEnabled(true);
		// 设置一个样式背景给导航栏
		  tintManager.setNavigationBarTintResource(R.color.title);
		  tintManager.setStatusBarTintResource(R.color.title);
	}
}	
