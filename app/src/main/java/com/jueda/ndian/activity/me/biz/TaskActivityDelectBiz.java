package com.jueda.ndian.activity.me.biz;

import android.app.Activity;
import android.os.Handler;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.MyTask_ActivityFragment;
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
 * Created by Administrator on 2016/9/2.
 * 任务发布活动删除
 */
public class TaskActivityDelectBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private Activity context;
    public TaskActivityDelectBiz(Activity context, Handler handler,String id){
        this.handler=handler;
        this.context=context;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.DELECT_ACTIVITY, method);
        mRequest.add("id",id);
        new LogUtil("TaskActivityDelectBiz",id);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("TaskActivityDelectBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
               new ToastShow(context,context.getResources().getString(R.string.delect_success),1000);
                context.finish();
                if(MyTask_ActivityFragment.instance!=null){
                    MyTask_ActivityFragment.instance.Reconnection();
                }
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else{
                new ToastShow(context,context.getResources().getString(R.string.delect_failure),1000);
            }
        } catch (JSONException e) {
            new ToastShow(context,context.getResources().getString(R.string.delect_failure),1000);
            e.printStackTrace();
        }catch (NullPointerException e){
            new ToastShow(context,context.getResources().getString(R.string.delect_failure),1000);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        new ToastShow(context,context.getResources().getString(R.string.delect_failure),1000);
    }
}
