package com.jueda.ndian.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastShow {
	private static Toast mToast;
	private static Handler mHandler = new Handler();
	private static Runnable r = new Runnable() {
		public void run() {
			mToast.cancel();
		}
	};

	public ToastShow(Context mContext, String text, int duration) {

		mHandler.removeCallbacks(r);
		if (mToast != null)
			mToast.setText(text);
		else
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		mHandler.postDelayed(r, duration);

		mToast.show();
	}

	/**
	 *
	 * @param mContext
	 * @param resId
	 * @param duration
	 */
	public ToastShow(Context mContext, int resId, int duration) {
		new ToastShow(mContext, mContext.getResources().getString(resId), duration);
	}
}
