package com.jueda.ndian.activity.me.view;

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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.view.CutImageViewActivity;
import com.jueda.ndian.activity.me.biz.UpdateInfoBiz;
import com.jueda.ndian.activity.home.biz.uploadImageBiz;
import com.jueda.ndian.activity.me.pop.PopEverydayTask;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageAddress;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.utils.LoginOutUtil;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import wheel.widget.ChangeBirthLayout;

/**
 * 个人中心
 */
public class PersonalCenterActivity extends AppCompatActivity implements View.OnClickListener{
    public static PersonalCenterActivity instance=null;
    public int RESULT_NICKNAME=1;//昵称返回
    public int RESULT_PHONENUMBER=2;//手机号返回
    public int RESULT_PROFESSION=3;//职业
    public int RESULT_SIGNATURE=4;//签名


    private RelativeLayout relativeLayout;//弹窗相对位置
    private RelativeLayout NickNameRelativeLayout;//昵称
    private RelativeLayout PhoneNumberRelativeLayout;//手机号码
    private ImageView PhoneNumberXian;//手机线
    private RelativeLayout professionRelativeLayout;//职业
    private RelativeLayout SignatureRelativeLayout;//签名
    private RelativeLayout educationRelativeLayout;//学历
    private RelativeLayout addRelativeLayout;//收货地址
    private TextView educationTextView;//学历填写
    private TextView SignatureTextView;//签名填写
    private TextView professionTextView;//职业填写
    private RelativeLayout sexRelativeLayout;//性别
    private TextView addTextView;//收货地址
    private TextView sexTextView;//性别填写
    private TextView PhoneTextView;//手机号码填写
    private TextView NickNameTextView;//昵称填写
    private RelativeLayout HeadImageRelativeLayout;//头像
    private CircleImageView HeadImage;//头像
    private RelativeLayout birthdayRelativeLayout;//生日
    private TextView birthday;//生日
    // 弹出窗口
    private PopupWindow popDate;//日期
    private PopupWindow popPricture;//照片
    private PopupWindow popSex;//性别
    private PopupWindow popPhone;//手机
    private PopupWindow popEducation;//学历
    //弹出布局
    private View layoutPricture;
    private View layoutDate;
    private View layoutSex;
    private View layoutPhone;
    private View layoutEducation;

    //学历
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    RadioButton radioButton4;
    RadioButton radioButton5;
    RadioButton radioButton6;

    private File Image;//临时存储头像图片
    private WaitDialog dialog;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**图片上传七牛成功*/
                case Constants.ON_SUCCEED_IMAGE:
                    new UpdateInfoBiz(PersonalCenterActivity.this,handler,"avatar",(String) msg.obj,false);
                    break;
                /**图片上传失败*/
                case Constants.FAILURE_IMAGE:
                    new ToastShow(PersonalCenterActivity.this,getResources().getString(R.string.Image_upload_failure),1000);
                    setUser();
                    break;
                /**头像修改成功*/
                case Constants.ON_SUCCEED:
                    /**
                     * 刷新用户缓存数据。
                     *
                     * @param userInfo 需要更新的用户缓存数据。
                     */
                    String bean= (String) msg.obj;
                    if(bean!=null&&!bean.equals("0")){
                        new PopEverydayTask(PersonalCenterActivity.this,relativeLayout,PopEverydayTask.Head,(String)msg.obj);
                    }
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(ConstantsUser.userEntity.getUid() + "", ConstantsUser.userEntity.getUname(), Uri.parse(ConstantsUser.userEntity.getAvater())));
                    new ToastShow(PersonalCenterActivity.this,getResources().getString(R.string.Modify_the_success),1000);
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    setUser();
                    break;
                /**头像修改失败*/
                case Constants.FAILURE:
                    new ToastShow(PersonalCenterActivity.this,getResources().getString(R.string.Modify_the_failure),1000);
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    setUser();
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    new LoginOutUtil(PersonalCenterActivity.this);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_personal_center);
        instance=this;
        InitView();
        setOnClick();
    }

    private void setOnClick() {
        professionRelativeLayout.setOnClickListener(this);
        SignatureRelativeLayout.setOnClickListener(this);
        HeadImageRelativeLayout.setOnClickListener(this);
        birthdayRelativeLayout.setOnClickListener(this);
        NickNameRelativeLayout.setOnClickListener(this);
        NickNameRelativeLayout.setOnClickListener(this);
        PhoneNumberRelativeLayout.setOnClickListener(this);
        educationRelativeLayout.setOnClickListener(this);
        sexRelativeLayout.setOnClickListener(this);
        professionRelativeLayout.setOnClickListener(this);
        addRelativeLayout.setOnClickListener(this);
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this,getResources().getString(R.string.personal_information),true);
        addTextView=(TextView)findViewById(R.id.addTextView);
        addRelativeLayout=(RelativeLayout)findViewById(R.id.addRelativeLayout);
        professionRelativeLayout=(RelativeLayout)findViewById(R.id.professionRelativeLayout);
        SignatureRelativeLayout=(RelativeLayout)findViewById(R.id.SignatureRelativeLayout);
        sexRelativeLayout=(RelativeLayout)findViewById(R.id.sexRelativeLayout);
        SignatureTextView=(TextView)findViewById(R.id.SignatureTextView);
        sexTextView=(TextView)findViewById(R.id.sexTextView);
        professionTextView=(TextView)findViewById(R.id.professionTextView);
        PhoneNumberRelativeLayout=(RelativeLayout)findViewById(R.id.PhoneNumberRelativeLayout);
        NickNameRelativeLayout=(RelativeLayout)findViewById(R.id.NickNameRelativeLayout);
        PhoneTextView=(TextView)findViewById(R.id.PhoneTextView);
        NickNameTextView=(TextView)findViewById(R.id.NickNameTextView);
        HeadImageRelativeLayout=(RelativeLayout)findViewById(R.id.HeadImageRelativeLayout);
        HeadImage=(CircleImageView)findViewById(R.id.HeadImage);
        birthdayRelativeLayout=(RelativeLayout)findViewById(R.id.birthdayRelativeLayout);
        birthday=(TextView)findViewById(R.id.birthday);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        educationRelativeLayout=(RelativeLayout)findViewById(R.id.educationRelativeLayout);
        educationTextView=(TextView)findViewById(R.id.educationTextView);
        PhoneNumberXian=(ImageView)findViewById(R.id.PhoneNumberXian);
    }

    @Override
    protected void onResume() {
        setUser();
        super.onResume();
    }

    /**设置用户信息*/
    public void setUser(){
        ImageLoaderUtil.ImageLoader(ConstantsUser.userEntity.getAvater(),HeadImage);
        NickNameTextView.setText(ConstantsUser.userEntity.getUname());
        if(ConstantsUser.userEntity.getPhoneNumber().equals("")){
            PhoneNumberRelativeLayout.setVisibility(View.GONE);
            PhoneNumberXian.setVisibility(View.GONE);
        }else {
            PhoneTextView.setText(ConstantsUser.userEntity.getPhoneNumber());
        }
        sexTextView.setText(ConstantsUser.userEntity.getSex());
        birthday.setText(ConstantsUser.userEntity.getBirth());
        professionTextView.setText(ConstantsUser.userEntity.getJob());
        educationTextView.setText(ConstantsUser.userEntity.getEducation());
        SignatureTextView.setText(ConstantsUser.userEntity.getSignature());
        addTextView.setText(ConstantsUser.userEntity.getAdd_location());
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            //收货地址
            case R.id.addRelativeLayout:
                intent=new Intent(PersonalCenterActivity.this,AddDetailsActivity.class);
                startActivity(intent);
                break;
            //手机号码
            case R.id.PhoneNumberRelativeLayout:
                if (popPhone != null && popPhone.isShowing()) {
                    popPhone.dismiss();
                } else {
                    layoutPhone=getLayoutInflater().inflate(
                            R.layout.pop_ok_cancel_content_general, null);

                    // 创建弹出窗口
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
                    popPhone = new PopupWindow(layoutPhone, ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT);
                    //取消
                    RelativeLayout cancelRelativeLayout=(RelativeLayout)layoutPhone.findViewById(R.id.cancelRelativeLayout);
                    cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popPhone.dismiss();
                        }
                    });
                    //确定
                    final TextView complete=(TextView)layoutPhone.findViewById(R.id.complete);
                    complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(PersonalCenterActivity.this,PhoneNumberActivity.class);
                            startActivityForResult(intent,RESULT_PHONENUMBER);
                            popPhone.dismiss();
                        }
                    });
                    //取消
                    final TextView center=(TextView)layoutPhone.findViewById(R.id.center);
                    center.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popPhone.dismiss();

                        }
                    });
                    ColorDrawable cd = new ColorDrawable(-0000);
                    popPhone.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
                    popPhone.update();
                    popPhone.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popPhone.setTouchable(true); // 设置popupwindow可点击
                    popPhone.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    popPhone.setFocusable(true); // 获取焦点
                    // 设置popupwindow的位置（相对tvLeft的位置）
                    popPhone.showAtLocation(relativeLayout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                    popPhone.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // 如果点击了popupwindow的外部，popupwindow也会消失
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popPhone.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
                }
                break;
            //签名
            case R.id.SignatureRelativeLayout:
                intent=new Intent(PersonalCenterActivity.this,NickNameActivity.class);
                intent.putExtra("content",SignatureTextView.getText().toString());
                intent.putExtra("id",RESULT_SIGNATURE);
                startActivity(intent);
                break;
            //职业
            case R.id.professionRelativeLayout:
                intent=new Intent(PersonalCenterActivity.this,NickNameActivity.class);
                intent.putExtra("content",professionTextView.getText().toString());
                intent.putExtra("id",RESULT_PROFESSION);
                startActivity(intent);
                break;
            //昵称
            case R.id.NickNameRelativeLayout:
                intent=new Intent(PersonalCenterActivity.this,NickNameActivity.class);
                intent.putExtra("content",NickNameTextView.getText().toString());
                intent.putExtra("id",RESULT_NICKNAME);
                startActivity(intent);
                break;
            //性别
            case R.id.sexRelativeLayout:

                if (popSex != null && popSex.isShowing()) {
                    popSex.dismiss();
                } else {
                    layoutSex=getLayoutInflater().inflate(
                            R.layout.sex_dialog, null);

                    // 创建弹出窗口
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
                    popSex = new PopupWindow(layoutSex, ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT);
                    //取消
                    RelativeLayout cancelRelativeLayout=(RelativeLayout)layoutSex.findViewById(R.id.cancelRelativeLayout);
                    cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popSex.dismiss();
                        }
                    });
                //选择男
                final RadioButton man=(RadioButton)layoutSex.findViewById(R.id.man);
                man.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        man.setChecked(true);
                        new UpdateInfoBiz(PersonalCenterActivity.this,handler,"sex","1",true);
                        popSex.dismiss();
                    }
                });
                //选择女
                final RadioButton woman=(RadioButton)layoutSex.findViewById(R.id.woman);
                woman.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        woman.setChecked(true);
                        new UpdateInfoBiz(PersonalCenterActivity.this,handler,"sex","0",true);
                        popSex.dismiss();

                    }
                });
                if(sexTextView.getText().toString().equals(man.getText().toString())){
                    man.setChecked(true);
                }else if(sexTextView.getText().toString().equals(woman.getText().toString())){
                    woman.setChecked(true);
                }

                    ColorDrawable cd = new ColorDrawable(-0000);
                    popSex.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
                    popSex.update();
                    popSex.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popSex.setTouchable(true); // 设置popupwindow可点击
                    popSex.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    popSex.setFocusable(true); // 获取焦点

                    // 设置popupwindow的位置（相对tvLeft的位置）
                    popSex.showAtLocation(relativeLayout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);


                    popSex.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // 如果点击了popupwindow的外部，popupwindow也会消失
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popSex.dismiss();
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
                    NdianApplication.instance.backgroundAlpha((float) 0.4, PersonalCenterActivity.this);
                    // 设置popupwindow的位置（相对tvLeft的位置）
                    popPricture.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

                    popPricture.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            NdianApplication.instance.backgroundAlpha(1, PersonalCenterActivity.this);
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
            //生日
            case R.id.birthdayRelativeLayout:
                if (popDate != null && popDate.isShowing()) {
                    popDate.dismiss();
                } else{

                    layoutDate=getLayoutInflater().inflate(
                            R.layout.personal_data_item, null);
                    final ChangeBirthLayout birth=(ChangeBirthLayout)layoutDate.findViewById(R.id.tv_birth);
                    RelativeLayout relativeLayout=(RelativeLayout)layoutDate.findViewById(R.id.relativeLayout);
                    TextView OK=(TextView)layoutDate.findViewById(R.id.OK);
                    TextView Cancel=(TextView)layoutDate.findViewById(R.id.Cancel);

                    //确定
                    OK.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            new UpdateInfoBiz(PersonalCenterActivity.this,handler,"birth",birth.getTime(),true);
                            popDate.dismiss();
                        }
                    });
                    //取消
                    Cancel.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            popDate.dismiss();
                        }
                    });
                    relativeLayout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popDate.dismiss();
                            return false;
                        }
                    });
                    /**初始化界面*/
                    if(!birthday.getText().toString().equals("")){
                        int year=Integer.parseInt(birthday.getText().toString().substring(0,4));
                        int month=Integer.parseInt(birthday.getText().toString().substring(5,7));
                        int day=Integer.parseInt(birthday.getText().toString().substring(8,10));
                        birth.setTime(year,month,day);
                    }
                    // 创建弹出窗口
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
                    popDate = new PopupWindow(layoutDate, ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT);

                    ColorDrawable cd = new ColorDrawable(-0000);
                    popDate.setBackgroundDrawable(cd);
//                    popDate.setAnimationStyle(R.style.PopupAnimationSex);
                    popDate.update();
                    popDate.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popDate.setTouchable(true); // 设置popupwindow可点击
                    popDate.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    popDate.setFocusable(true); // 获取焦点
                    NdianApplication.instance.backgroundAlpha((float) 0.4,PersonalCenterActivity.this);
                    // 设置popupwindow的位置（相对tvLeft的位置）
                    popDate.showAtLocation(relativeLayout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                    popDate.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            NdianApplication.instance.backgroundAlpha(1,PersonalCenterActivity.this);
                        }
                    });
                    popDate.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // 如果点击了popupwindow的外部，popupwindow也会消失
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popDate.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
                }
                break;
            //学历
            case R.id.educationRelativeLayout:
                if (popEducation != null && popEducation.isShowing()) {
                    popEducation.dismiss();
                } else {
                    layoutEducation=getLayoutInflater().inflate(
                            R.layout.education_popup, null);
                    RelativeLayout relativeLayout= (RelativeLayout) layoutEducation.findViewById(R.id.relativeLayout);
                     radioButton1=(RadioButton)layoutEducation.findViewById(R.id.radioButton1);
                     radioButton2=(RadioButton)layoutEducation.findViewById(R.id.radioButton2);
                     radioButton3=(RadioButton)layoutEducation.findViewById(R.id.radioButton3);
                     radioButton4=(RadioButton)layoutEducation.findViewById(R.id.radioButton4);
                     radioButton5=(RadioButton)layoutEducation.findViewById(R.id.radioButton5);
                     radioButton6=(RadioButton)layoutEducation.findViewById(R.id.radioButton6);
                    radioButton1.setOnClickListener(this);
                    radioButton2.setOnClickListener(this);
                    radioButton3.setOnClickListener(this);
                    radioButton4.setOnClickListener(this);
                    radioButton5.setOnClickListener(this);
                    radioButton6.setOnClickListener(this);


                    //初始化
                    if(educationTextView.getText().toString().equals("")){
                        radioButton1.setChecked(true);
                    }else if(educationTextView.getText().toString().equals(radioButton1.getText().toString())){
                        radioButton1.setChecked(true);
                    }else if(educationTextView.getText().toString().equals(radioButton2.getText().toString())){
                        radioButton2.setChecked(true);
                    }else if(educationTextView.getText().toString().equals(radioButton3.getText().toString())){
                        radioButton3.setChecked(true);
                    }else if(educationTextView.getText().toString().equals(radioButton4.getText().toString())){
                        radioButton4.setChecked(true);
                    }else if(educationTextView.getText().toString().equals(radioButton5.getText().toString())){
                        radioButton5.setChecked(true);
                    }else if(educationTextView.getText().toString().equals(radioButton6.getText().toString())){
                        radioButton6.setChecked(true);
                    }

                    relativeLayout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popEducation.dismiss();
                            return false;
                        }
                    });


                            // 创建弹出窗口
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
                    popEducation = new PopupWindow(layoutEducation, ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT);

                    ColorDrawable cd = new ColorDrawable(-0000);
                    popEducation.setBackgroundDrawable(cd);
//                    popEducation.setAnimationStyle(R.style.PopupAnimationSex);
                    popEducation.update();
                    popEducation.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popEducation.setTouchable(true); // 设置popupwindow可点击
                    popEducation.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    popEducation.setFocusable(true); // 获取焦点
                    NdianApplication.instance.backgroundAlpha((float) 0.4,PersonalCenterActivity.this);
                    // 设置popupwindow的位置（相对tvLeft的位置）
                    popEducation.showAtLocation(relativeLayout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);

                    popEducation.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            NdianApplication.instance.backgroundAlpha(1,PersonalCenterActivity.this);
                        }
                    });
                    popEducation.setTouchInterceptor(new View.OnTouchListener() {
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
            //学历选择
            case R.id.radioButton1:
                radioButton1.setChecked(true);
                new UpdateInfoBiz(PersonalCenterActivity.this,handler,"education","",true);
                popEducation.dismiss();
                break;
            case R.id.radioButton2:
                radioButton2.setChecked(true);
                new UpdateInfoBiz(PersonalCenterActivity.this,handler,"education",radioButton2.getText().toString(),true);
                popEducation.dismiss();
                break;
            case R.id.radioButton3:
                radioButton3.setChecked(true);
                new UpdateInfoBiz(PersonalCenterActivity.this,handler,"education",radioButton3.getText().toString(),true);
                popEducation.dismiss();
                break;
            case R.id.radioButton4:
                radioButton4.setChecked(true);
                new UpdateInfoBiz(PersonalCenterActivity.this,handler,"education",radioButton4.getText().toString(),true);
                popEducation.dismiss();
                break;
            case R.id.radioButton5:
                radioButton5.setChecked(true);
                new UpdateInfoBiz(PersonalCenterActivity.this,handler,"education",radioButton5.getText().toString(),true);
                popEducation.dismiss();
                break;
            case R.id.radioButton6:
                radioButton6.setChecked(true);
                new UpdateInfoBiz(PersonalCenterActivity.this,handler,"education",radioButton6.getText().toString(),true);
                popEducation.dismiss();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (Activity.RESULT_CANCELED != resultCode) {
                if (data != null) {
                    //获取相册图片
                    if (requestCode == Constants.PHOTO_ALBUM) {
                        ContentResolver resolver = getContentResolver();
                        Uri uri = data.getData();
                        byte[] mContent;
                        try {
                            mContent = CutImageViewActivity.readStream(resolver.openInputStream(Uri.parse(uri.toString())));
                            //将字节数组转换为ImageView可调用的Bitmap对象
                            CutImageViewActivity.myBitmap = CutImageViewActivity.getPicFromBytes(mContent, null);
                            startActivityForResult(new Intent(PersonalCenterActivity.this, CutImageViewActivity.class), Constants.CUT_IMAGE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    //获取拍照后的相片
                    if (requestCode == Constants.TAKING_PICTURES) {
                        Bundle bundle = data.getExtras();
                        CutImageViewActivity.myBitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                        startActivityForResult(new Intent(PersonalCenterActivity.this, CutImageViewActivity.class), Constants.CUT_IMAGE);
                    }
                }

                //裁剪后的图片
                if (requestCode == Constants.CUT_IMAGE) {
                    //把bitmap转化成uri
                     String uriToString=MediaStore.Images.Media.insertImage(getContentResolver(), CutImageViewActivity.cutBitmap, null, null);
                    if(uriToString!=null){
                        Uri uri = Uri.parse(uriToString);
                        final String i = uri.toString();
                        ImageLoaderUtil util = new ImageLoaderUtil();
                        util.ImageLoader(i, HeadImage);
                        String string = ImageLoader.getInstance().getDiscCache().get(i).getPath();
                        Image = new File(string);
                        if (Image == null) {
                            new ToastShow(PersonalCenterActivity.this, R.string.Incomplete_information, 1000);
                        } else {
                            dialog = new WaitDialog(PersonalCenterActivity.this);
                            new uploadImageBiz(PersonalCenterActivity.this, handler, Image, false, dialog);
                        }
                    }
                }
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
