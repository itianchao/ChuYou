package com.jueda.ndian.activity.circle.biz;

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
 * 修改圈信息
 * Created by Administrator on 2016/6/14.
 */
public class UpdateCircleBiz implements HttpListener<String> {
    private String key;
    private Request mRequest;
    private Handler handler;
    public UpdateCircleBiz(Context context, Handler handler, String key, String values,String cid,boolean isLoad){
        this.handler=handler;
        this.key=key;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.MODIFY_CIRCLE_INFORMATION, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add(key,values);
        mRequest.add("cid",cid);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, isLoad);
    }
    @Override
      public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("UpdateCircleBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            /**修改成功*/
            if(status.equals("1")) {
                String content=jsonObject.getJSONObject("data").getString(key);
                /**修改头像*/
                    Message message=new Message();
                    message.obj=content;
                    message.what=Constants.ON_SUCCEED;
                    handler.sendMessage(message);
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else if(status.equals("-1")){
                handler.sendEmptyMessage(Constants.FAILURE_TWO);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE);
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
