package com.jueda.ndian.activity.me.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.home.view.AllTaskActivity;
import com.jueda.ndian.activity.me.UserContent;
import com.jueda.ndian.activity.me.biz.GetRechargeBiz;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;

/***
 * 充值
 */
public class RechargeActivity extends AppCompatActivity implements UserContent {
    private TextView go_love;//去赚爱心豆
    private TextView love_bean;//爱心豆数量

    private TextView love_one;
    private TextView love_two;
    private TextView love_three;
    private TextView love_four;
    private TextView love_five;
    private TextView love_six;
    private TextView money_one;
    private TextView money_two;
    private TextView money_three;
    private TextView money_four;
    private TextView money_five;
    private TextView money_six;
    private String []bean;
    private String []money;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.ON_SUCCEED:
                    Bundle bundle= (Bundle) msg.obj;
                    bean=bundle.getStringArray("bean");
                    money=bundle.getStringArray("money");
                    setData();
                    break;
                case Constants.FAILURE:
                    money_one.setVisibility(View.GONE);
                    money_two.setVisibility(View.GONE);
                    money_three.setVisibility(View.GONE);
                    money_four.setVisibility(View.GONE);
                    money_five.setVisibility(View.GONE);
                    money_six.setVisibility(View.GONE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_recharge);
        InitView();
        setOnClick();
    }

    @Override
    protected void onDestroy() {
        PersonalFragment.userObservable.delect(this);
        super.onDestroy();
    }

    private void InitView() {
        PersonalFragment.userObservable.add(this);
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.top_up), true);
        go_love=(TextView)findViewById(R.id.go_love);
        love_bean=(TextView)findViewById(R.id.love_bean);
        love_one=(TextView)findViewById(R.id.love_one);
        love_two=(TextView)findViewById(R.id.love_two);
        love_three=(TextView)findViewById(R.id.love_three);
        love_four=(TextView)findViewById(R.id.love_four);
        love_five=(TextView)findViewById(R.id.love_five);
        love_six=(TextView)findViewById(R.id.love_six);
        money_one=(TextView)findViewById(R.id.money_one);
        money_two=(TextView)findViewById(R.id.money_two);
        money_three=(TextView)findViewById(R.id.money_three);
        money_four=(TextView)findViewById(R.id.money_four);
        money_five=(TextView)findViewById(R.id.money_five);
        money_six=(TextView)findViewById(R.id.money_six);
        love_bean.setText("爱心豆："+ ConstantsUser.userEntity.getLove_bean());
        new ChangeText(love_bean.getText().toString(),getResources().getColor(R.color.text_red),love_bean,4,love_bean.getText().toString().length());
        new GetRechargeBiz(RechargeActivity.this,handler);
    }

    private void setData() {
        if(bean!=null&&money!=null){
            love_one.setText(bean[0]);
            love_two.setText(bean[1]);
            love_three.setText(bean[2]);
            love_four.setText(bean[3]);
            love_five.setText(bean[4]);
            love_six.setText(bean[5]);
            money_one.setText("￥"+money[0]);
            money_two.setText("￥"+money[1]);
            money_three.setText("￥"+money[2]);
            money_four.setText("￥"+money[3]);
            money_five.setText("￥"+money[4]);
            money_six.setText("￥"+money[5]);
        }
    }
    private void setOnClick() {
        //去赚爱心豆
        go_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RechargeActivity.this, AllTaskActivity.class));
            }
        });
        money_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RechargeActivity.this,PayActivity.class);
                intent.putExtra("money","10");
                startActivity(intent);
            }
        });
        money_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RechargeActivity.this,PayActivity.class);
                intent.putExtra("money","30");
                startActivity(intent);
            }
        });
        money_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RechargeActivity.this,PayActivity.class);
                intent.putExtra("money","100");
                startActivity(intent);
            }
        });
        money_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RechargeActivity.this,PayActivity.class);
                intent.putExtra("money","300");
                startActivity(intent);
            }
        });
        money_five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RechargeActivity.this,PayActivity.class);
                intent.putExtra("money","500");
                startActivity(intent);
            }
        });
        money_six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RechargeActivity.this,PayActivity.class);
                intent.putExtra("money","1000");
                startActivity(intent);
            }
        });
    }

    @Override
    public void bean() {
        love_bean.setText("爱心豆："+ ConstantsUser.userEntity.getLove_bean());
    }

    @Override
    public void money() {

    }
}
