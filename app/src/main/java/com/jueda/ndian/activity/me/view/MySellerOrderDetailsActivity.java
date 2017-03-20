package com.jueda.ndian.activity.me.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ChangeTitle;

/**
 * 卖家订单详情
 */
public class MySellerOrderDetailsActivity extends AppCompatActivity {
    private TextView orderNumberTextView;//订单号
    private TextView consigneeTextView;//收货人
    private TextView phoneNumber;//手机号
    private TextView address;//详细地址
    private TextView title;//标题
    private TextView price;//价格
    private TextView freight;//运费

    private RelativeLayout service;//客服电话
    private CommodityEntity entity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_my_seller_order_details);
        InitView();
        setOnClick();
    }



    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.The_order_details), true);
        orderNumberTextView=(TextView)findViewById(R.id.orderNumberTextView);
        consigneeTextView=(TextView)findViewById(R.id.consigneeTextView);
        phoneNumber=(TextView)findViewById(R.id.phoneNumber);
        address=(TextView)findViewById(R.id.address);
        title=(TextView)findViewById(R.id.titles);
        price=(TextView)findViewById(R.id.price);
        freight=(TextView)findViewById(R.id.freight);
        service=(RelativeLayout)findViewById(R.id.service);

        orderNumberTextView.setText(getData().getOrder_no());
        consigneeTextView.setText(getData().getConsignee());
        phoneNumber.setText(getData().getPhone());
        address.setText(getData().getAddress());
        if(getData().getOfficial_or_personal().equals("1")){
            title.setText(getData().getTitle()+"【个人】");
        }else{
            title.setText(getData().getTitle()+"【自营】");
        }
        new ChangeText(title.getText().toString(),getResources().getColor(R.color.text_orange),title,title.getText().toString().length()-4,title.getText().toString().length());
        price.setText("价格：￥"+getData().getPrice());
        new ChangeText(price.getText().toString(),getResources().getColor(R.color.text_red),price,3,price.getText().toString().length());
        if(getData().getFreightage().equals("0.00")){
            freight.setText("运费：包邮");
        }else{
            freight.setText("运费：￥"+getData().getFreightage());
        }
        new ChangeText(freight.getText().toString(),getResources().getColor(R.color.text_red),freight,3,freight.getText().toString().length());
    }
    private void setOnClick() {
        //联系客服
        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + "0755-23026684");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });
    }

    private CommodityEntity getData(){
        if(entity==null) entity= (CommodityEntity) getIntent().getSerializableExtra("entity");
        return entity;
    }
}
