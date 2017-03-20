package com.jueda.ndian.activity.me.biz;

import android.app.Activity;
import android.os.Handler;

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
 * Created by Administrator on 2016/8/18.
 * 意见反馈
 */
public class FeedBackBiz  implements HttpListener<String> {
    private Handler handler;
    private Request mRequest;
    public FeedBackBiz(Activity activity, Handler handler, String qiniu_image, String content, String phone) {
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.FEEDBACK, method);
        mRequest.add("content", content);
        mRequest.add("phone",phone);
        mRequest.add("img",qiniu_image);
        mRequest.add("type","1");
        // 添加到请求队列
        CallServer.getRequestInstance().add(activity, 0, mRequest, this, true, true);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        try{
            new LogUtil("FeedBackBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            if(jsonObject.getString("status").equals("1")) {
                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE);
        }catch (NullPointerException e){
            handler.sendEmptyMessage(Constants.FAILURE);
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE);
    }
}
