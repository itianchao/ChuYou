package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.nohttp.WaitDialog;
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
 * 修改用户信息
 * Created by Administrator on 2016/5/6.
 */
public class UpdateInfoBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private Context context;
    private String key;
    public UpdateInfoBiz(Context context, Handler handler, String key, String values,boolean isLoad){
        this.handler=handler;
        this.key=key;
        this.context=context;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.UPDATE_INFO, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add(key,values);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, isLoad);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("UpdateInfoBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            /**修改成功*/
            if(status.equals("1")) {
                String content=jsonObject.getJSONObject("data").getString(key);
                /**修改头像*/
                if(key.equals("avatar")){
                    ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(), content, ConstantsUser.userEntity.getSex()
                            , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(), ConstantsUser.userEntity.getSignature(), ConstantsUser.userEntity.getDevotion()
                            , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken(), ConstantsUser.userEntity.getCountDonationsDost()
                            , ConstantsUser.userEntity.getLove_bean(), ConstantsUser.userEntity.getCount_post(), ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                            , ConstantsUser.userEntity.getAdd_location(), ConstantsUser.userEntity.getAdd_phoneNumber(), ConstantsUser.userEntity.getAdd_uname());
                    Message message=new Message();
                    message.obj=jsonObject.getJSONObject("data").getString("devotion");
                    message.what=Constants.ON_SUCCEED;
                    handler.sendMessage(message);
                }else if(key.equals("uname")){
                    /**修改昵称*/
                    ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), content, ConstantsUser.userEntity.getAvater(), ConstantsUser.userEntity.getSex()
                            , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(), ConstantsUser.userEntity.getSignature(), ConstantsUser.userEntity.getDevotion()
                            , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken(), ConstantsUser.userEntity.getCountDonationsDost()
                            ,ConstantsUser.userEntity.getLove_bean(),ConstantsUser.userEntity.getCount_post(),ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                            , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                    handler.sendEmptyMessage(Constants.ON_SUCCEED);
                }else if(key.equals("phone")){
                    /**修改手机号*/
                    ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), content, ConstantsUser.userEntity.getUname(), ConstantsUser.userEntity.getAvater(), ConstantsUser.userEntity.getSex()
                            , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(), ConstantsUser.userEntity.getSignature(), ConstantsUser.userEntity.getDevotion()
                            , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken(), ConstantsUser.userEntity.getCountDonationsDost()
                            ,ConstantsUser.userEntity.getLove_bean(),ConstantsUser.userEntity.getCount_post(),ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                            , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                    handler.sendEmptyMessage(Constants.ON_SUCCEED);
                }else if(key.equals("sex")){
                    /**修改性别*/
                    ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(), ConstantsUser.userEntity.getAvater(), content
                            , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(), ConstantsUser.userEntity.getSignature(), ConstantsUser.userEntity.getDevotion()
                            , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken(), ConstantsUser.userEntity.getCountDonationsDost()
                            ,ConstantsUser.userEntity.getLove_bean(),ConstantsUser.userEntity.getCount_post(),ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                            , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                    handler.sendEmptyMessage(Constants.ON_SUCCEED);
                }else if(key.equals("birth")){
                    /**修改生日*/
                    ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(), ConstantsUser.userEntity.getAvater(), ConstantsUser.userEntity.getSex()
                            , content, ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(), ConstantsUser.userEntity.getSignature(), ConstantsUser.userEntity.getDevotion()
                            , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken(), ConstantsUser.userEntity.getCountDonationsDost()
                            ,ConstantsUser.userEntity.getLove_bean(),ConstantsUser.userEntity.getCount_post(),ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                            , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                    handler.sendEmptyMessage(Constants.ON_SUCCEED);
                }else if(key.equals("job")){
                    /**修改工作*/
                    ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(), ConstantsUser.userEntity.getAvater(), ConstantsUser.userEntity.getSex()
                            , ConstantsUser.userEntity.getBirth(),content, ConstantsUser.userEntity.getEducation(), ConstantsUser.userEntity.getSignature(), ConstantsUser.userEntity.getDevotion()
                            , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken(), ConstantsUser.userEntity.getCountDonationsDost()
                            ,ConstantsUser.userEntity.getLove_bean(),ConstantsUser.userEntity.getCount_post(),ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                            , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                    handler.sendEmptyMessage(Constants.ON_SUCCEED);
                }else if(key.equals("education")){
                    /**修改学历*/
                    ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(), ConstantsUser.userEntity.getAvater(), ConstantsUser.userEntity.getSex()
                            , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), content, ConstantsUser.userEntity.getSignature(), ConstantsUser.userEntity.getDevotion()
                            , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken(), ConstantsUser.userEntity.getCountDonationsDost()
                            ,ConstantsUser.userEntity.getLove_bean(),ConstantsUser.userEntity.getCount_post(),ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                            , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                    handler.sendEmptyMessage(Constants.ON_SUCCEED);
                }else if(key.equals("signature")){
                    /**修改个性签名*/
                    ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(), ConstantsUser.userEntity.getAvater(), ConstantsUser.userEntity.getSex()
                            , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(), content, ConstantsUser.userEntity.getDevotion()
                            , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken(), ConstantsUser.userEntity.getCountDonationsDost()
                            ,ConstantsUser.userEntity.getLove_bean(),ConstantsUser.userEntity.getCount_post(),ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                            , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                    handler.sendEmptyMessage(Constants.ON_SUCCEED);
                } else {
                    handler.sendEmptyMessage(Constants.FAILURE);
                }
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else if(status.equals("-1")){
                handler.sendEmptyMessage(Constants.FAILURE_TWO);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE);
            }
        } catch (JSONException e) {
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
