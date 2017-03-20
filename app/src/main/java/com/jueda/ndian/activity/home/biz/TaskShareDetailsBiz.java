package com.jueda.ndian.activity.home.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
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
 * Created by Administrator on 2016/8/29.
 * 任务分享详情数据
 */
public class TaskShareDetailsBiz  implements HttpListener<String> {
    private Handler handler;
    private CommodityEntity entity;
    private Request mRequest;
    public TaskShareDetailsBiz(Context context, Handler handler, CommodityEntity entity) {
        this.handler=handler;
        this.entity=entity;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TASK_SHARE_DETALIS, method);
        mRequest.add("id",entity.getId());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("TaskShareDetailsBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            /**获取数据成功*/
            if(status.equals("1")){
                JSONObject object=jsonObject.getJSONObject("data");
                entity.setId(object.getString("id"));
                entity.setTitle(object.getString("title"));
                entity.setImg(object.getString("img"));
                entity.setOld_price(object.getString("orig_price"));
                entity.setBead(object.getString("eward_beans"));
                entity.setFreightage(object.getString("freightage"));
                entity.setCommission(object.getString("eward_beans"));
                entity.setContent(object.getString("desc"));
                entity.setCreat_time(object.getString("creat_time"));
                entity.setOfficial_or_personal(object.getString("off_per"));

                JSONArray jsonArray = new JSONArray(object.getString("img_arr"));
                String image[] = new String[jsonArray.length()];
                int hight[]=new int[jsonArray.length()];
                ArrayList<Photo> photoArrayList=new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    hight[i]= (int) ((MainActivity.instance.getScreenWidth()/Float.parseFloat(object1.getString("width")))*Float.parseFloat(object1.getString("height")));
                    Photo photo=new Photo();
                    photo.path = object1.getString("url");
                    image[i]=object1.getString("url");
                    photoArrayList.add(photo);
                }
                entity.setPhotoArrayList(photoArrayList);
                entity.setImgs_height(hight);
                entity.setImgs(image);
                Message message=new Message();
                message.obj=entity;
                message.what=Constants.ON_SUCCEED;
                handler.sendMessage(message);
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
