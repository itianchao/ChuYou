package com.jueda.ndian.activity.me.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.me.biz.LoginBiz;
import com.jueda.ndian.entity.UserEntity;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.ToastShow;

/***
 * 登录
 */
public class LoginActivity extends Activity implements View.OnClickListener{
    public static LoginActivity instance=null;
    private TextView register;//注册
    private EditText userName;//账号
    private EditText passWord;//密码
    private Button login;//登录
    private TextView findPassWord;//忘记密码
    public  UserEntity entity=new UserEntity();//用户信息
    public WaitDialog waitDialog;
    public static String is_add_circle;//判断是否加入圈子
    private PopupWindow popPrompt;//
    private View layoutPrompt;

//    private UMShareAPI mShareAPI;//友盟第三方登录
    public String oppenid="";//第三方登录id
    public String QQ_WX="";//
//    private Tencent mTencent;//QQ
//    private IWXAPI wxApi;//微信
//    public String code="";//微信token


    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /***登录成功*/
                case Constants.ON_SUCCEED:
                    new Configuration().writeaIsLogin(LoginActivity.this,true);
                    if(MainActivity.instance==null){
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }else{
                        MainActivity.instance.addTag();
                    }
                    if(waitDialog!=null){
                        waitDialog.dismiss();
                    }
                    new ToastShow(LoginActivity.this,getResources().getString(R.string.Login_successful),1000);
                    /**更新我的圈子*/
                    if(CharityCircleFragment.instance!=null){
                        CharityCircleFragment.instance.Reconnection();
                    }
                    finish();
                    break;
                /**第三方跳转到注册界面*/
                case Constants.UNREGISTERED:
                    if(waitDialog!=null){
                        waitDialog.dismiss();
                    }
                    Intent intent =new Intent(LoginActivity.this,RegisterUserActivity.class);
                    intent.putExtra("who","LoginActivity");
                    intent.putExtra("oppenid",oppenid);
                    intent.putExtra("qq_wx",QQ_WX);
                    intent.putExtra("entity", entity);
                    startActivity(intent);
                    break;
                /**密码错误*/
                case Constants.FAILURE:
                    if(waitDialog!=null){
                        waitDialog.dismiss();
                    }
                    if(!QQ_WX.equals("qq")&&!QQ_WX.equals("wx")){
                        new ToastShow(LoginActivity.this,getResources().getString(R.string.Account_or_password_error),1000);
                    }
                    break;
                /**登录失败*/
                case Constants.FAILURE_THREE:
                    if(waitDialog!=null){
                        waitDialog.dismiss();
                    }
                    new ToastShow(LoginActivity.this,getResources().getString(R.string.Login_failure),1000);
                    break;
                /**禁止登录*/
                case Constants.FAILURE_TWO:
                    if(waitDialog!=null){
                        waitDialog.dismiss();
                    }
                    new ToastShow(LoginActivity.this,getResources().getString(R.string.Your_account_has_been_banned_login_please_contact_your_administrator),1000);
                    break;
                /***被迫下线弹窗*/
                case Constants.ON_SUCEED_TWO:
                    /**判断是否登录*/
                    if(Constants.isOut){
                        Prompt();
                        Constants.isOut=false;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
        new ChangeTitle(this);
        setContentView(R.layout.activity_login);
        InitView();
        setOnClick();
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(500);
                    handler.sendEmptyMessage(Constants.ON_SUCEED_TWO);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setOnClick() {
        register.setOnClickListener(this);
        login.setOnClickListener(this);
        findPassWord.setOnClickListener(this);
    }

    private void InitView() {
        Constants.activityArrayList.clear();
        Constants.activityArrayList.add(LoginActivity.this);

        NdianApplication.instance.setTitle(this, getResources().getString(R.string.login), true);
        findPassWord=(TextView)findViewById(R.id.findPassWord);
        register=(TextView)findViewById(R.id.register);
        login=(Button)findViewById(R.id.login);
        userName=(EditText)findViewById(R.id.userName);
        passWord=(EditText)findViewById(R.id.passWord);
    }

    @Override
    public void onClick(View v) {
        new KeyboardManage().CloseKeyboard(v, LoginActivity.this);
        Intent intent;
        switch (v.getId()){

            //注册
            case R.id.register:
                intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                break;
            //登录
            case R.id.login:
                /**判断是否没有输入*/
                if(userName.getText().toString().trim().equals("")||passWord.getText().toString().trim().equals("")){
                    new ToastShow(LoginActivity.this,getResources().getString(R.string.Incomplete_information),1000);
                }else{
                    QQ_WX="";
                    new LoginBiz(LoginActivity.this,handler,userName.getText().toString().trim(),passWord.getText().toString().trim(),"",true);
                }
                break;
//            //第三方QQ登录
//            case R.id.QQImageView:
//                QQ_WX="qq";
//                waitDialog=new WaitDialog(LoginActivity.this);
//                mTencent.login(this,"",new BaseUiListener());
//                break;
//            //第三方微信
//            case R.id.WXImageView:
//                QQ_WX="wx";
//                waitDialog=new WaitDialog(LoginActivity.this);
//                if (wxApi == null) {
//                    wxApi= WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, true);
//                }
//                if (!wxApi.isWXAppInstalled()) {
//                    new ToastShow(LoginActivity.this,getResources().getString(R.string.No_WeChat_client_installed),1000);
//                    waitDialog.dismiss();
//                    return;
//                }
//                wxApi.registerApp(Constants.WX_APP_ID);
//                SendAuth.Req req = new SendAuth.Req();
//                req.scope = "snsapi_userinfo";
//                req.state = "carjob_wx_login";
//                wxApi.sendReq(req);
//                break;
            //忘记密码
            case R.id.findPassWord:
                intent=new Intent(LoginActivity.this,FindPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }


    /***
     * 被迫下线提示显示
     */
    private void Prompt(){
        if (popPrompt != null && popPrompt.isShowing()) {
            popPrompt.dismiss();
        } else {
            layoutPrompt =getLayoutInflater().inflate(
                    R.layout.pop_ok_content_title_general, null);
            TextView know=(TextView)layoutPrompt.findViewById(R.id.know);
            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPrompt.dismiss();
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
            popPrompt.showAtLocation(login, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
    @Override
    protected void onDestroy() {
        instance=null;
        super.onDestroy();
    }

    /**
     * 按两次退出
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                if(MainActivity.instance==null){
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
//    /**QQ登录*/
//    public class BaseUiListener implements IUiListener {
//        @Override
//        public void onComplete(Object o) {
//            new LogUtil("QQ",o.toString());
//            try {
//                oppenid="";
//                JSONObject object=new JSONObject(o.toString());
//                oppenid=object.getString("openid");
//                mTencent.setOpenId(object.getString("openid"));
//                mTencent.setAccessToken(object.getString("access_token"),object.getString("expires_in"));
//                UserInfo userInfo = new UserInfo(LoginActivity.this, mTencent.getQQToken());
//                userInfo.getUserInfo(new IUiListener() {
//                    @Override
//                    public void onComplete(Object o) {
//                        try {
//                            JSONObject object=new JSONObject(o.toString());
//                            entity.setAvater(object.getString("figureurl_qq_2"));
//                            entity.setSex(object.getString("gender"));
//                            entity.setUname(object.getString("nickname"));
//                            //检查是否登录。没有登录则跳转到登录界面
//                            new CheckOppenidBiz(LoginActivity.this,handler,oppenid);
//                        } catch (JSONException e) {
//                            handler.sendEmptyMessage(Constants.FAILURE);
//                            e.printStackTrace();
//                        }
//                    }
//                    @Override
//                    public void onError(UiError uiError) {
//                        handler.sendEmptyMessage(Constants.FAILURE);
//                    }
//                    @Override
//                    public void onCancel() {
//                        handler.sendEmptyMessage(Constants.FAILURE);
//                    }
//                });
//            } catch (JSONException e) {
//                handler.sendEmptyMessage(Constants.FAILURE);
//                e.printStackTrace();
//            }
//        }
//        @Override
//        public void onError(UiError e) {
//            handler.sendEmptyMessage(Constants.FAILURE);
//            new LogUtil("QQ","code:" + e.errorCode + ", msg:"
//                    + e.errorMessage + ", detail:" + e.errorDetail);
//        }
//        @Override
//        public void onCancel() {
//            handler.sendEmptyMessage(Constants.FAILURE);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        mTencent.onActivityResultData(requestCode, resultCode, data,new BaseUiListener());
//    }
}
