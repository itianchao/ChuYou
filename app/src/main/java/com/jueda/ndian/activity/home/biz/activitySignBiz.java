package com.jueda.ndian.activity.home.biz;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.utils.ToastShow;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/8/25.
 * 活动报名
 */
public class activitySignBiz implements HttpListener<String> {
    private Handler handler;
    private Request mRequest;
    private Activity context;
    private Button participate_activity;
    public activitySignBiz(Activity context, Handler handler, String aid, Button participate_activity) {
        this.handler=handler;
        this.context=context;
        this.participate_activity=participate_activity;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.SIGN_ACTIVITY, method);
        mRequest.add("aid",aid);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("activitySignBiz",response.get());
            JSONObject object = new JSONObject(response.get());
            String status=object.getString("status");
            if(status.equals("1")){
                participate_activity.setVisibility(View.GONE);
                /**获取余额成功*/
                ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(), ConstantsUser.userEntity.getAvater(), ConstantsUser.userEntity.getSex()
                        , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(), ConstantsUser.userEntity.getSignature(), ConstantsUser.userEntity.getDevotion()
                        , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken()
                        , ConstantsUser.userEntity.getCountDonationsDost(), object.getJSONObject("data").getString("love_bean"), ConstantsUser.userEntity.getCount_post(), ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                        , ConstantsUser.userEntity.getAdd_location(), ConstantsUser.userEntity.getAdd_phoneNumber(), ConstantsUser.userEntity.getAdd_uname());
                PersonalFragment.userObservable.bean();
                new ToastShow(context,context.getResources().getString(R.string.Sign_up_success),1000);

            }else if(status.equals("0")){
                new ToastShow(context,context.getResources().getString(R.string.Name_the_already_quoted),1000);
            }else if(status.equals("10007")){
               handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else{
                new ToastShow(context,context.getResources().getString(R.string.Sign_up_failed),1000);
            }
        } catch (JSONException e) {
            new ToastShow(context,context.getResources().getString(R.string.Sign_up_failed),1000);
            e.printStackTrace();
        }catch (NullPointerException e){
            new ToastShow(context,context.getResources().getString(R.string.Sign_up_failed),1000);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        new ToastShow(context,context.getResources().getString(R.string.Sign_up_failed),1000);
    }
}
