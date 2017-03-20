package com.jueda.ndian.activity.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.view.CommodityDetailsActivity;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.circle.view.NewCircleActivity;
import com.jueda.ndian.activity.circle.view.SearchActivity;
import com.jueda.ndian.activity.circle.view.TopicDetailsActivity;
import com.jueda.ndian.activity.home.view.VideoActivity;
import com.jueda.ndian.activity.home.view.WebActivity;
import com.jueda.ndian.activity.home.adapter.MyCircleAdapter;
import com.jueda.ndian.activity.me.adapter.RecommendAdapter;
import com.jueda.ndian.banner.NetworkImageHolderView;
import com.jueda.ndian.activity.me.biz.BannerBiz;
import com.jueda.ndian.activity.circle.biz.JoinCircleBiz;
import com.jueda.ndian.activity.circle.biz.RecomendCircleBiz;
import com.jueda.ndian.entity.ADInfo;
import com.jueda.ndian.entity.CircleEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.view.BanSlidingListView;
import java.util.ArrayList;

/**
 * 圈子
 * Created by Administrator on 2016/3/8.
 */
public class CharityCircleFragment extends LazyFragment implements View.OnClickListener,OnItemClickListener{
    public static CharityCircleFragment instance=null;
    public static final String TAG=CharityCircleFragment.class.getName();
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private View view;
    private BanSlidingListView MyCircleListView;//我的圈子
    private BanSlidingListView recommendCircleListView;//推荐圈子
    private ArrayList<CircleEntity> RecommendList;//推荐圈子数据
    private TextView MyCircleTextView;//显示我的圈子个数
    private RecommendAdapter recommendAdapter;//推荐圈子适配器
    private MyCircleAdapter myCircleAdapter;//我的圈子适配器
    public  ArrayList<CircleEntity> MyCircleList;//我的圈子数据

    private RelativeLayout statusBar;//状态栏高度
    private TextView newTextView;//创建
    private ImageView SearchImageView;//搜索

    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示
    private ConvenientBanner ad_view;//banner图
    //banner图
    private ArrayList<ADInfo> infos;

    private ScrollView scrollView;
    private int screenWidth;//屏幕宽度

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**我的圈子获取成功*/
                case Constants.ON_SUCCEED:
                    MyCircleList= (ArrayList<CircleEntity>) msg.obj;
                    if(CharityCircleFragment.this.isAdded()) {
                        MyCircleTextView.setText(getResources().getString(R.string.My_circle) + "(" + MyCircleList.size() + ")");
                    }
                    myCircleAdapter.updata(MyCircleList);
                    //推荐圈子
                    new RecomendCircleBiz(getActivity(),handler,RecommendList);
                    break;
                /**获取失败*/
                case Constants.FAILURE_TWO:
                    animation.stopAnim();
                    if(CharityCircleFragment.this.isAdded()) {
                        NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    }
                    include.setVisibility(View.VISIBLE);
                    break;
                /**推荐圈子获取成功*/
                case Constants.ON_SUCEED_TWO:
                    animation.stopAnim();
                    scrollView.setVisibility(View.VISIBLE);
                    RecommendList= (ArrayList<CircleEntity>) msg.obj;
                    recommendAdapter.updata(RecommendList);
                    break;
                /**banner图获取成功*/
                case Constants.ON_SUCEED_THREE:
                    infos= (ArrayList<ADInfo>) msg.obj;
                    if(infos.size()>0){
                        addBanner();
                    }
                    //获取圈子
                    new JoinCircleBiz(getActivity(),handler,MyCircleList);
                    break;
                /**banner图获取失败*/
                case Constants.FAILURE_THREE:
                    animation.stopAnim();
                    if(CharityCircleFragment.this.isAdded()) {
                        NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    }
                    include.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=View.inflate(getActivity(), R.layout.fragment_charity_topic,null);
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
        InitView();
        setOnClick();
    }

    public void setData() {
        //慈善圈
        MyCircleList=new ArrayList<>();
        RecommendList=new ArrayList<>();
        //我的圈子
        myCircleAdapter=new MyCircleAdapter(getActivity(),MyCircleList, TAG);
        MyCircleListView.setAdapter(myCircleAdapter);
        //获取banner横幅
        new BannerBiz(getActivity(),handler,TAG, screenWidth);
        /**推荐圈子*/
        recommendAdapter=new RecommendAdapter(getActivity(),RecommendList,TAG);
        recommendCircleListView.setAdapter(recommendAdapter);
    }
    public void updata(){
        //慈善圈
        MyCircleList.clear();
        RecommendList.clear();
        //获取banner横幅
        new BannerBiz(getActivity(),handler,TAG, screenWidth);
    }

    private void setOnClick() {
        include.setOnClickListener(this);
        SearchImageView.setOnClickListener(this);
        newTextView.setOnClickListener(this);

    }

    private void InitView() {
        screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        if(CharityCircleFragment.this.isAdded()) {
            NdianApplication.instance.setTitle(view, getResources().getString(R.string.Charity_circle), false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            statusBar=(RelativeLayout)view.findViewById(R.id.statusBar);
            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) statusBar.getLayoutParams();//获取空间当前布局参数
            layoutParams.height=MainActivity.instance.getStatusBarHeight();
            statusBar.setLayoutParams(layoutParams);
        }
        SearchImageView=(ImageView)view.findViewById(R.id.SearchImageView);
        newTextView=(TextView)view.findViewById(R.id.newTextView);

        ad_view=(ConvenientBanner)view.findViewById(R.id.convenientBanner);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ad_view.getLayoutParams();
        layoutParams.height= (int) (screenWidth/2.4);
        ad_view.setLayoutParams(layoutParams);

        MyCircleTextView=(TextView)view.findViewById(R.id.MyCircleTextView);
        MyCircleListView=(BanSlidingListView)view.findViewById(R.id.MyCircleListView);
        recommendCircleListView=(BanSlidingListView)view.findViewById(R.id.RecommendCircleListView);
        MyCircleListView.setFocusable(false);
        recommendCircleListView.setFocusable(false);
        scrollView=(ScrollView)view.findViewById(R.id.scrollView);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);//去掉滑到尽头的系统回弹效果
        /**初始化数据时界面显示*/
        include=(View)view.findViewById(R.id.failure);
        loadingInclude=(View)view.findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(getActivity(),loadingInclude);
        Reconnection();
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            //重新加载
            case R.id.failure:
               Reconnection();
                break;
            //搜索
            case R.id.SearchImageView:
                 intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            //新建
            case R.id.newTextView:
                if (new Configuration().readaIsLogin(getActivity())) {
                    intent = new Intent(getActivity(), NewCircleActivity.class);
                    startActivity(intent);
                } else {
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }



    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        scrollView.setVisibility(View.GONE);
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            animation.startAnim();
            include.setVisibility(View.GONE);
            setData();
        }else{
            if(CharityCircleFragment.this.isAdded()) {
                NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            }
            include.setVisibility(View.VISIBLE);
        }
    }


    /**
     * banner监听
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        if(infos.get(position).getType().equals("1")){
            //视频
            Intent intent=new Intent(getActivity(), VideoActivity.class);
            intent.putExtra("url",infos.get(position).getOpen_url());
            startActivity(intent);
        }else if(infos.get(position).getType().equals("2")){
            //网页
            Intent intent=new Intent(getActivity(), WebActivity.class);
            intent.putExtra("url",infos.get(position).getOpen_url());
            intent.putExtra("title", infos.get(position).getContent());
            startActivity(intent);
        }else if(infos.get(position).getType().equals("3")){
            //商品详情
            Intent intent=new Intent(getActivity(), CommodityDetailsActivity.class);
            intent.putExtra("id", infos.get(position).getOpen_url());
            startActivity(intent);
        }else if(infos.get(position).getType().equals("4")){
            //话题详情
            Intent intent = new Intent(getActivity(), TopicDetailsActivity.class);
            intent.putExtra("who", HomeFragment.TAG);
            intent.putExtra("id", infos.get(position).getOpen_url());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /***
     * 添加banner
     */
    private void addBanner() {
        ad_view.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        },infos) //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.kuang_corners_white_five, R.drawable.kuang_corners_red_dian_five})
                        //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
//                .setOnPageChangeListener(this)//监听翻页事件
                .setOnItemClickListener(this);

        ad_view.getViewPager().setPageTransformer(true, new AccordionTransformer());
        ad_view.setScrollDuration(1500);
        ad_view.startTurning(4000);
    }

    @Override
    public void onResume() {
        if(infos!=null){
            ad_view.startTurning(4000);
        }
        super.onResume();

    }

    @Override
    public void onDestroy() {
        instance=null;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if(infos!=null){
            ad_view.stopTurning();
        }
        super.onPause();
    }
}
