package com.jueda.ndian.activity.home.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.home.biz.CommodityDetailsBiz;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;

import java.util.ArrayList;

/***
 * 商品详情
 */
public class CommodityDetailsActivity extends AppCompatActivity {
    private RelativeLayout concealRelativeLayout;//没有网络或者连接失败时隐藏
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示
    private LinearLayout linearLayout;//装载图片的绝对布局
    private ArrayList<CommodityEntity> list;
    private Button submitOrders;//提交订单
    private String id;//商品id
    private double screenWidth;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**请求数据成功*/
                case Constants.ON_SUCCEED:
                    animation.stopAnim();
                    concealRelativeLayout.setVisibility(View.VISIBLE);
                    list= (ArrayList<CommodityEntity>) msg.obj;
                    setData();
                    break;
                /**请求数据失败或者没有数据时*/
                case Constants.FAILURE:
                    concealRelativeLayout.setVisibility(View.GONE);
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
        setContentView(R.layout.activity_commodity_details);
        InitView();
        setOnClick();
    }

    private void InitView() {
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        list=new ArrayList<>();
        id=getIntent().getStringExtra("id");
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Goods_details), true);
        /**初始化数据时界面显示*/
        submitOrders=(Button)findViewById(R.id.submitOrders);
        concealRelativeLayout=(RelativeLayout)findViewById(R.id.concealRelativeLayout);
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout);
        Reconnection();
    }

    private void setData() {
        /**设置图片*/
        String img[]=list.get(0).getImgs();
        for(int i=0;i<img.length;i++){
            LinearLayout.LayoutParams imgvwDimens =
                    new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            int s =list.get(0).getImgs_height()[i];
            imgvwDimens.height=list.get(0).getImgs_height()[i];
            ImageView im=new ImageView(CommodityDetailsActivity.this);
            //图片充满界面
            im.setLayoutParams(imgvwDimens);
            im.setScaleType(ImageView.ScaleType.FIT_XY);
            im.setPadding(0, 0, 0, 0);
            im.setCropToPadding(true);
            ImageLoaderUtil.ImageLoader(img[i], im);
            linearLayout.addView(im);
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
        /**提交订单*/
        submitOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Configuration().readaIsLogin(CommodityDetailsActivity.this)) {
                    Intent intent = new Intent(CommodityDetailsActivity.this, CommoditySubmitActivity.class);
                    intent.putExtra("entity", list.get(0));
                    startActivity(intent);
                }else{
                    //跳转到登录界面
                    Intent intent = new Intent(CommodityDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        concealRelativeLayout.setVisibility(View.GONE);
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            include.setVisibility(View.GONE);
            //设置获取数据接口
            animation.startAnim();
            new CommodityDetailsBiz(CommodityDetailsActivity.this,id,handler,list,screenWidth);
        }else{
            new ToastShow(CommodityDetailsActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }

}
