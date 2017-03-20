package com.jueda.ndian.activity.circle.biz;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.me.pop.PopEverydayTask;
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
 * Created by Administrator on 2016/8/12.
 * 点赞功能
 */
public class ThumbBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private Activity context;
    private View view;
    public  ThumbBiz(Activity context,Handler handler,String pid,View view){
        this.handler=handler;
        this.context=context;
        this.view=view;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.THUMB, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("pid",pid);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("ThumbBiz",response.get());
            JSONObject jsonObject = new JSONObject(response.get());
            if(jsonObject.getString("status").equals("1")){
                if(!jsonObject.getJSONObject("data").getString("devotion").equals("0")){
                    new PopEverydayTask(context,view,PopEverydayTask.Zan,jsonObject.getJSONObject("data").getString("devotion"));
                    PersonalFragment.instance.updata();
                }
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else if(jsonObject.getString("status").equals("0"))
                new ToastShow(context,jsonObject.getString("message"),1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

    }
}
