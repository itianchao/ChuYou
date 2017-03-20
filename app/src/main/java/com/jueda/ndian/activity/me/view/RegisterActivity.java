package com.jueda.ndian.activity.me.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.biz.CheckPhoneBiz;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.CountdownUtil;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.utils.ToastShow;
import org.json.JSONException;
import org.json.JSONObject;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
/**
 * 注册手机号检测
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG="RegisterActivity";
    public static RegisterActivity instance=null;
    private Button register;//注册\
    private EditText userName;//手机号
    private EditText passWord;//密码
    private Button get_code;//获取验证码
    private EditText code;//验证码输入
    private CountdownUtil tu=null;//计时器
    private  final int RESULT_OK=1;
    private final int RESULT_ERROR=2;
    private WaitDialog waitDialog;
    private int number=0;//次数，暂时先避免跳转多出

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //短信验证成功跳转
                case RESULT_OK:
                    if(waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    if(tu!=null){
                        tu.cancel();
                        tu.onFinish();
                    }
                    if(number==0){
                        number=1;
                        Intent intent=new Intent(RegisterActivity.this,RegisterUserActivity.class);
                        intent.putExtra("who","RegisterActivity");
                        intent.putExtra("phone",userName.getText().toString().trim());
                        intent.putExtra("password",passWord.getText().toString().trim());
                        startActivity(intent);
                    }
                    break;
                //短信出错
                case RESULT_ERROR:
                    if(waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    String error= (String) msg.obj;
                    new LogUtil("短信",error+"");
                    if(error.equals("468")){
                        new ToastShow(getApplicationContext(),"验证码错误",1000);
                    }else {
                        if (error.equals("466")) {
                            new ToastShow(getApplicationContext(), "验证码为空", 1000);
                        } else if (error.equals("456")) {
                            new ToastShow(getApplicationContext(), "手机号为空", 1000);
                        } else if (error.equals("457")) {
                            new ToastShow(getApplicationContext(), "手机号码格式错误", 1000);
                            if(tu!=null){
                                tu.cancel();
                                tu.onFinish();
                            }
                        } else if (error.equals("603")) {
                            new ToastShow(getApplicationContext(), "手机号码格式错误", 1000);
                            if(tu!=null){
                                tu.cancel();
                                tu.onFinish();
                            }
                        } else if (error.equals("478")||error.equals("465")||error.equals("464")||error.equals("463")||error.equals("477")||error.equals("476")) {
                            new ToastShow(getApplicationContext(), "短信发送超过限额", 1000);
                            if(tu!=null){
                                tu.cancel();
                                tu.onFinish();
                            }
                        }else if(error.equals("472")||error.equals("467")||error.equals("462")){
                            new ToastShow(getApplicationContext(), "操作过于频繁，稍后再试", 1000);
                            if(tu!=null){
                                tu.cancel();
                                tu.onFinish();
                            }
                        }else{
                            new ToastShow(getApplicationContext(), "短信错误", 1000);
                            if(tu!=null){
                                tu.cancel();
                                tu.onFinish();
                            }
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
                //手机号检测成功
                case Constants.ON_SUCCEED:
                    String status= (String) msg.obj;
                    if(status.equals("1")){
                        SMSSDK.submitVerificationCode("86", userName.getText().toString().trim(), code
                                .getText().toString());
                    }else{
                        if(waitDialog!=null&&waitDialog.isShowing()){
                            waitDialog.dismiss();
                        }
                        new ToastShow(RegisterActivity.this,R.string.The_phone_number_has_been_registered,1000);
                    }
                    break;
                //手机号检查失败
                case Constants.FAILURE:
                    if(waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    new ToastShow(RegisterActivity.this,R.string.Network_is_bad_please_try_again,1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
        new ChangeTitle(this);
        InitSDK();
        setContentView(R.layout.activity_register);
        InitView();
        setOnClick();
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
                        handler.sendEmptyMessage(RESULT_OK);

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

    private void setOnClick() {
        register.setOnClickListener(this);
        get_code.setOnClickListener(this);
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.registered), true);
        register=(Button)findViewById(R.id.register);
        get_code=(Button)findViewById(R.id.get_code);
        code=(EditText)findViewById(R.id.code);
        userName=(EditText)findViewById(R.id.userName);
        passWord=(EditText)findViewById(R.id.passWord);

    }
    @Override
    protected void onDestroy() {
        instance=null;
        if(tu!=null){
            tu.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        new KeyboardManage().CloseKeyboard(v,RegisterActivity.this);
        switch (v.getId()){
            //注册
            case R.id.register:
                /**判断是否输入完整*/
                if(userName.getText().toString().equals("")||passWord.getText().toString().equals("")||code.getText().toString().equals("")){
                        new ToastShow(RegisterActivity.this,getResources().getString(R.string.Incomplete_information),1000);
                }else {
                    if(passWord.getText().toString().length()>=6&&passWord.getText().toString().length()<=20){
                        if (userName.getText().toString().equals("")) {
                            new ToastShow(RegisterActivity.this, getResources().getString(R.string.Mobile_phone_number_is_empty), 1000);
                        }else {
                            //检查手机号是否注册
                            number=0;
                            waitDialog=new WaitDialog(RegisterActivity.this);
                            new CheckPhoneBiz(RegisterActivity.this, handler, userName.getText().toString().trim(),false);

                        }
                    }else{
                        new ToastShow(RegisterActivity.this,getResources().getString(R.string.Password_length_is_wrong),1000);
                    }
                }

                break;
            //获取验证码
            case R.id.get_code:
                //判断是否有网
                if(Constants.currentNetworkType==Constants.TYPE_NONE){
                    new ToastShow(RegisterActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
                }else {
                    if (userName.getText().toString().equals("")) {
                        new ToastShow(RegisterActivity.this, getResources().getString(R.string.Mobile_phone_number_is_empty), 1000);
                    } else {
                        Timing();
                        SMSSDK.getVerificationCode("86", userName.getText().toString().trim());
                    }
                }
                break;
        }
    }

}
