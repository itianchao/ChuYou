package com.jueda.ndian.activity.home.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

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

/**
 * 商品弹窗显示
 * Created by Administrator on 2016/6/28.
 */
public class CommodityPopBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public CommodityPopBiz(Context context,Handler handler) {
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.COMMODITY_POP, method);
        mRequest.add("id","1");
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("CommodityPopBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            if(jsonObject.getString("status").equals("1")){
                Message message=new Message();
                message.obj=jsonObject.getString("data");
                message.what=Constants.ON_SUCEED_TWO;
                handler.sendMessage(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

    }
}
