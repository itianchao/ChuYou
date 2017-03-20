package com.jueda.ndian.activity.me.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.me.UserContent;
import com.jueda.ndian.activity.me.biz.CashBiz;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.ToastShow;

/**
 * 提现
 */
public class WithdrawActivity extends AppCompatActivity implements UserContent {
    public static  WithdrawActivity instance=null;
    private TextView CanCarryTextView;//可提现
    private EditText moneyEditText;///输入的金额
    private EditText payAccountEditText;//支付金额
    private EditText NameTextView;//真实姓名
    private TextView submit;//提交
    private TextView moneyTextView;//设计字体颜色
    private Button exchange;//兑换

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**提交成功*/
                case Constants.ON_SUCCEED:
                    MyWalletActivity.instance.setData();
                    new ToastShow(WithdrawActivity.this,getResources().getString(R.string.submit_success),1000);
                    finish();
                    break;
                /**余额不足*/
                case Constants.FAILURE:
                    new ToastShow(WithdrawActivity.this,getResources().getString(R.string.Lack_of_balance),1000);
                    break;
                /**提交失败*/
                case Constants.FAILURE_TWO:
                    new ToastShow(WithdrawActivity.this,getResources().getString(R.string.submit_failure),1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面

                    new Configuration().writeaIsLogin(WithdrawActivity.this,false);
                    Intent intent = new Intent(WithdrawActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_withdraw);
        instance=this;
        InitView();
        setOnClick();
    }

    private void InitView() {
        PersonalFragment.userObservable.add(this);
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.withdrawal), true);
        CanCarryTextView=(TextView)findViewById(R.id.CanCarryTextView);
        moneyEditText=(EditText)findViewById(R.id.moneyEditText);
        payAccountEditText=(EditText)findViewById(R.id.payAccountEditText);
        NameTextView=(EditText)findViewById(R.id.NameTextView);
        submit=(TextView)findViewById(R.id.submit);
        moneyTextView=(TextView)findViewById(R.id.moneyTextView);
        exchange=(Button)findViewById(R.id.exchange);
        setData();

    }
    public void setData(){
        CanCarryTextView.setText(getResources().getString(R.string.Can_carry) + "：" + ConstantsUser.userEntity.getSurplus() + getResources().getString(R.string.yuan));
        new ChangeText(CanCarryTextView.getText().toString(),getResources().getColor(R.color.text_red),CanCarryTextView,4,CanCarryTextView.getText().toString().length()-1);
        new ChangeText(moneyTextView.getText().toString(),getResources().getColor(R.color.text_red),moneyTextView,3,moneyTextView.getText().toString().length()-1);
    }


    @Override
    protected void onDestroy() {
        PersonalFragment.userObservable.delect(this);
        instance=null;
        super.onDestroy();
    }

    private void setOnClick() {
        //去兑换
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WithdrawActivity.this,ExchangeActivity.class));
            }
        });
        /**监听输入的金额*/
        moneyEditText.addTextChangedListener(textWatcher);
        /***提交*/
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KeyboardManage().CloseKeyboard(submit,WithdrawActivity.this);
                if(moneyEditText.getText().toString().trim().equals("")||payAccountEditText.getText().toString().trim().equals("")||NameTextView.getText().toString().trim().equals("")){
                    new ToastShow(WithdrawActivity.this,getResources().getString(R.string.Incomplete_information),1000);
                }else if(Double.parseDouble(moneyEditText.getText().toString().trim())<100){
                    new ToastShow(WithdrawActivity.this,getResources().getString(R.string.The_minimum_withdrawal_20_yuan),1000);
                }else{
                    new CashBiz(WithdrawActivity.this,handler,moneyEditText.getText().toString().trim(),payAccountEditText.getText().toString().trim(),NameTextView.getText().toString().trim());
                }
            }
        });
    }
    //输入框变化
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if(!s.toString().equals("")){
                if(Double.parseDouble(s.toString())>Double.parseDouble(ConstantsUser.userEntity.getSurplus())){
                    moneyEditText.setText(Double.parseDouble(ConstantsUser.userEntity.getSurplus())+"");
                    moneyEditText.setSelection(moneyEditText.getText().toString().length());
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {

        }
    };

    @Override
    public void bean() {

    }

    @Override
    public void money() {
        setData();
    }
}
