package com.jueda.ndian.activity.me.view;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.biz.UpdateInfoBiz;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.LoginOutUtil;
import com.jueda.ndian.utils.ToastShow;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * 修改昵称个性签名职业
 */
public class NickNameActivity extends Activity {
    private EditText editText;//输入文本框
    private TextView save;//保存
    private int id;//判断是谁进入界面
    private TextView number;//显示还剩多少字
    private int numbers=30;//默认30
    private String textcontent="";//保存输入30个字符

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**修改成功*/
                case Constants.ON_SUCCEED:
                    new ToastShow(NickNameActivity.this,getResources().getString(R.string.Modify_the_success),1000);
                    /**
                     * 刷新用户缓存数据。
                     *
                     * @param userInfo 需要更新的用户缓存数据。
                     */
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(ConstantsUser.userEntity.getUid() + "", ConstantsUser.userEntity.getUname(), Uri.parse(ConstantsUser.userEntity.getAvater())));

                    finish();
                    break;
                /**修改失败*/
                case Constants.FAILURE:
                    new ToastShow(NickNameActivity.this,getResources().getString(R.string.Modify_the_failure),1000);
                    break;
                /**该昵称已被占用*/
                case Constants.FAILURE_TWO:
                    new ToastShow(NickNameActivity.this,getResources().getString(R.string.The_nickname_has_been_occupied),1000);

                    break;
                case Constants.USER_FAILURE:
                    new LoginOutUtil(NickNameActivity.this);
                    finish();
                    if(PersonalCenterActivity.instance!=null){
                        PersonalCenterActivity.instance.finish();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_nick_name);
        id=getIntent().getIntExtra("id",1);
        InitView();
        setOnClick();
    }

    private void setOnClick() {
        //保存按钮
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KeyboardManage().CloseKeyboard(save,NickNameActivity.this);
                //昵称
                if(id==PersonalCenterActivity.instance.RESULT_NICKNAME){
                    new UpdateInfoBiz(NickNameActivity.this,handler,"uname",editText.getText().toString(),true);

                }else if(id==PersonalCenterActivity.instance.RESULT_PROFESSION){
                //职业
                    new UpdateInfoBiz(NickNameActivity.this,handler,"job",editText.getText().toString(),true);

                }else if(id==PersonalCenterActivity.instance.RESULT_SIGNATURE){
                //签名
                    new UpdateInfoBiz(NickNameActivity.this,handler,"signature",editText.getText().toString(),true);

                }
            }
        });
        //如果是签名修改，则需要限制输入字数
        if(id==PersonalCenterActivity.instance.RESULT_SIGNATURE){
            numbers=30;
            editText.addTextChangedListener(textWatcher);
            number.setVisibility(View.VISIBLE);
        }else if(id==PersonalCenterActivity.instance.RESULT_NICKNAME||id==PersonalCenterActivity.instance.RESULT_PROFESSION){
            numbers=16;
            editText.addTextChangedListener(textWatcher);
            number.setVisibility(View.VISIBLE);
            number.setText(16+"");
        }
    }

    private void InitView() {
        number=(TextView)findViewById(R.id.number);
        editText=(EditText)findViewById(R.id.editText);
        save=(TextView)findViewById(R.id.save);
        //昵称
        if(id==PersonalCenterActivity.instance.RESULT_NICKNAME){
            NdianApplication.instance.setTitle(this, getResources().getString(R.string.to_change_the_nickname), true);
            editText.setText(getIntent().getStringExtra("content"));
            editText.setSelection(getIntent().getStringExtra("content").length());
            editText.setPadding(dip2px(getApplicationContext(), 10), 0, dip2px(getApplicationContext(), 25), 0);

        }else if(id==PersonalCenterActivity.instance.RESULT_PROFESSION){
        //职业
            NdianApplication.instance.setTitle(this,getResources().getString(R.string.Change_jobs),true);
            editText.setText(getIntent().getStringExtra("content"));
            editText.setSelection(getIntent().getStringExtra("content").length());
        }else if(id==PersonalCenterActivity.instance.RESULT_SIGNATURE){
        //签名
            NdianApplication.instance.setTitle(this, getResources().getString(R.string.Change_the_signature), true);
            editText.setText(getIntent().getStringExtra("content"));
            editText.setSelection(getIntent().getStringExtra("content").length());
            editText.setPadding(dip2px(getApplicationContext(),10),0,dip2px(getApplicationContext(),25),0);
        }
        textcontent=editText.getText().toString();
    }
    //输入框变化
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            //个性签名
            if(id==PersonalCenterActivity.instance.RESULT_SIGNATURE){
                int i=s.length();
                if(i>30){
                    new ToastShow(getApplicationContext(),"超过30字符",1000);
                }
                if(i<=30){
                    number.setText(numbers-i+"");
                    textcontent=s.toString();
                }else{
                    editText.setText(textcontent);
                    editText.setSelection(textcontent.length());
                }
            }else if(id==PersonalCenterActivity.instance.RESULT_NICKNAME||id==PersonalCenterActivity.instance.RESULT_PROFESSION){
                int i=s.length();
                if(i>16){
                    new ToastShow(getApplicationContext(),"超过16字符",1000);
                }
                if(i<=16){
                    number.setText(numbers-i+"");
                    textcontent=s.toString();
                }else{
                    editText.setText(textcontent);
                    editText.setSelection(textcontent.length());
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
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
