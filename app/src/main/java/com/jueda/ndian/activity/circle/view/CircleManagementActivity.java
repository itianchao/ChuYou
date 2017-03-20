package com.jueda.ndian.activity.circle.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.CircleContent;
import com.jueda.ndian.activity.home.view.CutImageViewActivity;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.circle.biz.SetAddCircleBiz;
import com.jueda.ndian.activity.circle.biz.UpdateCircleBiz;
import com.jueda.ndian.activity.home.biz.uploadImageBiz;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.RoundCornerImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/***
 * 圈管理
 */
public class CircleManagementActivity extends AppCompatActivity implements View.OnClickListener,CircleContent {
    public static CircleManagementActivity instance=null;
    private RadioButton allow;//禁止外人加圈
    private RadioButton notallow;//不禁止外人加圈
    private boolean Check=false;//记录是否禁止外人加圈
    private RoundCornerImageView HeadImage;//圈头像
    private RelativeLayout HeadImageRelativeLayout;//圈头像
    public TextView CircleNameTextView;//圈名称
    private RelativeLayout CicleNameRelativeLayout;//圈昵称
    public TextView IntroduceEditText;//圈介绍
    private RelativeLayout IntroduceRelativeLayout;//圈介绍
    private RelativeLayout relativeLayout;//弹窗相对位置
    private PopupWindow popVote;
    private PopupWindow popPricture;//照片
    private View layoutVote;
    private View layoutPricture;
    private File Image;//临时存储头像图片
    private WaitDialog dialog;//加载框



    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**图片上传七牛成功*/
                case Constants.ON_SUCCEED_IMAGE:
                    new UpdateCircleBiz(getApplicationContext(),handler,"avatar",(String) msg.obj, CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getCid(),false);
                    break;
                /**图片上传失败*/
                case Constants.FAILURE_IMAGE:
                    new ToastShow(CircleManagementActivity.this,getResources().getString(R.string.Image_upload_failure),1000);
                    break;
                /**修改头像成功*/
                case Constants.ON_SUCCEED:
                    String avatar= (String) msg.obj;
                    ImageLoaderUtil.ImageLoader(avatar, HeadImage, R.drawable.head_circle);
                    CircleContentActivity.observable.head(avatar);
                    CharityCircleFragment.instance.Reconnection();
                    new ToastShow(CircleManagementActivity.this,getResources().getString(R.string.Modify_the_success),1000);
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    break;
                /**修改头像失败*/
                case Constants.FAILURE:
                    ImageLoaderUtil.ImageLoader(CircleContentActivity.entityList.get(0).getAvatar(),HeadImage,R.drawable.head_circle);
                    new ToastShow(CircleManagementActivity.this,getResources().getString(R.string.Modify_the_failure),1000);
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    break;

                /**设置禁止外人加圈成功或不禁止成功*/
                case Constants.ON_SUCEED_TWO:
                    if((int)(msg.obj)==0){
                        /**允许*/
                        Check=false;
                        notallow.setChecked(true);
                        notallow.setClickable(false);
                        allow.setClickable(true);
                        CircleContentActivity.entityList.get(0).setAdd_set("0");
                        new ToastShow(CircleManagementActivity.this,getResources().getString(R.string.Changes_not_allowed_to_add_circle_of_success),1000);
                    }else{
                        /**禁止*/
                        allow.setChecked(true);
                        allow.setClickable(false);
                        notallow.setClickable(true);
                        Check=true;
                        CircleContentActivity.entityList.get(0).setAdd_set("1");
                        new ToastShow(CircleManagementActivity.this,getResources().getString(R.string.Changes_not_allowed_to_add_circle_of_failed),1000);
                    }
                    break;
                /**设置失败*/
                case Constants.FAILURE_TWO:
                    if((int)(msg.obj)==0) {
                        /**不允许*/
                        allow.setChecked(true);
                        allow.setClickable(false);
                        notallow.setClickable(true);
                        Check=true;
                        new ToastShow(CircleManagementActivity.this,getResources().getString(R.string.Setup_failed),1000);
                    }else if((int)(msg.obj)==1){
                        /**允许*/
                        Check=false;
                        notallow.setChecked(true);
                        notallow.setClickable(false);
                        allow.setClickable(true);
                        new ToastShow(CircleManagementActivity.this,getResources().getString(R.string.Setup_failed),1000);
                    }else{
                        /**没有权限*/
                        if(msg.obj.toString().substring(0,1).equals("0")){
                            /**不允许*/
                            allow.setChecked(true);
                            allow.setClickable(false);
                            notallow.setClickable(true);
                            Check=true;
                        }else{
                            /**允许*/
                            Check=false;
                            notallow.setChecked(true);
                            notallow.setClickable(false);
                            allow.setClickable(true);
                        }
                        new ToastShow(CircleManagementActivity.this,getResources().getString(R.string.You_not_the_Lord_without_the_operating_authority),1000);
                    }
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    new Configuration().writeaIsLogin(CircleManagementActivity.this,false);
                    Intent intent = new Intent(CircleManagementActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new ChangeTitle(this);
        super.onCreate(savedInstanceState);
        instance=this;
        setContentView(R.layout.activity_circle_management);
        InitView();
        setOnClick();
    }
    private void setOnClick() {
        allow.setOnClickListener(this);
        notallow.setOnClickListener(this);
        IntroduceRelativeLayout.setOnClickListener(this);
        CicleNameRelativeLayout.setOnClickListener(this);
        HeadImageRelativeLayout.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if(CircleContentActivity.observable!=null){
            CircleContentActivity.observable.delect(this);
        }
        super.onDestroy();
    }

    private void InitView() {
        if(CircleContentActivity.observable!=null){
            CircleContentActivity.observable.add(this);
        }
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Management_circles), true);
        allow=(RadioButton)findViewById(R.id.allow);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        HeadImageRelativeLayout=(RelativeLayout)findViewById(R.id.HeadImageRelativeLayout);
        notallow=(RadioButton)findViewById(R.id.notallow);
        HeadImage=(RoundCornerImageView)findViewById(R.id.HeadImage);
        CircleNameTextView=(TextView)findViewById(R.id.CircleNameTextView);
        IntroduceEditText=(TextView)findViewById(R.id.IntroduceEditText);
        CicleNameRelativeLayout=(RelativeLayout)findViewById(R.id.CicleNameRelativeLayout);
        IntroduceRelativeLayout=(RelativeLayout)findViewById(R.id.IntroduceRelativeLayout);
        /**设置数据*/
        ImageLoaderUtil.ImageLoader(CircleContentActivity.entityList.get(0).getAvatar(),HeadImage,R.drawable.head_circle);
        CircleNameTextView.setText(CircleContentActivity.entityList.get(0).getName());
        IntroduceEditText.setText(CircleContentActivity.entityList.get(0).getCdesc());
        /**设置是否允许外人加圈*/
        if( CircleContentActivity.entityList.get(0).getAdd_set().equals("0")){
            //允许
            Check=false;
            notallow.setChecked(true);
            notallow.setClickable(false);
            allow.setClickable(true);

        }else{
            allow.setChecked(true);
            allow.setClickable(false);
            notallow.setClickable(true);
            Check=true;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            /**圈昵称*/
            case R.id.CicleNameRelativeLayout:
                intent=new Intent(CircleManagementActivity.this,CircleSetActivity.class);
                intent.putExtra("who","name");
                intent.putExtra("content",CircleContentActivity.entityList.get(0).getName());

                startActivity(intent);
                break;
            /**圈简介*/
            case R.id.IntroduceRelativeLayout:
                intent=new Intent(CircleManagementActivity.this,CircleSetActivity.class);
                intent.putExtra("who","introduce");
                intent.putExtra("content",CircleContentActivity.entityList.get(0).getCdesc());
                startActivity(intent);
                break;
            /**允许外人加圈*/
            case R.id.notallow:
                new SetAddCircleBiz(CircleManagementActivity.this,handler,0,CircleContentActivity.entityList.get(0).getCid());
                break;
            /**禁止外人加圈*/
            case R.id.allow:
                notallow.setChecked(!Check);
                allow.setChecked(Check);
                if (popVote != null && popVote.isShowing()) {
                    popVote.dismiss();
                } else {
                    layoutVote = getLayoutInflater().inflate(
                            R.layout.pop_circle_particulars, null);

                    // 创建弹出窗口
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
                    popVote = new PopupWindow(layoutVote, ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT);
                    TextView content = (TextView) layoutVote.findViewById(R.id.content);//内容
                    TextView center = (TextView) layoutVote.findViewById(R.id.center);//取消按钮
                    TextView complete = (TextView) layoutVote.findViewById(R.id.complete);//确定按钮
                    //取消
                    RelativeLayout cancelRelativeLayout = (RelativeLayout) layoutVote.findViewById(R.id.cancelRelativeLayout);
                    cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popVote.dismiss();
                        }
                    });
                    /**取消*/
                    center.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popVote.dismiss();
                        }
                    });
                    /**确定*/
                    complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popVote.dismiss();
                            new SetAddCircleBiz(CircleManagementActivity.this, handler, 1, CircleContentActivity.entityList.get(0).getCid());
                        }
                    });
                    ColorDrawable cd = new ColorDrawable(-0000);
                    popVote.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
                    popVote.update();
                    popVote.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popVote.setTouchable(true); // 设置popupwindow可点击
                    popVote.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    popVote.setFocusable(true); // 获取焦点

                    // 设置popupwindow的位置（相对tvLeft的位置）
                    popVote.showAtLocation(relativeLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    popVote.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // 如果点击了popupwindow的外部，popupwindow也会消失
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popVote.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
                }
                break;
            //设置头像
            case R.id.HeadImageRelativeLayout:
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
                            Intent intent = new Intent();
			             /* 开启Pictures画面Type设定为image */
                            intent.setType("image/*");
			             /* 使用Intent.ACTION_GET_CONTENT这个Action */
                            intent.setAction(Intent.ACTION_GET_CONTENT);
			             /* 取得相片后返回本画面 */
                            startActivityForResult(intent, Constants.PHOTO_ALBUM);
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
                    NdianApplication.instance.backgroundAlpha((float) 0.4, CircleManagementActivity.this);
                    // 设置popupwindow的位置（相对tvLeft的位置）
                    popPricture.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

                    popPricture.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            NdianApplication.instance.backgroundAlpha(1, CircleManagementActivity.this);
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
                            startActivityForResult(new Intent(CircleManagementActivity.this, CutImageViewActivity.class), Constants.CUT_IMAGE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    //获取拍照后的相片
                    if (requestCode == Constants.TAKING_PICTURES) {
                        Bundle bundle = data.getExtras();
                        CutImageViewActivity.myBitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                        startActivityForResult(new Intent(CircleManagementActivity.this, CutImageViewActivity.class), Constants.CUT_IMAGE);
                    }
                }
                //裁剪后的图片
                if (requestCode == Constants.CUT_IMAGE) {
                    //把bitmap转化成uri
                    String uriToString = MediaStore.Images.Media.insertImage(getContentResolver(), CutImageViewActivity.cutBitmap, null, null);
                    if (uriToString != null) {
                        Uri uri = Uri.parse(uriToString);
                        final String i = uri.toString();
                        ImageLoaderUtil util = new ImageLoaderUtil();
                        util.ImageLoader(i, HeadImage);
                        String string = ImageLoader.getInstance().getDiscCache().get(i).getPath();
                        Image = new File(string);
                        if (Image == null) {
                            new ToastShow(CircleManagementActivity.this, R.string.Incomplete_information, 1000);
                        } else {
                            dialog = new WaitDialog(CircleManagementActivity.this);
                            new LogUtil("onActivityResult", Image.toString());
                            new uploadImageBiz(CircleManagementActivity.this, handler, Image, false, dialog);
                        }
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void content(String content) {
        IntroduceEditText.setText(content);
    }

    @Override
    public void name(String name) {
        CircleNameTextView.setText(name);
    }

    @Override
    public void head(String head) {

    }
}
