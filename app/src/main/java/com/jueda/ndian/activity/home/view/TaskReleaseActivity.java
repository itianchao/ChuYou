package com.jueda.ndian.activity.home.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.SendActivityTaskFragment;
import com.jueda.ndian.activity.fragment.SendQuestionnaireTaskFragment;
import com.jueda.ndian.activity.home.adapter.GuidepageAdapter;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.view.IndexViewPager;

import java.util.ArrayList;

/**
 * 发布任务
 */
public class TaskReleaseActivity extends FragmentActivity implements View.OnClickListener{
    public static TaskReleaseActivity instance=null;
    private TextView send;//发送
    private ArrayList<Fragment> fragmentList;
    private IndexViewPager viewPager;
    //切换界面按钮
    private RadioButton radioButton1;//活动
    private RadioButton radioButton2;//问卷
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_release_task);
        instance=this;
        InitView();
        setOnClick();
    }

    @Override
    protected void onDestroy() {
        instance=null;
        super.onDestroy();
    }

    private String getCid(){
        String cid=getIntent().getStringExtra("cid");
        if(cid==null)cid="";
        return cid;
    }

    private void setOnClick() {
        radioButton1.setOnClickListener(this);
        radioButton2.setOnClickListener(this);
        send.setOnClickListener(this);
    }
    private void InitView() {
        NdianApplication.instance.setTitle(this, "发布任务", true);
        send=(TextView)findViewById(R.id.send_task);
        viewPager=(IndexViewPager)findViewById(R.id.viewPager);
        radioButton1=(RadioButton)findViewById(R.id.radioButton1);
        radioButton2=(RadioButton)findViewById(R.id.radioButton2);
        fragmentList=new ArrayList<>();
        fragmentList.add(new SendActivityTaskFragment());
        fragmentList.add(new SendQuestionnaireTaskFragment());

        FragmentManager manager=this.getSupportFragmentManager();
        GuidepageAdapter adapter=new GuidepageAdapter(manager,fragmentList);
        viewPager.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //发任务
            case R.id.send_task:
                if(viewPager.getCurrentItem()==0){
                    //发布活动
                    if(SendActivityTaskFragment.instance!=null){
                        SendActivityTaskFragment.instance.updata(getCid());
                        new LogUtil("任务","SendActivityTaskFragment");
                    }
                }else{
                    //发布问卷调查
                    if(SendQuestionnaireTaskFragment.instance!=null){
                        SendQuestionnaireTaskFragment.instance.updata(getCid());
                        new LogUtil("任务","SendActivityTaskFragment");
                    }
                }
                break;
            //活动
            case R.id.radioButton1:
                viewPager.setCurrentItem(0,false);
                break;
            //问卷
            case R.id.radioButton2:
                viewPager.setCurrentItem(1,false);
                break;

        }
    }
}
