package com.jueda.ndian.activity.home.biz;

import android.content.Context;

import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.LogUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

/**
 * 定位提交
 * Created by Administrator on 2016/6/21.
 */
public class LocationBiz implements HttpListener<String> {
    private Request mRequest;
    public LocationBiz(Context context,String data){
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.LOCATION, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("data",data);

        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        if(response!=null) {


            new LogUtil("LocationBiz", response.get());
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
    }
}
