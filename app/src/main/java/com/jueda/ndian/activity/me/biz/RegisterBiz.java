package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.RegisterActivity;
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
 * 注册
 * Created by Administrator on 2016/4/25.
 */
public class RegisterBiz implements HttpListener<String> {
    private Request mRequest;
    private Context context;
    private Handler handler;
    private String who;//判断是普通注册(RegisterActivity)还是第三方(LoginActivity)
    private String openid;//第三方唯一标识
    private String qq_wx;//qq微信
    public RegisterBiz(String who, Context context, Handler handler, String phone, String password, String uname, String sex, String invite_code,String openid,String qq_wx){
        this.context=context;
        this.qq_wx=qq_wx;
        this.openid=openid;
        this.who=who;
        this.handler=handler;
        if(sex.equals(context.getResources().getString(R.string.man))){
            sex="1";
        }else{
            sex="0";
        }
        RequestMethod method = RequestMethod.POST;// 默认get请求
        /**普通*/
        if(who.equals(RegisterActivity.TAG)){
            mRequest=  NoHttp.createStringRequest(Constants.REGISTER_USER, method);
            mRequest.add("phone",phone);
            mRequest.add("password",password);
            mRequest.add("uname",uname);
            mRequest.add("sex",sex);
            mRequest.add("invite_code",invite_code);
            // 添加到请求队列
            CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
        }else{
            /**第三方*/
            mRequest=  NoHttp.createStringRequest(Constants.REGISTER_USER_SNS, method);
            mRequest.add("openid",openid);
            mRequest.add("type",qq_wx);
            mRequest.add("uname",uname);
            mRequest.add("sex",sex);
            mRequest.add("invite_code",invite_code);
            // 添加到请求队列
            CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
        }

    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("RegisterBiz",response.get());
            JSONObject object=new JSONObject(response.get());
            String status=object.getString("status");
            if(status.equals("1")||status.equals("2")){
                JSONObject data=object.getJSONObject("data");
                //注册成功
                String phone;
                String birth;
                String job;
                String education;
                String signature;
                /**设置手机号*/
                if(data.getString("phone").equals("0")){
                    phone="";
                }else{
                    phone=data.getString("phone");
                }
                /**生日*/
                if(data.getString("birth").equals("0")){
                    birth="";
                }else{
                    birth=data.getString("birth");
                }
                /**职业*/
                if(data.getString("job").equals("0")){
                    job="";
                }else{
                    job=data.getString("job");
                }
                /**学历*/
                if(data.getString("education").equals("0")){
                    education="";
                }else{
                    education=data.getString("education");
                }
                /**个性签名*/
                if(data.getString("signature").equals("0")){
                    signature="";
                }else{
                    signature=data.getString("signature");
                }
                ConstantsUser.setUserEntity(context, Integer.parseInt(data.getString("uid")), phone, data.getString("uname"), data.getString("avatar"), data.getString("sex")
                        , birth, job, education, signature, data.getString("devotion"), data.getString("last_time"), data.getString("invite_code"), data.getString("user_token"), data.getString("ry_token")
                        ,data.getString("count_donations_cost"),data.getString("love_bean"),data.getString("count_post"),data.getString("surplus"), ConstantsUser.userEntity.getCount_ticket()
                        , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(status.equals("3")){
                JSONObject data=object.getJSONObject("data");
                //注册成功
                String phone;
                String birth;
                String job;
                String education;
                String signature;
                /**设置手机号*/
                if(data.getString("phone").equals("0")){
                    phone="";
                }else{
                    phone=data.getString("phone");
                }
                /**生日*/
                if(data.getString("birth").equals("0")){
                    birth="";
                }else{
                    birth=data.getString("birth");
                }
                /**职业*/
                if(data.getString("job").equals("0")){
                    job="";
                }else{
                    job=data.getString("job");
                }
                /**学历*/
                if(data.getString("education").equals("0")){
                    education="";
                }else{
                    education=data.getString("education");
                }
                /**个性签名*/
                if(data.getString("signature").equals("0")){
                    signature="";
                }else{
                    signature=data.getString("signature");
                }
                ConstantsUser.setUserEntity(context, Integer.parseInt(data.getString("uid")), phone, data.getString("uname"), data.getString("avatar"), data.getString("sex")
                        , birth, job, education, signature, data.getString("devotion"), data.getString("last_time"), data.getString("invite_code"), data.getString("user_token")
                        , data.getString("ry_token"),data.getString("count_donations_cost"),data.getString("love_bean"),data.getString("count_post"),data.getString("surplus"), ConstantsUser.userEntity.getCount_ticket()
                        , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                handler.sendEmptyMessage(Constants.ON_SUCEED_TWO);
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
