package com.jueda.ndian.activity.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.ConversationDetailActivity;
import com.jueda.ndian.activity.me.view.MyExchangeGoodsActivity;
import com.jueda.ndian.activity.me.view.MyReleaseGoodsActivity;
import com.jueda.ndian.activity.me.view.MySellerOrderActivity;
import com.jueda.ndian.activity.me.view.InviteFriendsActivity;
import com.jueda.ndian.activity.me.view.LVActivity;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.me.view.MessageActivity;
import com.jueda.ndian.activity.me.view.MyBuyersOrderActivity;
import com.jueda.ndian.activity.me.view.MyTaskActivity;
import com.jueda.ndian.activity.me.view.MyTicketActivity;
import com.jueda.ndian.activity.me.view.MyTopicActivity;
import com.jueda.ndian.activity.me.view.MyWalletActivity;
import com.jueda.ndian.activity.me.view.PersonalCenterActivity;
import com.jueda.ndian.activity.me.view.SettingActivity;
import com.jueda.ndian.activity.me.view.UserChange;
import com.jueda.ndian.activity.me.UserContent;
import com.jueda.ndian.activity.me.UserObservable;
import com.jueda.ndian.activity.me.biz.GetInfoBiz;
import com.jueda.ndian.entity.MessageEntity;
import com.jueda.ndian.entity.UserEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.savedata.MessageData;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.LvUtil;
import com.jueda.ndian.view.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *我
 * Created by Administrator on 2016/3/3.
 */
public class PersonalFragment extends Fragment implements View.OnClickListener,UserContent {
    public static final String TAG="PersonalFragment";
    public static PersonalFragment instance=null;
    private View view;
    private ScrollView scrollView;
    private RelativeLayout statusBar;//状态栏高度
    private RelativeLayout personal_set;//进入查看个人中心界面
    private CircleImageView HeadImage;//头像
    private TextView NickNameTextView;//昵称
    private RelativeLayout setting;//跳转到设置界面
    private RelativeLayout MyMessageRelativeLayout;//我的消息
    private RelativeLayout MyTopicRelativeLayout;//我的话题
    private RelativeLayout MyOrderRelativeLayout;//买家订单
    private RelativeLayout walletRelativeLayout;//我的收益
    private RelativeLayout feedbackRelativeLayout;//意见反馈
    private RelativeLayout Invite_friends;//邀请好友
    private RelativeLayout my_task;//我的任务
    private RelativeLayout ticketRelativeLayout;//我的旅游票
    private RelativeLayout ExchangeRelativeLayout;//兑换商品
    private RelativeLayout SellerOrderRelativeLayout;//卖家订单
    private RelativeLayout ReleaseGoodRelativeLayout;//发布商品
    private TextView devotionTextView;//我的爱心豆
    private TextView topic_number;//话题数量
    private TextView LveTextView;//等级
    private TextView ticketTextView;//旅游票数量
    private ImageView MessageImageView;//消息信息提醒
    private ImageView TaskMessage;//任务消息提醒
    public static UserObservable userObservable;//注册用户通知信息

    private ArrayList<UserEntity> userList;//用户信息
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**获取用户信息成功*/
                case Constants.ON_SUCCEED:

                    userList= (ArrayList<UserEntity>) msg.obj;
                    ConstantsUser.setUserEntity(getActivity(), ConstantsUser.userEntity.getUid(), ConstantsUser.userEntity.getPhoneNumber(), userList.get(0).getUname(), userList.get(0).getAvater(), userList.get(0).getSex()
                            , ConstantsUser.userEntity.getBirth(), ConstantsUser.userEntity.getJob(), ConstantsUser.userEntity.getEducation(), userList.get(0).getSignature(), userList.get(0).getDevotion()
                            , ConstantsUser.userEntity.getLastTime(), ConstantsUser.userEntity.getInviteCode(), ConstantsUser.userEntity.getUserToken(), ConstantsUser.userEntity.getRyToken()
                            ,userList.get(0).getCountDonationsDost(),userList.get(0).getLove_bean(),userList.get(0).getCount_post(),userList.get(0).getSurplus(),userList.get(0).getCount_ticket()
                            ,userList.get(0).getAdd_location(),ConstantsUser.userEntity.getAdd_phoneNumber(),ConstantsUser.userEntity.getAdd_uname());
                    userObservable.bean();
                    userObservable.money();
                    setUser();
                    break;
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=View.inflate(getActivity(), R.layout.fragment_personal,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        instance=this;
        InitView();
        setOnClick();
        super.onActivityCreated(savedInstanceState);
    }

    private void setOnClick() {
        personal_set.setOnClickListener(this);
        setting.setOnClickListener(this);
        feedbackRelativeLayout.setOnClickListener(this);
        MyMessageRelativeLayout.setOnClickListener(this);
        MyTopicRelativeLayout.setOnClickListener(this);
        walletRelativeLayout.setOnClickListener(this);
        MyOrderRelativeLayout.setOnClickListener(this);
        LveTextView.setOnClickListener(this);
        my_task.setOnClickListener(this);
        Invite_friends.setOnClickListener(this);
        ticketRelativeLayout.setOnClickListener(this);
        ExchangeRelativeLayout.setOnClickListener(this);
        SellerOrderRelativeLayout.setOnClickListener(this);
        ReleaseGoodRelativeLayout.setOnClickListener(this);
    }
    private void InitView() {
        NdianApplication.instance.setTitle(view, "我", false);
        userObservable=new UserChange();
        userObservable.add(PersonalFragment.this);

        ReleaseGoodRelativeLayout=(RelativeLayout)view.findViewById(R.id.ReleaseGoodRelativeLayout);
        SellerOrderRelativeLayout=(RelativeLayout)view.findViewById(R.id.SellerOrderRelativeLayout);
        ExchangeRelativeLayout=(RelativeLayout)view.findViewById(R.id.ExchangeRelativeLayout);
        feedbackRelativeLayout=(RelativeLayout)view.findViewById(R.id.feedbackRelativeLayout);
        MyOrderRelativeLayout=(RelativeLayout)view.findViewById(R.id.MyOrderRelativeLayout);
        my_task=(RelativeLayout)view.findViewById(R.id.my_task);
        MessageImageView=(ImageView)view.findViewById(R.id.MessageImageView);
        NickNameTextView=(TextView)view.findViewById(R.id.NickNameTextView);
        ticketTextView=(TextView)view.findViewById(R.id.ticketTextView);
        LveTextView=(TextView)view.findViewById(R.id.LveTextView);
        ticketRelativeLayout=(RelativeLayout)view.findViewById(R.id.ticketRelativeLayout);
        devotionTextView=(TextView)view.findViewById(R.id.devotionTextView);
        walletRelativeLayout=(RelativeLayout)view.findViewById(R.id.walletRelativeLayout);
        MyTopicRelativeLayout=(RelativeLayout)view.findViewById(R.id.MyTopicRelativeLayout);
        MyMessageRelativeLayout=(RelativeLayout)view.findViewById(R.id.MyMessageRelativeLayout);
        Invite_friends=(RelativeLayout)view.findViewById(R.id.Invite_friends);
        personal_set=(RelativeLayout)view.findViewById(R.id.personal_set);
        setting=(RelativeLayout)view.findViewById(R.id.setting);
        HeadImage=(CircleImageView)view.findViewById(R.id.HeadImage);
        scrollView=(ScrollView)view.findViewById(R.id.scrollView);
        topic_number=(TextView)view.findViewById(R.id.topic_number);
        TaskMessage=(ImageView)view.findViewById(R.id.TaskMessage);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);//去掉滑到尽头的系统回弹效果

        LinearLayout linearLayout1=(LinearLayout)view.findViewById(R.id.linearLayout1);
        GridLayout.LayoutParams layoutParams1= (GridLayout.LayoutParams) linearLayout1.getLayoutParams();
        layoutParams1.height=MainActivity.instance.screenWidth/3;
        linearLayout1.setLayoutParams(layoutParams1);

        LinearLayout linearLayout2=(LinearLayout)view.findViewById(R.id.linearLayout2);
        GridLayout.LayoutParams layoutParams2= (GridLayout.LayoutParams) linearLayout2.getLayoutParams();
        layoutParams2.height=MainActivity.instance.screenWidth/3;
        linearLayout2.setLayoutParams(layoutParams2);

        LinearLayout linearLayout3=(LinearLayout)view.findViewById(R.id.linearLayout3);
        GridLayout.LayoutParams layoutParams3= (GridLayout.LayoutParams) linearLayout3.getLayoutParams();
        layoutParams3.height=MainActivity.instance.screenWidth/3;
        linearLayout3.setLayoutParams(layoutParams3);

        /**
         * 判断系统
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            statusBar=(RelativeLayout)view.findViewById(R.id.statusBar);
            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) statusBar.getLayoutParams();//获取空间当前布局参数
            layoutParams.height= MainActivity.instance.getStatusBarHeight();
            statusBar.setLayoutParams(layoutParams);
        }
        updata();
    }

    /***
     * 更新用户信息
     */
    public void updata(){
        /**获取用户信息*/
        userList=new ArrayList<>();
        new GetInfoBiz(getActivity(),handler,ConstantsUser.userEntity.getUid()+"",userList);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            //发布商品
            case R.id.ReleaseGoodRelativeLayout:
                if(new Configuration().readaIsLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), MyReleaseGoodsActivity.class));
                }else{
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //卖家订单
            case R.id.SellerOrderRelativeLayout:
                if(new Configuration().readaIsLogin(getActivity())) {
                    intent = new Intent(getActivity(), MySellerOrderActivity.class);
                    startActivity(intent);
                }else{
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //兑换商品
            case R.id.ExchangeRelativeLayout:
                if(new Configuration().readaIsLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), MyExchangeGoodsActivity.class));
                }else{
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //我的旅游票
            case R.id.ticketRelativeLayout:
                if(new Configuration().readaIsLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), MyTicketActivity.class));
                }else{
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //邀请好友
            case R.id.Invite_friends:
                if(new Configuration().readaIsLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), InviteFriendsActivity.class));
                }else{
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //查看等级
            case R.id.LveTextView:
                    intent = new Intent(getActivity(), LVActivity.class);
                    startActivity(intent);
                break;
            //买家订单
            case R.id.MyOrderRelativeLayout:
                if(new Configuration().readaIsLogin(getActivity())) {
                    intent = new Intent(getActivity(), MyBuyersOrderActivity.class);
                    startActivity(intent);
                }else{
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //进去个人中心界面
            case R.id.personal_set:
                if(new Configuration().readaIsLogin(getActivity())) {
                    intent = new Intent(getActivity(), PersonalCenterActivity.class);
                    startActivity(intent);
                }else{
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //进入设置界面
            case R.id.setting:
                if(new Configuration().readaIsLogin(getActivity())) {
                    intent = new Intent(getActivity(), SettingActivity.class);
                    startActivity(intent);
                }else {
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //我的任务
            case R.id.my_task:
                if(new Configuration().readaIsLogin(getActivity())) {
                    startActivity(new Intent(getActivity(), MyTaskActivity.class));
                    //清除消息
                    TaskMessage.setVisibility(View.GONE);
                    MainActivity.instance.Message.setVisibility(View.GONE);
                    ArrayList<MessageEntity> messageArrayList = new MessageData().readaMessageData(getActivity());
                    for (int i = 0; i < messageArrayList.size(); i++) {
                        if (messageArrayList.get(i).getUserId().equals(ConstantsUser.userEntity.getUid() + "")) {
                            if (messageArrayList.get(i).getTaskAudit().equals("1")||messageArrayList.get(i).getTaskCompleted().equals("1")||messageArrayList.get(i).getTaskUnfinished().equals("1")) {
                                messageArrayList.remove(i);
                                --i;
                            }
                        }
                    }
                    new MessageData().writeaMessageData(getActivity(), messageArrayList);
                } else{
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //我的消息
            case R.id.MyMessageRelativeLayout:
                if(new Configuration().readaIsLogin(getActivity())) {
                    intent = new Intent(getActivity(), MessageActivity.class);
                    startActivity(intent);
                    //清除消息
                    MessageImageView.setVisibility(View.GONE);
                    MainActivity.instance.Message.setVisibility(View.GONE);
                    ArrayList<MessageEntity> messageArrayList = new MessageData().readaMessageData(getActivity());
                    for (int i = 0; i < messageArrayList.size(); i++) {
                        if (messageArrayList.get(i).getUserId().equals(ConstantsUser.userEntity.getUid() + "")) {
                            if (messageArrayList.get(i).getOtherMessage().equals("1") || messageArrayList.get(i).getCommentMessage().equals("1")) {
                                messageArrayList.remove(i);
                                --i;
                            }
                        }
                    }
                    new MessageData().writeaMessageData(getActivity(), messageArrayList);
                }else {
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //我的话题
            case R.id.MyTopicRelativeLayout:
                if(new Configuration().readaIsLogin(getActivity())) {
                    intent = new Intent(getActivity(), MyTopicActivity.class);
                    startActivity(intent);
                }else {
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;

            //我的收益
            case R.id.walletRelativeLayout:
                if(new Configuration().readaIsLogin(getActivity())) {
                    intent = new Intent(getActivity(), MyWalletActivity.class);
                    startActivity(intent);
                }else {
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //意见反馈
            case R.id.feedbackRelativeLayout:
                    intent = new Intent(getActivity(), ConversationDetailActivity.class);
                    startActivity(intent);
//                startActivity(new Intent(getActivity(), personActivity.class));
                break;
        }
    }

    /**
     * 设置用户信息
     */
    public void setUser(){
        /**加载用户数据*/
        if(new Configuration().readaIsLogin(getActivity())){
            ImageLoaderUtil.ImageLoader(ConstantsUser.userEntity.getAvater(), HeadImage);
            if(PersonalFragment.this.isAdded()) {
                devotionTextView.setText(ConstantsUser.userEntity.getLove_bean());
                topic_number.setText(ConstantsUser.userEntity.getCount_post());
            }
            NickNameTextView.setText(ConstantsUser.userEntity.getUname());
            ticketTextView.setText(ConstantsUser.userEntity.getCount_ticket());
            /**计算用户等级*/
            float lve=Integer.parseInt(ConstantsUser.userEntity.getDevotion());
            HashMap<String,String> hashMap= new LvUtil().LvUtil(lve);
            LveTextView.setVisibility(View.VISIBLE);
            String lvv=hashMap.get("lv");
            LveTextView.setText(lvv.substring(2, lvv.length()) + "级");
            messagePoint();
        }else{
            HeadImage.setImageResource(R.drawable.head_portrait);
            LveTextView.setVisibility(View.GONE);
            if(MainActivity.instance!=null){
                MainActivity.instance.Message.setVisibility(View.GONE);
            }
            MessageImageView.setVisibility(View.GONE);
            TaskMessage.setVisibility(View.GONE);
            if(PersonalFragment.this.isAdded()) {
                NickNameTextView.setText(getResources().getString(R.string.Please_log_in));
                devotionTextView.setText(0+"");
                topic_number.setText(0 + "");
                ticketTextView.setText("0");
            }
        }
    }

    /***
     * 控制消息红点
     *
     */
    public void messagePoint(){
        //查看消息提醒
        ArrayList<MessageEntity> messageArrayList=new MessageData().readaMessageData(getActivity());
        A:for(int i=0;i<messageArrayList.size();i++){
            if(messageArrayList.get(i).getUserId().equals(ConstantsUser.userEntity.getUid()+"")){
                if(MainActivity.instance!=null){
                    MainActivity.instance.Message.setVisibility(View.VISIBLE);
                }
                //除去任务消息外
                if(!messageArrayList.get(i).getTaskAudit().equals("1")&&!messageArrayList.get(i).getTaskCompleted().equals("1")&&!messageArrayList.get(i).getTaskUnfinished().equals("1")) {
                    MessageImageView.setVisibility(View.VISIBLE);
                }else{
                    TaskMessage.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onResume() {
        setUser();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        instance=null;
        super.onDestroy();
    }

    @Override
    public void bean() {
        setUser();
    }

    @Override
    public void money() {
    }
}
