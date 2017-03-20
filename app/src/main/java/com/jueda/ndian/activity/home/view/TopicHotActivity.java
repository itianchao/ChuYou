package com.jueda.ndian.activity.home.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.adapter.TopicHotAdapter;
import com.jueda.ndian.activity.home.biz.TopicHotBiz;
import com.jueda.ndian.entity.TopicEntity;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;

/***
 * 热门话题
 */
public class TopicHotActivity extends AppCompatActivity implements TopicHotAdapter.OnChangeList {
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示

    private MyRefreshListView refreshListView;
    private ArrayList<TopicEntity> entityList;
    private int page=1;//页数
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载
    private TopicHotAdapter adapter;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.ON_SUCCEED:
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
                    adapter.updata(entityList);
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**获取话题列表没有数据了*/
                case Constants.FAILURE:
                    if(page==1){
                        refreshListView.onFinish(isRefreshOrLoading);
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Temporarily_no_data), include);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        refreshListView.onFinish(isRefreshOrLoading);
                        refreshListView.loading(false);
                        new ToastShow(TopicHotActivity.this,getResources().getString(R.string.No_more),1000);
                    }
                    break;
                /**获取话题列表失败*/
                case Constants.FAILURE_TWO:
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
        new ChangeTitle(this);
        setContentView(R.layout.activity_topic_hot);
        InitView();
        setOnClick();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.hot_topic), true);
        refreshListView=(MyRefreshListView)findViewById(R.id.refreshListView);
        /**初始化数据时界面显示*/
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);
        Reconnection();
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
            new ToastShow(TopicHotActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }
    private void setData() {
        animation.startAnim();
        entityList=new ArrayList<>();
        adapter=new TopicHotAdapter(TopicHotActivity.this,entityList);
        refreshListView.setAdapter(adapter);
        adapter.setOnChangeList(this);
        /**获取话题列表数据*/
       new TopicHotBiz(TopicHotActivity.this,handler,entityList,page);
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
                isRefreshOrLoading=true;
                page=1;
                entityList=new ArrayList<TopicEntity>();
                new TopicHotBiz(TopicHotActivity.this,handler,entityList,page);
            }

            @Override
            public void onLoadMore() {
                isRefreshOrLoading=true;
                ++page;
                new TopicHotBiz(TopicHotActivity.this,handler,entityList,page);
            }
        });
    }

    @Override
    public void change(ArrayList<TopicEntity> entityLis) {
        this.entityList=entityLis;
    }
}
