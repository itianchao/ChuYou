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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.me.view.RechargeActivity;
import com.jueda.ndian.activity.home.biz.ExchangeGoodBiz;
import com.jueda.ndian.entity.TravelEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ToastShow;

/**
 * 兑换旅游票
 */
public class TraveExchangeActivity extends AppCompatActivity{
    private TextView recharge;//充值
    private TextView title;//标题
    private EditText name;//姓名
    private EditText phoneNumber;//手机号
    private Button exchange;//确定兑换
    private TextView bean;//爱心豆
    private PopupWindow popPrompt;
    private View layoutPrompt;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**成功*/
                case Constants.ON_SUCCEED:
                    new ToastShow(TraveExchangeActivity.this,getResources().getString(R.string.For_successful),1000);
                    if(PersonalFragment.instance!=null){
                        PersonalFragment.instance.updata();
                    }
                    TraveExchangeActivity.this.finish();
                    break;
                /**余额不足*/
                case Constants.FAILURE:
                    if(PersonalFragment.instance!=null){
                        PersonalFragment.instance.updata();
                    }
                    deficiency();
                    break;
                /**失败*/
                case Constants.FAILURE_TWO:
                    new ToastShow(TraveExchangeActivity.this,getResources().getString(R.string.exchange_failure),1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_trave_exchange);
        InitView();
        setOnClick();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.To_exchange_information), true);
        recharge=(TextView)findViewById(R.id.recharge);
        title=(TextView)findViewById(R.id.titles);
        bean=(TextView)findViewById(R.id.bean);
        name=(EditText)findViewById(R.id.name);
        phoneNumber=(EditText)findViewById(R.id.phoneNumber);
        exchange=(Button)findViewById(R.id.exchange);
        title.setText(getData().getTitle());
        bean.setText("兑换价格："+getData().getMoney()+"爱心豆");
        new ChangeText(bean.getText().toString(),getResources().getColor(R.color.text_love),bean,5,bean.getText().toString().length()-3);
    }
    private TravelEntity getData(){
        TravelEntity entity= (TravelEntity) getIntent().getSerializableExtra("entity");
        return entity;
    }

    private void setOnClick() {
        /**充值*/
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(TraveExchangeActivity.this,RechargeActivity.class));
            }
        });
        /**兑换*/
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("")||phoneNumber.getText().toString().equals("")){
                    new ToastShow(TraveExchangeActivity.this,getResources().getString(R.string.Incomplete_information),1000);
                }else{
                    new ExchangeGoodBiz(TraveExchangeActivity.this,handler,getData().getDid(),name.getText().toString().trim(),phoneNumber.getText().toString().trim());
                }

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
                    startActivity(new Intent(TraveExchangeActivity.this, RechargeActivity.class));

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
            popPrompt.showAtLocation(recharge, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
