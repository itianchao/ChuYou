package com.jueda.ndian.activity.circle.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.view.CutImageViewActivity;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.circle.biz.CreateCircleBiz;
import com.jueda.ndian.activity.home.biz.uploadImageBiz;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.RoundCornerImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.io.File;

/**
 * 新建圈子
 */
public class NewCircleActivity extends Activity implements View.OnClickListener{
    private RelativeLayout HeadImageRelativeLayout;//添加头像
    private RoundCornerImageView HeadImage;//头像
    private RelativeLayout relativeLayout;//弹窗相对位置
    private TextView submit;//提交
    private EditText editText;//圈名输入框(1-16)
    private EditText ContentEditText;//内容输入框（5-60）字

    private PopupWindow popPricture;//照片
    private View layoutPricture;
    private File Image;//图片临时存储
    private WaitDialog dialog;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**图片上传成功*/
                case Constants.ON_SUCCEED_IMAGE:
                    new CreateCircleBiz(NewCircleActivity.this,handler,editText.getText().toString(),ContentEditText.getText().toString(),(String) msg.obj);
                    break;
                /**图片上传失败*/
                case Constants.FAILURE_IMAGE:
                    new ToastShow(NewCircleActivity.this,getResources().getString(R.string.Image_upload_failure),1000);
                    break;
                /**创建成功*/
                case Constants.ON_SUCCEED:
                    if( CharityCircleFragment.instance!=null){
                        CharityCircleFragment.instance.Reconnection();
                    }
                    new ToastShow(NewCircleActivity.this,getResources().getString(R.string.Creating_a_successful),1000);
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    finish();
                    break;
                /**已经创建了三个了*/
                case Constants.FAILURE:
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    new ToastShow(NewCircleActivity.this,getResources().getString(R.string.Up_to_create_three_circles),1000);
                    break;
                /**创建失败*/
                case Constants.FAILURE_TWO:
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    new ToastShow(NewCircleActivity.this,getResources().getString(R.string.Create_a_failure),1000);
                    break;
                /***圈昵称被占用*/
                case Constants.FAILURE_THREE:
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    new ToastShow(NewCircleActivity.this,getResources().getString(R.string.The_ring_has_been_occupied),1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    new Configuration().writeaIsLogin(NewCircleActivity.this,false);
                    Intent intent = new Intent(NewCircleActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_new_circle);
        InitView();
        setOnClick();

    }

    private void setOnClick() {
        HeadImageRelativeLayout.setOnClickListener(this);
        submit.setOnClickListener(this);
    }


    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.The_new_circle), true);
        editText=(EditText)findViewById(R.id.editText);
        HeadImageRelativeLayout=(RelativeLayout)findViewById(R.id.HeadImageRelativeLayout);
        HeadImage=(RoundCornerImageView)findViewById(R.id.HeadImage);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        ContentEditText=(EditText)findViewById(R.id.ContentEditText);
        submit=(TextView)findViewById(R.id.submit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //点击添加头像
            case R.id.HeadImageRelativeLayout:
                new KeyboardManage().CloseKeyboard(HeadImageRelativeLayout,NewCircleActivity.this);
                if (popPricture != null && popPricture.isShowing()) {
                    popPricture.dismiss();
                } else {
                    layoutPricture=getLayoutInflater().inflate(
                            R.layout.register_user_dialog, null);

                    //拍照
                    Button button1 = (Button) layoutPricture.findViewById(R.id.photograph);
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //调用相机拍照
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, Constants.TAKING_PICTURES);
                            popPricture.dismiss();

                        }
                    });
                    //相册
                    Button button2 = (Button) layoutPricture.findViewById(R.id.album);
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /**获取存储空间权限*/
                            Intent intent = new Intent();
			                                    /* 开启Pictures画面Type设定为image */
                            intent.setType("image/*");
			                                     /* 使用Intent.ACTION_GET_CONTENT这个Action */
                            intent.setAction(Intent.ACTION_GET_CONTENT);
			                                     /* 取得相片后返回本画面 */
                            startActivityForResult(intent, Constants.PHOTO_ALBUM);
                            popPricture.dismiss();
                            popPricture.dismiss();
                        }
                    });
                    //取消
                    Button button3 = (Button) layoutPricture.findViewById(R.id.cancel);
                    button3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popPricture.dismiss();
                        }
                    });
                    // 创建弹出窗口
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
                    popPricture = new PopupWindow(layoutPricture, ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    ColorDrawable cd = new ColorDrawable(-0000);
                    popPricture.setBackgroundDrawable(cd);
//                    popPricture.setAnimationStyle(R.style.PopupAnimationSex);
                    popPricture.update();
                    popPricture.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popPricture.setTouchable(true); // 设置popupwindow可点击
                    popPricture.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    popPricture.setFocusable(true); // 获取焦点
                    NdianApplication.instance.backgroundAlpha((float) 0.4,NewCircleActivity.this);
                    // 设置popupwindow的位置（相对tvLeft的位置）
                    popPricture.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

                    popPricture.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            NdianApplication.instance.backgroundAlpha(1,NewCircleActivity.this);
                        }
                    });
                    popPricture.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // 如果点击了popupwindow的外部，popupwindow也会消失
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popPricture.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
                }
                break;
            //提交
            case R.id.submit:
                if(editText.getText().toString().length()>=1&&editText.getText().toString().length()<=16){
                    if(ContentEditText.getText().toString().length()>=5&&ContentEditText.getText().toString().length()<=50){
                        if(Image==null){
                            new ToastShow(NewCircleActivity.this,R.string.Head_set,1000);
                        }else{
                            dialog=new WaitDialog(NewCircleActivity.this);
                            new uploadImageBiz(NewCircleActivity.this, handler, Image,false,dialog);
                        }
                    }else{
                        new ToastShow(NewCircleActivity.this,getResources().getString(R.string.Circle_content_must_be_in_the_range_of_5_to_60_characters),1000);
                    }
                }else{
                    new ToastShow(NewCircleActivity.this,getResources().getString(R.string.Circle_name_must_be_in_the_range_of_1_to_16_characters),1000);
                }
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (Activity.RESULT_CANCELED != resultCode) {
                if(data!=null) {
                    //获取相册图片
                    if (requestCode == Constants.PHOTO_ALBUM) {
                        ContentResolver resolver = getContentResolver();
                        Uri uri = data.getData();
                        byte[] mContent;
                        try {
                            mContent = CutImageViewActivity.readStream(resolver.openInputStream(Uri.parse(uri.toString())));
                            //将字节数组转换为ImageView可调用的Bitmap对象
                            CutImageViewActivity.myBitmap = CutImageViewActivity.getPicFromBytes(mContent, null);
                            startActivityForResult(new Intent(NewCircleActivity.this, CutImageViewActivity.class), Constants.CUT_IMAGE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    //获取拍照后的相片
                    if (requestCode == Constants.TAKING_PICTURES) {
                        Bundle bundle = data.getExtras();
                        CutImageViewActivity.myBitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                        startActivityForResult(new Intent(NewCircleActivity.this, CutImageViewActivity.class), Constants.CUT_IMAGE);
                    }
                }
                //裁剪后的图片
                if (requestCode == Constants.CUT_IMAGE) {
                    //把bitmap转化成uri
                    String uriToString=MediaStore.Images.Media.insertImage(getContentResolver(), CutImageViewActivity.cutBitmap, null, null);
                    if(uriToString!=null) {
                        Uri uri = Uri.parse(uriToString);
                        final String i = uri.toString();
                        ImageLoaderUtil.ImageLoader(i, HeadImage);
                        String string = ImageLoader.getInstance().getDiscCache().get(i).getPath();
                        Image = new File(string);
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
