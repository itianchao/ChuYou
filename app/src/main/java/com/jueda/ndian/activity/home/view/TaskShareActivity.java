package com.jueda.ndian.activity.home.view;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.home.adapter.TaskShareAdapter;
import com.jueda.ndian.activity.home.biz.TaskShareBiz;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;


/***
 * 分享任务
 */
public class TaskShareActivity extends AppCompatActivity {
    public static final String TAG=TaskShareActivity.class.getName();
    private TextView Send_goods;//发商品

    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示
    private MyRefreshListView refreshListView;
    private ArrayList<CommodityEntity> entityList;
    private TaskShareAdapter adapter;

    private int page=1;//页数
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**请求数据成功*/
                case Constants.ON_SUCCEED:
                    animation.stopAnim();
                    refreshListView.setVisibility(View.VISIBLE);
                    adapter.updata(entityList);
                    if(entityList.size()==page*Constants.Page){
                        refreshListView.loading(true);
                    }else{
                        if(page==1){
                            refreshListView.loading(false);
                        }
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**没有数据时*/
                case Constants.FAILURE:
                    if(page==1){
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Temporarily_no_message), include);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        refreshListView.onFinish(isRefreshOrLoading);
                        refreshListView.loading(false);
                        new ToastShow(TaskShareActivity.this,getResources().getString(R.string.No_more),1000);
                    }
                    break;
                /**请求失败*/
                case Constants.FAILURE_TWO:
                    if(page==1){
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        --page;
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_task_share);
        InitView();
        setOnClick();
    }
    private void InitView() {
        NdianApplication.instance.setTitle(this, "分享赚豆", true);
        refreshListView=(MyRefreshListView)findViewById(R.id.refreshListView);
        Send_goods=(TextView)findViewById(R.id.Send_goods);
        /**初始化数据时界面显示*/
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);
        entityList=new ArrayList<>();
        adapter=new TaskShareAdapter(TaskShareActivity.this,entityList);
        refreshListView.setAdapter(adapter);
        Reconnection();

    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            animation.startAnim();
            refreshListView.setVisibility(View.GONE);
            include.setVisibility(View.GONE);
            new TaskShareBiz(TaskShareActivity.this,handler,page,entityList);
        }else{
            new ToastShow(TaskShareActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClick() {
        /**发布商品*/
        Send_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Configuration().readaIsLogin(TaskShareActivity.this)){
                    Intent intent=new Intent(TaskShareActivity.this,ReleaseGoodsActivity.class);
                    startActivity(intent);
                }else{
                    startActivity(new Intent(TaskShareActivity.this, LoginActivity.class));
                }

            }
        });
        /**重连*/
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reconnection();
            }
        });
        refreshListView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                entityList = new ArrayList<>();
                page = 1;
                isRefreshOrLoading = true;
                new TaskShareBiz(TaskShareActivity.this, handler, page, entityList);
            }

            @Override
            public void onLoadMore() {
                ++page;
                isRefreshOrLoading = true;
                new TaskShareBiz(TaskShareActivity.this, handler, page, entityList);
            }
        });
    }
}
