package com.jueda.ndian.activity.home.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

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
 * 签到
 * Created by Administrator on 2016/7/2.
 */
public class SignBiz implements HttpListener<String> {
    private Handler handler;
    private Request mRequest;
    public SignBiz(Context context, Handler handler) {
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.SING, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("SignBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            if(jsonObject.getString("status").equals("1")){
                /**签到成功*/
                Message message=new Message();
                message.obj=jsonObject.getJSONObject("data").getString("devotion");
                message.what=Constants.ON_SUCCEED;
                handler.sendMessage(message);
            }else if(jsonObject.getString("status").equals("-1")){
                /**已签*/
                Message message=new Message();
                message.obj=jsonObject.getString("message");
                message.what=Constants.FAILURE_TWO;
                handler.sendMessage(message);
            }else if(jsonObject.getString("status").equals("10007")){
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE);
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
