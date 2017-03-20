package com.jueda.ndian.activity.home.biz;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.entity.ADInfo;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.entity.TaskCommonEntity;
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
 * Created by Administrator on 2016/8/25.
 * 活动任务详情
 */
public class TaskDetailsActivityBiz implements HttpListener<String> {
    private Handler handler;
    private ArrayList<TaskCommonEntity> entityList;
    private Request mRequest;
    private int page;

    public TaskDetailsActivityBiz(Activity context, Handler handler, ArrayList<TaskCommonEntity> entityList, String aid,int page) {
        this.entityList=entityList;
        this.handler=handler;
        this.page=page;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TASK_ACTIVITY_DETAILS, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("aid",aid);
        mRequest.add("p",page);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("TaskDetailsActivityBiz",response.get());
            JSONObject object = new JSONObject(response.get());
            String status=object.getString("status");
            if(status.equals("1")){
                JSONObject jsonObject=object.getJSONObject("data");
                if(page==1){
                    TaskCommonEntity entity=new TaskCommonEntity();
                    entity.setAid(jsonObject.getString("id"));
                    entity.setUid(jsonObject.getString("uid"));
                    entity.setTitle(jsonObject.getString("title"));
                    entity.setDescript(jsonObject.getString("descript"));
                    entity.setReward(jsonObject.getString("reward"));
                    entity.setContent(jsonObject.getString("content"));
                    entity.setCreat_time(jsonObject.getString("creat_time"));
                    entity.setStatus(jsonObject.getString("status"));
                    entity.setIs_join(jsonObject.getString("is_join"));
                    JSONArray array=new JSONArray(jsonObject.getString("img_url"));
                    String image[]=new String[array.length()];
                    int hight[]=new int[array.length()];
                    ArrayList<Photo> photoArrayList=new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object1 = array.getJSONObject(i);
                        hight[i]= (int) ((MainActivity.instance.getScreenWidth()/Float.parseFloat(object1.getString("width")))*Float.parseFloat(object1.getString("height")));
                        Photo photo=new Photo();
                        photo.path = object1.getString("url");
                        image[i]=object1.getString("url");
                        photoArrayList.add(photo);
                    }
                    entity.setPhotoArrayList(photoArrayList);
                    entity.setImg_url(image);
                    entity.setImg_height(hight);

                    JSONArray jsonArray=new JSONArray(jsonObject.getString("enroll"));
                       ArrayList<String> enroll=new ArrayList<>();

                    if (jsonObject.getString("uid").equals(ConstantsUser.userEntity.getUid()+"")){
                        for(int i=0;i<jsonArray.length();i++){
                            enroll.add(jsonArray.getString(i));
                        }
                    }
                    entity.setEnroll(enroll);
                    entityList.add(entity);
                    Message message=new Message();
                    message.obj=entityList;
                    message.what=Constants.ON_SUCCEED;
                    handler.sendMessage(message);
                }else{
                    JSONArray jsonArray=jsonObject.getJSONArray("enroll");
                    if(jsonArray.length()>0){
                        ArrayList<String> enroll=entityList.get(0).getEnroll();
                        for(int i=0;i<jsonArray.length();i++){
                            enroll.add(jsonArray.getString(i));
                        }
                        entityList.get(0).setEnroll(enroll);
                        Message message=new Message();
                        message.obj=entityList;
                        message.what=Constants.ON_SUCCEED;
                        handler.sendMessage(message);
                    }else{
                        handler.sendEmptyMessage(Constants.FAILURE_TWO);
                    }
                }

            }else{
                handler.sendEmptyMessage(Constants.FAILURE);
            }
        } catch (JSONException e) {
            handler.sendEmptyMessage(Constants.FAILURE);
            e.printStackTrace();
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
