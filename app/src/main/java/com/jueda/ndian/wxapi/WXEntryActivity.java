package com.jueda.ndian.wxapi;

import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

/**
 * Created by ntop on 15/9/4.
 */
public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {
    public static WXEntryActivity instance=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        instance=this;
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onReq(BaseReq arg0) {
        // TODO Auto-generated method stub
//        if(UserChangeActivity.instance.waitDialog!=null){
//            UserChangeActivity.instance.waitDialog.dismiss();
//        }
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
//        if(UserChangeActivity.instance!=null){
//            if(resp.errCode==BaseResp.ErrCode.ERR_OK){
//                if(UserChangeActivity.instance.waitDialog.isShowing()){
//                    UserChangeActivity.instance.code = ((SendAuth.Resp) resp).code;
//                    new WXUserBiz(UserChangeActivity.instance,UserChangeActivity.instance.code);
//                }else{
//                    UserChangeActivity.instance.handler.sendEmptyMessage(Constants.FAILURE);
//                }
//            }else{
//                UserChangeActivity.instance.handler.sendEmptyMessage(Constants.FAILURE);
//            }
//        }
        finish();
    }
}
