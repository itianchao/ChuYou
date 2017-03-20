package com.jueda.ndian.activity.me.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.view.TravelRouteActivity;
import com.jueda.ndian.entity.TravelEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;

/**
 * 旅游票详情
 */
public class MyTicketDetailsActivity extends AppCompatActivity {
    private RelativeLayout service;//客服电话
    private TextView order;//订单号
    private TextView name;//姓名
    private TextView phoneNumber;//手机号
    private ImageView imageView;
    private TextView title;//标题
    private TextView bean;//爱心豆数量
    private TextView money;//原价
    private RelativeLayout relativeLayout;//出游详情
    private TravelEntity entity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_my_ticket_details);
        InitView();
        setOnClick();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Tour_ticket_details), true);
        service=(RelativeLayout)findViewById(R.id.service);
        order=(TextView)findViewById(R.id.order);
        name=(TextView)findViewById(R.id.name);
        phoneNumber=(TextView)findViewById(R.id.phoneNumber);
        imageView=(ImageView)findViewById(R.id.imageView);
        title=(TextView)findViewById(R.id.titles);
        bean=(TextView)findViewById(R.id.bean);
        money=(TextView)findViewById(R.id.money);
        relativeLayout=(RelativeLayout)findViewById(R.id.detailsRelativeLayout);
        order.setText("订单号："+getData().getOrder());
        name.setText(getData().getName());
        phoneNumber.setText(getData().getPhone());
        title.setText(getData().getTitle());
        money.setText("￥"+getData().getCost_price());
        bean.setText(getData().getMoney() + getResources().getString(R.string.dedication));
        new ChangeText(bean.getText().toString(),getResources().getColor(R.color.text_love), bean,0, bean.length()-3);
        ImageLoaderUtil.ImageLoader(getData().getBig_img() + Constants.QINIU1 + Constants.dip2px(MyTicketDetailsActivity.this, 100), imageView);

    }
    private TravelEntity getData(){
        if(entity==null) entity= (TravelEntity) getIntent().getSerializableExtra("entity");
        return entity;
    }
    private void setOnClick(){
        //出游详情
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyTicketDetailsActivity.this, TravelRouteActivity.class);
                intent.putExtra("did",getData().getDid());
                startActivity(intent);
            }
        });
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
}
