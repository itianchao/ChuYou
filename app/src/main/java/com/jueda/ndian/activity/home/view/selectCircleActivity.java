package com.jueda.ndian.activity.home.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.adapter.MyCircleAdapter;
import com.jueda.ndian.activity.circle.biz.JoinCircleBiz;
import com.jueda.ndian.entity.CircleEntity;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;

/**
 * 选择圈子
 */
public class selectCircleActivity extends AppCompatActivity {
    public static final String TAG="selectCircleActivity";

    private MyRefreshListView MyCircleListView;//我的圈子
    private TextView MyCircleTextView;//显示我的圈子个数

    private MyCircleAdapter myCircleAdapter;//我的圈子适配器
    public  ArrayList<CircleEntity> MyCircleList;//我的圈子数据

    private RelativeLayout relativeLayout;
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示


    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**我的圈子获取成功*/
                case Constants.ON_SUCCEED:
                    animation.stopAnim();
                    relativeLayout.setVisibility(View.VISIBLE);
                    MyCircleList= (ArrayList<CircleEntity>) msg.obj;
                    MyCircleTextView.setText(getResources().getString(R.string.My_circle) + "(" + MyCircleList.size() + ")");
                    myCircleAdapter.notifyDataSetChanged();
                    break;
                /**获取失败*/
                case Constants.FAILURE_TWO:
                    animation.stopAnim();
                    NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    include.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_select_circle);
        InitView();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this,"选择圈子",true);

        MyCircleListView=(MyRefreshListView)findViewById(R.id.MyCircleListView);
        MyCircleTextView=(TextView)findViewById(R.id.MyCircleTextView);
        relativeLayout=(RelativeLayout)findViewById(R.id.myrelativeLayout);
        /**初始化数据时界面显示*/
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(selectCircleActivity.this,loadingInclude);
        Reconnection();
    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        relativeLayout.setVisibility(View.GONE);
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            animation.startAnim();
            include.setVisibility(View.GONE);
            setData();
        }else{
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }
    public void setData() {
        //慈善圈
        MyCircleList=new ArrayList<>();
        //我的圈子
        myCircleAdapter=new MyCircleAdapter(selectCircleActivity.this,MyCircleList,TAG);
        MyCircleListView.setAdapter(myCircleAdapter);
        //获取圈子
        new JoinCircleBiz(selectCircleActivity.this,handler,MyCircleList);
    }

}
