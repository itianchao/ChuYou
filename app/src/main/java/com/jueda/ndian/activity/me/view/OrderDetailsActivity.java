package com.jueda.ndian.activity.me.view;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.view.TaskExchangeDetailsActivity;
import com.jueda.ndian.activity.home.view.TaskShareDetailsActivity;
import com.jueda.ndian.activity.me.biz.MyExchangeOrderConfirmBiz;
import com.jueda.ndian.activity.me.biz.MyExchangeOrderDetailsBiz;
import com.jueda.ndian.activity.me.biz.MyBuyersOrderDetailsBiz;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;

/***
 * 订单详情
 */
public class OrderDetailsActivity extends AppCompatActivity {
    private TextView CheckTextView;//查看物流信息
    private ScrollView scrollView;//没获取到则隐藏
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示

    private TextView orderNumberTextView;//订单号
    private TextView consigneeTextView;//收货人
    private TextView phoneNumber;//手机号
    private TextView address;//详细地址
    private Button Receipt;//确认收货
    private PopupWindow popPrompt;
    private View layoutPrompt;
    //买家订单
    private RelativeLayout relativeLayout;//跳转到分享商品详情
    private ImageView view;//图片
    private TextView TitleTextView;//标题
    private TextView priceTextView;//商品金额
    private TextView oldPricTextView;//原价
    private TextView freight;//运费
    private TextView total_fee;//总金额

    //兑换商品
    private RelativeLayout exchangeRelativeLayout;//跳转到兑换商品详情
    private ImageView exchangeView;//图片
    private TextView exchangeTitleTextView;//标题
    private TextView bean;//爱心豆
    private TextView exchangeOldbean;//原价
    private TextView exchangeFreight;//运费

    private CommodityEntity entity;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**获取数据成功*/
                case Constants.ON_SUCCEED:
                    animation.stopAnim();
                    Receipt.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    entity= (CommodityEntity) msg.obj;
                    setData();
                    break;
                /**获取数据失败*/
                case Constants.FAILURE:
                    animation.stopAnim();
                    NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    include.setVisibility(View.VISIBLE);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    animation.stopAnim();
                    Receipt.setVisibility(View.GONE);
                    scrollView.setVisibility(View.GONE);
                    NdianApplication.connectionFail(getResources().getString(R.string.For_failure), include);
                    include.setVisibility(View.VISIBLE);
                    new Configuration().writeaIsLogin(OrderDetailsActivity.this,false);
                    Intent intent = new Intent(OrderDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
                //订单收货确认成功
                case Constants.ON_SUCEED_TWO:
                    Receipt.setEnabled(false);
                    Receipt.setBackgroundColor(getResources().getColor(R.color.button_gray));
                    Receipt.setText("已完成");
                    break;
                //订单确认失败
                case Constants.FAILURE_TWO:
                    new ToastShow(OrderDetailsActivity.this,getResources().getString(R.string.Confirmation_failed),1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_order_details);
        InitView();
        setOnClick();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.The_order_details), true);
        entity= (CommodityEntity) getIntent().getSerializableExtra("entity");
        bean=(TextView)findViewById(R.id.bean);
        exchangeFreight=(TextView)findViewById(R.id.exchangeFreight);
        exchangeOldbean=(TextView)findViewById(R.id.exchangeOldbean);
        exchangeRelativeLayout=(RelativeLayout)findViewById(R.id.exchangeRelativeLayout);
        exchangeTitleTextView=(TextView)findViewById(R.id.exchangeTitleTextView);
        exchangeView=(ImageView)findViewById(R.id.exchangeView);
        CheckTextView=(TextView)findViewById(R.id.CheckTextView);
        scrollView=(ScrollView)findViewById(R.id.scrollView);
        relativeLayout=(RelativeLayout)findViewById(R.id.BuyersRelativeLayout);
        oldPricTextView=(TextView)findViewById(R.id.oldPricTextView);
        orderNumberTextView=(TextView)findViewById(R.id.orderNumberTextView);
        consigneeTextView=(TextView)findViewById(R.id.consigneeTextView);
        phoneNumber=(TextView)findViewById(R.id.phoneNumber);
        address=(TextView)findViewById(R.id.address);
        view=(ImageView)findViewById(R.id.view);
        TitleTextView=(TextView)findViewById(R.id.TitleTextView);
        priceTextView=(TextView)findViewById(R.id.priceTextView);
        freight=(TextView)findViewById(R.id.freight);
        total_fee=(TextView)findViewById(R.id.total_fee);
        Receipt=(Button)findViewById(R.id.Receipt);
        /**初始化数据时界面显示*/
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(OrderDetailsActivity.this,loadingInclude);
        Reconnection();

    }
    private CommodityEntity getdata(){
        if(entity==null)
            entity= (CommodityEntity) getIntent().getSerializableExtra("entity");
        return entity;
    }

    /**
     * 1表示兑换商品 2是买家订单
     * @return
     */
    private int getWho(){
        String who=getIntent().getStringExtra("who");
        if(who!=null){
            if(who.equals(MyExchangeGoodsActivity.TAG)){
                return 1;
            }else{
                return 2;
            }
        }
        return 0;
    }

    private void setOnClick() {
        /**确认收货*/
        Receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                are_you_sure(Receipt);

            }
        });
        /**跳转到分享商品详情*/
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailsActivity.this, TaskShareDetailsActivity.class);
                intent.putExtra("entity",getdata());
                startActivity(intent);

            }
        });
        /**跳转到兑换商品详情*/
        exchangeRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailsActivity.this, TaskExchangeDetailsActivity.class);
                intent.putExtra("id",getdata().getGid());
                startActivity(intent);
            }
        });
        /**查看物流*/
        CheckTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(OrderDetailsActivity.this, LogisticsActivity.class);
                    intent.putExtra("who",getIntent().getStringExtra("who"));
                    intent.putExtra("entity",getdata());
                    startActivity(intent);

            }
        });
        /**重新加载*/
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reconnection();
            }
        });
    }
    private void setData() {
        orderNumberTextView.setText(entity.getOrder_no());
        consigneeTextView.setText(entity.getConsignee());
        phoneNumber.setText(entity.getPhone());
        address.setText(entity.getAddress());
        if(entity.getGoodsState().equals("3")||entity.getGoodsState().equals("4")||entity.getGoodsState().equals("1")){
            //收货
            Receipt.setEnabled(false);
            Receipt.setBackgroundColor(getResources().getColor(R.color.button_gray));
            Receipt.setText("已完成");
        }
        if(getWho()==1){
            relativeLayout.setVisibility(View.GONE);
            ImageLoaderUtil.ImageLoader(entity.getImg(), exchangeView);
            exchangeTitleTextView.setText(entity.getTitle());
            bean.setText(entity.getBead() + "爱心豆");
            new ChangeText(bean.getText().toString(),getResources().getColor(R.color.text_love),bean, 0, bean.getText().toString().length()-3);
            exchangeOldbean.setText("￥"+entity.getOld_price());
            exchangeFreight.setText("免运费");
        }else{
            exchangeRelativeLayout.setVisibility(View.GONE);
            ImageLoaderUtil.ImageLoader(entity.getImg(), view);
            TitleTextView.setText(entity.getTitle());
            oldPricTextView.setText("￥"+entity.getOld_price());
            priceTextView.setText("￥"+entity.getPrice());
            total_fee.setText("￥"+entity.getTotal_fee());
            if(entity.getFreightage().equals("0.00")){
                freight.setText("免运费");
            }else{
                freight.setText("￥"+entity.getFreightage());
            }
        }
    }

    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        scrollView.setVisibility(View.GONE);
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            include.setVisibility(View.GONE);
            animation.startAnim();
            if(getWho()==1){
                new MyExchangeOrderDetailsBiz(OrderDetailsActivity.this,handler,entity);
            }else{
                new MyBuyersOrderDetailsBiz(OrderDetailsActivity.this,handler,entity);
            }

        }else{
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }
    /**
     * 确认收货
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
            contents.setText("亲，确定收到了货吗？");

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
                    if(getWho()==1){
                        new MyExchangeOrderConfirmBiz(OrderDetailsActivity.this,handler,entity.getOrder_id(), Constants.MY_EXCHANGE_ORDER_CONFIRM);
                    }else{
                        new MyExchangeOrderConfirmBiz(OrderDetailsActivity.this,handler,entity.getOrder_id(),Constants.MY_BUYERS_ORDER_CONFIRM);
                    }
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
