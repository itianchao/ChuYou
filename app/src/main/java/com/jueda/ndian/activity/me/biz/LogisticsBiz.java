package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.CommodityEntity;
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
 * 获取物流信息
 * Created by Administrator on 2016/6/29.
 */
public class LogisticsBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public LogisticsBiz(Context context, CommodityEntity entity, Handler handler, String logistics) {
        this.handler= handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(logistics, method);
        mRequest.add("user_token",ConstantsUser.userEntity.getUserToken());
        mRequest.add("id", entity.getId());

        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("LogisticsBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                String s=jsonObject.getString("data");
                    Message message = new Message();
                    message.obj = s;
                    message.what = Constants.ON_SUCCEED;
                    handler.sendMessage(message);
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
