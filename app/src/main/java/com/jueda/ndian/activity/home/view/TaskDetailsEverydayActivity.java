package com.jueda.ndian.activity.home.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.entity.AppEntity;
import com.jueda.ndian.utils.ChangeTitle;

import java.util.ArrayList;

//每日任务
public class TaskDetailsEverydayActivity extends AppCompatActivity {
    private AppEntity entity;
    private TextView titles;//标题
    private TextView bean;//爱心豆
    private TextView content;//内容
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_task_details_everyday);
        InitView();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.For_the_task), true);
        titles=(TextView)findViewById(R.id.titles);
        bean=(TextView)findViewById(R.id.bean);
        content=(TextView)findViewById(R.id.content);

        titles.setText(getdata().getName());
        bean.setText(getdata().getReward());
        content.setText(getdata().getLastcount());
    }
    private AppEntity getdata(){
        if(entity==null) entity= (AppEntity) getIntent().getSerializableExtra("entity");
        return entity;
    }
}
