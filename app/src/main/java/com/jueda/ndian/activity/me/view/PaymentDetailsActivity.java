package com.jueda.ndian.activity.me.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.adapter.PaymentDetailsAdapter;
import com.jueda.ndian.activity.me.biz.PaymentDetailsBiz;
import com.jueda.ndian.entity.PaymentDetailsEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;

/**
 * 收支明细
 */
public class PaymentDetailsActivity extends AppCompatActivity {
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示

    private MyRefreshListView refreshListView;
    private ArrayList<PaymentDetailsEntity> entityList;
    private PaymentDetailsAdapter adapter;
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
                    entityList= (ArrayList<PaymentDetailsEntity>) msg.obj;
                    adapter.notifyDataSetChanged();
                    refreshListView.onFinish(isRefreshOrLoading);
                    if(entityList.size()==page*Constants.Page){
                        refreshListView.loading(true);
                    }else{
                        if(page==1){
                            refreshListView.loading(false);
                        }
                    }
                    break;
                /**请求数据失败或者没有数据时*/
                case Constants.FAILURE:
                    if(page==1){
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Temporarily_no_data), include);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        if(entityList.size()!=page*Constants.Page){
                            refreshListView.loading(false);
                        }else{
                            refreshListView.loading(true);
                        }
                        refreshListView.onFinish(isRefreshOrLoading);
                        refreshListView.loading(false);
                        new ToastShow(PaymentDetailsActivity.this,getResources().getString(R.string.No_more),1000);
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
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面

                    animation.stopAnim();
                    NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    include.setVisibility(View.VISIBLE);
                    new Configuration().writeaIsLogin(PaymentDetailsActivity.this,false);
                    Intent intent = new Intent(PaymentDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_payment_details);
        InitView();
        setOnClick();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Payment_details), true);
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
        adapter=new PaymentDetailsAdapter(getApplicationContext(),entityList);
        refreshListView.setAdapter(adapter);
        new PaymentDetailsBiz(PaymentDetailsActivity.this,handler,page,entityList);
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

            }

            @Override
            public void onLoadMore() {
                isRefreshOrLoading=true;
                ++page;
                new PaymentDetailsBiz(PaymentDetailsActivity.this,handler,page,entityList);
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
            new ToastShow(PaymentDetailsActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }

}
