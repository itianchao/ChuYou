package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;

import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 提现
 * Created by Administrator on 2016/5/9.
 */
public class CashBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public CashBiz(Context context, Handler handler,String money,String alipay,String real_name){
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.CASH, method);
        mRequest.add("money",money);
        mRequest.add("alipay",alipay);
        mRequest.add("real_name",real_name);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(status.equals("0")){
                handler.sendEmptyMessage(Constants.FAILURE);
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
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
