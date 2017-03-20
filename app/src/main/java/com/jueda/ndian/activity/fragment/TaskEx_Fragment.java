package com.jueda.ndian.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.view.CircleAllTaskActivity;
import com.jueda.ndian.activity.home.adapter.TaskExperienceListAdapter;
import com.jueda.ndian.activity.home.biz.TaskExperienceListBiz;
import com.jueda.ndian.download.DownloadInfo;
import com.jueda.ndian.entity.AppEntity;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.GetDeviceId;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 体验任务
 * Created by Administrator on 2016/6/7.
 */
public class TaskEx_Fragment extends LazyFragment {
    public static TaskEx_Fragment instance=null;
    public static String TAG=TaskEx_Fragment.class.getName();
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private View view;
    private MyRefreshListView listView;
    public TaskExperienceListAdapter adapter;//任务适配器
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示
    private int page=1;//数据页数
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载
    private ArrayList<AppEntity> appEntityArrayList;


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**任务列表获取成功*/
                case Constants.ON_SUCEED_TWO:
                    animation.stopAnim();
                    listView.setVisibility(View.VISIBLE);
                    appEntityArrayList= (ArrayList<AppEntity>) msg.obj;
                    adapter.updata(appEntityArrayList);
                    if(appEntityArrayList.size()==page*Constants.Page){
                        listView.loading(true);
                    }else{
                        if(page==1){
                            listView.loading(false);
                        }
                    }
                    listView.onFinish(isRefreshOrLoading);
                    break;
                /**没有数据了*/
                case Constants.FAILURE_THREE:
                    if(page==1){
                        listView.onFinish(true);
                        animation.stopAnim();
                        if(TaskEx_Fragment.this.isAdded()) {
                            NdianApplication.connectionFail(getResources().getString(R.string.No_downloadable_applications_please_feedback_to_the_platform), include);
                        }
                        include.setVisibility(View.VISIBLE);
                    }else{
                        /**根据返回的数据数量判断是否开始上滑加载*/
                        if(appEntityArrayList.size()!=page*Constants.Page){
                            listView.loading(false);
                        }else{
                            listView.loading(true);
                        }
                        listView.onFinish(true);
                        listView.loading(false);
                        if(TaskEx_Fragment.this.isAdded()) {
                            new ToastShow(getActivity(), getResources().getString(R.string.No_more), 1000);
                        }
                    }
                    break;
                /**任务列表获取失败*/
                case Constants.FAILURE_FOUR:
                    if(page==1){
                        listView.setVisibility(View.GONE);
                        animation.stopAnim();
                        if(TaskEx_Fragment.this.isAdded()) {
                            listView.setVisibility(View.GONE);
                            NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                        }
                        include.setVisibility(View.VISIBLE);
                    }else {
                        --page;
                    }
                    listView.onFinish(isRefreshOrLoading);
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=View.inflate(getActivity(), R.layout.outside_task,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        isPrepared = true;
        lazyLoad();
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
        instance=this;
        clearData();
        InitView();
        setOnClick();
    }

    /***
     * 上个界面传递的cid
     * @return
     */
    public String cid(){
        Bundle bundle= getArguments();
        String cid=bundle.getString("cid");
        if(cid==null) return "";
        return bundle.getString("cid");
    }

    /**
     * 判断是圈外(true)还是圈内(false)
     * @return
     */
    public boolean Ciecle_or_Outside(){
        Bundle bundle= getArguments();
        if(bundle.getString("tag").equals(CircleAllTaskActivity.class.getName())){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 调用接口获取数据
     */
    public void loadData(){
        if(Ciecle_or_Outside()){
            new TaskExperienceListBiz(getActivity(), handler, appEntityArrayList, MainActivity.instance.HOME, page,cid());
        }else{
            new TaskExperienceListBiz(getActivity(), handler, appEntityArrayList, MainActivity.instance.CIRCLE, page,cid());
        }
    }
    private void setOnClick() {
        /**重新加载*/
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reconnection();
            }
        });
        listView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                isRefreshOrLoading = true;
                appEntityArrayList = new ArrayList<AppEntity>();
                //获取任务列表
                loadData();
            }

            @Override
            public void onLoadMore() {
                isRefreshOrLoading = true;
                ++page;
                loadData();
            }
        });
    }

    private void InitView() {
        listView=(MyRefreshListView)view.findViewById(R.id.listView);
        /**初始化数据时界面显示*/
        include=(View)view.findViewById(R.id.failure);
        loadingInclude=(View)view.findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(getActivity(),loadingInclude);

        Reconnection();
    }

    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        listView.setVisibility(View.GONE);
        if (Constants.DeviceId.equals("")) {
            /**获取机器码*/
            Constants.DeviceId = GetDeviceId.GetDeviceIds(getActivity());
        }
        /**没有网络时界面隐藏。只显示图片*/
        if (Constants.currentNetworkType != Constants.TYPE_NONE) {
            include.setVisibility(View.GONE);
            animation.startAnim();
            appEntityArrayList=new ArrayList<>();
            if(Ciecle_or_Outside()){
                adapter=new TaskExperienceListAdapter(getActivity(),appEntityArrayList,MainActivity.instance.HOME);
            }else{
                adapter=new TaskExperienceListAdapter(getActivity(),appEntityArrayList,MainActivity.instance.CIRCLE);
            }
            listView.setAdapter(adapter);
            loadData();
        } else {
            if (TaskEx_Fragment.this.isAdded()) {
                NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            }
            include.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onDestroy() {
        instance=null;
        super.onDestroy();
    }

    public void clearData() {
        /**获取数据*/
        List<DownloadInfo> list;
        list=MainActivity.instance.downloadManager.getDownloadInfoList();
        int n=list.size();
        if(list!=null){
            for(int i=0;i<n;i++){
                try {
                    DownloadInfo downloadInfo = MainActivity.instance.downloadManager.getDownloadInfo(0);
                    MainActivity.instance.downloadManager.removeDownload(downloadInfo);
                    File file = new File(downloadInfo.getFileSavePath());
                    if(file.exists()){
                        file.delete();
                    }
                } catch (com.lidroid.xutils.exception.DbException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
