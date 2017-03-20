package com.jueda.ndian.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.adapter.GuidepageAdapter;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/30.
 * 任务模块中的发布
 */
public class MyReleaseFragment extends LazyFragment implements  View.OnClickListener,ViewPager.OnPageChangeListener{
    private View view;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    private ArrayList<Fragment> fragmentList;
    private ViewPager viewPager;
    //切换界面按钮
    private RadioButton radioButton1;
    private RadioButton radioButton2;

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
        view=View.inflate(getActivity(), R.layout.fragment_myrelease,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        isPrepared=true;
        lazyLoad();
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
       InitView();
       setOnClick();
    }

    private void InitView() {
        viewPager=(ViewPager)view.findViewById(R.id.viewPager);
        radioButton1=(RadioButton)view.findViewById(R.id.radioButton1);
        radioButton2=(RadioButton)view.findViewById(R.id.radioButton2);
        start_location1=new int[2];
        radioButton1.getLocationInWindow(start_location1);
        select= (TextView) view.findViewById(R.id.select);
        RelativeLayout.LayoutParams linearParams1 = (RelativeLayout.LayoutParams) select.getLayoutParams(); // 取控件当前的布局参数
        linearParams1.width= MainActivity.instance.getScreenWidth()/2;// 当控件的高强制设成图片高度linearParams1.height=i/2;// 当控件的高强制设成图片高度
        select.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件imageview中

        fragmentList=new ArrayList<>();
        fragmentList.add(new MyTask_ActivityFragment());//活动
        fragmentList.add(new MyTask_QuestionnaireFragment());//问卷
        FragmentManager manager=getChildFragmentManager();
        GuidepageAdapter adapter=new GuidepageAdapter(manager,fragmentList);
        viewPager.setAdapter(adapter);
    }
    private void setOnClick() {
        radioButton1.setOnClickListener(this);
        radioButton2.setOnClickListener(this);
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
                viewPager.setCurrentItem(0, false);
                starAnim(radioButton1);
                break;
            //已完成
            case R.id.radioButton2:
                starAnim(radioButton2);
                viewPager.setCurrentItem(1, false);
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
            radioButton2.setChecked(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
