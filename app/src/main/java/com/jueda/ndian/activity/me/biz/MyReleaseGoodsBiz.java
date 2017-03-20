package com.jueda.ndian.activity.me.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.entity.CommodityEntity;
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
 * Created by Administrator on 2016/8/30.
 * 我发布商品列表
 */
public class MyReleaseGoodsBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private ArrayList<CommodityEntity> entityList;
    public MyReleaseGoodsBiz(Context context, Handler handler, ArrayList<CommodityEntity> entityList, int page){
        this.entityList=entityList;
        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.MY_RELEASE_GOODS_LIST, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("p",page);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("MyReleaseGoodsBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                JSONArray array=new JSONArray(jsonObject.getString("data"));
                if(array.length()==0){
                    handler.sendEmptyMessage(Constants.FAILURE);
                }else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        CommodityEntity entity = new CommodityEntity();
                        entity.setId(object.getString("id"));
                        entity.setTitle(object.getString("title"));
                        entity.setPrice(object.getString("price"));
                        entity.setCreat_time(object.getString("creat_time"));
                        entity.setSales(object.getString("sales_volume"));
                        entity.setImg(object.getString("img"));
                        entityList.add(entity);
                    }
                    Message message = new Message();
                    message.obj = entityList;
                    message.what = Constants.ON_SUCCEED;
                    handler.sendMessage(message);
                }
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
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
