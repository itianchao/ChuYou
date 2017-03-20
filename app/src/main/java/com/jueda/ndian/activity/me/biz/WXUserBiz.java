package com.jueda.ndian.activity.me.biz;

import android.content.Context;

import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LogUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取微信用户信息
 * Created by Administrator on 2016/5/9.
 */
public class WXUserBiz  implements HttpListener<String> {
    private Request mRequest;
    private Context context;
    public WXUserBiz(Context context,String code){
        this.context=context;
        RequestMethod method = RequestMethod.GET;// 默认get请求
        mRequest=  NoHttp.createStringRequest("https://api.weixin.qq.com/sns/oauth2/access_token?appid="+Constants.WX_APP_ID+"&secret="+Constants.WX_APP_SECRET+"&code="+code+"&grant_type=authorization_code", method);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("WXUserBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            new WXuser(jsonObject.getString("access_token"),jsonObject.getString("openid"));
        } catch (JSONException e) {
//            UserChangeActivity.instance.handler.sendEmptyMessage(Constants.FAILURE);
            e.printStackTrace();
        }catch (NullPointerException e){
//            UserChangeActivity.instance.handler.sendEmptyMessage(Constants.FAILURE);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
//        UserChangeActivity.instance.handler.sendEmptyMessage(Constants.FAILURE);
    }
    class WXuser implements HttpListener<String>{
        public  WXuser(String access_token,String openid){
            RequestMethod method = RequestMethod.GET;// 默认get请求
            mRequest=  NoHttp.createStringRequest("https://api.weixin.qq.com/sns/userinfo", method);
            mRequest.add("access_token",access_token);
            mRequest.add("openid",openid);
            new LogUtil("WXuser",access_token);
            new LogUtil("WXuser",openid);
            // 添加到请求队列
            CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {

//            try {
//                new LogUtil("WXuser",response.get());
//                JSONObject object=new JSONObject(response.get());
////                UserChangeActivity.instance.entity.setAvater(object.getString("headimgurl"));
//                if(object.getString("sex").equals("1")){
//                    UserChangeActivity.instance.entity.setSex("男");
//                }else{
//                    UserChangeActivity.instance.entity.setSex("女");
//                }
//                UserChangeActivity.instance.entity.setUname(object.getString("nickname"));
//                //检查是否登录。没有登录则跳转到登录界面
//                UserChangeActivity.instance.oppenid=object.getString("openid");
//                new CheckOppenidBiz( UserChangeActivity.instance, UserChangeActivity.instance.handler, UserChangeActivity.instance.oppenid);
//
//            } catch (JSONException e) {
//                UserChangeActivity.instance.handler.sendEmptyMessage(Constants.FAILURE);
//                e.printStackTrace();
//            }catch (NullPointerException e){
//                UserChangeActivity.instance.handler.sendEmptyMessage(Constants.FAILURE);
//                e.printStackTrace();
//            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
//            UserChangeActivity.instance.handler.sendEmptyMessage(Constants.FAILURE);
        }
    }
}
