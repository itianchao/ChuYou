package com.jueda.ndian.activity.circle.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.CircleEntity;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.LogUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 获取推荐的圈子
 * Created by Administrator on 2016/4/27.
 */
public class RecomendCircleBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private ArrayList<CircleEntity> recommendList;
    public RecomendCircleBiz(Context context, Handler handler, ArrayList<CircleEntity> recommendList){
        this.handler=handler;
        this.recommendList=recommendList;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.RECOMMEND_CIRCLE, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("RecomendCircleBiz",response.get());
            JSONObject object = new JSONObject(response.get());
            String status=object.getString("status");
            if(status.equals("1")){
                JSONArray array=new JSONArray(object.getString("data"));
                for(int i=0;i<array.length();i++){
                    JSONObject jsonObject=array.getJSONObject(i);
                    CircleEntity entity=new CircleEntity();
                    entity.setName(jsonObject.getString("cname"));
                    entity.setAvatar(jsonObject.getString("avatar"));
                    entity.setCid(jsonObject.getString("cid"));
                    entity.setCdesc(jsonObject.getString("cdesc"));
                    entity.setCount(jsonObject.getString("count"));
                    recommendList.add(entity);
                }
            }
            Message message=new Message();
            message.obj=recommendList;
            message.what=Constants.ON_SUCEED_TWO;
            handler.sendMessage(message);
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
