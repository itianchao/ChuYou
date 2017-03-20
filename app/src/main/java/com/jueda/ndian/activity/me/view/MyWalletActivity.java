package com.jueda.ndian.activity.me.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.me.UserContent;
import com.jueda.ndian.activity.me.biz.BalanceBiz;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.LoginOutUtil;
import com.jueda.ndian.utils.ToastShow;

/**
 * 我的收益
 */
public class MyWalletActivity extends AppCompatActivity implements View.OnClickListener,UserContent {
    public static  MyWalletActivity instance=null;
    private Button withdrawal;//提现
    private TextView MoneyTextView;//爱心豆
    private Button top_up;//充值

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**获取余额成功*/
                case Constants.ON_SUCCEED:
                    PersonalFragment.userObservable.money();
                    PersonalFragment.userObservable.bean();
                    break;
                /**失败*/
                case Constants.FAILURE:
                    new ToastShow(MyWalletActivity.this,getResources().getString(R.string.For_failure),1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new LoginOutUtil(MyWalletActivity.this);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        instance=this;
        setContentView(R.layout.activity_my_wallet);
        InitView();
        setData();
        setOnClick();
    }
    private void InitView() {
        PersonalFragment.userObservable.add(this);
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.My_wallet), true);
        MoneyTextView=(TextView)findViewById(R.id.MoneyTextView);
        top_up=(Button)findViewById(R.id.top_up);
        withdrawal=(Button)findViewById(R.id.Withdrawal);
    }
    @Override
    protected void onDestroy() {
        PersonalFragment.userObservable.delect(this);
        super.onDestroy();
    }
    public void setData() {
        new BalanceBiz(MyWalletActivity.this,handler);
    }
    private void setOnClick() {
        withdrawal.setOnClickListener(this);
        top_up.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            //充值
            case R.id.top_up:
                startActivity(new Intent(MyWalletActivity.this,RechargeActivity.class));
                break;
            //余额提现
            case R.id.Withdrawal:
                intent=new Intent(MyWalletActivity.this,WithdrawActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void bean() {

    }

    @Override
    public void money() {
        MoneyTextView.setText(ConstantsUser.userEntity.getLove_bean());
    }
}
