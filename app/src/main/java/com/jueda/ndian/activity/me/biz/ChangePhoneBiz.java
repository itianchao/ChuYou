package com.jueda.ndian.activity.me.biz;

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

/**
 * 修改手机号
 * Created by Administrator on 2016/5/10.
 */
public class ChangePhoneBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private Context context;
    private String key;
    public ChangePhoneBiz(Context context, Handler handler, String key, String values,boolean isLoad,String code){
        this.handler=handler;
        this.key=key;
        this.context=context;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.CHANGE_PHONE, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("code",code);
        mRequest.add("from","android");
        mRequest.add(key,values);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, isLoad);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("ChangePhoneBiz",response.get());
            JSONObject jsonObject = new JSONObject(response.get());
            String status = jsonObject.getString("status");
            /**修改成功*/
            if (status.equals("1")) {
                String content=jsonObject.getJSONObject("data").getString(key);
                    /**修改手机号*/
                    ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), content, ConstantsUser.userEntity.getUname(), ConstantsUser.userEntity.getAvater(), ConstantsUser.userEntity.getSex()
                            , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(), ConstantsUser.userEntity.getSignature(), ConstantsUser.userEntity.getDevotion()
                            , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken(), ConstantsUser.userEntity.getCountDonationsDost()
                            ,ConstantsUser.userEntity.getLove_bean(),ConstantsUser.userEntity.getCount_post(),ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                            , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                    handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else if(jsonObject.getString("status").equals("-1")){
                /**该手机已绑定*/
                handler.sendEmptyMessage(Constants.FAILURE_TWO);
            }else if(jsonObject.getString("status").equals("0")){
                /**验证码错误*/
                Message message=new Message();
                message.what=Constants.FAILURE_THREE;
                message.obj=jsonObject.getString("data");
                handler.sendMessage(message);
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
