package com.jueda.ndian.activity.home.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.AppEntity;
import com.jueda.ndian.entity.TaskCommonEntity;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
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
 * Created by Administrator on 2016/8/25.
 * 问卷任务列表
 */
public class TaskQuestionnaireListBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private ArrayList<TaskCommonEntity> appEntityArrayList;
    public TaskQuestionnaireListBiz(Context context, Handler handler, ArrayList<TaskCommonEntity> appEntityArrayList, int page, String cid){
        this.handler=handler;
        this.appEntityArrayList=appEntityArrayList;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TASK, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("cid",cid);
        mRequest.add("device",Constants.DeviceId);
        mRequest.add("type","android");
        mRequest.add("p",page);
        mRequest.add("ex_or_sh",2);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("GetTaskBiz",response.get());
            JSONObject object = new JSONObject(response.get());
            String status=object.getString("status");
            if(status.equals("1")){
                JSONArray array=new JSONArray(object.getString("data"));
                if(array.length()>0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object1 = array.getJSONObject(i);
                        TaskCommonEntity entity = new TaskCommonEntity();
                        entity.setAid(object1.getString("aid"));
                        entity.setUid(object1.getString("uid"));
                        entity.setName(object1.getString("aname"));
                        entity.setReward(object1.getString("reward"));
                        appEntityArrayList.add(entity);
                    }
                    Message message=new Message();
                    message.obj=appEntityArrayList;
                    message.what=Constants.ON_SUCEED_TWO;
                    handler.sendMessage(message);
                }else{
                    handler.sendEmptyMessage(Constants.FAILURE_THREE);
                }
            }
        } catch (JSONException e) {
            handler.sendEmptyMessage(Constants.FAILURE_FOUR);
            e.printStackTrace();
        }catch (NullPointerException e){
            handler.sendEmptyMessage(Constants.FAILURE_FOUR);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE_FOUR);
    }

}
