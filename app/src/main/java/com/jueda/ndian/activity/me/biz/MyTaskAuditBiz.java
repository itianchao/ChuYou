package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.AppEntity;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.utils.getCurrentTime;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 我的任务
 * Created by Administrator on 2016/6/14.
 */
public class MyTaskAuditBiz implements HttpListener<String> {
    private Handler handler;
    private ArrayList<AppEntity> entityList;
    private Request mRequest;
    private int type;//审核中，已完成，未完成（1，2，0）
    /**
     *
     * @param context
     * @param handler
     * @param type 1为审核中。2为已完成、3为未完成
     * @param page
     * @param entityList
     */
    public MyTaskAuditBiz(Context context, Handler handler, int type, int page, ArrayList<AppEntity> entityList) {
        this.handler=handler;
        this.entityList=entityList;
        this.type=type;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.MY_TASK, method);
        mRequest.add("p",page);
        mRequest.add("type",type);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("MyTaskBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            /**获取数据成功*/
            if(status.equals("1")){
                JSONArray array=new JSONArray(jsonObject.getString("data"));
                if(array.length()!=0){
                    for(int i=0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        AppEntity entity=new AppEntity();
                        entity.setName(object.getString("aname"));
                        entity.setCid(object.getString("cid"));
                        entity.setReward(object.getString("reward"));
                        entity.setTime(getCurrentTime.getCurrentTime(Long.parseLong(object.getString("creat_time"))));
                        entity.setType(object.getString("type"));
                        entity.setEx_or_sh(object.getString("ex_or_sh"));
                        entity.setReason(object.getString("info"));
                        entityList.add(entity);
                    }
                    Message message=new Message();
                    message.obj=entityList;
                    message.what=Constants.ON_SUCCEED;
                    handler.sendMessage(message);
                }else if(jsonObject.getString("status").equals("10007")){
                    /**用户失效*/
                    if(type==1){
                        handler.sendEmptyMessage(Constants.USER_FAILURE);
                    }else{
                        handler.sendEmptyMessage(Constants.FAILURE_TWO);
                    }
                }else{
                    handler.sendEmptyMessage(Constants.FAILURE);
                }

            }else{
                handler.sendEmptyMessage(Constants.FAILURE_TWO);
            }
        } catch (JSONException e) {
            handler.sendEmptyMessage(Constants.FAILURE_TWO);
            e.printStackTrace();
        }catch (NullPointerException e){
            handler.sendEmptyMessage(Constants.FAILURE_TWO);
            e.printStackTrace();
        }

    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE_TWO);
    }
}
