package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;

import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.LogUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户退出登录
 * Created by Administrator on 2016/5/10.
 */
public class LoginOutBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public LoginOutBiz(Context context, Handler handler, boolean b) {
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.LOGIN_OUT, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());

        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, b);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("LoginOutBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else{
                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }
        } catch (JSONException e) {
            handler.sendEmptyMessage(Constants.FAILURE);
            e.printStackTrace();
        }catch (NullPointerException e){
            handler.sendEmptyMessage(Constants.FAILURE);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE);
    }
}
