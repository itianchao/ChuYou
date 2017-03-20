package com.jueda.ndian.activity.home.biz;

import android.app.Activity;
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
 * Created by Administrator on 2016/8/27.
 * 兑换商品
 */
public class TaskExchangeDetailsSubmitBiz implements HttpListener<String> {
    private Handler handler;
    private Request mRequest;
    private Activity activity;
    public TaskExchangeDetailsSubmitBiz(Activity activity, Handler handler, String id) {
        this.handler=handler;
        this.activity=activity;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.EXCHANGE_GOODS_DETAILS_SUMBIT, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("id",id);
        // 添加到请求队列
        CallServer.getRequestInstance().add(activity, 0, mRequest, this, true, true);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        try{
            new LogUtil("ExchangeGoodBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            if(jsonObject.getString("status").equals("1")) {
                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(jsonObject.getString("status").equals("0")){
                ConstantsUser.setUserEntity(activity, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(), ConstantsUser.userEntity.getAvater(), ConstantsUser.userEntity.getSex()
                        , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(), ConstantsUser.userEntity.getSignature(), ConstantsUser.userEntity.getDevotion()
                        , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken(), ConstantsUser.userEntity.getCountDonationsDost()
                        ,jsonObject.getJSONObject("data").getString("love_bean"),ConstantsUser.userEntity.getCount_post(),ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                        , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                handler.sendEmptyMessage(Constants.FAILURE);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE_TWO);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE_TWO);
        }catch (NullPointerException e){
            handler.sendEmptyMessage(Constants.FAILURE_TWO);
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE_TWO);
    }
}
