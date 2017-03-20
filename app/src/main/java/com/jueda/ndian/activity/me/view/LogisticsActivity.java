package com.jueda.ndian.activity.me.view;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.biz.LogisticsBiz;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;

/***
 * 查看物流信息
 */
public class LogisticsActivity extends AppCompatActivity {
    private TextView logisticsTextView;//物流信息
    private CommodityEntity entity;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.ON_SUCCEED:
                    String data= (String) msg.obj;
                    logisticsTextView.setText(data);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_logistics);
        InitView();
    }

    private void InitView() {
        entity= (CommodityEntity) getIntent().getSerializableExtra("entity");
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Check_the_logistics), true);
        logisticsTextView=(TextView)findViewById(R.id.logisticsTextView);
        if(getWho()==1){
            new LogisticsBiz(LogisticsActivity.this,entity,handler,Constants.LOGISTICS);
        }else{
            new LogisticsBiz(LogisticsActivity.this,entity,handler,Constants.BUYERS_LOGISIICS);
        }

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
}
