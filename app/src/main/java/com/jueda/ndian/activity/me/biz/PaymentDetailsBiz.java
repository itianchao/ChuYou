package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.PaymentDetailsEntity;
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

import java.util.ArrayList;

/**
 * 收支明细
 * Created by Administrator on 2016/5/6.
 */
public class PaymentDetailsBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private ArrayList<PaymentDetailsEntity> entityList;
    public PaymentDetailsBiz(Context context, Handler handler, int page, ArrayList<PaymentDetailsEntity> entityList){
        this.entityList=entityList;
        this.handler=handler;
        RequestMethod method = RequestMethod.GET;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.MONEYBAG_DETAIL, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("p",page);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("PaymentDetailsBiz",response.get());
            JSONObject object=new JSONObject(response.get());
            String status=object.getString("status");
            if(status.equals("1")){
                JSONArray array=new JSONArray(object.getString("data"));
                if(array.length()==0){
                    handler.sendEmptyMessage(Constants.FAILURE);
                }else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        PaymentDetailsEntity entity = new PaymentDetailsEntity();
                        entity.setId(jsonObject.getString("id"));
                        entity.setMoney(jsonObject.getString("money"));
                        entity.setRemark(jsonObject.getString("remark"));
                        entity.setType(jsonObject.getString("type"));
                        entity.setCreat_time(jsonObject.getString("creat_time"));
                        entityList.add(entity);
                    }
                    Message message = new Message();
                    message.what = Constants.ON_SUCCEED;
                    message.obj = entityList;
                    handler.sendMessage(message);
                }
            }else if(object.getString("status").equals("10007")){
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
