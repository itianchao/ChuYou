package com.jueda.ndian.activity.home.biz;

import android.content.Context;
import android.os.Handler;

import com.jueda.ndian.R;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.utils.ToastShow;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 完成任务上传截图信息
 * Created by Administrator on 2016/6/16.
 */
public class CompleteTaskBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public CompleteTaskBiz(Context context, Handler handler, String cid, String aid,String img_url[]){
        this.handler=handler;
        StringBuffer image = new StringBuffer();
        for(int i=0;i<img_url.length;i++){
            if(img_url.length==i+1){
                image.append(img_url[i]);
            }else {
                image.append(img_url[i] + ",");
            }
        }
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.COMPLETE_TASK, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("cid",cid);
        mRequest.add("aid",aid);
        mRequest.add("img_arr", image.toString());
        mRequest.add("device",Constants.DeviceId);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
       
        try {
            new LogUtil("CompleteTaskBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else if(jsonObject.getString("status").equals("0")){
                /**重复提交*/
                handler.sendEmptyMessage(Constants.FAILURE_TWO);
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
