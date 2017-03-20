package com.jueda.ndian.activity.me.view;

import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.me.UserContent;
import com.jueda.ndian.activity.me.biz.ExchangeBiz;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ToastShow;

/**
 * 兑换爱心豆
 */
public class ExchangeActivity extends AppCompatActivity implements UserContent {
    public static ExchangeActivity instance=null;
    private TextView love_bean;//爱心豆数量
    private Button one_love;//100豆
    private Button five_love;//500豆
    private Button ten_love;//1000豆
    private int Number;//记录
    //是否兑换
    private PopupWindow popPrompt;
    private View layoutPrompt;
    //爱心豆不足
    private PopupWindow popBean;
    private View layoutBean;

    //兑换成功
    private PopupWindow popSuccess;
    private View layoutSuccess;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //兑换成功
                case Constants.ON_SUCCEED:
                    Success(love_bean);
                    PersonalFragment.userObservable.bean();
                    PersonalFragment.userObservable.money();
                    break;
                //爱心豆不足
                case Constants.FAILURE_TWO:
                    Bean(love_bean);
                    setData();
                    break;
                //兑换失败
                case Constants.FAILURE:
                    new ToastShow(ExchangeActivity.this,getResources().getString(R.string.exchange_failure),1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_exchange);
        instance=this;
        InitView();
        setData();
        setOnClick();

    }


    @Override
    protected void onDestroy() {
        instance=null;
        PersonalFragment.userObservable.delect(this);
        super.onDestroy();
    }

    private void InitView() {
        PersonalFragment.userObservable.add(this);
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.exchange), true);
        love_bean=(TextView)findViewById(R.id.love_bean);
        one_love=(Button)findViewById(R.id.one_love);
        five_love=(Button)findViewById(R.id.five_love);
        ten_love=(Button)findViewById(R.id.ten_love);
    }
    public void setData(){
        love_bean.setText(ConstantsUser.userEntity.getLove_bean() + getResources().getString(R.string.dedication));
    }

    private void setOnClick() {
        one_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                are_you_sure(one_love);
                Number=100;
            }
        });
        five_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                are_you_sure(five_love);
                Number=500;
            }
        });
        ten_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                are_you_sure(ten_love);
                Number = 1000;
            }
        });
    }

    /**
     * 是否兑换
     */
    public void are_you_sure(final View view){
        if (popPrompt != null && popPrompt.isShowing()) {
            popPrompt.dismiss();
        } else {
            layoutPrompt =getLayoutInflater().inflate(
                    R.layout.pop_ok_cancel_content_general, null);
            TextView know=(TextView)layoutPrompt.findViewById(R.id.complete);
            TextView contents=(TextView)layoutPrompt.findViewById(R.id.content);
            TextView center=(TextView)layoutPrompt.findViewById(R.id.center);
            contents.setText("确定兑换吗？");

            center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPrompt.dismiss();
                }
            });
            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPrompt.dismiss();
                    if(Integer.parseInt(ConstantsUser.userEntity.getLove_bean())>=Number){
                       new ExchangeBiz(ExchangeActivity.this,handler,Number);
                    }else{
                        Bean(view);
                    }
                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popPrompt = new PopupWindow(layoutPrompt, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popPrompt.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
            popPrompt.update();
            popPrompt.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popPrompt.setTouchable(true); // 设置popupwindow可点击
            popPrompt.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popPrompt.setFocusable(true); // 获取焦点
            // 设置popupwindow的位置（相对tvLeft的位置）
            popPrompt.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            popPrompt.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popPrompt.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    /***
     * 爱心豆不足
     * @param view
     */
    public void Bean(View view){
        if (popBean != null && popBean.isShowing()) {
            popBean.dismiss();
        } else {
            layoutBean =getLayoutInflater().inflate(
                    R.layout.pop_ok_content_title_general, null);
            TextView know=(TextView)layoutBean.findViewById(R.id.know);
            TextView title=(TextView)layoutBean.findViewById(R.id.title);
            TextView contents=(TextView)layoutBean.findViewById(R.id.Content);
            contents.setText("爱心豆不足");
            title.setVisibility(View.GONE);
            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popBean.dismiss();
                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popBean = new PopupWindow(layoutBean, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popBean.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
            popBean.update();
            popBean.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popBean.setTouchable(true); // 设置popupwindow可点击
            popBean.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popBean.setFocusable(true); // 获取焦点
            // 设置popupwindow的位置（相对tvLeft的位置）
            popBean.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            popBean.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popBean.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
    /***
     * 兑换成功
     */
    public void Success(View view){
        if (popSuccess != null && popSuccess.isShowing()) {
            popSuccess.dismiss();
        } else {
            layoutSuccess =getLayoutInflater().inflate(
                    R.layout.pop_ok_content_title_general, null);
            TextView know=(TextView)layoutSuccess.findViewById(R.id.know);
            TextView title=(TextView)layoutSuccess.findViewById(R.id.title);
            TextView contents=(TextView)layoutSuccess.findViewById(R.id.Content);
            contents.setText("兑换成功");
            title.setVisibility(View.GONE);
            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popSuccess.dismiss();
                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popSuccess = new PopupWindow(layoutSuccess, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popSuccess.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
            popSuccess.update();
            popSuccess.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popSuccess.setTouchable(true); // 设置popupwindow可点击
            popSuccess.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popSuccess.setFocusable(true); // 获取焦点
            // 设置popupwindow的位置（相对tvLeft的位置）
            popSuccess.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            popSuccess.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popSuccess.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void bean() {
        setData();
    }

    @Override
    public void money() {

    }
}
