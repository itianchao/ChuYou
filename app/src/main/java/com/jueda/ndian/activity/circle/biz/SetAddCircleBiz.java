package com.jueda.ndian.activity.circle.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

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
 * 设置是否允许外人加圈
 * Created by Administrator on 2016/5/4.
 */
public class SetAddCircleBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private int add_set;
    public SetAddCircleBiz(Context context, Handler handler,int add_set,String cid){
        this.handler=handler;
        this.add_set=add_set;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.SET_ADD_CIRCLE, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("cid",cid);
        mRequest.add("add_set",add_set);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("SetAddCircleBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                if(add_set==1){
                    /**不允许外人加入*/
                    Message message=new Message();
                    message.what=Constants.ON_SUCEED_TWO;
                    message.obj=1;
                    handler.sendMessage(message);
                }else{
                    /**允许外人加入*/
                    Message message=new Message();
                    message.what=Constants.ON_SUCEED_TWO;
                    message.obj=0;
                    handler.sendMessage(message);
                }
            }else if(status.equals("0")){
                /**您不是圈主，无此操作权限*/
                Message message=new Message();
                message.what=Constants.FAILURE_TWO;
                message.obj=add_set+"1";
                handler.sendMessage(message);
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else{
                Message message=new Message();
                message.what=Constants.FAILURE_TWO;
                message.obj=add_set;
                handler.sendMessage(message);
            }
        } catch (JSONException e) {
            Message message=new Message();
            message.what=Constants.FAILURE_TWO;
            message.obj=add_set;
            handler.sendMessage(message);
            e.printStackTrace();
        }catch (NullPointerException e){
            Message message=new Message();
            message.what=Constants.FAILURE_TWO;
            message.obj=add_set;
            handler.sendMessage(message);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        Message message=new Message();
        message.what=Constants.FAILURE_TWO;
        message.obj=add_set;
        handler.sendMessage(message);

    }
}
