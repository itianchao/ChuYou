package com.jueda.ndian.activity.home.view;

import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.me.view.AddDetailsActivity;
import com.jueda.ndian.activity.me.view.RechargeActivity;
import com.jueda.ndian.activity.home.biz.TaskExchangeDetailsSubmitBiz;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ToastShow;

/**
 * 立即兑换商品
 */
public class TaskExchangeDetailsSubmitActivity extends AppCompatActivity {
    private PopupWindow popPrompt;
    private View layoutPrompt;
    private TextView recharge;//充豆
    private TextView titles;//标题
    private TextView bean;//爱心豆
    private Button exchange;//兑换

    private TextView go_edit;//去编辑
    private RelativeLayout go_edit_relativeLatout;//去编辑
    private TextView address;//收货地址
    private TextView phoneNumber;//手机号
    private TextView name;//姓名

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**成功*/
                case Constants.ON_SUCCEED:
                    new ToastShow(TaskExchangeDetailsSubmitActivity.this,getResources().getString(R.string.For_successful),1000);
                    if(PersonalFragment.instance!=null){
                        PersonalFragment.instance.updata();
                    }
                    TaskExchangeDetailsSubmitActivity.this.finish();
                    break;
                /**余额不足*/
                case Constants.FAILURE:
                    if(PersonalFragment.instance!=null){
                        PersonalFragment.userObservable.bean();
                    }
                    deficiency();
                    break;
                /**失败*/
                case Constants.FAILURE_TWO:
                    new ToastShow(TaskExchangeDetailsSubmitActivity.this,getResources().getString(R.string.exchange_failure),1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_task_exchange_details_submit);
        InitView();
        setOnClick();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.To_exchange_information), true);
        recharge=(TextView)findViewById(R.id.recharge);
        titles=(TextView)findViewById(R.id.titles);
        bean=(TextView)findViewById(R.id.bean);
        go_edit=(TextView)findViewById(R.id.go_edit);
        exchange=(Button)findViewById(R.id.exchange);
        go_edit_relativeLatout=(RelativeLayout)findViewById(R.id.go_edit_relativeLatout);
        address=(TextView)findViewById(R.id.address);
        phoneNumber=(TextView)findViewById(R.id.phoneNumber);
        name=(TextView)findViewById(R.id.name);
        titles.setText(getData().getTitle());
        bean.setText("兑换价格："+getData().getBead()+"爱心豆");
        new ChangeText(bean.getText().toString(),getResources().getColor(R.color.text_orange),bean,5,bean.getText().toString().length()-3);
    }

    @Override
    protected void onResume() {
        if(ConstantsUser.userEntity.getAdd_location().equals("")||ConstantsUser.userEntity.getAdd_uname().equals("")||ConstantsUser.userEntity.getAdd_phoneNumber().equals("")){
            go_edit_relativeLatout.setVisibility(View.GONE);
            go_edit.setVisibility(View.VISIBLE);
        }else{
            go_edit.setVisibility(View.GONE);
            go_edit_relativeLatout.setVisibility(View.VISIBLE);
            address.setText(ConstantsUser.userEntity.getAdd_location());
            phoneNumber.setText(ConstantsUser.userEntity.getAdd_phoneNumber());
            name.setText(ConstantsUser.userEntity.getAdd_uname());
        }
        super.onResume();
    }

    private CommodityEntity getData(){
        CommodityEntity entity= (CommodityEntity) getIntent().getSerializableExtra("entity");
        return entity;
    }

    private void setOnClick() {
        //去编辑
        go_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TaskExchangeDetailsSubmitActivity.this,AddDetailsActivity.class);
                startActivity(intent);
            }
        });
        go_edit_relativeLatout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TaskExchangeDetailsSubmitActivity.this,AddDetailsActivity.class);
                startActivity(intent);
            }
        });
        //充豆
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaskExchangeDetailsSubmitActivity.this, RechargeActivity.class));
            }
        });
        //兑换
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TaskExchangeDetailsSubmitBiz(TaskExchangeDetailsSubmitActivity.this,handler,getData().getId());
            }
        });
    }
    /**
     * 爱心豆不足
     */
    private void deficiency(){
        if (popPrompt != null && popPrompt.isShowing()) {
            popPrompt.dismiss();
        } else {
            layoutPrompt =getLayoutInflater().inflate(
                    R.layout.pop_ok_cancel_content_general, null);
            TextView know=(TextView)layoutPrompt.findViewById(R.id.complete);
            TextView contents=(TextView)layoutPrompt.findViewById(R.id.content);
            TextView center=(TextView)layoutPrompt.findViewById(R.id.center);
            contents.setText("亲，您的爱心豆余额不足");
            know.setText("去充值");
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
                    startActivity(new Intent(TaskExchangeDetailsSubmitActivity.this, RechargeActivity.class));
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
            popPrompt.showAtLocation(layoutPrompt, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
}
