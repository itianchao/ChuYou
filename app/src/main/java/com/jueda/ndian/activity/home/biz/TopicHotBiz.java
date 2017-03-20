package com.jueda.ndian.activity.home.biz;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.AppEntity;
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
 * Created by Administrator on 2016/8/15.
 * 热门话题
 */
public class TopicHotBiz  implements HttpListener<String> {
    private ArrayList<TopicEntity> entityList;
    private Handler handler;
    private Request mRequest;
    public TopicHotBiz(Activity activity, Handler handler, ArrayList<TopicEntity> entityList, int page) {
        this.entityList=entityList;
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TOPIC_HOT, method);
        mRequest.add("p", page);
        mRequest.add("uuid", ConstantsUser.userEntity.getUid());
        // 添加到请求队列
        CallServer.getRequestInstance().add(activity, 0, mRequest, this, true, false);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("TopicHotBiz",response.get());
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
                        entityList.add(entity);
                    }
                    Message message = new Message();
                    message.what = Constants.ON_SUCCEED;
                    message.obj = entityList;
                    handler.sendMessage(message);
                }else{
                    handler.sendEmptyMessage(Constants.FAILURE);
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
