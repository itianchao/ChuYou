package com.jueda.ndian.activity.home.biz;

import android.content.Context;
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

import java.util.ArrayList;

/**
 * 跑马灯
 * Created by Administrator on 2016/8/8.
 */
public class MarqueeBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private ArrayList<String>  list;
    public MarqueeBiz(Context context, Handler handler) {
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.MAREQUEE, method);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("MarqueeBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                list=new ArrayList<>();
                JSONArray array=jsonObject.getJSONObject("data").getJSONArray("announcement");
                for(int i=0;i<array.length();i++){
                    list.add(array.getString(i));
                }
                /**判断是否显示最火热，1为显示，否则不显示*/
                String hot_tip=jsonObject.getJSONObject("data").getString("hot_tip");
                Bundle bundle=new Bundle();
                bundle.putSerializable("list",list);
                bundle.putString("hot", hot_tip);

                Message message=new Message();
                message.obj=bundle;
                message.what=Constants.ON_SUCEED_FOUR;
                handler.sendMessage(message);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE_THREE);
            }
        } catch (JSONException e) {
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
