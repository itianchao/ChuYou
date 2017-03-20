package com.jueda.ndian.activity.circle.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.activity.circle.view.CircleContentActivity;
import com.jueda.ndian.activity.me.view.MyTopicActivity;
import com.jueda.ndian.entity.CommentsEntity;
import com.jueda.ndian.entity.TopicCommentsEntity;
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
 * 评论话题/旅游路线
 * Created by Administrator on 2016/5/6.
 */
public class CommentPostsBiz  implements HttpListener<String> {
    private Request mRequest;
    private Handler handler;
    private String who;
    /**回复*/
    public CommentPostsBiz(Context context, Handler handler,String content,String pid,String by_id,String who){
        this.handler=handler;
        this.who=who;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        if(who.equals(CircleContentActivity.TAG)||who.equals(MyTopicActivity.TAG)||who.equals(MainActivity.TAG)){
            mRequest=  NoHttp.createStringRequest(Constants.COMMENT_POSTS, method);
            mRequest.add("pid",pid);
        }else{
            mRequest=  NoHttp.createStringRequest(Constants.COMMENT_TRAVE, method);
            mRequest.add("did",pid);
        }
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("content",content);
        mRequest.add("by_id",by_id);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }
    /**评论*/
    public CommentPostsBiz(Context context, Handler handler,String content,String pid,String who){
        this.handler=handler;
        this.who=who;
        RequestMethod method = RequestMethod.POST;// 默认get请求
        new LogUtil("CommentPostsBiz",who);
        if(who.equals(CircleContentActivity.TAG)||who.equals(MyTopicActivity.TAG)||who.equals(MainActivity.TAG)){
            mRequest=  NoHttp.createStringRequest(Constants.COMMENT_POSTS, method);
            mRequest.add("pid",pid);
        }else{
            mRequest=  NoHttp.createStringRequest(Constants.COMMENT_TRAVE, method);
            mRequest.add("did",pid);
        }
        mRequest.add("user_token", ConstantsUser.userEntity.getUserToken());
        mRequest.add("content",content);

        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, true);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        new LogUtil("CommentPostsBiz",response.get());
        try {
            JSONObject jsonObject=new JSONObject(response.get());
            String status=jsonObject.getString("status");
            if(status.equals("1")){
                /**评论成功*/
                JSONObject object=jsonObject.getJSONObject("data");
                //话题
                if(who.equals(CircleContentActivity.TAG)||who.equals(MyTopicActivity.TAG)||who.equals(MainActivity.TAG)){
                    TopicCommentsEntity entity=new TopicCommentsEntity();
                    entity.setPuid(object.getString("uid"));
                    entity.setPname(object.getString("uname"));
                    entity.setPavatar(object.getString("avatar"));
                    entity.setPcontent(object.getString("content"));
                    entity.setPcreat_time(object.getString("creat_time"));
                    entity.setBy_id(object.getString("by_id"));
                    entity.setPid(object.getString("id"));
                    entity.setBy_uname(object.getString("by_uname"));
                    entity.setDevotion(object.getString("devotion"));
                    Message message=new Message();
                    message.obj=entity;
                    message.what=Constants.ON_SUCEED_TWO;
                    handler.sendMessage(message);
                }else{
                    //旅游
                    CommentsEntity entity=new CommentsEntity();
                    entity.setPuid(object.getString("uid"));
                    entity.setPname(object.getString("uname"));
                    entity.setPavatar(object.getString("avatar"));
                    entity.setPcontent(object.getString("content"));
                    entity.setPcreat_time(object.getString("creat_time"));
                    entity.setBy_id(object.getString("by_id"));
                    entity.setPid(object.getString("id"));
                    entity.setBy_uname(object.getString("by_uname"));
                    Message message=new Message();
                    message.obj=entity;
                    message.what=Constants.ON_SUCEED_TWO;
                    handler.sendMessage(message);
                }
            }else if(jsonObject.getString("status").equals("10007")){
                /**用户失效*/
                handler.sendEmptyMessage(Constants.USER_FAILURE);
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
