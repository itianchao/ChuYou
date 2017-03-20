package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.CircleEntity;
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
 * 账户余额
 * Created by Administrator on 2016/5/5.
 */
public class BalanceBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private Context context;
    public BalanceBiz(Context context, Handler handler){
        this.handler=handler;
        this.context=context;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.MONEYBAG_SUPLUS, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("BalanceBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            if(jsonObject.getString("status").equals("1")){
                JSONObject object=jsonObject.getJSONObject("data");
                /**获取余额成功*/
                ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(),  ConstantsUser.userEntity.getAvater(),  ConstantsUser.userEntity.getSex()
                        , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(),  ConstantsUser.userEntity.getSignature(),  ConstantsUser.userEntity.getDevotion()
                        , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken()
                        , ConstantsUser.userEntity.getCountDonationsDost(), object.getString("love_bean"), ConstantsUser.userEntity.getCount_post(), object.getString("surplus"), ConstantsUser.userEntity.getCount_ticket()
                        , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());

                handler.sendEmptyMessage(Constants.ON_SUCCEED);
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
