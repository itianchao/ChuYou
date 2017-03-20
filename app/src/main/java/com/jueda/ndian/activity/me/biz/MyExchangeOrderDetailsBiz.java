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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/8/30.
 * 我兑换商品的订单详情
 */
public class MyExchangeOrderDetailsBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private CommodityEntity entity;
    public MyExchangeOrderDetailsBiz(Context context, Handler handler, CommodityEntity entity) {
        this.handler=handler;
        this.entity=entity;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.EXCHANGE_OEDERS_DETAILS, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("id",entity.getOrder_id());
        new LogUtil("MyExchangeOrderDetailsBiz",entity.getOrder_id());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("MyExchangeOrderDetailsBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                JSONObject object=jsonObject.getJSONObject("data");
                entity.setId(object.getString("id"));
                entity.setBead(object.getString("love_bean"));
                entity.setOrder_no(object.getString("order_no"));
                entity.setGoodsState(object.getString("status"));
                entity.setCreat_time(object.getString("creat_time"));
                entity.setConsignee(object.getString("consignee"));
                entity.setPhone(object.getString("consignee_phone"));
                entity.setAddress(object.getString("consignee_add"));
                entity.setGid(object.getString("gid"));
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
