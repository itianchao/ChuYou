package com.jueda.ndian.wxapi;

import com.jueda.ndian.activity.home.view.CommoditySubmitActivity;
import com.jueda.ndian.activity.me.view.PayActivity;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LogUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		new LogUtil("WXpay","onPayFinish, errCode = " + resp.errCode+"  "+resp.openId);
		String msg = "";
		if(resp.errCode == 0)
		{
			msg = "1";//成功

		}
		else if(resp.errCode == -1)
		{
			msg = "-1";//失败
		}
		else if(resp.errCode == -2)
		{
			msg = "-2";//取消
		}
		Message message=new Message();
		message.obj=msg;
		message.what=Constants.WX_PAY_SUCCEED;
		if(CommoditySubmitActivity.instance!=null){
			CommoditySubmitActivity.instance.handler.sendMessage(message);
		}else if(PayActivity.instance!=null){
			PayActivity.instance.handler.sendMessage(message);
		}

		finish();
	}
}