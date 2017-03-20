package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;

import com.jueda.ndian.activity.me.view.LoginActivity;
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
 * 登录
 * Created by Administrator on 2016/4/26.
 */
public class LoginBiz implements HttpListener<String> {
    private Request mRequest;
    private Context context;
    private Handler handler;
    public LoginBiz(Context context, Handler handler, String phone, String password,String openid,boolean isLoding) {
        this.context=context;
        this.handler=handler;

        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.LOGIN, method);
        mRequest.add("phone",phone);
        mRequest.add("password",password);
        mRequest.add("openid",openid);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, isLoding);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            JSONObject object=new JSONObject(response.get());
            String status=object.getString("status");
            new LogUtil("LoginBiz",response.get());
            /**登录成功*/
            if(status.equals("1")){
                JSONObject data=object.getJSONObject("data");
                LoginActivity.is_add_circle=data.getString("is_add_circle");
                String phone;
                String birth;
                String job;
                String education;
                String signature;
                String consignee;
                String consignee_phone;
                String consignee_add;
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
                /**收货人*/
                if(data.getString("consignee").equals("0")){
                    consignee="";
                }else{
                    consignee=data.getString("consignee");
                }
                /**收货人手机号*/
                if(data.getString("consignee_phone").equals("0")){
                    consignee_phone="";
                }else{
                    consignee_phone=data.getString("consignee_phone");
                }
                /**收货地址*/
                if(data.getString("consignee_add").equals("0")){
                    consignee_add="";
                }else{
                    consignee_add=data.getString("consignee_add");
                }

                ConstantsUser.setUserEntity(context, Integer.parseInt(data.getString("uid")), phone, data.getString("uname"), data.getString("avatar"), data.getString("sex")
                        , birth, job, education, signature, data.getString("devotion"), data.getString("last_time"), data.getString("invite_code"), data.getString("user_token"), data.getString("ry_token")
                        ,data.getString("count_donations_cost"),data.getString("love_bean"),data.getString("count_post"),data.getString("surplus"), data.getString("count_tour")
                        ,consignee_add,consignee_phone,consignee);

                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(status.equals("2")){
            /**第三方未注册*/
                handler.sendEmptyMessage(Constants.UNREGISTERED);
            }else if(status.equals("10003")) {
                handler.sendEmptyMessage(Constants.FAILURE);
            }else if(status.equals("10008")){
                handler.sendEmptyMessage(Constants.FAILURE_TWO);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE_THREE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE_THREE);
        }catch (NullPointerException e){
            handler.sendEmptyMessage(Constants.FAILURE_THREE);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE_THREE);
    }
}
