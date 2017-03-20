package com.jueda.ndian.activity.me.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.me.biz.RegisterBiz;
import com.jueda.ndian.entity.UserEntity;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ToastShow;

/***
 * 注册用户信息
 */
public class RegisterUserActivity extends Activity implements View.OnClickListener{
    private Button submit;//提交
    private EditText NickNameTextView;//昵称输入
    private RadioButton man;//男
    private RadioButton woman;//女
    private EditText invitationCode;//邀请码
    private RelativeLayout relativeLayout;//弹窗相对位置
//    private TextView agreement;//服务协议点击按钮
    private String who;//判断是普通注册(RegisterActivity)还是第三方(LoginActivity)
    private String openid="";//第三方注册使用的id
    private String qq_wx="";//第三QQ还是微信
    private UserEntity entity_sns;//第三方信息
    private String phone="";//手机号码
    private String passWord="";//密码
    private String sex="男";//默认设置男
    private String textcontent="";//保存输入16个字符
    private WaitDialog dialog;//弹窗

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**注册成功*/
                case Constants.ON_SUCCEED:
                    /**保存登录状态*/
                    new Configuration().writeaIsLogin(RegisterUserActivity.this, true);
                    if(MainActivity.instance==null){
                        startActivity(new Intent(RegisterUserActivity.this,MainActivity.class));
                    }else{
                        MainActivity.instance.addTag();
                    }
                    //获取地址定位
                    if( NdianApplication.instance.mLocationClient!=null){
                        NdianApplication.instance.mLocationClient.start();
                    }
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    /**普通注册*/
                    new ToastShow(RegisterUserActivity.this,R.string.Registered_successfully,1000);
                    /**更新我的圈子*/
                    if(CharityCircleFragment.instance!=null){
                        CharityCircleFragment.instance.Reconnection();
                    }
                    if(who.equals(RegisterActivity.TAG)&& RegisterActivity.instance!=null){
                        RegisterActivity.instance.finish();
                    }
                    if(LoginActivity.instance!=null){
                        LoginActivity.instance.finish();
                    }
                    RegisterUserActivity.this.finish();
                    break;
                /**邀请码注册成功*/
                case Constants.ON_SUCEED_TWO:
                    /**保存登录状态*/
                    new Configuration().writeaIsLogin(RegisterUserActivity.this, true);
                    if(MainActivity.instance==null){
                        startActivity(new Intent(RegisterUserActivity.this,MainActivity.class));
                    }else{
                        MainActivity.instance.addTag();
                    }
                    //获取地址定位
                    if( NdianApplication.instance.mLocationClient!=null){
                        NdianApplication.instance.mLocationClient.start();
                    }
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    /**普通注册*/
                    new ToastShow(RegisterUserActivity.this,R.string.Registered_successfully,1000);
                    /**更新我的圈子*/
                    if(CharityCircleFragment.instance!=null){
                        CharityCircleFragment.instance.Reconnection();
                    }
                    if(who.equals(RegisterActivity.TAG)&&RegisterActivity.instance!=null){
                        RegisterActivity.instance.finish();
                    }
                    if(LoginActivity.instance!=null){
                        LoginActivity.instance.finish();
                    }
                    RegisterUserActivity.this.finish();
                    break;
                /**注册失败*/
                case Constants.FAILURE:
                    new ToastShow(RegisterUserActivity.this,R.string.Registered_failure,1000);
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    break;
                /**该昵称被占用*/
                case Constants.FAILURE_TWO:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    new ToastShow(RegisterUserActivity.this,getResources().getString(R.string.The_nickname_has_been_occupied),1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_register_user);
        InitView();
        setOnClick();
    }
    private void setOnClick() {
        submit.setOnClickListener(this);
        man.setOnClickListener(this);
        woman.setOnClickListener(this);
//        agreement.setOnClickListener(this);
        NickNameTextView.addTextChangedListener(textWatcher);
    }
    private void InitView() {
        NdianApplication.instance.setTitle(this, "填写资料", true);
        invitationCode=(EditText)findViewById(R.id.invitationCode);
        man=(RadioButton)findViewById(R.id.man);
        woman=(RadioButton)findViewById(R.id.woman);
//        agreement=(TextView)findViewById(R.id.agreement);
        NickNameTextView=(EditText)findViewById(R.id.NickNameTextView);
        submit=(Button)findViewById(R.id.submit);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
//        setTextColor("提交即表示同意《服务协议》");

        /**获取上个activity传递过来的数据*/
        who=getIntent().getStringExtra("who");
        if(who.equals(RegisterActivity.TAG)){
            phone=getIntent().getStringExtra("phone");
            passWord=getIntent().getStringExtra("password");
        }else{
             entity_sns=(UserEntity) getIntent().getSerializableExtra("entity");
            if(entity_sns.getSex().equals(getResources().getString(R.string.man))){
                man.setChecked(true);
                sex=getResources().getString(R.string.man);
            }else{
                woman.setChecked(true);
                sex=getResources().getString(R.string.woman);
            }
            NickNameTextView.setText(entity_sns.getUname());
            NickNameTextView.setSelection(NickNameTextView.getText().toString().length());
            openid=getIntent().getStringExtra("oppenid");
            qq_wx=getIntent().getStringExtra("qq_wx");
        }
    }
    /**
     * 改变部分字体颜色
     * @param text
     */
    private void setTextColor(String text) {
        SpannableStringBuilder builder=new SpannableStringBuilder(text);
        ForegroundColorSpan teachingAdvantageColor=new ForegroundColorSpan(getResources().getColor(R.color.text_red));
        builder.setSpan(teachingAdvantageColor, 7, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        agreement.setText(builder);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            //进入服务协议界面
//            case R.id.agreement:
//                Intent intent=new Intent(RegisterUserActivity.this,AgreementActivity.class);
//                startActivity(intent);
//                break;
            //设置男女
            case R.id.woman:
                sex="女";
                break;
            case R.id.man:
                sex="男";
                break;
            //提交
            case R.id.submit:
                /**注册前保证获取到数据*/
                /**获取上个activity传递过来的数据*/
                who=getIntent().getStringExtra("who");
                if(who.equals(RegisterActivity.TAG)){
                    phone=getIntent().getStringExtra("phone");
                    passWord=getIntent().getStringExtra("password");
                }else{
                    entity_sns=(UserEntity) getIntent().getSerializableExtra("entity");
                    openid=getIntent().getStringExtra("oppenid");
                    qq_wx=getIntent().getStringExtra("qq_wx");
                }
                /***注册*/
                if(NickNameTextView.getText().toString().trim().equals("")){
                   new ToastShow(RegisterUserActivity.this,R.string.Please_enter_a_nickname,1000);
                }else{
                    if(NickNameTextView.getText().toString().trim().length()>16){
                        new ToastShow(RegisterUserActivity.this,R.string.Nickname_16_characters_in_length,1000);
                    }else {
                        dialog=new WaitDialog(RegisterUserActivity.this);
                        new RegisterBiz(who,RegisterUserActivity.this,handler,phone,passWord, NickNameTextView.getText().toString().trim(),sex,invitationCode.getText().toString().trim(),openid,qq_wx);
                    }
                }
                break;
        }
    }

    //输入框变化
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
                int i=s.length();
                if(i>16){
                    new ToastShow(getApplicationContext(),"昵称超过16字符",1000);
                }
                if(i<=16){
                    textcontent=s.toString();
                }else{
                    NickNameTextView.setText(textcontent);
                    NickNameTextView.setSelection(textcontent.length());
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

}
