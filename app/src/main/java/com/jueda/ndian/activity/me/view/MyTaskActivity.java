package com.jueda.ndian.activity.me.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.MyAuditFragment;
import com.jueda.ndian.activity.fragment.MyReleaseFragment;
import com.jueda.ndian.activity.home.adapter.GuidepageAdapter;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.view.IndexViewPager;

import java.util.ArrayList;

/**
 * 我的任务
 */
public class MyTaskActivity extends FragmentActivity {
    public static final String TAG=MyTaskActivity.class.getName();
    public IndexViewPager viewPager;
    private ArrayList<Fragment> fragmentList;//界面集合
    private Button back;//返回
    //切换界面
    public RadioButton radioButton1;//审核
    private RadioButton radioButton2;//发布
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_my_task2);
        InitView();
    }

    private void InitView() {
        viewPager=(IndexViewPager)findViewById(R.id.viewPager);
        radioButton1=(RadioButton)findViewById(R.id.radioButton1);
        radioButton2=(RadioButton)findViewById(R.id.radioButton2);
        back=(Button)findViewById(R.id.back);
        fragmentList = new ArrayList<>();
        fragmentList.add(new MyAuditFragment());
        fragmentList.add(new MyReleaseFragment());
        FragmentManager manager = this.getSupportFragmentManager();
        GuidepageAdapter adapter = new GuidepageAdapter(manager, fragmentList);
        viewPager.setAdapter(adapter);

        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
