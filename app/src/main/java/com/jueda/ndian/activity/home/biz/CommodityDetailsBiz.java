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
 * Created by Administrator on 2016/6/28.
 * 商品详情
 */
public class CommodityDetailsBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private ArrayList<CommodityEntity> list;
    private double screenWidth;
    public CommodityDetailsBiz(Context context, String id, Handler handler, ArrayList<CommodityEntity> list,double screenWidth) {
        this.handler=handler;
        this.screenWidth=screenWidth;
        this.list=list;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.COMMODITY_DETAILS, method);
        mRequest.add("id",id);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("CommodityDetailsBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            /**获取数据成功*/
            if(status.equals("1")){
                JSONObject object=jsonObject.getJSONObject("data");
                CommodityEntity entity=new CommodityEntity();
                entity.setId(object.getString("id"));
                entity.setTitle(object.getString("title"));
                entity.setPrice(object.getString("price"));
                entity.setFreightage(object.getString("freightage"));
                entity.setImg(object.getString("img"));
                entity.setCreat_time(object.getString("creat_time"));
                entity.setOld_price(object.getString("orig_price"));

                JSONArray jsonArray = new JSONArray(object.getString("img_arr"));
                String image[] = new String[jsonArray.length()];
                int hight[]=new int[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    image[i] = object1.getString("url");
                    hight[i]= (int) ((screenWidth/Double.parseDouble(object1.getString("width")))*Double.parseDouble(object1.getString("height")));
                }
                entity.setImgs_height(hight);
                entity.setImgs(image);
                list.add(entity);
                Message message=new Message();
                message.obj=list;
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
