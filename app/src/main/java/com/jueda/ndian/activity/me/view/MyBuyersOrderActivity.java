package com.jueda.ndian.activity.me.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.adapter.MyBuyersOrderAdapter;
import com.jueda.ndian.activity.me.biz.MyBuyersOrderBiz;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.LoginOutUtil;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;

/***
 * 买家订单
 */
public class MyBuyersOrderActivity extends AppCompatActivity {
    public static final String TAG=MyBuyersOrderActivity.class.getName();
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示

    private MyRefreshListView refreshListView;//刷新
    private int page=1;//页数
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载
    private ArrayList<CommodityEntity> entityList;
    private MyBuyersOrderAdapter adapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**获取数据成功*/
                case Constants.ON_SUCCEED:

                    if(entityList.size()==page*Constants.Page){
                        refreshListView.loading(true);
                    }else{
                        if(page==1){
                            refreshListView.loading(false);
                        }
                    }
                    animation.stopAnim();
                    refreshListView.setVisibility(View.VISIBLE);
                    entityList= (ArrayList<CommodityEntity>) msg.obj;
                    adapter.notifyDataSetChanged();
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**没有更多数据*/
                case Constants.FAILURE:
                    if(page==1){
                        refreshListView.setVisibility(View.GONE);
                        animation.stopAnim();
                        NdianApplication.connectionFail("您没有已付款订单", include);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        if(entityList.size()!=page*Constants.Page){
                            refreshListView.loading(false);
                        }else{
                            refreshListView.loading(true);
                        }
                        refreshListView.loading(false);
                        new ToastShow(MyBuyersOrderActivity.this,getResources().getString(R.string.No_more),1000);
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**获取数据失败*/
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
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new LoginOutUtil(MyBuyersOrderActivity.this);
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_my_order);
        InitView();
        setOnClick();
    }

    private void setOnClick() {
        /**重新加载*/
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Reconnection();
            }
        });
        refreshListView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshOrLoading=true;
                page=1;
                entityList=new ArrayList<>();
                new MyBuyersOrderBiz(MyBuyersOrderActivity.this,handler,entityList,page);
            }

            @Override
            public void onLoadMore() {
                isRefreshOrLoading=true;
                ++page;
                new MyBuyersOrderBiz(MyBuyersOrderActivity.this,handler,entityList,page);
            }
        });
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.My_order), true);
        refreshListView=(MyRefreshListView)findViewById(R.id.refreshListView);
        /**初始化数据时界面显示*/
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(MyBuyersOrderActivity.this,loadingInclude);
        Reconnection();
    }


    private void setData() {
        animation.startAnim();
        entityList=new ArrayList<>();
        adapter=new MyBuyersOrderAdapter(MyBuyersOrderActivity.this,entityList);
        refreshListView.setAdapter(adapter);
        new MyBuyersOrderBiz(MyBuyersOrderActivity.this,handler,entityList,page);
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
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }
}
