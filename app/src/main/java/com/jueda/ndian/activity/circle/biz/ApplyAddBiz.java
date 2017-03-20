package com.jueda.ndian.activity.circle.biz;

import android.content.Context;
import android.os.Handler;

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
 * 加入圈子
 * Created by Administrator on 2016/5/4.
 */
public class ApplyAddBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public ApplyAddBiz(Context context, Handler handler,String cid){
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.WRITE_APPLY_CIRCLE, method);
        mRequest.add("cid",cid);
        mRequest.add("verify","");
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("ApplyAddBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            /**申请成功*/
            if(status.equals("1")){
                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(status.equals("0")){
            /**该圈不准任何人加入*/
                handler.sendEmptyMessage(Constants.FAILURE);
            }else if(status.equals("3")){
            /***已经加入圈子**/
                handler.sendEmptyMessage(Constants.FAILURE_FOUR);
            }else if(status.equals("2")){
            /**申请成功，耐心等待*/
                handler.sendEmptyMessage(Constants.FAILURE_TWO);
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
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
