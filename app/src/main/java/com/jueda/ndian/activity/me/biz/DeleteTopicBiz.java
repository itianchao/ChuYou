package com.jueda.ndian.activity.me.biz;

import android.app.Activity;
import android.os.Handler;

import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.LogUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 删除单条话题
 * Created by Administrator on 2016/6/16.
 */
public class DeleteTopicBiz implements HttpListener<String> {
    private Request mRequest;
//    private Handler handler;
    public DeleteTopicBiz(Activity context, String pid){
//        this.handler=handler;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.DELETE_TOPIC, method);
        mRequest.add("pid",pid);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("DeleteTopicBiz",response.get());
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            /**获取数据成功*/
            if(status.equals("1")) {
//                handler.sendEmptyMessage(Constants.ON_SUCCEED);
                ConstantsUser.userEntity.setCount_post((Integer.parseInt(ConstantsUser.userEntity.getCount_post())-1)+"");
                if(PersonalFragment.instance!=null){
                    PersonalFragment.instance.setUser();
                }
            }
//            }else{
//                handler.sendEmptyMessage(Constants.FAILURE);
//            }
        } catch (JSONException e) {
//            handler.sendEmptyMessage(Constants.FAILURE);
            e.printStackTrace();
        }catch (NullPointerException e){
//            handler.sendEmptyMessage(Constants.FAILURE);
            e.printStackTrace();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
//        handler.sendEmptyMessage(Constants.FAILURE);
    }
}
