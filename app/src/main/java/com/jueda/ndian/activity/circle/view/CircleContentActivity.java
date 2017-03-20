package com.jueda.ndian.activity.circle.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.CircleContent;
import com.jueda.ndian.activity.circle.CircleContentObservable;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.circle.adapter.CircleContentAdapter;
import com.jueda.ndian.activity.circle.biz.CircleMemberBiz;
import com.jueda.ndian.activity.circle.biz.CirclesDetailsBiz;
import com.jueda.ndian.activity.circle.biz.TopicBiz;
import com.jueda.ndian.entity.CircleEntity;
import com.jueda.ndian.entity.TopicEntity;
import com.jueda.ndian.savedata.rong.addFriend;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import sortListView.SortModel;

/***
 * 某个慈善圈
 */
public class CircleContentActivity extends Activity implements OnClickListener,CircleContent,CircleContentAdapter.OnChangeList{
    public static final String TAG="CircleContentActivity";
    public static CircleContentActivity instance=null;
    private RelativeLayout concealRelativeLayout;//没有网络或者连接失败时隐藏
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示
    private MyRefreshListView refreshListView;//
    private CircleContentAdapter adapter;//适配器
    public static ArrayList<CircleEntity> entityList;//圈子详情数据集合
    public ArrayList<TopicEntity> topicList;//圈子话题列表
    public Button newConstruction;//点击跳转到新建话题界面

    private List<SortModel> SourceDateList;//用户数据
    private TextView newTextView;//没有话题时显示

    private int screenWidth;//屏幕宽度
    private String Cid;//圈子id
    private int page=1;//页数
    public static final int CHAT=1;
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载

    public static CircleContentObservable observable;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**获取圈子详情数据成功*/
                case Constants.ON_SUCCEED:
                    entityList= (ArrayList<CircleEntity>) msg.obj;
                    NdianApplication.instance.setTitle(CircleContentActivity.this, entityList.get(0).getName(), true);
                    /**获取话题列表数据*/
                    new TopicBiz(CircleContentActivity.this,handler,Cid,topicList,page);
                    break;
                /**获取圈子详情数据失败*/
                case Constants.FAILURE:
                    concealRelativeLayout.setVisibility(View.GONE);
                    refreshListView.onFinish(isRefreshOrLoading);
                    animation.stopAnim();
                    NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    include.setVisibility(View.VISIBLE);
                    break;
                /**获取圈子话题列表数据成功*/
                case Constants.ON_SUCEED_TWO:
                    topicList= ((ArrayList<TopicEntity>) msg.obj);
                    /**根据返回的数据数量判断是否开始上滑加载*/
                    if ((topicList.size()) - 2 == page * Constants.Page) {
                        refreshListView.loading(true);
                    }else{
                        if(page==1){
                            refreshListView.loading(false);
                        }
                    }
                    /**没有话题列表时*/
                    if(topicList.size()==2){
                        newTextView.setVisibility(View.VISIBLE);
                    }else{
                        newTextView.setVisibility(View.GONE);
                    }
                    /**获取用户信息*/
                    if(page==1&&entityList.size()>0){
                        new CircleMemberBiz(CircleContentActivity.this,handler,SourceDateList,entityList.get(0).getCid());
                        adapter.updata(topicList);

                    }else {
                        //表示获取圈子详情失败
                        if(adapter==null){
                            handler.sendEmptyMessage(Constants.FAILURE);
                        }else {
                            adapter.updata(topicList);
                            refreshListView.onFinish(isRefreshOrLoading);
                        }
                    }
                    break;
                /**获取话题列表没有数据了*/
                case Constants.FAILURE_TWO:
                    if(page==1){
                        animation.stopAnim();
                        concealRelativeLayout.setVisibility(View.VISIBLE);
                    }else{
                        if((topicList.size())-2!=page*Constants.Page){
                            refreshListView.loading(false);
                        }else{
                            refreshListView.loading(true);
                        }
                        refreshListView.onFinish(isRefreshOrLoading);
                        refreshListView.loading(false);
                        new ToastShow(CircleContentActivity.this,getResources().getString(R.string.No_more),1000);
                    }
                    break;
                /**获取话题列表失败*/
                case Constants.FAILURE_THREE:
                    if(page==1){
                        animation.stopAnim();
                        concealRelativeLayout.setVisibility(View.GONE);
                        NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        --page;
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**获取用户数据成功*/
                case Constants.ON_SUCEED_THREE:
                    /**关闭动画*/
                    refreshListView.onFinish(isRefreshOrLoading);
                    animation.stopAnim();
                    concealRelativeLayout.setVisibility(View.VISIBLE);
                    SourceDateList= (List<SortModel>) msg.obj;
                    /**添加用户信息到数据库中*/
                    new addFriend(CircleContentActivity.this,SourceDateList);
                    break;
                /**获取用户数据失败*/
                case Constants.FAILURE_FOUR:
                    concealRelativeLayout.setVisibility(View.GONE);
                    animation.stopAnim();
                    NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    include.setVisibility(View.VISIBLE);
                    break;
                /**进入聊天室*/
                case CHAT:
                    RongIM.getInstance().startConversation(CircleContentActivity.this, Conversation.ConversationType.CHATROOM, entityList.get(0).getCid(), entityList.get(0).getName() + "聊天室");
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(CircleContentActivity.this,false);
                    Intent intent = new Intent(CircleContentActivity.this, LoginActivity.class);
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
        setContentView(R.layout.activity_circle_content);
        InitView();
        setOnClick();
    }

    private void InitView() {
        observable=new CircleContentSetout();
        observable.add(this);
        Cid=getIntent().getStringExtra("cid");
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        NdianApplication.instance.setTitle(this, getIntent().getStringExtra("name"), true);
        refreshListView=(MyRefreshListView)findViewById(R.id.refreshListView);
        refreshListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        newConstruction=(Button)findViewById(R.id.newConstruction);
        newTextView=(TextView)findViewById(R.id.newTextView);

        /**初始化数据时界面显示*/
        concealRelativeLayout=(RelativeLayout)findViewById(R.id.concealRelativeLayout);
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);


        Reconnection();
    }
    /**
     * 数据初始化
     */
    private void setData() {

        animation.startAnim();
        entityList=new ArrayList<>();
        topicList=new ArrayList<>();
        SourceDateList=new ArrayList<>();
        /**添加头部数据占据2个位置*/
        for(int i=0;i<2;i++){
            TopicEntity entity=new TopicEntity();
            entity.setCircleList(entityList);
            topicList.add(entity);
        }
        adapter=new CircleContentAdapter(CircleContentActivity.this,topicList,handler,screenWidth);
        refreshListView.setAdapter(adapter);
        adapter.setOnChangeList(this);
        new CirclesDetailsBiz(CircleContentActivity.this,handler,entityList,Cid);
    }
    /**
     * 交互事件
     */
    private void setOnClick() {
        refreshListView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshOrLoading=true;
                page=1;
                refresh();
            }

            @Override
            public void onLoadMore() {
                isRefreshOrLoading=true;
                ++page;
                new TopicBiz(CircleContentActivity.this, handler, Cid, topicList, page);
            }
        });
        //重连
        include.setOnClickListener(this);
        //跳转到新建话题界面
        newConstruction.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            //跳转到新建话题界面
            case R.id.newConstruction:
                if(new Configuration().readaIsLogin(getApplicationContext())){
                    /**判断是否为该圈成员*/
                    if(entityList.get(0).getIs_member().equals("1")){
                        intent=new Intent(CircleContentActivity.this,ReleaseActivity.class);
                        intent.putExtra("cid",Cid);
                        intent.putExtra("who","CircleContentActivity");
                        startActivity(intent);
                    }else{
                        new ToastShow(CircleContentActivity.this,getResources().getString(R.string.To_join_the_circle_can_be_released),1500);
                    }
                }else{
                    //跳转到登录界面
                    intent = new Intent(CircleContentActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;

            //重连
            case R.id.failure:
                Reconnection();
                break;
        }
    }
    /**刷新*/
    public void refresh(){
        topicList =new ArrayList<TopicEntity>();
        page=1;
        for(int i=0;i<2;i++){
            TopicEntity entity=new TopicEntity();
            entity.setCircleList(entityList);
            topicList.add(entity);
        }
        new TopicBiz(CircleContentActivity.this,handler,Cid,topicList,page);
    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        concealRelativeLayout.setVisibility(View.GONE);
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            include.setVisibility(View.GONE);
            setData();
        }else{
            new ToastShow(CircleContentActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        instance=null;
        observable.delect(this);
        super.onDestroy();
    }

    @Override
    public void content(String content) {
        topicList.get(0).getCircleList().get(0).setCdesc(content);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void name(String name) {
        NdianApplication.instance.setTitle(this, name, true);
        topicList.get(0).getCircleList().get(0).setName(name);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void head(String head) {
        topicList.get(0).getCircleList().get(0).setAvatar(head);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void change(ArrayList<TopicEntity> entityLis) {
        topicList=entityLis;
    }
}
