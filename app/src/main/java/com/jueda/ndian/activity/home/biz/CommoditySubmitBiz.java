package com.jueda.ndian.activity.home.biz;

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

import java.util.HashMap;

/**
 * Created by Administrator on 2016/6/28.
 * 订单信息提交
 */
public class CommoditySubmitBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public CommoditySubmitBiz(Context context, Handler handler, String id,boolean isloading) {
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.SUBMIT_ORDERS, method);
        mRequest.add("id",id );
        mRequest.add("consignee",ConstantsUser.userEntity.getAdd_uname());
        mRequest.add("address",ConstantsUser.userEntity.getAdd_location() );
        mRequest.add("phone",ConstantsUser.userEntity.getAdd_phoneNumber() );
        mRequest.add("remark","" );
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, isloading);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("CommoditySubmitBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                HashMap<String,String> map=new HashMap<>();
                map.put("order_id",jsonObject.getJSONObject("data").getString("order_no"));
                map.put("total_fee", jsonObject.getJSONObject("data").getString("total_fee"));
                Message message=new Message();
                message.obj=map;
                message.what=Constants.ON_SUCCEED;
                handler.sendMessage(message);
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
