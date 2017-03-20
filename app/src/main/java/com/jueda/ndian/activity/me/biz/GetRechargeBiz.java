package com.jueda.ndian.activity.me.biz;

import android.app.Activity;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/8/26.
 * 获取实时数据
 */
public class GetRechargeBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public GetRechargeBiz(Activity context, Handler handler) {
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.GET_RECHARGE, method);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("CreateCircleBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                JSONArray array=new JSONArray(jsonObject.getString("data"));
                String []bean=new String[array.length()];
                String []money=new String[array.length()];
                for(int i=0;i<array.length();i++){
                    JSONObject object=array.getJSONObject(i);
                    bean[i]=object.getString("love_bean");
                    money[i]=object.getString("money");
                }
                Bundle bundle=new Bundle();
                bundle.putStringArray("bean",bean);
                bundle.putStringArray("money",money);

                Message message=new Message();
                message.obj=bundle;
                message.what=Constants.ON_SUCCEED;
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
