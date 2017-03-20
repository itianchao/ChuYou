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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 获取圈子详情
 * Created by Administrator on 2016/4/28.
 */
public class CirclesDetailsBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private ArrayList<CircleEntity> entityList;
    public CirclesDetailsBiz(Context context, Handler handler, ArrayList<CircleEntity> entityList, String cid){
        this.handler=handler;
        this.entityList=entityList;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.CIRCLES_DESC, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("cid",cid);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("CirclesDetailsBiz",response.get());
            JSONObject object=new JSONObject(response.get());
            String status=object.getString("status");
            /**获取数据成功*/
            if(status.equals("1")){
                JSONObject jsonObject=object.getJSONObject("data");
                CircleEntity entity=new CircleEntity();
                entity.setCid(jsonObject.getString("cid"));
                entity.setName(jsonObject.getString("cname"));
                entity.setCdesc(jsonObject.getString("cdesc"));
                entity.setAvatar(jsonObject.getString("c_avatar"));
                entity.setOwner_id(jsonObject.getString("owner_id"));
                entity.setOwner_name(jsonObject.getString("owner_name"));
                entity.setOwner_avatar(jsonObject.getString("owner_avatar"));
                entity.setC_member(jsonObject.getString("c_member"));
                entity.setInvite_code(jsonObject.getString("invite_code"));
                entity.setAdd_set(jsonObject.getString("add_set"));
                entity.setIs_host(jsonObject.getString("is_host"));
                entity.setIs_member(jsonObject.getString("is_member"));
                entity.setIs_collect(jsonObject.getString("is_collect"));
                entity.setDevotion(jsonObject.getString("devotion"));
                entityList.add(entity);

                Message message=new Message();
                message.obj=entityList;
                message.what=Constants.ON_SUCCEED;
                handler.sendMessage(message);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE);
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
