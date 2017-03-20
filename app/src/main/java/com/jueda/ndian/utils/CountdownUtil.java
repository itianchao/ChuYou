package com.jueda.ndian.utils;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

public class CountdownUtil extends CountDownTimer {
	private Handler handler;
	/**
	 * 
	 * @param millisInFuture 总时长
	 * @param countDownInterval 间隔时间
	 * @param handler
	 */
	public CountdownUtil(long millisInFuture, long countDownInterval, Handler handler) {
		super(millisInFuture, countDownInterval);
		this.handler=handler;
	}

	@Override
	public void onFinish() {
		handler.sendEmptyMessage(Constants.CMT_TIME_MARKSTOP);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		Message m=new Message();
		m.obj=millisUntilFinished;
		m.what=Constants.TIMING_OF;
		handler.sendMessage(m);
	}
}
