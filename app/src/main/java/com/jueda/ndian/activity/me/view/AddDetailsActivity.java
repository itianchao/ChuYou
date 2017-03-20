package com.jueda.ndian.activity.me.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.biz.UpdateAddBiz;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.LoginOutUtil;
import com.jueda.ndian.utils.ToastShow;

/**
 * 编辑收货人
 */
public class AddDetailsActivity extends AppCompatActivity {
    private TextView save;//保存
    private EditText name;//姓名
    private EditText phoneNumber;//手机号码
    private EditText address;//联系地址

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**修改成功*/
                case Constants.ON_SUCCEED:
                    new ToastShow(AddDetailsActivity.this,getResources().getString(R.string.Modify_the_success),1000);
                    finish();
                    break;
                /**修改失败*/
                case Constants.FAILURE:
                    new ToastShow(AddDetailsActivity.this,getResources().getString(R.string.Modify_the_failure),1000);
                    break;
                case Constants.USER_FAILURE:
                    new LoginOutUtil(AddDetailsActivity.this);
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
        setContentView(R.layout.activity_add_details);
        InitView();
        setOnClick();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Edit_the_consignee), true);
        save=(TextView)findViewById(R.id.save);
        name=(EditText)findViewById(R.id.name);
        phoneNumber=(EditText)findViewById(R.id.phoneNumber);
        address=(EditText)findViewById(R.id.address);

        name.setText(ConstantsUser.userEntity.getAdd_uname());
        phoneNumber.setText(ConstantsUser.userEntity.getAdd_phoneNumber());
        address.setText(ConstantsUser.userEntity.getAdd_location());
    }
    private void setOnClick() {
        //保存
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KeyboardManage().CloseKeyboard(save,AddDetailsActivity.this);
                new UpdateAddBiz(AddDetailsActivity.this,handler,name.getText().toString().trim(),phoneNumber.getText().toString().trim(),address.getText().toString().trim());
            }
        });
    }
}
