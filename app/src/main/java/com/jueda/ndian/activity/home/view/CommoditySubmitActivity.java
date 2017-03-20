package com.jueda.ndian.activity.home.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.AddDetailsActivity;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.alipay.PayDemoActivity;
import com.jueda.ndian.activity.home.biz.CommoditySubmitBiz;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.wxapi.WXpay;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * 提交订单
 */
public class CommoditySubmitActivity extends AppCompatActivity {

    public static CommoditySubmitActivity instance=null;
    private RelativeLayout WXRelativeLayout;//微信支付
    private RadioButton radioButton1;//微信支付
    private RelativeLayout ZFBRelativeLayout;//支付宝支付
    private RadioButton radioButton2;//支付宝支付
    private int WhoPay=1;//1微信  2支付宝

    private ImageView view;//图片
    private TextView TitleTextView;//标题
    private TextView moneyTextView;//商品金额
    private TextView freightageTextView;//运费
    private TextView oldPricTextView;//原价
    private TextView RealPaymentTextView;//真实付款

    private RelativeLayout go_edit;//去编辑收货地址
    private RelativeLayout go_edit_relativeLatout;//去编辑收货地址
    private TextView name;//收货人
    private TextView phoneNumber;//收货人联系方式
    private TextView address;//联系地址

    private TextView submitOrders;//提交订单
    private CommodityEntity entity;
    private String OutTradeNo;//订单编号
    private WaitDialog waitDialog;


    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //获取订单信息成功
                case Constants.ON_SUCCEED:
                    HashMap<String,String> map= (HashMap<String, String>) msg.obj;
                    OutTradeNo=map.get("order_id");
                    if(WhoPay==2) {
                    //支付宝
                        new PayDemoActivity().pay(CommoditySubmitActivity.this,"绝搭出游",map.get("total_fee"),"",Constants.ZFB_ASY_COMMODITY_URL,OutTradeNo,handler);
                    }else if(WhoPay==1) {
                    //微信
                        new WXpay(CommoditySubmitActivity.this,OutTradeNo,map.get("total_fee"),handler,Constants.WX_COMMODITY);
                    }
                    break;
                //支付宝下订单失败
                case Constants.FAILURE:
                    if(waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    new ToastShow(CommoditySubmitActivity.this,getResources().getString(R.string.Submit_orders_failure),1000);
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
                    new ToastShow(CommoditySubmitActivity.this,getResources().getString(R.string.Submit_orders_failure),1000);
                    break;
                /**微信支付结果*/
                case Constants.WX_PAY_SUCCEED:
                    String data= (String) msg.obj;
                    if(data.equals("1")){
                        //支付成功
                        new ToastShow(CommoditySubmitActivity.this,"支付成功",1000);
                    }else if(data.equals("-1")){
                        //支付失败
                        new ToastShow(CommoditySubmitActivity.this,"支付失败",1000);
                    }else if(data.equals("-2")){
                        //支付取消
                        new ToastShow(CommoditySubmitActivity.this,"支付取消",1000);
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
                    new ToastShow(CommoditySubmitActivity.this,"支付成功",1000);
                    break;
                /**支付失败*/
                case Constants.ZFB_PAY_FAILURE:
                    if(waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    new ToastShow(CommoditySubmitActivity.this,"支付成功",1000);
                    break;
                //跳转到登录界面
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(CommoditySubmitActivity.this, false);
                    Intent intent = new Intent(CommoditySubmitActivity.this, LoginActivity.class);
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
        setContentView(R.layout.activity_commodity_submit);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        InitView();
        setOnClick();
    }


    private void InitView() {

        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Submit_orders), true);
        view=(ImageView)findViewById(R.id.view);
        go_edit=(RelativeLayout)findViewById(R.id.go_edit);
        name=(TextView)findViewById(R.id.name);
        phoneNumber=(TextView)findViewById(R.id.phoneNumber);
        address=(TextView)findViewById(R.id.address);

        go_edit_relativeLatout=(RelativeLayout)findViewById(R.id.go_edit_relativeLatout);
        submitOrders=(TextView)findViewById(R.id.submitOrders);
        TitleTextView=(TextView)findViewById(R.id.TitleTextView);
        moneyTextView=(TextView)findViewById(R.id.moneyTextView);
        freightageTextView=(TextView)findViewById(R.id.freightageTextView);
        RealPaymentTextView=(TextView)findViewById(R.id.RealPaymentTextView);
        WXRelativeLayout=(RelativeLayout)findViewById(R.id.WXRelativeLayout);
        radioButton1=(RadioButton)findViewById(R.id.radioButton1);
        ZFBRelativeLayout=(RelativeLayout)findViewById(R.id.ZFBRelativeLayout);
        radioButton2=(RadioButton)findViewById(R.id.radioButton2);
        oldPricTextView=(TextView)findViewById(R.id.oldPricTextView);

        ImageLoaderUtil.ImageLoader(getdata().getImg()+Constants.QINIU1+Constants.dip2px(CommoditySubmitActivity.this,90), view);
        TitleTextView.setText(getdata().getTitle());
        moneyTextView.setText("￥"+getdata().getPrice());
        if(getdata().getOld_price().equals("0.00")){
            oldPricTextView.setVisibility(View.GONE);
        }else{
            oldPricTextView.setText("￥"+getdata().getOld_price());
        }
        if(getdata().getFreightage().equals("0.00")){
            freightageTextView.setText("免运费");

        }else{
            freightageTextView.setText("￥"+getdata().getFreightage());
        }

        RealPaymentTextView.setText("实付款：￥"+(Double.parseDouble(getdata().getPrice())+Double.parseDouble(getdata().getFreightage())));
    }

    @Override
    protected void onResume() {
        if(ConstantsUser.userEntity.getAdd_location().equals("")||ConstantsUser.userEntity.getAdd_uname().equals("")||ConstantsUser.userEntity.getAdd_phoneNumber().equals("")){
            go_edit.setVisibility(View.VISIBLE);
            go_edit_relativeLatout.setVisibility(View.GONE);
        }else{
            go_edit.setVisibility(View.GONE);
            go_edit_relativeLatout.setVisibility(View.VISIBLE);
            address.setText(ConstantsUser.userEntity.getAdd_location());
            phoneNumber.setText(ConstantsUser.userEntity.getAdd_phoneNumber());
            name.setText(ConstantsUser.userEntity.getAdd_uname());
        }
        super.onResume();
    }

    private CommodityEntity getdata(){
        if(entity==null)   entity= (CommodityEntity) getIntent().getSerializableExtra("entity");
        return entity;
    }

    private void setOnClick() {
        //提交订单支付
        submitOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Configuration().readaIsLogin(CommoditySubmitActivity.this)){
                    if(!go_edit_relativeLatout.isShown()){
                        new ToastShow(CommoditySubmitActivity.this,"请先完善收货地址哦",1000);
                    }else{
                        //支付宝
                        if(WhoPay==2) {
                            if(isAvilible(CommoditySubmitActivity.this,"com.eg.android.AlipayGphone")) {
                                new CommoditySubmitBiz(CommoditySubmitActivity.this, handler, getdata().getId(),true);
                            }else{
                                new ToastShow(CommoditySubmitActivity.this,getResources().getString(R.string.Please_install_the_pay_treasure_to_the_client),1000);
                            }
                        }else if(WhoPay==1) {
                            //微信
                            if (isAvilible(CommoditySubmitActivity.this, "com.tencent.mm")) {
                                waitDialog=new WaitDialog(CommoditySubmitActivity.this);
                                NdianApplication.instance.api = WXAPIFactory.createWXAPI(CommoditySubmitActivity.this, Constants.WX_APP_ID);
                                new CommoditySubmitBiz(CommoditySubmitActivity.this, handler, getdata().getId(),false);

                            }else{
                                new ToastShow(CommoditySubmitActivity.this,getResources().getString(R.string.Please_install_WeChat_client_firsts),1000);
                            }
                        }
                    }
                }else{
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(CommoditySubmitActivity.this, false);
                    Intent intent = new Intent(CommoditySubmitActivity.this, LoginActivity.class);
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
        //去编辑
        go_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CommoditySubmitActivity.this,AddDetailsActivity.class);
                startActivity(intent);
            }
        });
        go_edit_relativeLatout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CommoditySubmitActivity.this,AddDetailsActivity.class);
                startActivity(intent);
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
