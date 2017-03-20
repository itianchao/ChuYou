package com.jueda.ndian.activity.me.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.biz.FeedBackBiz;
import com.jueda.ndian.activity.home.biz.uploadImageBiz;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.ToastShow;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;


/***
 * 反馈
 */
public class ConversationDetailActivity extends FragmentActivity {
    private ImageView image;//选择反馈图片
    private EditText contact;//联系方式
    private EditText content;//反馈内容
    private TextView send;//发送
    private File Image;//临时存储头像图片
    private WaitDialog dialog;
    private String qiniu_image="";//七牛返回图地址

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**图片上传七牛成功*/
                case Constants.ON_SUCCEED_IMAGE:
                    qiniu_image=(String) msg.obj;
                    new FeedBackBiz(ConversationDetailActivity.this,handler,qiniu_image,content.getText().toString().trim(),contact.getText().toString().trim());
                    break;
                /**图片上传失败*/
                case Constants.FAILURE_IMAGE:
                    new ToastShow(ConversationDetailActivity.this,getResources().getString(R.string.Image_upload_failure),1000);
                    break;
                /**反馈成功*/
                case Constants.ON_SUCCEED:
                    new ToastShow(ConversationDetailActivity.this,getResources().getString(R.string.Feedback_success),1000);

                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    ConversationDetailActivity.this.finish();
                    break;
                /**反馈失败*/
                case Constants.FAILURE:
                    new ToastShow(ConversationDetailActivity.this,getResources().getString(R.string.Network_is_bad_please_try_again),1000);
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_conversation_detail);
        Init();
        setOnClick();
    }

    private void Init() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.feedback), true);
        image=(ImageView)findViewById(R.id.image);
        contact=(EditText)findViewById(R.id.contact);
        content=(EditText)findViewById(R.id.content);
        send=(TextView)findViewById(R.id.send);
    }
    private void setOnClick() {
        /**选择图片*/
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
			             /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
			             /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
			             /* 取得相片后返回本画面 */
                startActivityForResult(intent, Constants.PHOTO_ALBUM);
            }
        });
        /**发送*/
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(content.getText().toString().trim().equals("")){
                    new ToastShow(ConversationDetailActivity.this, R.string.Forgot_to_fill_in_the_feedback, 1000);
                }else{
                    dialog = new WaitDialog(ConversationDetailActivity.this);
                    if (Image == null) {
                       new FeedBackBiz(ConversationDetailActivity.this,handler,qiniu_image,content.getText().toString().trim(),contact.getText().toString().trim());
                    } else {
                        new uploadImageBiz(ConversationDetailActivity.this, handler, Image, false, dialog);
                    }
                }

            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (Activity.RESULT_CANCELED != resultCode) {
            if (data != null) {
                //获取相册图片
                if (requestCode == Constants.PHOTO_ALBUM) {
                    Uri uri = data.getData();
                    ImageLoaderUtil.ImageLoader(uri.toString(),image);
                    String string = ImageLoader.getInstance().getDiscCache().get(uri.toString()).getPath();
                    Image = new File(string);
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
