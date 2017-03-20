package com.jueda.ndian.wxapi;

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
 * Created by Administrator on 2016/6/29.
 */
public class WXpay  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public WXpay(Context context, String outTradeNo, String total_fee, Handler handler,String url) {
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(url, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("out_trade_no",outTradeNo);
        mRequest.add("total_fee",total_fee);

        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        new LogUtil("WXpay",response.get());
        try {
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                JSONObject object=jsonObject.getJSONObject("data");
                HashMap<String,String> map=new HashMap<>();
                map.put("appId",object.getString("appid"));
                map.put("nonceStr",object.getString("noncestr"));
                map.put("sign",object.getString("sign"));
                map.put("prepayId",object.getString("prepayid"));
                map.put("partnerId",object.getString("partnerid"));
                map.put("timeStamp",object.getString("timestamp"));
                Message message=new Message();
                message.obj=map;
                message.what= Constants.WX_ORDER_SUCCEED;
                handler.sendMessage(message);
            }else{
                handler.sendEmptyMessage(Constants.WX_ORDER_FAILURE);
            }
        } catch (JSONException e) {
            handler.sendEmptyMessage(Constants.WX_ORDER_FAILURE);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.WX_ORDER_FAILURE);
    }
}
