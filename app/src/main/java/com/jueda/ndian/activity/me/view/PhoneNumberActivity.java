package com.jueda.ndian.activity.me.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.biz.ChangePhoneBiz;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.CountdownUtil;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.ToastShow;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 更换手机号
 */
public class PhoneNumberActivity extends AppCompatActivity {
    private EditText phoneNumber;//输入手机号码
    private EditText code;//输入验证码
    private Button get_code;//获取验证码
    private Button submit;//提交
    private CountdownUtil tu=null;//计时器
    private final  int RESULT_ERROR=2;//短信错误

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**修改成功*/
                case Constants.ON_SUCCEED:
                    new ToastShow(PhoneNumberActivity.this,getResources().getString(R.string.Modify_the_success),1000);
                    PersonalCenterActivity.instance.setUser();
                    finish();
                    break;
                /**修改失败*/
                case Constants.FAILURE:
                    new ToastShow(PhoneNumberActivity.this,getResources().getString(R.string.Modify_the_failure),1000);
                    break;
                /**该手机已绑定*/
                case Constants.FAILURE_TWO:
                    new ToastShow(PhoneNumberActivity.this,getResources().getString(R.string.The_mobile_phone_has_been_binding),1000);
                    if(tu!=null){
                        tu.cancel();
                        tu.onFinish();
                    }
                    break;
                /**验证码错误*/
                case Constants.FAILURE_THREE:
                    String error= (String) msg.obj;
                    if(error.equals("468")){
                        new ToastShow(PhoneNumberActivity.this,"验证码错误",1000);
                    }else if(error.equals("466")){
                        new ToastShow(PhoneNumberActivity.this,"验证码为空",1000);
                    }else if(error.equals("456")){
                        new ToastShow(PhoneNumberActivity.this,"手机号为空",1000);
                    }else if(error.equals("457")){
                        new ToastShow(PhoneNumberActivity.this,"手机号码格式错误",1000);
                        if(tu!=null){
                            tu.cancel();
                            tu.onFinish();
                        }
                    }else if (error.equals("467")) {
                        new ToastShow(PhoneNumberActivity.this, "操作过于频繁，稍后再试", 1000);
                    }
                    break;
                //短信出错
                case RESULT_ERROR:
                    String errors= (String) msg.obj;
                        if (errors.equals("466")) {
                            new ToastShow(getApplicationContext(), "验证码为空", 1000);
                        } else if (errors.equals("456")) {
                            new ToastShow(getApplicationContext(), "手机号为空", 1000);
                        } else if (errors.equals("457")) {
                            new ToastShow(getApplicationContext(), "手机号码格式错误", 1000);
                            if(tu!=null){
                                tu.cancel();
                                tu.onFinish();
                            }
                        } else if (errors.equals("603")) {
                            new ToastShow(getApplicationContext(), "手机号码格式错误", 1000);
                            if(tu!=null){
                                tu.cancel();
                                tu.onFinish();
                            }
                        }  else if (errors.equals("478")||errors.equals("465")||errors.equals("464")||errors.equals("463")||errors.equals("477")||errors.equals("476")) {
                            new ToastShow(getApplicationContext(), "短信发送超过限额", 1000);
                            if(tu!=null){
                                tu.cancel();
                                tu.onFinish();
                            }
                        }else if(errors.equals("472")||errors.equals("467")||errors.equals("462")){
                            new ToastShow(getApplicationContext(), "操作过于频繁，稍后再试", 1000);
                            if(tu!=null){
                                tu.cancel();
                                tu.onFinish();
                            }
                        }else{
                            new ToastShow(getApplicationContext(), "获取失败", 1000);
                            if(tu!=null){
                                tu.cancel();
                                tu.onFinish();
                            }
                        }
                    break;
                //短信倒计时
                case Constants.TIMING_OF:
                    long s=(Long) msg.obj/1000;
                    get_code.setText(s+"");
                    break;
                //倒计时结束
                case Constants.CMT_TIME_MARKSTOP:
                    tu.cancel();
                    tu=null;
                    get_code.setEnabled(true);
                    get_code.setClickable(true);
                    get_code.setText(getResources().getString(R.string.To_resend));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        InitSDK();
        setContentView(R.layout.activity_phone_number);
        InitView();
        setOnClick();
    }

    private void setOnClick() {
        new KeyboardManage().CloseKeyboard(get_code,PhoneNumberActivity.this);
        get_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumber.getText().toString().length()!=0){
                    Timing();
                    SMSSDK.getVerificationCode("86", phoneNumber.getText().toString().trim());
                    get_code.setClickable(false);
                    get_code.setBackgroundResource(R.drawable.kuang_corners_button_gray);
                }else{
                    new ToastShow(PhoneNumberActivity.this, getResources().getString(R.string.Mobile_phone_number_is_empty), 1000);
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumber.getText().toString().length()!=0){
                    if(get_code.getText().toString().length()!=0){
                        new ChangePhoneBiz(PhoneNumberActivity.this,handler,"phone",phoneNumber.getText().toString(),true,code.getText().toString().trim());
                    }else{
                        new ToastShow(PhoneNumberActivity.this, getResources().getString(R.string.Incomplete_information), 1000);
                    }
                }else{
                    new ToastShow(PhoneNumberActivity.this, getResources().getString(R.string.Mobile_phone_number_is_empty), 1000);
                }
            }
        });
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.replace_a_phone_number), true);

        phoneNumber=(EditText)findViewById(R.id.phoneNumber);
        code=(EditText)findViewById(R.id.code);
        get_code=(Button)findViewById(R.id.get_code);
        submit=(Button)findViewById(R.id.submit);
    }
    private void InitSDK() {
        //mob短信初始化
        SMSSDK.initSDK(this, Constants.APPKEY, Constants.APPSECRET);
        EventHandler eh=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //短信已发送
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                    try {
                        Throwable throwable = (Throwable) data;
                        throwable.printStackTrace();
                        JSONObject object = new JSONObject(throwable.getMessage());

                        Message message=new Message();
                        message.obj=object.getString("status");
                        message.what=RESULT_ERROR;
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }
    //计时开始
    private void Timing() {
        get_code.setClickable(false);
        get_code.setEnabled(false);
        tu=new CountdownUtil(120000, 1000, handler);
        tu.start();
    }
    @Override
    protected void onDestroy() {
        if(tu!=null){
            tu.cancel();
        }
        super.onDestroy();
    }
}
