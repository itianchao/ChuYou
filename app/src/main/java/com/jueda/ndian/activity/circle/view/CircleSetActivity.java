package com.jueda.ndian.activity.circle.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.circle.biz.UpdateCircleBiz;

import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.ToastShow;

/***
 * 圈子设置
 */
public class CircleSetActivity extends AppCompatActivity {
    private EditText editText;//输入文本框
    private TextView save;//保存


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**修改成功*/
                case Constants.ON_SUCCEED:
                    String content= (String) msg.obj;
                    if(getIntent().getStringExtra("who").equals("name")){
                         CircleContentActivity.observable.name(content);
                        CharityCircleFragment.instance.Reconnection();
                    }else{
                        CircleContentActivity.observable.content(content);
                        CharityCircleFragment.instance.Reconnection();
                    }
                    new ToastShow(CircleSetActivity.this,getResources().getString(R.string.Modify_the_success),1000);
                    finish();
                    break;
                /**修改失败*/
                case Constants.FAILURE:
                    new ToastShow(CircleSetActivity.this,getResources().getString(R.string.Modify_the_failure),1000);
                    break;
                /**该昵称已被占用*/
                case Constants.FAILURE_TWO:
                    new ToastShow(CircleSetActivity.this,getResources().getString(R.string.The_nickname_has_been_occupied),1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_circle_set);
        InitView();
        setOnClick();
    }
    private void InitView() {

        editText=(EditText)findViewById(R.id.editText);
        save=(TextView)findViewById(R.id.save);
        //圈昵称
        if(getIntent().getStringExtra("who").equals("name")){
            NdianApplication.instance.setTitle(this,getResources().getString(R.string.change_circle_name),true);
            editText.setText(getIntent().getStringExtra("content"));
            editText.setSelection(getIntent().getStringExtra("content").length());
        }else{
            NdianApplication.instance.setTitle(this,getResources().getString(R.string.change_ciecle_introduce),true);
            editText.setText(getIntent().getStringExtra("content"));
            editText.setSelection(getIntent().getStringExtra("content").length());
        }
    }
    private void setOnClick() {
        //保存按钮
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KeyboardManage().CloseKeyboard(save,CircleSetActivity.this);
                if(getIntent().getStringExtra("who").equals("name")){
                    if(editText.getText().toString().length()>=1&&editText.getText().toString().length()<=16) {
                        new UpdateCircleBiz(CircleSetActivity.this, handler, "cname", editText.getText().toString(), CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getCid(), true);
                    }else{
                        new ToastShow(CircleSetActivity.this,getResources().getString(R.string.Circle_name_must_be_in_the_range_of_1_to_16_characters),1000);
                    }
                }else{
                    if(editText.getText().toString().length()>=5&&editText.getText().toString().length()<=50) {
                        new UpdateCircleBiz(CircleSetActivity.this, handler, "cdesc", editText.getText().toString(), CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getCid(), true);
                    }else{
                        new ToastShow(CircleSetActivity.this,getResources().getString(R.string.Circle_content_must_be_in_the_range_of_5_to_60_characters),1000);
                    }
                }
            }
        });
    }
}
