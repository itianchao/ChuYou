package com.jueda.ndian.activity.home.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

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

import java.util.HashMap;

/**
 * 获取版本号
 * Created by Administrator on 2016/5/23.
 */
public class VersionsBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public VersionsBiz(Context context,Handler handler){
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.VERSION, method);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("VersionsBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                JSONObject object=jsonObject.getJSONObject("data");
                HashMap<String,String> versions=new HashMap<>();
                versions.put("code",object.getString("code_android"));
                versions.put("version",object.getString("version_android"));
                versions.put("update_log",object.getString("update_log_android"));
                versions.put("url",object.getString("sdk_url"));
                versions.put("size",object.getString("size"));
                versions.put("mask",jsonObject.getString("mask"));//为0则关闭发起筹款项
                Message message=new Message();
                message.obj=versions;
                message.what=Constants.ON_SUCCEED;
                handler.sendMessage(message);
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
