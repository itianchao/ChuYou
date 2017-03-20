package com.jueda.ndian.activity.circle.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.me.pop.PopEverydayTask;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.me.view.MyTopicActivity;
import com.jueda.ndian.activity.circle.adapter.TopicDetailsAdapter;
import com.jueda.ndian.activity.circle.biz.CommentPostsBiz;
import com.jueda.ndian.activity.circle.biz.ThumbBiz;
import com.jueda.ndian.activity.circle.biz.TopicDetailsBiz;
import com.jueda.ndian.entity.TopicCommentsEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.Share;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;

/**
 * 话题详情
 */
public class TopicDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    public static  final String TAG="TopicDetailsActivity";
    private RelativeLayout concealRelativeLayout;//没有网络或者连接失败时隐藏
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示

    private TopicDetailsAdapter adapter;
    private MyRefreshListView listView;
    private ArrayList<TopicCommentsEntity> commentsEntityList;
    private EditText commentsEditText;
    private Button ResendButton;//发送
    private RelativeLayout Thumb;//点赞
    private ImageView thumbImage;//点赞图标
    private TextView thumbNumber;//点赞数量

    private ImageButton share;//分享
    private RelativeLayout relativeLayout;//分享弹出位置

    private String who;//判断是圈内的话题（CircleContentActivity）还是我的话题（MyTopicActivity）或圈外话题（MainActivity）
    private float screenWidth;//屏幕宽度

    public static final int COMMENTS = 1;//评论
    private int number = 0;//记录回复时 回复某某某的长度
    private int position=0;//评论的位置
    private String id;//详情的id;
    private int page=1;//分页页数
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**数据请求成功*/
                case Constants.ON_SUCCEED:
                    commentsEntityList= (ArrayList<TopicCommentsEntity>) msg.obj;
                    /**根据返回的数据数量判断是否开始上滑加载*/
                    if ((commentsEntityList.size()) - 1 == page * Constants.Page) {
                        listView.loading(true);
                    }else{
                        if(page==1){
                            listView.loading(false);
                        }
                    }
                    if(page==1){
                        /**动画取消*/
                        animation.stopAnim();
                        adapter = new TopicDetailsAdapter(TopicDetailsActivity.this, commentsEntityList, who, handler,screenWidth);
                        listView.setAdapter(adapter);
                    }else {
                        adapter.notifyDataSetChanged();
                        /**动画取消*/
                        animation.stopAnim();
                    }
                    share.setVisibility(View.VISIBLE);
                    concealRelativeLayout.setVisibility(View.VISIBLE);
                    setThumb();
                    listView.onFinish(isRefreshOrLoading);
                    break;
                /**没有更多数据*/
                case Constants.FAILURE:
                    listView.onFinish(isRefreshOrLoading);
                    if(page==1){
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Temporarily_no_data), include);
                        concealRelativeLayout.setVisibility(View.GONE);
                        share.setVisibility(View.GONE);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        /**根据返回的数据数量判断是否开始上滑加载*/
                        if((commentsEntityList.size())-1!=page*Constants.Page){
                            listView.loading(false);
                        }else{
                            listView.loading(true);
                        }

                        new ToastShow(TopicDetailsActivity.this,getResources().getString(R.string.No_more),1000);
                    }
                    break;
                /**获取数据失败*/
                case Constants.FAILURE_TWO:
                    listView.onFinish(isRefreshOrLoading);
                    if(page==1){
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                        concealRelativeLayout.setVisibility(View.GONE);
                        share.setVisibility(View.GONE);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        --page;
                    }
                    break;

                /**回复*/
                case COMMENTS:
                    onImport();
                    position= (int) msg.obj;
                    commentsEditText.setText("回复" + commentsEntityList.get(position).getPname()+":");
                    number = commentsEditText.length();
                    commentsEditText.setSelection(number);
                    break;
                /**评论成功*/
                case Constants.ON_SUCEED_TWO:
                    TopicCommentsEntity entitys=(TopicCommentsEntity) msg.obj;
                    //弹窗奖励窗口
                    if(!entitys.getDevotion().equals("0")){
                        new PopEverydayTask(TopicDetailsActivity.this,commentsEditText,PopEverydayTask.Comment_Topic,entitys.getDevotion());
                        PersonalFragment.instance.updata();
                    }
                    commentsEditText.setText("");
                    new ToastShow(TopicDetailsActivity.this,getResources().getString(R.string.Comment_on_success),1000);
                        /**话题类的*/
                        commentsEntityList.add(1,entitys);
                    commentsEntityList.get(0).setCount_comments(Integer.parseInt(commentsEntityList.get(0).getCount_comments())+1+"");
                        adapter.notifyDataSetChanged();
                    break;
                /**评论失败*/
                case Constants.FAILURE_THREE:
                    new ToastShow(TopicDetailsActivity.this,getResources().getString(R.string.Comment_on_failure),1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(TopicDetailsActivity.this,false);
                    Intent intent = new Intent(TopicDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_topic_details);
        who = getIntent().getStringExtra("who");
        InitView();
        setOnClick();
    }


    private void InitView() {
        id=getIntent().getStringExtra("id");
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        listView = (MyRefreshListView) findViewById(R.id.listView);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        commentsEditText = (EditText) findViewById(R.id.commentsEditText);
        ResendButton=(Button)findViewById(R.id.ResendButton);
        concealRelativeLayout=(RelativeLayout)findViewById(R.id.concealRelativeLayout);
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);
        Thumb=(RelativeLayout)findViewById(R.id.thumb);
        share=(ImageButton)findViewById(R.id.share);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        thumbImage=(ImageView)findViewById(R.id.thumbImage);
        thumbNumber=(TextView)findViewById(R.id.thumbNumber);

        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Topic_details), true);

        Reconnection();
    }
    private void setData() {
        //开始动画
        animation.startAnim();
        commentsEntityList = new ArrayList<>();

        /**话题类的*/
        new TopicDetailsBiz(TopicDetailsActivity.this,handler,page,id,commentsEntityList,screenWidth);
        setThumb_switch(true);

    }
    /***
     * 设置点赞
     */
    private void setThumb() {
        if(who.equals(CircleContentActivity.TAG)||who.equals(MyTopicActivity.TAG)||who.equals(MainActivity.TAG)){
            thumbNumber.setText(commentsEntityList.get(0).getLike_count());
            if(commentsEntityList.get(0).getIs_like().equals("1")){
                    thumbImage.setImageResource(R.drawable.thumb_false);
                    Thumb.setEnabled(true);
            }else {
                thumbImage.setImageResource(R.drawable.thumb_true);
                Thumb.setEnabled(false);
            }
        }
    }

    /***
     * 开关点赞功能
     * @param b 是否开启点赞
     */
    private void setThumb_switch(boolean b){
        if(b){
            /**获取焦点变化*/
            commentsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        Thumb.setVisibility(View.GONE);
                        ResendButton.setVisibility(View.VISIBLE);
                    }else{
                        Thumb.setVisibility(View.VISIBLE);
                        ResendButton.setVisibility(View.GONE);
                    }
                }
            });
        }else{
            //关闭点赞功能
            Thumb.setVisibility(View.GONE);
            ResendButton.setVisibility(View.VISIBLE);
        }
    }


    private void setOnClick() {
        commentsEditText.setOnClickListener(this);
        ResendButton.setOnClickListener(this);
        include.setOnClickListener(this);
        Thumb.setOnClickListener(this);
        share.setOnClickListener(this);

        //刷新
        listView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshOrLoading=true;
                page=1;
                commentsEntityList=new ArrayList<>();

                new TopicDetailsBiz(TopicDetailsActivity.this,handler,page,id,commentsEntityList,screenWidth);

            }

            @Override
            public void onLoadMore() {
                isRefreshOrLoading=true;
                ++page;
                new TopicDetailsBiz(TopicDetailsActivity.this,handler,page,id,commentsEntityList,screenWidth);

            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                commentsEditText.setFocusable(false);
                new KeyboardManage().CloseKeyboard(commentsEditText, TopicDetailsActivity.this);
                return false;
            }
        });
        commentsEditText.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**分享*/
            case R.id.share:
                if(new Configuration().readaIsLogin(getApplicationContext())) {
                    if(commentsEntityList.get(0).getImg().length==0){
                        new Share(TopicDetailsActivity.this, relativeLayout, TAG, id,"","",commentsEntityList.get(0).getContent());
                    }else {
                        new Share(TopicDetailsActivity.this, relativeLayout, TAG, id, commentsEntityList.get(0).getImg()[0] + Constants.QINIU1 + Constants.dip2px(TopicDetailsActivity.this, 100), "", commentsEntityList.get(0).getContent());
                    }
                }else{
                    //跳转到登录界面
                    Intent intent = new Intent(TopicDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            /**评论输入内容*/
            case R.id.commentsEditText:
                onImport();
                break;
            /**重新加载*/
            case R.id.failure:
                Reconnection();
                break;
            /**点赞*/
            case R.id.thumb:
                if(new Configuration().readaIsLogin(getApplicationContext())){
                    new ThumbBiz(TopicDetailsActivity.this,handler,id,thumbNumber);
                    thumbNumber.setText((Integer.parseInt(commentsEntityList.get(0).getLike_count())+1)+"");
                    thumbImage.setImageResource(R.drawable.thumb_true);
                    Thumb.setEnabled(false);
                }else{
                    //跳转到登录界面
                    Intent intent = new Intent(TopicDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            /**评论发送*/
            case R.id.ResendButton:
                new KeyboardManage().CloseKeyboard(ResendButton, TopicDetailsActivity.this);
                if(new Configuration().readaIsLogin(getApplicationContext())){
                    /**评论*/
                    if(position==0){
                        /**内容为空则不能评论*/
                        if(commentsEditText.getText().toString().equals("")){
                            new ToastShow(TopicDetailsActivity.this,getResources().getString(R.string.Please_enter_a_comment),1000);
                        }else if(commentsEditText.getText().toString().trim().length()>60){
                            new ToastShow(TopicDetailsActivity.this,getResources().getString(R.string.Comment_cannot_be_more_than_sixty_characters),1000);
                        }else {
                            new CommentPostsBiz(TopicDetailsActivity.this, handler, commentsEditText.getText().toString(), commentsEntityList.get(0).getPpid(), who);
                        }
                    }else{
                        /**回复*/
                        /**内容为空则不能评论*/
                        if(commentsEditText.getText().toString().substring(commentsEntityList.get(position).getPname().length() + 3, commentsEditText.getText().toString().length()).equals("")){
                            new ToastShow(TopicDetailsActivity.this,getResources().getString(R.string.Please_enter_a_comment),1000);
                        }else if(commentsEditText.getText().toString().substring(commentsEntityList.get(position).getPname().length() + 3, commentsEditText.getText().toString().length()).trim().length()>60){
                            new ToastShow(TopicDetailsActivity.this,getResources().getString(R.string.Comment_cannot_be_more_than_sixty_characters),1000);
                        }else {
                            new CommentPostsBiz(TopicDetailsActivity.this, handler, commentsEditText.getText().toString().substring(commentsEntityList.get(position).getPname().length() + 3, commentsEditText.getText().toString().length()), commentsEntityList.get(0).getPpid(), commentsEntityList.get(position).getPid(), who);

                        }
                    }
                }else{
                    //跳转到登录界面
                    Intent intent = new Intent(TopicDetailsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
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
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            new ToastShow(TopicDetailsActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            include.setVisibility(View.VISIBLE);
        }
    }
    /**
     * 打开输入框
     */
    public void onImport() {
        commentsEditText.setFocusable(true);
        commentsEditText.setFocusableInTouchMode(true);
        commentsEditText.requestFocus();
        new KeyboardManage().OpenKeyboard(commentsEditText);
    }
    //输入框变化
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (s.length() < number) {
                number=0;
                position=0;
                commentsEditText.setText("");

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
