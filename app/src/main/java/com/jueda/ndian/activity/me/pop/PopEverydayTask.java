package com.jueda.ndian.activity.me.pop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.home.biz.SignBiz;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoginOutUtil;
import com.jueda.ndian.utils.ToastShow;

/**
 * Created by Administrator on 2016/9/2.
 * 日常任务奖励弹窗
 */
public class PopEverydayTask {
    private Activity activity;
    private View view;
    private PopupWindow popSign;
    private View layoutSign;
    private TextView signText;//签到文本
    private String content;//文本内容
    public static final String Zan="点赞奖励";
    public static final String Comment_Topic="评论奖励";
    public static final String Release_Topic="话题奖励";
    public static final String Head="上传头像奖励";
    /**
     * 签到调用
     * @param activity
     * @param view
     * @param signText
     */
    public PopEverydayTask(Activity activity,View view,TextView signText,String content){
        this.activity=activity;
        this.view=view;
        this.signText=signText;
        this.content=content;
        new SignBiz(activity, mhandler);
    }

    /***
     * 发布话题，话题点赞 、评论、上传头像
     * @param activity
     * @param view
     * @param content
     * @param bean
     */
    public PopEverydayTask(Activity activity,View view,String content,String bean){
        this.activity=activity;
        this.view=view;
        this.content=content;
        SignPop(bean);
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1500);
                    mhandler.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


   public  Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //签到成功
                case Constants.ON_SUCCEED:
                    signText.setEnabled(false);
                    signText.setText("已签到");
                    signText.setTextColor(activity.getResources().getColor(R.color.text_gray));
                    new Configuration().writeaSign_in(activity,System.currentTimeMillis());
                    SignPop((String) msg.obj);
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                sleep(1500);
                                mhandler.sendEmptyMessage(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
                case 1:
                    if(popSign!=null){
                        popSign.dismiss();
                        if(PersonalFragment.instance!=null){
                            PersonalFragment.instance.updata();
                        }
                    }
                    break;
                //签到失败
                case Constants.FAILURE:
                    new ToastShow(activity,"签到失败,请稍后再试",1000);
                    break;
                //已签
                case Constants.FAILURE_TWO:
                    signText.setEnabled(false);
                    signText.setText("已签到");
                    signText.setTextColor(activity.getResources().getColor(R.color.text_gray));
                    new Configuration().writeaSign_in(activity, System.currentTimeMillis());
                    String content= (String) msg.obj;
                    new ToastShow(activity,content,1000);
                    break;
                //用户失效
                case Constants.USER_FAILURE:
                    new LoginOutUtil(activity);
                    break;
            }
        }
    };


    /***
     * 签名爱心豆数量
     * @param sign
     */
    public void SignPop(String sign){
        if(view!=null){
            if (popSign != null && popSign.isShowing()) {
                popSign.dismiss();
            } else {
                layoutSign= activity.getLayoutInflater().inflate(
                        R.layout.sign_success, null);
                TextView contents=(TextView)layoutSign.findViewById(R.id.content);
                contents.setText(content);
                // 创建弹出窗口
                // 窗口内容为layoutLeft，里面包含一个ListView
                // 窗口宽度跟tvLeft一样
                popSign = new PopupWindow(layoutSign, ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT);
                TextView textView=(TextView)layoutSign.findViewById(R.id.textView);
                textView.setText("爱心豆+"+sign);
                ColorDrawable cd = new ColorDrawable(-0000);
                popSign.setBackgroundDrawable(cd);
                popSign.update();
                popSign.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                popSign.setTouchable(true); // 设置popupwindow可点击
                popSign.setOutsideTouchable(true); // 设置popupwindow外部可点击
                popSign.setFocusable(true); // 获取焦点
                // 设置popupwindow的位置（相对tvLeft的位置）
                popSign.showAtLocation(view, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                popSign.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // 如果点击了popupwindow的外部，popupwindow也会消失
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
    }
}
