package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.entity.ADInfo;
import com.jueda.ndian.entity.AppEntity;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.savedata.Configuration;
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
 * 广告横幅
 * Created by Administrator on 2016/6/15.
 */
public class BannerBiz implements HttpListener<String> {
    private Handler handler;
    private Request mRequest;
    private Context context;
    private int screenWidth;

    public BannerBiz(Context context, Handler handler, String who, int screenWidth) {
        this.handler=handler;
        this.context=context;
        this.screenWidth=screenWidth;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.BANNER, method);
        if(who.equals(CharityCircleFragment.TAG)){
            mRequest.add("from","2");
        }
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("BannerBiz",response.get());
            JSONObject object = new JSONObject(response.get());
            String status=object.getString("status");

            if(status.equals("1")){
                JSONArray array=new JSONArray(object.getString("data"));
                ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
                for(int i=0;i<array.length();i++){
                    JSONObject jsonObject=array.getJSONObject(i);
                    ADInfo adInfo=new ADInfo();
                    adInfo.setUrl(jsonObject.getString("img_url"));
                    adInfo.setId(jsonObject.getString("id"));
                    adInfo.setOpen_url(jsonObject.getString("open_url"));
                    adInfo.setType(jsonObject.getString("type"));
                    adInfo.setContent(jsonObject.getString("title"));
                    infos.add(adInfo);
                }

                Message message=new Message();
                message.what=Constants.ON_SUCEED_THREE;
                message.obj=infos;
                handler.sendMessage(message);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE_THREE);
            }
        } catch (JSONException e) {
            handler.sendEmptyMessage(Constants.FAILURE_THREE);
            e.printStackTrace();
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
