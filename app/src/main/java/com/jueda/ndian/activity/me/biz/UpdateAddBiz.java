package com.jueda.ndian.activity.me.biz;

import android.content.Context;
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
 * Created by Administrator on 2016/8/29.
 * 修改收货人信息
 */
public class UpdateAddBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private Context context;
    public UpdateAddBiz(Context context, Handler handler, String name,String phone,String add){
        this.handler=handler;
        this.context=context;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.UPDATE_INFO, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("consignee",name);
        mRequest.add("consignee_phone",phone);
        mRequest.add("consignee_add",add);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("UpdateInfoBiz", response.get());
            JSONObject jsonObject = new JSONObject(response.get());
            String status = jsonObject.getString("status");
            /**修改成功*/
            if (status.equals("1")) {
                    ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(), ConstantsUser.userEntity.getAvater(), ConstantsUser.userEntity.getSex()
                            , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(), ConstantsUser.userEntity.getSignature(), ConstantsUser.userEntity.getDevotion()
                            , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken(), ConstantsUser.userEntity.getCountDonationsDost()
                            , ConstantsUser.userEntity.getLove_bean(), ConstantsUser.userEntity.getCount_post(), ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                            ,jsonObject.getJSONObject("data").getString("consignee_add"),jsonObject.getJSONObject("data").getString("consignee_phone"), jsonObject.getJSONObject("data").getString("consignee"));
                    handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE);
            }
        }catch (JSONException e) {
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
