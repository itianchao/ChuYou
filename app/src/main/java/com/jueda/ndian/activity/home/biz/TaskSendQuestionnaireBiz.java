package com.jueda.ndian.activity.home.biz;

import android.app.Activity;
import android.os.Handler;

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
 * Created by Administrator on 2016/8/31.
 * 发布问卷任务
 */
public class TaskSendQuestionnaireBiz implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private Activity context;
    public TaskSendQuestionnaireBiz(Activity context, Handler handler, String cid, String reward, String sdk_url, String content, String title, String number, String[] img_arr){
        this.handler=handler;
        this.context=context;
        StringBuffer image = new StringBuffer();
        for(int i=0;i<img_arr.length;i++){
            if(img_arr.length==i+1){
                image.append(img_arr[i]);
            }else {
                image.append(img_arr[i] + ",");
            }
        }

        RequestMethod method = RequestMethod.POST;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.TASK_SEND_QUESTIONNAIRE, method);
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("reward",reward);
        mRequest.add("sdk_url",sdk_url);
        mRequest.add("content",content);
        mRequest.add("img_arr",image.toString());
        mRequest.add("title",title);
        mRequest.add("cid",cid);
        mRequest.add("num",number);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, false);
    }
    @Override
    public void onSucceed(int what, Response<String> response) {
        try {
            new LogUtil("TaskSendActivityBiz",response.get());
            JSONObject jsonObject = new JSONObject(response.get());
            if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
            }else if(jsonObject.getString("status").equals("1")){
                /**获取余额成功*/
                ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(),  ConstantsUser.userEntity.getAvater(),  ConstantsUser.userEntity.getSex()
                        , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(),  ConstantsUser.userEntity.getSignature(),  ConstantsUser.userEntity.getDevotion()
                        , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken()
                        , ConstantsUser.userEntity.getCountDonationsDost(), jsonObject.getJSONObject("data").getString("love_bean"), ConstantsUser.userEntity.getCount_post(), ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                        , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());

                handler.sendEmptyMessage(Constants.ON_SUCCEED);
            }else if(jsonObject.getString("status").equals("-1")){
                ConstantsUser.setUserEntity(context, ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), ConstantsUser.userEntity.getUname(),  ConstantsUser.userEntity.getAvater(),  ConstantsUser.userEntity.getSex()
                        , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(),  ConstantsUser.userEntity.getSignature(),  ConstantsUser.userEntity.getDevotion()
                        , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken()
                        , ConstantsUser.userEntity.getCountDonationsDost(), jsonObject.getJSONObject("data").getString("love_bean"), ConstantsUser.userEntity.getCount_post(), ConstantsUser.userEntity.getSurplus(), ConstantsUser.userEntity.getCount_ticket()
                        , ConstantsUser.userEntity.getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                handler.sendEmptyMessage(Constants.FAILURE_TWO);
            }else{
                handler.sendEmptyMessage(Constants.FAILURE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE);
        }catch (NullPointerException e){
            e.printStackTrace();
            handler.sendEmptyMessage(Constants.FAILURE);
        }
    }
    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE);
    }
}
