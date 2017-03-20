package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import com.jueda.ndian.entity.CommodityEntity;
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
 * Created by Administrator on 2016/8/30.
 * 我发布的商品删除
 */
public class MyRelesaseDelectBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private CommodityEntity entity;
    public MyRelesaseDelectBiz(Context context, Handler handler, CommodityEntity entity){
        this.entity=entity;
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.MY_RELEASE_GOODS_DELECT, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("id",entity.getId());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("MyOrderBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                handler.sendEmptyMessage(Constants.ON_SUCEED_TWO);
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE_TWO);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE_TWO);
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
