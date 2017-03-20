package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.MessageEntity;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.utils.getCurrentTime;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 获取消息列表
 * Created by Administrator on 2016/5/9.
 */
public class MessageListBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private  ArrayList<MessageEntity> entityList;
    private int who;//1表示为圈子审核 2表示消息
    private int page;

    //消息
    public MessageListBiz(Context context, Handler handler, ArrayList<MessageEntity> entityList, int page) {
        this.handler=handler;
        this.entityList=entityList;
        this.page=page;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.MESSAGE_LIST, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("p",page);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("MessageListBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){

                JSONArray array=new JSONArray(jsonObject.getString("data"));
                if(array.length()!=0){
                    for(int i=0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        MessageEntity entity=new MessageEntity();
                        entity.setId(object.getString("id"));
                        if(object.getString("type").equals("5")||object.getString("type").equals("2")){
                            entity.setDid(object.getString("did"));
                        }
                        if(object.getString("type").equals("1")||object.getString("type").equals("15")){
                            entity.setPid(object.getString("pid"));
                        }
                        if(object.getString("type").equals("11")){
                            entity.setRid(object.getString("rid"));
                        }
                        entity.setCname(object.getString("cname"));
                        entity.setName(object.getString("uname"));
                        entity.setAvatar(object.getString("avatar"));
                        entity.setContent(object.getString("content"));
                        entity.setFrom_uid(object.getString("from_uid"));
                        entity.setCid(object.getString("cid"));

                        entity.setType(object.getString("type"));
                        entity.setCreat_time(getCurrentTime.getCurrentTime(Long.parseLong(object.getString("creat_time"))));
                        entity.setStatus(object.getString("status"));
                        entityList.add(entity);
                    }
                    Message message=new Message();
                    message.what=Constants.ON_SUCCEED;
                    message.obj=entityList;
                    handler.sendMessage(message);
                }else if(jsonObject.getString("status").equals("10007")){
                    /**用户失效*/
                    handler.sendEmptyMessage(Constants.USER_FAILURE);
                }else{
                    if(who==2){
                        if(page==1){
                            Message message=new Message();
                            message.what=Constants.ON_SUCCEED;
                            message.obj=entityList;
                            handler.sendMessage(message);
                        }else{
                            handler.sendEmptyMessage(Constants.FAILURE);
                        }
                    }else {
                        handler.sendEmptyMessage(Constants.FAILURE);
                    }
                }
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
