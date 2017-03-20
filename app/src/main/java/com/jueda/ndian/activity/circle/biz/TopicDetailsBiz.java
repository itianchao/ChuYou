package com.jueda.ndian.activity.circle.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.TopicCommentsEntity;
import com.jueda.ndian.entity.Photo;
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
 * 话题详情
 * Created by Administrator on 2016/5/3.
 */
public class TopicDetailsBiz implements HttpListener<String> {
    private Request mRequest;
    private Context context;
    private Handler handler;
    private ArrayList<TopicCommentsEntity> commentsEntityList;
    private int page;
    private float screenWidth;
    public TopicDetailsBiz(Context context, Handler handler, int page, String id, ArrayList<TopicCommentsEntity> commentsEntityList,float screenWidth){
        this.handler=handler;
        this.context=context;
        this.page=page;
        this.screenWidth=screenWidth;
        this.commentsEntityList=commentsEntityList;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TOPIC_DESC, method);
        mRequest.add("pid",id);
        mRequest.add("p",page);
        mRequest.add("uid", ConstantsUser.userEntity.getUid());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {

        try {
            new LogUtil("TopicDetailsBiz",response.get()+"");
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                JSONObject object=jsonObject.getJSONObject("data");
                if(page==1) {
                    TopicCommentsEntity entity = new TopicCommentsEntity();
                    entity.setAvatar(object.getString("avatar"));
                    entity.setName(object.getString("uname"));
                    entity.setPpid(object.getString("pid"));
                    entity.setCid(object.getString("cid"));
                    entity.setCname(object.getString("cname"));
                    entity.setTitle(object.getString("title"));
                    entity.setContent(object.getString("content"));
                    entity.setCount(object.getString("count"));
                    entity.setCreat_time(object.getString("creat_time"));
                    entity.setUid(object.getString("uid"));
                    entity.setCount_comments(object.getString("count_comments"));
                    entity.setIs_host(object.getString("is_host"));
                    entity.setLike_count(object.getString("like_count"));
                    entity.setIs_like(object.getString("is_like"));

                    JSONArray jsonArray = new JSONArray(object.getString("img"));
                    String image[] = new String[jsonArray.length()];
                    int hight[]=new int[jsonArray.length()];
                    ArrayList<Photo> photoArrayList=new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.getJSONObject(i);
                        hight[i]= (int) ((screenWidth/Float.parseFloat(object1.getString("width")))*Float.parseFloat(object1.getString("height")));
                        Photo photo=new Photo();
                        photo.path = object1.getString("url");
                        image[i]=object1.getString("url");
                        photoArrayList.add(photo);
                    }
                    entity.setPhotoArrayList(photoArrayList);
                    entity.setImg_height(hight);
                    entity.setImg(image);
                    commentsEntityList.add(entity);
                    /**第二页以上只获取评论数*/
                    /**添加评论数据*/
                    JSONArray array = new JSONArray(object.getString("comments"));
                        for (int j = 0; j < array.length(); j++) {
                            JSONObject object2 = array.getJSONObject(j);
                            TopicCommentsEntity entity1 = new TopicCommentsEntity();
                            entity1.setPid(object2.getString("id"));
                            entity1.setPuid(object2.getString("uid"));
                            entity1.setPname(object2.getString("uname"));
                            entity1.setPavatar(object2.getString("avatar"));
                            entity1.setPcontent(object2.getString("content"));
                            entity1.setPcreat_time(object2.getString("creat_time"));
                            entity1.setBy_id(object2.getString("by_id"));
                            entity1.setBy_uname(object2.getString("by_uname"));
                            entity1.setBy_uid(object2.getString("by_uid"));
                            commentsEntityList.add(entity1);
                        }
                    Message message=new Message();
                    message.what=Constants.ON_SUCCEED;
                    message.obj=commentsEntityList;
                    handler.sendMessage(message);
                }else{
                    /**第二页以上只获取评论数*/
                    /**添加评论数据*/
                    JSONArray array = new JSONArray(object.getString("comments"));
                    if(array.length()==0){
                        handler.sendEmptyMessage(Constants.FAILURE);
                    }else {
                        for (int j = 0; j < array.length(); j++) {
                            JSONObject object2 = array.getJSONObject(j);
                            TopicCommentsEntity entity1 = new TopicCommentsEntity();
                            entity1.setPid(object2.getString("id"));
                            entity1.setPuid(object2.getString("uid"));
                            entity1.setPname(object2.getString("uname"));
                            entity1.setPavatar(object2.getString("avatar"));
                            entity1.setPcontent(object2.getString("content"));
                            entity1.setPcreat_time(object2.getString("creat_time"));
                            entity1.setBy_id(object2.getString("by_id"));
                            entity1.setBy_uname(object2.getString("by_uname"));
                            entity1.setBy_uid(object2.getString("by_uid"));
                            commentsEntityList.add(entity1);
                        }
                        Message message=new Message();
                        message.what=Constants.ON_SUCCEED;
                        message.obj=commentsEntityList;
                        handler.sendMessage(message);
                    }
                }

            }else{
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
