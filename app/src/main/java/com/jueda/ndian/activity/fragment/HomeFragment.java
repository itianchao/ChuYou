package com.jueda.ndian.activity.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.me.pop.PopEverydayTask;
import com.jueda.ndian.activity.home.adapter.HomeAdapter;
import com.jueda.ndian.activity.me.biz.BannerBiz;
import com.jueda.ndian.activity.home.biz.MarqueeBiz;
import com.jueda.ndian.activity.home.biz.TourismBiz;
import com.jueda.ndian.entity.ADInfo;
import com.jueda.ndian.entity.TravelEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.utils.getCurrentTime;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;

/**
 * 首页
 * Created by Administrator on 2016/7/2.
 */
public class HomeFragment extends Fragment{
    public static final String TAG="HomeFragment";
    private View view;
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示
    private RelativeLayout statusBar;//状态栏高度
    private ArrayList<TravelEntity> entityList;//出游列表
    private MyRefreshListView refreshListView;
    private HomeAdapter adapter;
    private int screenWidth;//屏幕宽度
    private int page=1;//页数
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载
    private RelativeLayout relativeLayout;//
    public TextView sign;//签到
    //banner图
    ArrayList<ADInfo> infos;

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**出游列表获取成功*/
                case Constants.ON_SUCCEED:
                    entityList= (ArrayList<TravelEntity>) msg.obj;
                    //获取banner横幅
                    new BannerBiz(getActivity(),handler,TAG,screenWidth);
                    break;
                /**出游列表没有更多*/
                case Constants.FAILURE:
                    if(page==1){
                        //获取banner横幅
                        new BannerBiz(getActivity(),handler,TAG, screenWidth);
                    }else{
                        refreshListView.onFinish(isRefreshOrLoading);
                        refreshListView.loading(false);
                        if(HomeFragment.this.isAdded()) {
                            new ToastShow(getActivity(), getResources().getString(R.string.No_more), 1000);
                        }
                    }
                    break;
                /**获取出游列表数据失败*/
                case Constants.FAILURE_TWO:
                    if(page==1){
                        refreshListView.setVisibility(View.GONE);
                        animation.stopAnim();
                        if(HomeFragment.this.isAdded()) {
                            NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                        }
                        include.setVisibility(View.VISIBLE);
                    }else{
                        --page;
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**banner图获取成功*/
                case Constants.ON_SUCEED_THREE:
                    infos= (ArrayList<ADInfo>) msg.obj;
                    new MarqueeBiz(getActivity(),handler);
                    break;
                /**banner图获取失败或跑马灯获取失败*/
                case Constants.FAILURE_THREE:
                    refreshListView.setVisibility(View.GONE);
                    animation.stopAnim();
                    if(HomeFragment.this.isAdded()) {
                        NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    }
                    include.setVisibility(View.VISIBLE);
                    break;
                /**获取跑马灯成功*/
                case Constants.ON_SUCEED_FOUR:
                    Bundle bundle= (Bundle) msg.obj;
                    ArrayList<String> list= bundle.getStringArrayList("list");
                    animation.stopAnim();
                    refreshListView.setVisibility(View.VISIBLE);
                    /**根据返回的数据数量判断是否开始上滑加载*/
                    if(entityList.size()-2==page*Constants.Page){
                        refreshListView.loading(true);
                    }else{
                        if(page==1){
                            refreshListView.loading(false);
                        }
                    }
                    //更新数据
                    if(page==1){
                        adapter=new HomeAdapter(getActivity(),entityList,screenWidth,infos,relativeLayout,list);
                        refreshListView.setAdapter(adapter);
                    }else{
                        adapter.updata(entityList,infos,list);
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=View.inflate(getActivity(), R.layout.fragment_home,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        InitView();
        setOnClick();
        super.onActivityCreated(savedInstanceState);
    }

    private void InitView() {
        screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        NdianApplication.instance.setTitle(view, getResources().getString(R.string.app_name), false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            statusBar=(RelativeLayout)view.findViewById(R.id.statusBar);
            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) statusBar.getLayoutParams();//获取空间当前布局参数
            layoutParams.height= MainActivity.instance.getStatusBarHeight();
            statusBar.setLayoutParams(layoutParams);
        }
        sign=(TextView)view.findViewById(R.id.sign);
        relativeLayout=(RelativeLayout)view.findViewById(R.id.relativeLayout);
        refreshListView=(MyRefreshListView)view.findViewById(R.id.refreshListView);
        /**初始化数据时界面显示*/
        include=(View)view.findViewById(R.id.failure);
        loadingInclude=(View)view.findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(getActivity(),loadingInclude);
        Reconnection();
    }

    /**
     * 计算签到时间
     */
    private void Sign_Time(){
        if(new Configuration().readaIsLogin(getActivity())){
            int time= Integer.parseInt(getCurrentTime.getCurrentTime(new Configuration().readaSign_in(getActivity()),"yyMMdd"));
            int ntime=Integer.parseInt(getCurrentTime.getCurrentTime(System.currentTimeMillis(),"yyMMdd"));
            new LogUtil("Time","Time:"+time+"\n+ntime:"+ntime);
            if(time==ntime){
                sign.setEnabled(false);
                sign.setText("已签到");
                sign.setTextColor(getResources().getColor(R.color.text_gray));
            }else{
                sign.setEnabled(true);
                sign.setText("签到");
                sign.setTextColor(getResources().getColor(R.color.white));
            }
        }else{
            sign.setEnabled(true);
            sign.setText("签到");
            sign.setTextColor(getResources().getColor(R.color.white));
        }

    }
    private void setOnClick() {
        //签到
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (new Configuration().readaIsLogin(getActivity())) {
                    new PopEverydayTask(getActivity(),relativeLayout,sign,"签到奖励");
                } else {
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
        /**点击重新加载*/
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reconnection();
            }
        });
        refreshListView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                entityList = new ArrayList<>();
                for (int i = 0; i < 2; i++) {
                    TravelEntity entity = new TravelEntity();
                    entityList.add(entity);
                }
                isRefreshOrLoading = true;
                new TourismBiz(getActivity(),handler,entityList,page);
            }

            @Override
            public void onLoadMore() {
                isRefreshOrLoading = true;
                ++page;
                new TourismBiz(getActivity(),handler,entityList,page);
            }
        });
    }

    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        refreshListView.setVisibility(View.GONE);
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            include.setVisibility(View.GONE);
            setData();
        }else{
            if(HomeFragment.this.isAdded()) {
                NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            }
            include.setVisibility(View.VISIBLE);
        }
    }
    private void setData() {
        animation.startAnim();
        entityList=new ArrayList<>();
        for(int i=0;i<2;i++){
            TravelEntity entity=new TravelEntity();
            entityList.add(entity);
        }
        new TourismBiz(getActivity(),handler,entityList,page);

    }



    @Override
    public void onResume() {
        Sign_Time();
        if(adapter!=null&&infos!=null){
            adapter.startBanner();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if(adapter!=null&&infos!=null){
            adapter.stopBanner();
        }
        super.onPause();
    }

}
