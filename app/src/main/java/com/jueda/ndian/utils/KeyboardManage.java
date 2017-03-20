package com.jueda.ndian.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardManage {
	public  void OpenKeyboard(View ed){
		//弹出输入法
		InputMethodManager inputManager =
				(InputMethodManager)ed.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(ed, 0); 
	}
	public void CloseKeyboard(View v,Context context){
		//关闭输入法
		((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).
			     hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
