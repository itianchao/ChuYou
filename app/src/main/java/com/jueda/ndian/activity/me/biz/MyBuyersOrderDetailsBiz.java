package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
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

import java.util.Objects;

/**
 * Created by Administrator on 2016/6/28.
 * 订单详情
 */
public class MyBuyersOrderDetailsBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private CommodityEntity entity;
    public MyBuyersOrderDetailsBiz(Context context, Handler handler, CommodityEntity entity) {
        this.handler=handler;
        this.entity=entity;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.ORDERS_DETAILS, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("id",entity.getOrder_id());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("MyBuyersOrderDetailsBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                JSONObject object=jsonObject.getJSONObject("data");
                entity.setId(object.getString("id"));
                entity.setGid(object.getString("product_id"));
                entity.setTotal_fee(object.getString("total_fee"));
                entity.setOrder_no(object.getString("order_no"));
                entity.setGoodsState(object.getString("status"));
                entity.setCreat_time(object.getString("creat_time"));
                entity.setConsignee(object.getString("consignee"));
                entity.setPhone(object.getString("consignee_phone"));
                entity.setAddress(object.getString("consignee_add"));
                    Message message = new Message();
                    message.obj = entity;
                    message.what = Constants.ON_SUCCEED;
                    handler.sendMessage(message);
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE);
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
