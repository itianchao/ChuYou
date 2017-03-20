package com.jueda.ndian.activity.circle.biz;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.entity.TopicEntity;
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
 * 获取话题
 * Created by Administrator on 2016/4/28.
 */
public class TopicBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private ArrayList<TopicEntity> topicList;//话题
    private int page;
    private String who="";
    /**圈内话题*/
    public TopicBiz(Context context, Handler handler, String cid, ArrayList<TopicEntity> topicList, int page){
        this.topicList=topicList;
        this.page=page;
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TOPIC, method);
        mRequest.add("uuid", ConstantsUser.userEntity.getUid());
        mRequest.add("cid", cid);
        mRequest.add("p", page);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    /**我的话题列表*/
    public TopicBiz(Context context, Handler handler, ArrayList<TopicEntity> topicList, int page){
        this.topicList=topicList;
        this.handler=handler;
        this.page=page;
        who="MyTopic";
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TOPIC, method);
        mRequest.add("uuid", ConstantsUser.userEntity.getUid());
        mRequest.add("uid", ConstantsUser.userEntity.getUid());
        mRequest.add("p", page);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    /**某个用户话题列表*/
    public TopicBiz(Context context, Handler handler, ArrayList<TopicEntity> topicList, int page,String uid){
        this.topicList=topicList;
        this.handler=handler;
        this.page=page;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TOPIC, method);
        mRequest.add("uuid", ConstantsUser.userEntity.getUid());
        mRequest.add("uid", uid);
        mRequest.add("p", page);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("TopicBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            /**获取数据成功*/
            if(status.equals("1")){
                JSONArray array=new JSONArray(jsonObject.getString("data"));
                if(array.length()>0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        TopicEntity entity = new TopicEntity();
                        entity.setPid(object.getString("pid"));
                        entity.setTitle(object.getString("title"));
                        entity.setContent(object.getString("content"));
                        entity.setCount(object.getString("count"));
                        entity.setUid(object.getString("uid"));
                        entity.setAvatar(object.getString("avatar"));
                        entity.setName(object.getString("uname"));
                        entity.setLike_count(object.getString("like_count"));
                        entity.setIs_like(object.getString("is_like"));
                        entity.setCreat_time(getCurrentTime.getCurrentTime(Long.parseLong(object.getString("creat_time"))));
                        /**获取图片*/
                        JSONArray jsonArray = new JSONArray(object.getString("img"));
                        String[] image = new String[jsonArray.length()];
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject object1 = jsonArray.getJSONObject(j);
                            image[j] = object1.getString("img_url");
                        }
                        entity.setImg(image);
                        topicList.add(entity);
                    }
                    Message message = new Message();
                    message.what = Constants.ON_SUCEED_TWO;
                    message.obj = topicList;
                    handler.sendMessage(message);
                }else{
                    if(page==1){
                        if(who.equals("MyTopic")){
                            handler.sendEmptyMessage(Constants.FAILURE_TWO);
                        }else {
                            Message message = new Message();
                            message.what = Constants.ON_SUCEED_TWO;
                            message.obj = topicList;
                            handler.sendMessage(message);
                        }
                    }else {
                        handler.sendEmptyMessage(Constants.FAILURE_TWO);
                    }
                }
            }else{
                handler.sendEmptyMessage(Constants.FAILURE_THREE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE_THREE);
        }catch (NullPointerException e){
            handler.sendEmptyMessage(Constants.FAILURE_THREE);
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE_THREE);
    }
}
