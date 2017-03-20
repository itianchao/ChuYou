package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.UserEntity;
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

import java.util.ArrayList;

/**
 * 获取用户信息
 * Created by Administrator on 2016/5/6.
 */
public class GetInfoBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private  ArrayList<UserEntity> userList;
    public GetInfoBiz(Context context, Handler handler, String uid, ArrayList<UserEntity> userList){
        this.handler=handler;
        this.userList=userList;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.GET_INFO, method);
        mRequest.add("uid", uid);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("GetInfoBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                JSONObject object=jsonObject.getJSONObject("data");
                UserEntity entity=new UserEntity();
                entity.setUid(Integer.parseInt(object.getString("uid")));
                entity.setUname(object.getString("uname"));
                entity.setAvater(object.getString("avatar"));
                entity.setSex(object.getString("sex"));
                if(object.getString("signature").equals("0")){
                    entity.setSignature("");
                }else {
                    entity.setSignature(object.getString("signature"));
                }
                entity.setLove_bean(object.getString("love_bean"));
                entity.setCount_post(object.getString("count_post"));
                entity.setSurplus(object.getString("surplus"));
                entity.setDevotion(object.getString("devotion"));
                entity.setRyToken(object.getString("ry_token"));
                entity.setCountDonationsDost(object.getString("count_donations_cost"));
                entity.setAdd_uname(object.getString("consignee"));
                entity.setAdd_phoneNumber(object.getString("consignee_phone"));
                entity.setAdd_location(object.getString("consignee_add"));
                entity.setCount_ticket(object.getString("count_tour"));
                userList.add(entity);
                Message message=new Message();
                message.what=Constants.ON_SUCCEED;
                message.obj=userList;
                handler.sendMessage(message);
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
