package com.jueda.ndian.activity.home.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.CommodityEntity;
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
 * 分享赚豆列表
 */
public class TaskShareBiz  implements HttpListener<String> {
    private Handler handler;
    private ArrayList<CommodityEntity> entityList;
    private Request mRequest;
    public TaskShareBiz(Context context, Handler handler, int page, ArrayList<CommodityEntity> entityList) {
        this.handler=handler;
        this.entityList=entityList;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TASK_SHARE_LIST, method);
        mRequest.add("p",page);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("TaskShareBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            /**获取数据成功*/
            if(status.equals("1")){
                JSONArray array=new JSONArray(jsonObject.getString("data"));
                if(array.length()>0){
                    for(int i=0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        CommodityEntity entity=new CommodityEntity();
                        entity.setId(object.getString("id"));
                        entity.setTitle(object.getString("title"));
                        entity.setImg(object.getString("img"));
                        entity.setPrice(object.getString("price"));
                        entity.setFreightage(object.getString("freightage"));
                        entity.setOld_price(object.getString("orig_price"));
                        entity.setCommission(object.getString("eward_beans"));
                        entity.setCreat_time(object.getString("creat_time"));
                        entity.setOfficial_or_personal(object.getString("off_per"));
                        entityList.add(entity);
                    }
                    Message message=new Message();
                    message.obj=entityList;
                    message.what=Constants.ON_SUCCEED;
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
