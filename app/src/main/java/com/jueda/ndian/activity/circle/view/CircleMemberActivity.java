package com.jueda.ndian.activity.circle.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.me.view.PersonalDetailsActivity;
import com.jueda.ndian.activity.circle.biz.CircleMemberBiz;
import com.jueda.ndian.entity.CircleEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sortListView.CharacterParser;
import sortListView.PinyinComparator;
import sortListView.SideBar;
import sortListView.SortAdapter;
import sortListView.SortModel;

/**
 * 圈成员
 */
public class CircleMemberActivity extends AppCompatActivity {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;

    private FrameLayout frameLayout;//有数据时显示
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    private CircleEntity entity;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**获取数据成功*/
                case Constants.ON_SUCEED_THREE:
                    SourceDateList= (List<SortModel>) msg.obj;
                    SourceDateList = filledData(SourceDateList);
                    // 根据a-z进行排序源数据
                    Collections.sort(SourceDateList, pinyinComparator);
                    adapter = new SortAdapter(CircleMemberActivity.this, SourceDateList,handler,entity);
                    sortListView.setAdapter(adapter);

                    animation.stopAnim();
                    frameLayout.setVisibility(View.VISIBLE);
                    break;
                /**获取数据失败*/
                case Constants.FAILURE_FOUR:
                    animation.stopAnim();
                    NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    include.setVisibility(View.VISIBLE);
                    break;
                /**踢出成功*/
                case Constants.ON_SUCEED_TWO:
                    int p= (int) msg.obj;
                    SourceDateList.remove(p);
                    adapter.updateListView(SourceDateList);
                    new ToastShow(CircleMemberActivity.this,getResources().getString(R.string.Kick_out_the_success),1000);
                    break;
                /**没有权限*/
                case Constants.FAILURE_TWO:
                    new ToastShow(CircleMemberActivity.this,getResources().getString(R.string.You_not_the_Lord_without_the_operating_authority),1000);
                    break;
                /**踢出失败*/
                case Constants.FAILURE_THREE:
                    new ToastShow(CircleMemberActivity.this,getResources().getString(R.string.Kick_out_the_failure),1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(CircleMemberActivity.this,false);
                    Intent intent = new Intent(CircleMemberActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_circle_member);
        InitView();
        setOnClick();
    }
    private void InitView() {
        entity= (CircleEntity) getIntent().getSerializableExtra("entity");
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.circle_member), true);
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        /**初始化数据时界面显示*/
        frameLayout=(FrameLayout)findViewById(R.id.frameLayout);
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);
        Reconnection();
    }
    private void setOnClick() {
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                if(! SourceDateList.get((int) id).getUid().equals(ConstantsUser.userEntity.getUid()+"")) {
                    Intent intent = new Intent(CircleMemberActivity.this, PersonalDetailsActivity.class);
                    intent.putExtra("uid", SourceDateList.get((int) id).getUid());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            include.setVisibility(View.GONE);
            setData();
        }else{
            new ToastShow(CircleMemberActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }

    public void setData(){
        animation.startAnim();
        SourceDateList=new ArrayList<>();
        new CircleMemberBiz(CircleMemberActivity.this,handler,SourceDateList,entity.getCid());

    }

    /**
     * 为ListView填充数据
     * @param date
     * @return
     */
    private List<SortModel> filledData( List<SortModel> SourceDateList){
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for(int i=0; i<SourceDateList.size(); i++){
            SortModel sortModel =SourceDateList.get(i);
            sortModel.setName(SourceDateList.get(i).getName());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(SourceDateList.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }
}
