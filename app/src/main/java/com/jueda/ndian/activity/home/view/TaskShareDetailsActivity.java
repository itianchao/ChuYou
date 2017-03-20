package com.jueda.ndian.activity.home.view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.me.view.MyReleaseGoodsActivity;
import com.jueda.ndian.activity.me.biz.MyRelesaseDelectBiz;
import com.jueda.ndian.activity.home.biz.TaskShareDetailsBiz;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.Share;
import com.jueda.ndian.utils.ToastShow;

/**
 * 分享商品详情
 */
public class TaskShareDetailsActivity extends AppCompatActivity {
    public static final String TAG=TaskShareDetailsActivity.class.getName();
    private ScrollView scrollView;// 没有数据时隐藏
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示

    private CommodityEntity entity;
    private TextView titles;//标题
    private TextView bean;//爱心豆
    private TextView freight;//运费
    private TextView oldermonet;//原价
    private TextView money;//现价
    private TextView content;//内容
    private LinearLayout linearLayout;//图片显示
    private LinearLayout buttonLinearLayout;//点击按钮显示
    private Button buy;//购买
    private Button share;//分享
    private TextView delect;//删除商品
    private PopupWindow popPrompt;
    private View layoutPrompt;
    private RelativeLayout tanchuangRelativeLayout;//相对位置

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //获取数据成功
                case Constants.ON_SUCCEED:
                    entity= (CommodityEntity) msg.obj;
                    animation.stopAnim();
                    scrollView.setVisibility(View.VISIBLE);
                    setData();
                    break;
                //获取数据失败
                case Constants.FAILURE:
                    scrollView.setVisibility(View.GONE);
                    buttonLinearLayout.setVisibility(View.GONE);
                    animation.stopAnim();
                    NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    include.setVisibility(View.VISIBLE);
                    break;
                //删除成功
                case Constants.ON_SUCEED_TWO:
                    new ToastShow(TaskShareDetailsActivity.this,getResources().getString(R.string.delect_success),1000);
                    TaskShareDetailsActivity.this.finish();
                    break;
                //删除失败
                case Constants.FAILURE_TWO:
                    new ToastShow(TaskShareDetailsActivity.this,getResources().getString(R.string.delect_failure),1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_task_share_details);
        InitView();
        setOnClick();
    }

    private void InitView() {
        if(getWho()==1){
            NdianApplication.instance.setTitle(this, getResources().getString(R.string.Commodity_management), true);
        }else{
            NdianApplication.instance.setTitle(this, getResources().getString(R.string.Share_the_goods), true);
        }

        scrollView=(ScrollView)findViewById(R.id.scrollView);
        delect=(TextView)findViewById(R.id.delect);
        buy=(Button)findViewById(R.id.buy);
        share=(Button)findViewById(R.id.share);
        buttonLinearLayout=(LinearLayout)findViewById(R.id.buttonLinearLayout);
        titles=(TextView)findViewById(R.id.titles);
        oldermonet=(TextView)findViewById(R.id.oldermoney);
        bean=(TextView)findViewById(R.id.bean);
        money=(TextView)findViewById(R.id.money);
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout);
        content=(TextView)findViewById(R.id.content);
        freight=(TextView)findViewById(R.id.freight);
        tanchuangRelativeLayout=(RelativeLayout)findViewById(R.id.tanchuangRelativeLayout);
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
            scrollView.setVisibility(View.GONE);
            include.setVisibility(View.GONE);
            animation.startAnim();
            new TaskShareDetailsBiz(TaskShareDetailsActivity.this, handler,getData());
        }else{
            new ToastShow(TaskShareDetailsActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 添加数据
     */
    private void setData() {
        //我的商品和其余商品区别
        if(getWho()==1){
            delect.setVisibility(View.VISIBLE);
            buttonLinearLayout.setVisibility(View.GONE);
        }else{
            delect.setVisibility(View.GONE);
            buttonLinearLayout.setVisibility(View.VISIBLE);
        }

        //设置内容
        if(entity.getOfficial_or_personal().equals("1")){
            //个人
            oldermonet.setVisibility(View.GONE);
            bean.setVisibility(View.GONE);
            titles.setText(entity.getTitle() + "【个人】");
        }else {
            oldermonet.setText("￥"+entity.getOld_price());
            bean.setText("佣金："+entity.getBead()+"爱心豆");
            new ChangeText(bean.getText().toString(),getResources().getColor(R.color.text_love),bean,3,bean.getText().toString().length()-3);
            titles.setText(entity.getTitle()+"【自营】");
        }
        new ChangeText(titles.getText().toString(),getResources().getColor(R.color.text_off_per),titles,titles.getText().toString().length()-4,titles.getText().toString().length());
        if(entity.getFreightage().equals("0.00")){
            freight.setText("运费：包邮");
        }else{
            freight.setText("运费：￥"+entity.getFreightage());
        }
        new ChangeText(freight.getText().toString(),getResources().getColor(R.color.text_red),freight,3,freight.getText().toString().length());
        content.setText(entity.getContent());

        money.setText("价格：￥" + entity.getPrice());
        new ChangeText(money.getText().toString(),getResources().getColor(R.color.text_red),money,3,money.getText().toString().length());

        String img[] = entity.getImgs();
        for (int i = 0; i < img.length; i++) {
            LinearLayout.LayoutParams imgvwDimens =
                    new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            imgvwDimens.height = entity.getImgs_height()[i];
            ImageView im = new ImageView(TaskShareDetailsActivity.this);
            //图片充满界面
            im.setLayoutParams(imgvwDimens);
            im.setScaleType(ImageView.ScaleType.FIT_XY);
//            im.setPadding(0, 0, 0, Constants.dip2px(TaskShareDetailsActivity.this, 15));
            im.setCropToPadding(true);
            ImageLoaderUtil.ImageLoader(img[i] + Constants.QINIU1 + (int) MainActivity.instance.getScreenWidth(), im);
            linearLayout.addView(im);
        }
    }
    private void setOnClick() {
        /**删除*/
        delect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Configuration().readaIsLogin(TaskShareDetailsActivity.this)) {
                    are_you_sure(delect);
                }else{
                    startActivity(new Intent(TaskShareDetailsActivity.this, LoginActivity.class));
                }

            }
        });
        /**分享*/
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Configuration().readaIsLogin(TaskShareDetailsActivity.this)){
                    new Share(TaskShareDetailsActivity.this, tanchuangRelativeLayout, TaskShareDetailsActivity.TAG,getData().getId(),getData().getImg()+Constants.QINIU1+Constants.dip2px(TaskShareDetailsActivity.this,100),"",getData().getTitle());
                }else{
                    startActivity(new Intent(TaskShareDetailsActivity.this, LoginActivity.class));
                }

            }
        });
        /**购买*/
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Configuration().readaIsLogin(TaskShareDetailsActivity.this)) {
                    Intent intent = new Intent(TaskShareDetailsActivity.this, CommoditySubmitActivity.class);
                    intent.putExtra("entity", entity);
                    startActivity(intent);
                }else{
                    startActivity(new Intent(TaskShareDetailsActivity.this, LoginActivity.class));
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

    }
    /**
     * 判断是1我进入的还是2其余的
     * @return
     */
    private int getWho(){
        String who=getIntent().getStringExtra("who");
        if(who.equals(MyReleaseGoodsActivity.TAG)){
            return 1;
        }else{
            return 2;
        }
    }
    private CommodityEntity getData(){
        if(entity==null)entity= (CommodityEntity) getIntent().getSerializableExtra("entity");
        return entity;
    }

    /**
     * 是否兑换
     */
    public void are_you_sure(final View view){
        if (popPrompt != null && popPrompt.isShowing()) {
            popPrompt.dismiss();
        } else {
            layoutPrompt =getLayoutInflater().inflate(
                    R.layout.pop_ok_cancel_content_general, null);
            TextView know=(TextView)layoutPrompt.findViewById(R.id.complete);
            TextView contents=(TextView)layoutPrompt.findViewById(R.id.content);
            TextView center=(TextView)layoutPrompt.findViewById(R.id.center);
            contents.setText("确定删除商品吗？");

            center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPrompt.dismiss();
                }
            });
            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPrompt.dismiss();
                    new MyRelesaseDelectBiz(TaskShareDetailsActivity.this, handler, entity);
                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popPrompt = new PopupWindow(layoutPrompt, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popPrompt.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
            popPrompt.update();
            popPrompt.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popPrompt.setTouchable(true); // 设置popupwindow可点击
            popPrompt.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popPrompt.setFocusable(true); // 获取焦点
            // 设置popupwindow的位置（相对tvLeft的位置）
            popPrompt.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            popPrompt.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popPrompt.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

}
