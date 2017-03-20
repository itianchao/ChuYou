package com.jueda.ndian.activity.circle.biz;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.jueda.ndian.activity.circle.view.CircleContentActivity;
import com.jueda.ndian.activity.me.pop.PopEverydayTask;
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
 * 发布话题
 * Created by Administrator on 2016/5/5.
 */
public class CreatPostBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private Activity context;
    public CreatPostBiz(Activity context, Handler handler, String cid, String title, String content, String img_url[]){
        this.handler=handler;
        this.context=context;
        StringBuffer image = new StringBuffer();
        for(int i=0;i<img_url.length;i++){
            if(img_url.length==i+1){
                image.append(img_url[i]);
            }else {
                image.append(img_url[i] + ",");
            }
        }
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.CREAT_POST, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("cid",cid);
        mRequest.add("title",title);
        mRequest.add("content",content);
        mRequest.add("img_arr", image.toString());

        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("CreatPostBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                if(!jsonObject.getJSONObject("data").getString("devotion").equals("0")){
                    new PopEverydayTask(CircleContentActivity.instance,CircleContentActivity.instance.newConstruction,PopEverydayTask.Release_Topic,jsonObject.getJSONObject("data").getString("devotion"));
                }
                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
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
