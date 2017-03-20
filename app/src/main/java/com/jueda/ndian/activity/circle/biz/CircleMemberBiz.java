package com.jueda.ndian.activity.circle.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

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
import java.util.List;

import sortListView.SortModel;

/**
 * 获取成员
 * Created by Administrator on 2016/4/29.
 */
public class CircleMemberBiz implements HttpListener<String> {
    private Request mRequest;
    private Context context;
    private Handler handler;
    private List<SortModel> sourceDateList;
    private String cid;
    public CircleMemberBiz(Context context, Handler handler, List<SortModel> sourceDateList, String cid){
        this.context=context;
        this.handler=handler;
        this.cid=cid;
        this.sourceDateList=sourceDateList;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.CIRCLES_MEMBER, method);
        mRequest.add("cid",cid);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("CircleMemberBiz",response.get());
            JSONObject object=new JSONObject(response.get());
            String status=object.getString("status");
            /**数据获取成功*/
            if(status.equals("1")){
                JSONObject jsonObject=object.getJSONObject("data");
                JSONArray array=new JSONArray(jsonObject.getString("member"));
                for(int i=0;i<array.length();i++){
                    JSONObject object1=array.getJSONObject(i);
                    SortModel entity=new SortModel();
                    entity.setName(object1.getString("uname"));
                    entity.setAvatar(object1.getString("avatar"));
                    entity.setUid(object1.getString("uid"));
                    entity.setIs_host(jsonObject.getString("is_host"));
                    entity.setOwner_id(jsonObject.getString("owner_id"));

                    sourceDateList.add(entity);
                }
                Message message=new Message();
                message.what=Constants.ON_SUCEED_THREE;
                message.obj=sourceDateList;
                handler.sendMessage(message);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE_FOUR);
            }
        } catch (JSONException e) {
            handler.sendEmptyMessage(Constants.FAILURE_FOUR);
            e.printStackTrace();
        }catch (NullPointerException e){
            handler.sendEmptyMessage(Constants.FAILURE_FOUR);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE_FOUR);
    }
}
