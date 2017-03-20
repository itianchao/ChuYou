package com.jueda.ndian.activity.circle.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.CircleContent;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.me.view.PersonalDetailsActivity;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.circle.biz.ExitCircleBiz;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.Share;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.CircleImageView;
import com.jueda.ndian.view.RoundCornerImageView;

/**
 * 圈子详情
 */
public class CircleParticularsActivity extends Activity implements View.OnClickListener,CircleContent {
    public static final String TAG="CircleParticularsActivity";
    public static CircleParticularsActivity  instance=null;
    private RelativeLayout MembersRelativeLayout;//圈成员
    private RelativeLayout shareRelativeLayout;//分享
    private RelativeLayout BottomRelativeLayout;//相对位置

    private PopupWindow popBack;//手机
    private View layoutBack;

    public RoundCornerImageView CircleHeadImage;//圈子头像
    public TextView CircleName;//圈子名称
    public TextView CircleContentTextView;//圈子简介
    private TextView RingBackTextView;//退圈
    private CircleImageView HeadImage;//圈主头像
    private TextView NickNameTextView;//圈主昵称
    private TextView CirlceNumberTextView;//圈子人数
    private TextView invitationCodeTextView;//圈子邀请码
    private RelativeLayout ManageRelativeLayout;//管理圈子

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**退圈成功*/
                case Constants.ON_SUCEED_THREE:
                     new ToastShow(CircleParticularsActivity.this,getResources().getString(R.string.Circle_back_success),1000);
                    if(CharityCircleFragment.instance!=null){
                        CharityCircleFragment.instance.Reconnection();
                    }
                    if(CircleContentActivity.instance!=null){
                        CircleContentActivity.instance.finish();
                    }
                    finish();
                    break;
                /**退圈失败*/
                case Constants.FAILURE_THREE:
                    new ToastShow(CircleParticularsActivity.this,getResources().getString(R.string.Circle_back_failure),1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(CircleParticularsActivity.this,false);
                    Intent intent = new Intent(CircleParticularsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        instance=this;
        setContentView(R.layout.activity_circle_particulars);
        InitView();
        setOnClick();
    }

    private void setOnClick() {
        MembersRelativeLayout.setOnClickListener(this);

        shareRelativeLayout.setOnClickListener(this);
        RingBackTextView.setOnClickListener(this);
        HeadImage.setOnClickListener(this);
        NickNameTextView.setOnClickListener(this);
        ManageRelativeLayout.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if(CircleContentActivity.observable!=null){
            CircleContentActivity.observable.delect(this);
        }

        super.onDestroy();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.circle_particulars), true);
        if(CircleContentActivity.observable!=null){
            CircleContentActivity.observable.add(this);
        }

        ManageRelativeLayout=(RelativeLayout)findViewById(R.id.ManageRelativeLayout);

        CircleHeadImage=(RoundCornerImageView)findViewById(R.id.CircleHeadImage);
        CircleName=(TextView)findViewById(R.id.CircleName);
        CircleContentTextView=(TextView)findViewById(R.id.CircleContentTextView);
        HeadImage=(CircleImageView)findViewById(R.id.HeadImage);
        NickNameTextView=(TextView)findViewById(R.id.NickNameTextView);
        CirlceNumberTextView=(TextView)findViewById(R.id.CirlceNumberTextView);
        invitationCodeTextView=(TextView)findViewById(R.id.invitationCodeTextView);
        MembersRelativeLayout=(RelativeLayout)findViewById(R.id.MembersRelativeLayout);

        shareRelativeLayout=(RelativeLayout)findViewById(R.id.shareRelativeLayout);
        BottomRelativeLayout=(RelativeLayout)findViewById(R.id.BottomRelativeLayout);
        RingBackTextView=(TextView)findViewById(R.id.RingBackTextView);

        /**判断是否是圈内人*/
        if(CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getIs_member().equals("0")){
            RingBackTextView.setVisibility(View.GONE);
            MembersRelativeLayout.setVisibility(View.GONE);
            shareRelativeLayout.setVisibility(View.GONE);
            ManageRelativeLayout.setVisibility(View.GONE);
        }
        /**判断是否为圈主*/
        if(CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getIs_host().equals("0")){
            ManageRelativeLayout.setVisibility(View.GONE);
        }else{
            RingBackTextView.setVisibility(View.GONE);
        }
        /**设置数据*/
        ImageLoaderUtil.ImageLoader(CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getAvatar(),CircleHeadImage,R.drawable.head_circle);
        ImageLoaderUtil.ImageLoader(CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getOwner_avatar(), HeadImage,R.drawable.head_portrait);
        CircleName.setText(CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getName());
        CircleContentTextView.setText(CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getCdesc());

        NickNameTextView.setText(CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getOwner_name());
        CirlceNumberTextView.setText(CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getC_member());
        invitationCodeTextView.setText(CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getInvite_code());

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            /**查看个人信息*/
            case R.id.HeadImage:
                if(!CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getOwner_id().equals(ConstantsUser.userEntity.getUid()+"")) {
                    intent = new Intent(CircleParticularsActivity.this, PersonalDetailsActivity.class);
                    intent.putExtra("uid", CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getOwner_id());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                break;
            /**查看个人信息*/
            case R.id.NickNameTextView:
                if(!CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getOwner_id().equals(ConstantsUser.userEntity.getUid()+"")) {
                    intent = new Intent(CircleParticularsActivity.this, PersonalDetailsActivity.class);
                    intent.putExtra("uid", CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getOwner_id());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                break;
            /**退圈*/
            case R.id.RingBackTextView:
                if (popBack != null && popBack.isShowing()) {
                    popBack.dismiss();
                } else {
                    layoutBack=getLayoutInflater().inflate(
                            R.layout.pop_ok_cancel_content_general, null);

                    // 创建弹出窗口
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
                    popBack = new PopupWindow(layoutBack, ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT);
                    TextView content=(TextView)layoutBack.findViewById(R.id.content);
                    content.setText(getResources().getString(R.string.If_you_leave_the_circle));//你是否退出该圈子

                    //取消
                    RelativeLayout cancelRelativeLayout=(RelativeLayout)layoutBack.findViewById(R.id.cancelRelativeLayout);
                    cancelRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popBack.dismiss();
                            return false;
                        }
                    });
                    //确定
                    final TextView complete=(TextView)layoutBack.findViewById(R.id.complete);
                    complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new ExitCircleBiz(CircleParticularsActivity.this,handler,CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getCid());
                            popBack.dismiss();
                        }
                    });
                    //取消
                    final TextView center=(TextView)layoutBack.findViewById(R.id.center);
                    center.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popBack.dismiss();

                        }
                    });
                    ColorDrawable cd = new ColorDrawable(-0000);
                    popBack.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
                    popBack.update();
                    popBack.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popBack.setTouchable(true); // 设置popupwindow可点击
                    popBack.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    popBack.setFocusable(true); // 获取焦点
                    // 设置popupwindow的位置（相对tvLeft的位置）
                    popBack.showAtLocation(BottomRelativeLayout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                    popBack.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // 如果点击了popupwindow的外部，popupwindow也会消失
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popBack.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
                }
                break;
            /**分享*/
            case R.id.shareRelativeLayout:
                new Share(CircleParticularsActivity.this,BottomRelativeLayout,CircleParticularsActivity.TAG,CircleContentActivity.instance.topicList.get(0).getCircleList().get(0).getInvite_code(),"");
                break;
            /**圈子成员*/
            case R.id.MembersRelativeLayout:
                intent=new Intent(CircleParticularsActivity.this,CircleMemberActivity.class);
                intent.putExtra("entity",CircleContentActivity.instance.topicList.get(0).getCircleList().get(0));
                startActivity(intent);
                break;

            /**管理圈子*/
            case R.id.ManageRelativeLayout:
                intent=new Intent(CircleParticularsActivity.this,CircleManagementActivity.class);

                startActivity(intent);
                break;
        }
    }

    @Override
    public void content(String content) {
        CircleContentTextView.setText(content);
    }

    @Override
    public void name(String name) {

        CircleName.setText(name);
    }

    @Override
    public void head(String head) {
        ImageLoaderUtil.ImageLoader(head, CircleHeadImage,R.drawable.head_circle);
    }
}
