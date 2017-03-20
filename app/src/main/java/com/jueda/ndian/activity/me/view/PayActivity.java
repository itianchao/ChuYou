package com.jueda.ndian.activity.me.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.alipay.PayDemoActivity;
import com.jueda.ndian.activity.me.biz.RechargeBiz;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.wxapi.WXpay;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 公告充值界面
 */
public class PayActivity extends AppCompatActivity {
    public static PayActivity instance=null;
    private RelativeLayout WXRelativeLayout;//微信支付
    private RadioButton radioButton1;//微信支付
    private RelativeLayout ZFBRelativeLayout;//支付宝支付
    private RadioButton radioButton2;//支付宝支付
    private int WhoPay=1;//1微信  2支付宝

    private TextView money;//需支付金额
    private TextView submitOrders;//提交订单
    private WaitDialog waitDialog;
    private String moneys="";//充值金额

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //获取订单信息成功
                case Constants.ON_SUCCEED:
                    HashMap<String,String> map= (HashMap<String, String>) msg.obj;
                    String OutTradeNo=map.get("order_id");
                    if(WhoPay==2) {
                        //支付宝
                        new PayDemoActivity().pay(PayActivity.this,"绝搭出游",map.get("total_fee"),"",Constants.ZFB_PAY_URL,OutTradeNo,handler);
                    }else if(WhoPay==1) {
                        //微信
                        new WXpay(PayActivity.this,OutTradeNo,map.get("total_fee"),handler,Constants.WX_RECHARGE);
                    }
                    break;
                //支付宝下订单失败
                case Constants.FAILURE:
                    if(waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    new ToastShow(PayActivity.this,getResources().getString(R.string.Submit_orders_failure),1000);
                    break;
                /**微信下订单成功*/
                case Constants.WX_ORDER_SUCCEED:
                    HashMap<String,String> maps= (HashMap<String, String>) msg.obj;
                    PayReq request = new PayReq();
                    request.appId = maps.get("appId");
                    request.partnerId = maps.get("partnerId");
                    request.prepayId = maps.get("prepayId");
                    request.packageValue = "Sign=WXPay";
                    request.nonceStr = maps.get("nonceStr");
                    request.timeStamp = maps.get("timeStamp");
                    request.sign = maps.get("sign");
                    NdianApplication.instance.api.sendReq(request);
                    break;
                /**微信下订单失败*/
                case Constants.WX_ORDER_FAILURE:
                    if(waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    new ToastShow(PayActivity.this,getResources().getString(R.string.Submit_orders_failure),1000);
                    break;
                /**微信支付结果*/
                case Constants.WX_PAY_SUCCEED:
                    String data= (String) msg.obj;
                    if(data.equals("1")){
                        //支付成功
                        new ToastShow(PayActivity.this,"支付成功",1000);
                        PersonalFragment.instance.updata();
                    }else if(data.equals("-1")){
                        //支付失败
                        new ToastShow(PayActivity.this,"支付失败",1000);
                    }else if(data.equals("-2")){
                        //支付取消
                        new ToastShow(PayActivity.this,"支付取消",1000);
                    }
                    if(waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    break;
                /**支付成功*/
                case Constants.ZFB_PAY_SUCCEED:
                    if(waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    new ToastShow(PayActivity.this,"支付成功",1000);
                    PersonalFragment.instance.updata();
                    break;
                /**支付失败*/
                case Constants.ZFB_PAY_FAILURE:
                    if(waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    new ToastShow(PayActivity.this,"支付成功",1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    if(waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(PayActivity.this, false);
                    Intent intent = new Intent(PayActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        instance=this;
        setContentView(R.layout.activity_pay);
        InitView();
        setOnClick();
    }

    @Override
    protected void onDestroy() {
        instance=null;
        super.onDestroy();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.pay), true);
        money=(TextView)findViewById(R.id.money);
        money.setText("￥"+getdata());
        submitOrders=(TextView)findViewById(R.id.submitOrders);
        WXRelativeLayout=(RelativeLayout)findViewById(R.id.WXRelativeLayout);
        radioButton1=(RadioButton)findViewById(R.id.radioButton1);
        ZFBRelativeLayout=(RelativeLayout)findViewById(R.id.ZFBRelativeLayout);
        radioButton2=(RadioButton)findViewById(R.id.radioButton2);
    }
    private String getdata(){
        if (moneys.equals("")) moneys=getIntent().getStringExtra("money");
        return moneys;
    }
    private void setOnClick() {
        //提交订单支付
        submitOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Configuration().readaIsLogin(PayActivity.this)) {
                    //支付宝
                    if (WhoPay == 2) {
                        if (isAvilible(PayActivity.this, "com.eg.android.AlipayGphone")) {
                            new RechargeBiz(PayActivity.this, handler, getdata(), true);
                        } else {
                            new ToastShow(PayActivity.this, getResources().getString(R.string.Please_install_the_pay_treasure_to_the_client), 1000);
                        }
                    } else if (WhoPay == 1) {
                        //微信
                        if (isAvilible(PayActivity.this, "com.tencent.mm")) {
                            waitDialog = new WaitDialog(PayActivity.this);
                            NdianApplication.instance.api = WXAPIFactory.createWXAPI(PayActivity.this, Constants.WX_APP_ID);
                            new RechargeBiz(PayActivity.this, handler, getdata(), false);
                        } else {
                            new ToastShow(PayActivity.this, getResources().getString(R.string.Please_install_WeChat_client_firsts), 1000);
                        }
                    }
                }else{
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(PayActivity.this, false);
                    Intent intent = new Intent(PayActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        //微信支付
        WXRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton1.setChecked(true);
                radioButton2.setChecked(false);
                WhoPay=1;
            }
        });
        //支付宝支付
        ZFBRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton1.setChecked(false);
                radioButton2.setChecked(true);
                WhoPay=2;
            }
        });
    }
    /**
     * 判断是否安装
     * @param context
     * @param packageName
     * @return
     */
    private boolean isAvilible(Context context, String packageName){
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if(pinfo != null){
            for(int i = 0; i < pinfo.size(); i++){
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }

}
