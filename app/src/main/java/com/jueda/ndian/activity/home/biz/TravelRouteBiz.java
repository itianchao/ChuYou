package com.jueda.ndian.activity.home.biz;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.entity.CommentsEntity;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.entity.TopicCommentsEntity;
import com.jueda.ndian.entity.TravelEntity;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.view.MyRefreshListView;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/26.
 * 获取旅游路线及评论
 */
public class TravelRouteBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private ArrayList<CommentsEntity> entityList;
    private TravelEntity travelEntity;
    private int page;
    public TravelRouteBiz(Context context, Handler handler, ArrayList<CommentsEntity> entityList,TravelEntity travelEntity,String did,int page){
        this.handler=handler;
        this.page=page;
        this.travelEntity=travelEntity;
        this.entityList=entityList;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TOURISM_ROUTE, method);
        mRequest.add("p",page);
        mRequest.add("did",did);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("TravelRouteBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                JSONObject object = jsonObject.getJSONObject("data");
                if(page==1) {
                    travelEntity.setDid(object.getString("did"));
                    travelEntity.setTitle(object.getString("title"));
                    travelEntity.setBig_img(object.getString("big_img"));
                    travelEntity.setMoney(object.getString("money"));
                    travelEntity.setCost_price(object.getString("cost_price"));
                    travelEntity.setCount(object.getString("count"));
                    travelEntity.setStatus(object.getString("status"));
                    travelEntity.setCreat_time(object.getString("creat_time"));
                    travelEntity.setComment_count(object.getString("comment_count"));
                    ArrayList<HashMap> list = new ArrayList<>();
                    JSONArray array = new JSONArray(object.getString("content"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object1 = array.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("text", object1.getString("text"));
                        map.put("url", object1.getJSONObject("img").getString("url"));
                        list.add(map);
                    }
                    travelEntity.setContent(list);
                    //评论
                    JSONArray jsonArray=new JSONArray(object.getString("comments"));
                    for(int j=0;j<jsonArray.length();j++){
                        JSONObject object2=jsonArray.getJSONObject(j);
                        CommentsEntity entity=new CommentsEntity();
                        entity.setPid(object2.getString("id"));
                        entity.setPuid(object2.getString("uid"));
                        entity.setPname(object2.getString("uname"));
                        entity.setPavatar(object2.getString("avatar"));
                        entity.setPcontent(object2.getString("content"));
                        entity.setPcreat_time(object2.getString("creat_time"));
                        entity.setBy_id(object2.getString("by_id"));
                        entity.setBy_uname(object2.getString("by_uname"));
                        entity.setBy_uid(object2.getString("by_uid"));
                        entityList.add(entity);
                    }
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("comment", entityList);
                    bundle.putSerializable("entity",travelEntity);
                    Message message=new Message();
                    message.what=Constants.ON_SUCCEED;
                    message.obj=bundle;
                    handler.sendMessage(message);
                }else{
                    /**第二页以上只获取评论数*/
                    /**添加评论数据*/
                    JSONArray array = new JSONArray(object.getString("comments"));
                    if(array.length()==0){
                        handler.sendEmptyMessage(Constants.FAILURE);
                    }else {
                        for(int j=0;j<array.length();j++){
                            JSONObject object2=array.getJSONObject(j);
                            CommentsEntity entity=new CommentsEntity();
                            entity.setPid(object2.getString("id"));
                            entity.setPuid(object2.getString("uid"));
                            entity.setPname(object2.getString("uname"));
                            entity.setPavatar(object2.getString("avatar"));
                            entity.setPcontent(object2.getString("content"));
                            entity.setPcreat_time(object2.getString("creat_time"));
                            entity.setBy_id(object2.getString("by_id"));
                            entity.setBy_uname(object2.getString("by_uname"));
                            entity.setBy_uid(object2.getString("by_uid"));
                            entityList.add(entity);
                        }
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("comment",entityList);
                        bundle.putSerializable("entity",travelEntity);
                        Message message=new Message();
                        message.what=Constants.ON_SUCCEED;
                        message.obj=bundle;
                        handler.sendMessage(message);
                    }
                }
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
