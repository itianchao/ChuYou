package com.jueda.ndian.activity.me.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.adapter.MyTopicAdapter;
import com.jueda.ndian.activity.circle.biz.TopicBiz;
import com.jueda.ndian.entity.TopicEntity;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;

/**
 * 我的话题
 */
public class MyTopicActivity extends AppCompatActivity {
    public static final String TAG="MyTopicActivity";
    public static MyTopicActivity instance=null;
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示


    private MyRefreshListView refreshListView;
    private ArrayList<TopicEntity> entityList;
    private MyTopicAdapter adapter;
    private int screenWidth;//屏幕宽度
    private int page=1;//页数
    public RelativeLayout relativeLayout;//弹窗相对位置
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.ON_SUCEED_TWO:
                    /**关闭动画*/
                    animation.stopAnim();
                    refreshListView.setVisibility(View.VISIBLE);
                    entityList= (ArrayList<TopicEntity>) msg.obj;
                    /**根据返回的数据数量判断是否开始上滑加载*/
                    if(entityList.size()==page*Constants.Page){
                        refreshListView.loading(true);
                    }else{
                        if(page==1){
                            refreshListView.loading(false);
                        }
                    }
                    /**加载数据*/
                    adapter.notifyDataSetChanged();
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**获取话题列表没有数据了*/
                case Constants.FAILURE_TWO:
                    if(page==1){
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Temporarily_no_data), include);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        refreshListView.loading(false);
                        new ToastShow(MyTopicActivity.this,getResources().getString(R.string.No_more),1000);
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**获取话题列表失败*/
                case Constants.FAILURE_THREE:
                    refreshListView.onFinish(isRefreshOrLoading);
                    if(page==1){
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                        include.setVisibility(View.VISIBLE);
                    }else {
                        --page;
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
        new ChangeTitle(this);
        setContentView(R.layout.activity_my_topic);
        InitView();
        setOnClick();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.My_topic), true);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        refreshListView=(MyRefreshListView)findViewById(R.id.refreshListView);
        /**初始化数据时界面显示*/
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);
        Reconnection();
    }

    private void setData() {
        animation.startAnim();
        entityList=new ArrayList<>();
        adapter=new MyTopicAdapter(MyTopicActivity.this,entityList,screenWidth,relativeLayout);
        refreshListView.setAdapter(adapter);
        /**获取话题列表数据*/
        new TopicBiz(MyTopicActivity.this,handler,entityList,page);
    }


    private void setOnClick() {
        /**重连*/
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reconnection();
            }
        });
        /**
         * 刷新
         */
        refreshListView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                isRefreshOrLoading=true;
                ++page;
                new TopicBiz(MyTopicActivity.this,handler,entityList,page);
            }
        });
    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            include.setVisibility(View.GONE);
            setData();
        }else{
            new ToastShow(MyTopicActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }
}
