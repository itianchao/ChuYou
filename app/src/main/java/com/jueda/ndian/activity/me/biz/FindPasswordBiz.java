package com.jueda.ndian.activity.me.biz;

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
 * 找回密码
 * Created by Administrator on 2016/5/10.
 */
public class FindPasswordBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public FindPasswordBiz(Context context, Handler handler,String code,String new_password,String phone){
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.FIND_PASSWORD, method);
        mRequest.add("new_password", new_password);
        mRequest.add("code",code);
        mRequest.add("phone",phone);
        mRequest.add("from","android");
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("ChangePhoneBiz",response.get());
            JSONObject jsonObject = new JSONObject(response.get());
            String status = jsonObject.getString("status");
            /**修改成功*/
            if (status.equals("1")) {
                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(jsonObject.getString("status").equals("-1")){
                /**该手机未注册*/
                handler.sendEmptyMessage(Constants.FAILURE);
            }else if(jsonObject.getString("status").equals("0")){
                /**验证码错误*/
                Message message=new Message();
                message.what=Constants.FAILURE_TWO;
                message.obj=jsonObject.getString("data");
                handler.sendMessage(message);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE_THREE);
            }
        }catch (JSONException e) {
            handler.sendEmptyMessage(Constants.FAILURE_THREE);
            e.printStackTrace();
        }catch (NullPointerException e){
            handler.sendEmptyMessage(Constants.FAILURE_THREE);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE_THREE);
    }
}
