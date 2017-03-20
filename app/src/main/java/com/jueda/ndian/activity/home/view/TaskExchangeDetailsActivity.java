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
import android.widget.ScrollView;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.home.biz.TaskExchangeDetailsBiz;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.Image_look_preview;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;

/**
 * 兑换商品详情
 */
public class TaskExchangeDetailsActivity extends AppCompatActivity {
    private ScrollView scrollView;// 没有数据时隐藏
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示

    private CommodityEntity entity;
    private TextView titles;//标题
    private TextView bean;//爱心豆
    private TextView money;//原价
    private Button submit;//立即兑换

    private LinearLayout linearLayout;//图片显示

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //获取数据成功
                case Constants.ON_SUCCEED:
                    entity= (CommodityEntity) msg.obj;
                    animation.stopAnim();
                    submit.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    setData();
                    break;
                //获取数据失败
                case Constants.FAILURE:
                    scrollView.setVisibility(View.GONE);
                    submit.setVisibility(View.GONE);
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
        setContentView(R.layout.activity_task_exchange_details);
        InitView();
        setOnClick();
    }



    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.For_the_details), true);
        scrollView=(ScrollView)findViewById(R.id.scrollView);
        titles=(TextView)findViewById(R.id.titles);
        bean=(TextView)findViewById(R.id.bean);
        money=(TextView)findViewById(R.id.money);
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout);
        submit=(Button)findViewById(R.id.submit);
        /**初始化数据时界面显示*/
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);
        Reconnection();

    }

    /**
     * 添加数据
     */
    private void setData() {
        titles.setText(entity.getTitle());
        bean.setText(entity.getBead()+"爱心豆");
        new ChangeText(bean.getText().toString(),getResources().getColor(R.color.text_orange),bean,0,bean.getText().toString().length()-3);
        money.setText("￥"+entity.getOld_price());
        String img[] = entity.getImgs();
        for (int i = 0; i < img.length; i++) {
            LinearLayout.LayoutParams imgvwDimens =
                    new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            imgvwDimens.height = entity.getImgs_height()[i];
            ImageView im = new ImageView(TaskExchangeDetailsActivity.this);
            //图片充满界面
            im.setLayoutParams(imgvwDimens);
            im.setScaleType(ImageView.ScaleType.FIT_XY);
//            im.setPadding(0, 0, 0, Constants.dip2px(TaskExchangeDetailsActivity.this, 15));
            im.setCropToPadding(true);
            ImageLoaderUtil.ImageLoader(img[i] + Constants.QINIU1 + (int) MainActivity.instance.getScreenWidth(), im);
            linearLayout.addView(im);
            final int finalI = i;
            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Image_look_preview.look(TaskExchangeDetailsActivity.this, finalI, entity.getPhotoArrayList());
                }
            });

        }
    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            scrollView.setVisibility(View.GONE);
            include.setVisibility(View.GONE);
            animation.startAnim();
            //TODO 数据获取
            new TaskExchangeDetailsBiz(TaskExchangeDetailsActivity.this,handler,getId());
        }else{
            new ToastShow(TaskExchangeDetailsActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClick() {
        /**重连*/
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reconnection();
            }
        });
        //立即兑换
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Configuration().readaIsLogin(TaskExchangeDetailsActivity.this)){
                    Intent intent=new Intent(TaskExchangeDetailsActivity.this,TaskExchangeDetailsSubmitActivity.class);
                    intent.putExtra("entity",entity);
                    startActivity(intent);
                }else{
                    startActivity(new Intent(TaskExchangeDetailsActivity.this, LoginActivity.class));
                }

            }
        });
    }
    private String getId(){
       String id= getIntent().getStringExtra("id");
        return id;
    }
}
