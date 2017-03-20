package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LogUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 检查手机号是否注册
 * Created by Administrator on 2016/4/23.
 */
public class CheckPhoneBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public CheckPhoneBiz(Context context,Handler handler,String phone,boolean isload){
        this.handler=handler;
        RequestMethod method = RequestMethod.GET;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.CHECK_PHONE, method);
        mRequest.add("phone",phone);

        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, isload);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            JSONObject object=new JSONObject(response.get());
            String status=object.getString("status");
            Message message=new Message();
            message.obj=status;
            message.what=Constants.ON_SUCCEED;
            handler.sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE);
        }catch (NullPointerException e){
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE);
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE);
    }
}
