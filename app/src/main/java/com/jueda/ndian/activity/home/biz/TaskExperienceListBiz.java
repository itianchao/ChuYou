package com.jueda.ndian.activity.home.biz;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.entity.AppEntity;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.savedata.Configuration;
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
import java.util.List;

/**
 * 获取体验任务列表
 * Created by Administrator on 2016/4/26.
 */
public class TaskExperienceListBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private String who;
    private ArrayList<AppEntity> appEntityArrayList;
    private String cid;
    public TaskExperienceListBiz(Context context, Handler handler, ArrayList<AppEntity> appEntityArrayList, String who, int page, String cid){
        this.handler=handler;
        this.who=who;
        this.cid=cid;
        this.appEntityArrayList=appEntityArrayList;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TASK, method);
        mRequest.add("user_token",ConstantsUser.userEntity.getUserToken());
        mRequest.add("cid",cid);
        mRequest.add("device",Constants.DeviceId);
        mRequest.add("type","android");
        mRequest.add("p",page);
        mRequest.add("ex_or_sh",1);
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

                        AppEntity entity = new AppEntity();
                        entity.setIs_fulfil(object.getString("is_fulfil"));
                        entity.setIs_fulfil_tip(object.getString("is_fulfil_tip"));
                        entity.setAid(object1.getString("aid"));
                        entity.setLastcount(object1.getString("lastcount"));
                        entity.setName(object1.getString("aname"));
                        entity.setReward(object1.getString("reward"));
                        entity.setAudit(object1.getString("audit"));
                        entity.setCid(cid);
                        entity.setWho(who);
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
