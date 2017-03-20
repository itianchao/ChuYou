package com.jueda.ndian.activity.home.biz;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import com.jueda.ndian.entity.TaskCommonEntity;
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

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/25.
 * 问卷详情
 */
public class TaskDetailsQuestionnaireBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private ArrayList<TaskCommonEntity> arrayList;
    public TaskDetailsQuestionnaireBiz(Activity context,ArrayList<TaskCommonEntity> arrayList, Handler handler, String aid){
        this.handler=handler;
        this.arrayList=arrayList;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.EXPEROEMCE, method);
        mRequest.add("aid",aid);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("TaskExperienceBiz",response.get());
            JSONObject object=new JSONObject(response.get());
            String status=object.getString("status");
            /**获取数据成功*/
            if(status.equals("1")){
                JSONObject jsonObject=object.getJSONObject("data");
                TaskCommonEntity entity=new TaskCommonEntity();
                entity.setCid(jsonObject.getString("aid"));
                entity.setName(jsonObject.getString("aname"));
                entity.setReward(jsonObject.getString("reward"));
                entity.setDownloadUrl(jsonObject.getString("sdk_url"));
                entity.setUid(jsonObject.getString("uid"));
                JSONObject jsonObject1=jsonObject.getJSONObject("required");
                entity.setContent(jsonObject1.getString("content"));
                arrayList.add(entity);
                Message message=new Message();
                message.what=Constants.ON_SUCEED_TWO;
                message.obj=arrayList;
                handler.sendMessage(message);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE_THREE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE_THREE);
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
