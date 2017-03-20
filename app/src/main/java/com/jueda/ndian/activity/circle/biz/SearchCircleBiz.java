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
 * 搜索
 * Created by Administrator on 2016/4/27.
 */
public class SearchCircleBiz implements HttpListener<String> {
    private Request mRequest;
    private Context context;
    private Handler handler;
    private int page;//页数
    private String content;//搜索内容
    private boolean isLoding;//是否弹窗
    private ArrayList<CircleEntity> entitydList;
    public SearchCircleBiz(Context context, Handler handler, int page, String content, ArrayList<CircleEntity> entitydList,boolean isLoding){
        this.context=context;
        this.handler=handler;
        this.page=page;
        this.content=content;
        this.isLoding=isLoding;
        this.entitydList=entitydList;

        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.SEARCH_CIRCLES, method);
        mRequest.add("wd",content);
        mRequest.add("p",page);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, isLoding);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        new LogUtil("SearchCircleBiz",response.get());
        try {
            JSONObject object=new JSONObject(response.get());
            String status=object.getString("status");
            /**为1则表示有内容，为2表示没有内容*/
            if(status.equals("1")){
                JSONArray array=new JSONArray(object.getString("data"));
                for(int i=0;i<array.length();i++){
                    JSONObject jsonObject=array.getJSONObject(i);
                    CircleEntity entity=new CircleEntity();
                    entity.setCid(jsonObject.getString("cid"));
                    entity.setName(jsonObject.getString("cname"));
                    entity.setCdesc(jsonObject.getString("cdesc"));
                    entity.setAvatar(jsonObject.getString("avatar"));
                    entity.setCount(jsonObject.getString("count"));
                    entity.setIs_member(jsonObject.getString("is_member"));
                    entitydList.add(entity);
                }
                Message message=new Message();
                message.obj=entitydList;
                message.what=Constants.ON_SUCCEED;
                handler.sendMessage(message);
            }else{
                /**没有搜索到数据*/
                handler.sendEmptyMessage(Constants.FAILURE);
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
