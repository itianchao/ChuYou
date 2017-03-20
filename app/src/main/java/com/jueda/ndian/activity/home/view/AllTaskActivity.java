package com.jueda.ndian.activity.home.view;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.TaskActivity_Fragment;
import com.jueda.ndian.activity.fragment.TaskQuestionnaire_Fragment;
import com.jueda.ndian.activity.fragment.TaskEx_Fragment;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.home.adapter.GuidepageAdapter;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.view.IndexViewPager;

import java.util.ArrayList;


/**
 * 圈外任务
 * Created by Administrator on 2016/3/3.
 */
public class AllTaskActivity extends FragmentActivity implements View.OnClickListener{
    public static AllTaskActivity instance=null;
    public static final String TAG=AllTaskActivity.class.getName();
    private ArrayList<Fragment> fragmentList;
    private IndexViewPager viewPager;
    private TextView send_task;//发任务
    //切换界面按钮
    private RadioButton radioButton1;//体验
    private RadioButton radioButton2;//活动
    private RadioButton radioButton3;//问卷
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        instance=this;
        setContentView(R.layout.fragment_task);
        InitView();
        setOnClick();
    }

    private void setOnClick() {
        radioButton1.setOnClickListener(this);
        radioButton2.setOnClickListener(this);
        radioButton3.setOnClickListener(this);
        send_task.setOnClickListener(this);
    }
    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Travel_task), true);
        send_task=(TextView)findViewById(R.id.send_task);
        viewPager=(IndexViewPager)findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(4);
        radioButton1=(RadioButton)findViewById(R.id.radioButton1);
        radioButton2=(RadioButton)findViewById(R.id.radioButton2);
        radioButton3=(RadioButton)findViewById(R.id.radioButton3);
        start_location1=new int[2];
        radioButton1.getLocationInWindow(start_location1);
        select= (TextView)findViewById(R.id.select);
        RelativeLayout.LayoutParams linearParams1 = (RelativeLayout.LayoutParams) select.getLayoutParams(); // 取控件当前的布局参数
        linearParams1.width= MainActivity.instance.getScreenWidth()/3;// 当控件的高强制设成图片高度linearParams1.height=i/3;// 当控件的高强制设成图片高度
        select.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件imageview中

        fragmentList=new ArrayList<>();
        Bundle bundle=new Bundle();
        bundle.putString("cid", "");
        bundle.putString("tag", TAG);

        TaskEx_Fragment outsideEx_taskFragment =new TaskEx_Fragment();
        outsideEx_taskFragment.setArguments(bundle);

        TaskActivity_Fragment oursideActivity_taskFragment=new TaskActivity_Fragment();
        oursideActivity_taskFragment.setArguments(bundle);

        TaskQuestionnaire_Fragment oursideQuestionnaire_taskFragment=new TaskQuestionnaire_Fragment();
        oursideQuestionnaire_taskFragment.setArguments(bundle);

        fragmentList.add(outsideEx_taskFragment);
        fragmentList.add(oursideActivity_taskFragment);
        fragmentList.add(oursideQuestionnaire_taskFragment);

        FragmentManager manager=this.getSupportFragmentManager();
        GuidepageAdapter adapter=new GuidepageAdapter(manager,fragmentList);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //发任务
            case R.id.send_task:
                if(new Configuration().readaIsLogin(AllTaskActivity.this)){
                    Intent intent=new Intent(AllTaskActivity.this,TaskReleaseActivity.class);
                    startActivity(intent);
                }else{
                    startActivity(new Intent(AllTaskActivity.this, LoginActivity.class));
                }
                break;
            //体验
            case R.id.radioButton1:
                viewPager.setCurrentItem(0, false);
                starAnim(radioButton1);
                break;
            //活动
            case R.id.radioButton2:
                starAnim(radioButton2);
                viewPager.setCurrentItem(1, false);
                break;
            //问卷
            case R.id.radioButton3:
                starAnim(radioButton3);
                viewPager.setCurrentItem(2, false);
                break;

        }
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
    protected void onDestroy() {
        instance=null;
        super.onDestroy();
    }
}
