package com.jueda.ndian.activity.me.biz;

import android.app.Activity;

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
 * 删除单条信息
 * Created by Administrator on 2016/6/15.
 */
public class DeleteMessageBiz implements HttpListener<String> {
    private Request mRequest;
    public DeleteMessageBiz(Activity context, String id){
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.DEMETE_MESSAGE, method);
        mRequest.add("id",id);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }


    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("DeleteMessageBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            /**获取数据成功*/

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
    }
}
