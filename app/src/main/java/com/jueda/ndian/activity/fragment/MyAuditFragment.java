package com.jueda.ndian.activity.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.adapter.GuidepageAdapter;
import com.jueda.ndian.entity.MessageEntity;
import com.jueda.ndian.savedata.MessageData;
import com.jueda.ndian.utils.ConstantsUser;

import java.util.ArrayList;

/***
 * 任务模块中的审核
 */
public class MyAuditFragment extends Fragment implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private View view;
    private ArrayList<Fragment> fragmentList;
    private ViewPager viewPager;
    //切换界面按钮
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private ImageView unfinishedImageView;//未完成
    private ImageView completeImageView;//已完成
    private ImageView auditImageView;//审核中
    private int completeNumber=0;
    private int unfinishedNumber=0;

    private AnimationSet mAnimationSet;//动画集合
    private  int []start_location1;//起始位置
    private  int []end_location1;//终点位置
    private TextView select;//选择条

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    select.startAnimation(mAnimationSet);
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=View.inflate(getActivity(),R.layout.activity_my_task,null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        InitView();
        setOnClick();
        super.onActivityCreated(savedInstanceState);
    }

    private void InitView() {
        viewPager=(ViewPager)view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(4);
        radioButton1=(RadioButton)view.findViewById(R.id.radioButton1);
        radioButton2=(RadioButton)view.findViewById(R.id.radioButton2);
        radioButton3=(RadioButton)view.findViewById(R.id.radioButton3);
        unfinishedImageView=(ImageView)view.findViewById(R.id.unfinishedImageView);
        completeImageView=(ImageView)view.findViewById(R.id.completeImageView);
        auditImageView=(ImageView)view.findViewById(R.id.auditImageView);
        //清除审核中消息通知
        ArrayList<MessageEntity> messageArrayList=new MessageData().readaMessageData(getActivity());
        for(int i=0;i<messageArrayList.size();i++){
            if(messageArrayList.get(i).getUserId().equals(ConstantsUser.userEntity.getUid()+"")) {
                if(messageArrayList.get(i).getTaskCompleted().equals("1")){
                    completeImageView.setVisibility(View.VISIBLE);
                    ++completeNumber;
                }
                if(messageArrayList.get(i).getTaskUnfinished().equals("1")){
                    unfinishedImageView.setVisibility(View.VISIBLE);
                    ++unfinishedNumber;
                }
                if (messageArrayList.get(i).getTaskAudit().equals("1")) {
                    messageArrayList.remove(i);
                    --i;
                }
            }
        }
        new MessageData().writeaMessageData(getActivity(), messageArrayList);

        start_location1=new int[2];
        radioButton1.getLocationInWindow(start_location1);
        select= (TextView) view.findViewById(R.id.select);
        RelativeLayout.LayoutParams linearParams1 = (RelativeLayout.LayoutParams) select.getLayoutParams(); // 取控件当前的布局参数
        linearParams1.width= MainActivity.instance.getScreenWidth()/3;// 当控件的高强制设成图片高度linearParams1.height=i/3;// 当控件的高强制设成图片高度
        select.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件imageview中

        fragmentList=new ArrayList<>();
        fragmentList.add(new MyTask_AuditFragment());//审核中
        fragmentList.add(new MyTask_CompleteFragment());//已完成
        fragmentList.add(new MyTask_UnfinishedFragment());//未完成
        viewPager.setOffscreenPageLimit(4);
        FragmentManager manager=getChildFragmentManager();
        GuidepageAdapter adapter=new GuidepageAdapter(manager,fragmentList);
        viewPager.setAdapter(adapter);
    }


    private void setOnClick() {
        radioButton1.setOnClickListener(this);
        radioButton2.setOnClickListener(this);
        radioButton3.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
    }
    /**
     * 设置线条滑动
     * @param start_location
     * @param end_location
     */
    private void setAnim(int[] start_location,int [] end_location){
        mAnimationSet = new AnimationSet(true);
        int endX = end_location[0];
        Animation mTranslateAnimation = new TranslateAnimation(start_location[0],endX,0,0);
        mTranslateAnimation.setDuration(300);
        mAnimationSet.setFillAfter(true);
        mAnimationSet.addAnimation(mTranslateAnimation);
        handler.sendEmptyMessage(1);
    }
    private void starAnim(RadioButton button){
        int[] start=new int[2];
        button.getLocationInWindow(start);//获取线的初始位置
        if(start_location1[0]!=start[0]){
            end_location1=new int[2];
            button.getLocationInWindow(end_location1);//获取线最终的位置
            setAnim(start_location1, end_location1);
            start_location1=start;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //审核中
            case R.id.radioButton1:
                viewPager.setCurrentItem(0,false);
                starAnim(radioButton1);
                break;
            //已完成
            case R.id.radioButton2:
                if(completeNumber!=0){
                    //清除审核中消息通知
                    ArrayList<MessageEntity> messageArrayList=new MessageData().readaMessageData(getActivity());
                    for(int i=0;i<messageArrayList.size();i++){
                        if(messageArrayList.get(i).getUserId().equals(ConstantsUser.userEntity.getUid()+"")) {
                            if(messageArrayList.get(i).getTaskCompleted().equals("1")){
                                messageArrayList.remove(i);
                                --i;
                                --completeNumber;
                            }
                        }
                    }
                    new MessageData().writeaMessageData(getActivity(),messageArrayList);
                    completeImageView.setVisibility(View.GONE);
                }
                starAnim(radioButton2);
                viewPager.setCurrentItem(1, false);
                break;
            //未完成
            case R.id.radioButton3:
                if(unfinishedNumber!=0){
                    //清除审核中消息通知
                    ArrayList<MessageEntity> messageArrayList=new MessageData().readaMessageData(getActivity());
                    for(int i=0;i<messageArrayList.size();i++){
                        if(messageArrayList.get(i).getUserId().equals(ConstantsUser.userEntity.getUid()+"")) {
                            if(messageArrayList.get(i).getTaskUnfinished().equals("1")){
                                messageArrayList.remove(i);
                                --i;
                                --unfinishedNumber;
                            }
                        }
                    }

                    new MessageData().writeaMessageData(getActivity(),messageArrayList);
                    unfinishedImageView.setVisibility(View.GONE);
                }
                starAnim(radioButton3);
                viewPager.setCurrentItem(2,false);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position==0){
            starAnim(radioButton1);
            radioButton1.setChecked(true);
        }else if(position==1){
            starAnim(radioButton2);
            if(completeNumber!=0){
                //清除审核中消息通知
                ArrayList<MessageEntity> messageArrayList=new MessageData().readaMessageData(getActivity());
                for(int i=0;i<messageArrayList.size();i++){
                    if(messageArrayList.get(i).getUserId().equals(ConstantsUser.userEntity.getUid()+"")) {
                        if(messageArrayList.get(i).getTaskCompleted().equals("1")){
                            messageArrayList.remove(i);
                            --i;
                            --completeNumber;
                        }
                    }
                }
                new MessageData().writeaMessageData(getActivity(),messageArrayList);
                completeImageView.setVisibility(View.GONE);
            }
            radioButton2.setChecked(true);
        }else if(position==2){
            starAnim(radioButton3);
            if(unfinishedNumber!=0){
                //清除审核中消息通知
                ArrayList<MessageEntity> messageArrayList=new MessageData().readaMessageData(getActivity());
                for(int i=0;i<messageArrayList.size();i++){
                    if(messageArrayList.get(i).getUserId().equals(ConstantsUser.userEntity.getUid()+"")) {
                        if(messageArrayList.get(i).getTaskUnfinished().equals("1")){
                            messageArrayList.remove(i);
                            --i;
                            --unfinishedNumber;
                        }
                    }
                }
                new MessageData().writeaMessageData(getActivity(),messageArrayList);
                unfinishedImageView.setVisibility(View.GONE);
            }
            radioButton3.setChecked(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
