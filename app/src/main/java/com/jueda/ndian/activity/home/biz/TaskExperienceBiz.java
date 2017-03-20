package com.jueda.ndian.activity.home.biz;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.entity.AppEntity;
import com.jueda.ndian.entity.CircleEntity;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.utils.isAvilible;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 2016/8/23.
 * 体验任务详情数据
 */
public class TaskExperienceBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private Activity context;
    private String who;
    private String cid;
    public TaskExperienceBiz(Activity context,String who, Handler handler, String aid,String cid){
        this.handler=handler;
        this.who=who;
        this.cid=cid;
        this.context=context;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.EXPEROEMCE, method);
        mRequest.add("aid",aid);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("TaskExperienceBiz",response.get());
            JSONObject object=new JSONObject(response.get());
            String status=object.getString("status");
            /**获取数据成功*/
            if(status.equals("1")){
                JSONObject jsonObject=object.getJSONObject("data");
                AppEntity entity=new AppEntity();
                entity.setAid(jsonObject.getString("aid"));
                entity.setName(jsonObject.getString("aname"));
                entity.setReward(jsonObject.getString("reward"));
                entity.setEx_or_sh(jsonObject.getString("ex_or_sh"));
                entity.setDownloadUrl(jsonObject.getString("sdk_url"));
                entity.setPackageName(jsonObject.getString("sdk_name"));
                entity.setCid(cid);

                JSONObject jsonObject1=jsonObject.getJSONObject("required");
                entity.setRule(jsonObject1.getString("content"));
                JSONArray array=jsonObject1.getJSONArray("typical_drawing");
                String image[]=new String[array.length()];
                for(int i=0;i<array.length();i++){
                    image[i]=array.getString(i);
                }
                entity.setSample_image(image);

                if (!isAvilible.isAvilible(context, jsonObject.getString("sdk_name") + "")) {
                    entity.setKey(0);
                    entity.setState(2);
                    if (who.equals(MainActivity.instance.HOME)) {
                        MainActivity.instance.applist.add(entity);
                    } else if (who.equals(MainActivity.instance.CIRCLE)) {
//                                entity.setCid(cid);
                        MainActivity.instance.circleAppList.add(entity);
                    }
                } else {
                    entity.setKey(0);
                    entity.setState(1);
                    if (who.equals(MainActivity.instance.HOME)) {
                        MainActivity.instance.applist.add(entity);
                    }else if (who.equals(MainActivity.instance.CIRCLE)) {
//                                entity.setCid(cid);
                        MainActivity.instance.circleAppList.add(entity);
                    }
                }

                handler.sendEmptyMessage(Constants.ON_SUCEED_TWO);
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
