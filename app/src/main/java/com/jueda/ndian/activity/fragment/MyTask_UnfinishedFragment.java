package com.jueda.ndian.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.adapter.MyTaskAudit_UnAdapter;
import com.jueda.ndian.activity.me.biz.MyTaskAuditBiz;
import com.jueda.ndian.entity.AppEntity;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;

/**
 * 任务未完成
 * Created by Administrator on 2016/6/12.
 */
public class MyTask_UnfinishedFragment extends LazyFragment{
    private View view;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    private MyRefreshListView list;//没有网络或者连接失败时隐藏
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示
    private ArrayList<AppEntity> entityList;
    private int page=1;//数据页数
    private MyTaskAudit_UnAdapter adapter;
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**请求数据成功*/
                case Constants.ON_SUCCEED:
                    animation.stopAnim();
                    list.setVisibility(View.VISIBLE);
                    entityList= (ArrayList<AppEntity>) msg.obj;
                    adapter.notifyDataSetChanged();
                    if(entityList.size()==page*Constants.Page){
                        list.loading(true);
                    }else{
                        if(page==1){
                            list.loading(false);
                        }
                    }
                    list.onFinish(isRefreshOrLoading);
                    break;
                /**请求数据失败或者没有数据时*/
                case Constants.FAILURE:
                    if(page==1){
                        list.setVisibility(View.GONE);
                        animation.stopAnim();
                        if(MyTask_UnfinishedFragment.this.isAdded()) {
                            NdianApplication.connectionFail(getResources().getString(R.string.Temporarily_no_data), include);
                        }
                        include.setVisibility(View.VISIBLE);
                    }else{
                        if(entityList.size()!=page*Constants.Page){
                            list.loading(false);
                        }else{
                            list.loading(true);
                        }
                        list.onFinish(isRefreshOrLoading);
                        list.loading(false);
                        if(MyTask_UnfinishedFragment.this.isAdded()) {
                            new ToastShow(getActivity(), getResources().getString(R.string.No_more), 1000);
                        }
                    }
                    break;
                /**请求失败*/
                case Constants.FAILURE_TWO:
                    if(page==1){
                        list.setVisibility(View.GONE);
                        animation.stopAnim();
                        if(MyTask_UnfinishedFragment.this.isAdded()) {
                            NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                        }
                        include.setVisibility(View.VISIBLE);
                    }else{
                        --page;
                    }
                    list.onFinish(isRefreshOrLoading);
                    break;

            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=View.inflate(getActivity(), R.layout.mytask_fragment,null);
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
        list=(MyRefreshListView)view.findViewById(R.id.list);
        /**初始化数据时界面显示*/
        include=(View)view.findViewById(R.id.failure);
        loadingInclude=(View)view.findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(getActivity(),loadingInclude);
        Reconnection();
    }
    private void setOnClick() {
        /**重新加载*/
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reconnection();
            }
        });

        list.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                isRefreshOrLoading = true;
                ++page;
                new MyTaskAuditBiz(getActivity(), handler, 0, page, entityList);
            }
        });
    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        list.setVisibility(View.GONE);
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            include.setVisibility(View.GONE);
            setData();
        }else{
            if(MyTask_UnfinishedFragment.this.isAdded()) {
                new ToastShow(getActivity(), getResources().getString(R.string.No_network_please_check_the_network1), 1000);
                NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            }
            include.setVisibility(View.VISIBLE);
        }
    }
    private void setData() {
        animation.startAnim();
        entityList=new ArrayList<>();
        adapter=new MyTaskAudit_UnAdapter(getActivity(),entityList);
        list.setAdapter(adapter);
        new MyTaskAuditBiz(getActivity(),handler,0,page,entityList);
    }
}
