package com.jueda.ndian.activity.circle.biz;

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
 * 新建圈子
 * Created by Administrator on 2016/5/5.
 */
public class CreateCircleBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    public CreateCircleBiz(Context context, Handler handler , String cname, String cdesc, String img_url){
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.CREATE_CIRRCLE, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("cdesc",cdesc);
        mRequest.add("cname",cname);
        mRequest.add("avatar", img_url);

        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("CreateCircleBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(status.equals("0")){
                handler.sendEmptyMessage(Constants.FAILURE);
            }else if(status.equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else if(status.equals("-1")){
                /**圈昵称被占用*/
                handler.sendEmptyMessage(Constants.FAILURE_THREE);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE_TWO);
            }
        } catch (JSONException e) {
            handler.sendEmptyMessage(Constants.FAILURE_TWO);
            e.printStackTrace();
        }catch (NullPointerException e){
            handler.sendEmptyMessage(Constants.FAILURE_TWO);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE_TWO);
    }
}
