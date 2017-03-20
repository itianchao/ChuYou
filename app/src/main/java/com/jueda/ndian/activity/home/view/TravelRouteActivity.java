package com.jueda.ndian.activity.home.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.home.adapter.TravelRouteAdapter;
import com.jueda.ndian.activity.circle.biz.CommentPostsBiz;
import com.jueda.ndian.activity.home.biz.TravelRouteBiz;
import com.jueda.ndian.entity.CommentsEntity;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.entity.TravelEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.Image_look_preview;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 旅游路线详情
 */
public class TravelRouteActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG=TravelRouteActivity.class.getName();
    private RelativeLayout concealRelativeLayout;//没有网络或者连接失败时隐藏
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示

    private MyRefreshListView listView;
    private TravelEntity travelEntity;//旅游线路数据
    private ArrayList<CommentsEntity> entityArrayList;//评论数据
    private EditText commentsEditText;
    private Button ResendButton;//发送
    private TravelRouteAdapter adapter;

    private LinearLayout linearLayoutTop;//头部
    private LinearLayout linearLayout;//图文混排
    private TextView exchange;//立即兑换
    private TextView title;//标题
    private TextView bean;//爱心豆
    private TextView money;//原价
    private TextView commentNumberTextView;//评论数量


    public static final int COMMENTS = 1;//评论
    private int number = 0;//记录回复时 回复某某某的长度
    private int position=-1;//评论的位置
    private int page=1;//分页页数
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**数据请求成功*/
                case Constants.ON_SUCCEED:
                    Bundle bundle= (Bundle) msg.obj;
                    entityArrayList= (ArrayList<CommentsEntity>) bundle.getSerializable("comment");
                    travelEntity= (TravelEntity) bundle.getSerializable("entity");
                    /**根据返回的数据数量判断是否开始上滑加载*/
                    if ((entityArrayList.size())  == page * Constants.Page) {
                        listView.loading(true);
                    }else{
                        if(page==1){
                            listView.loading(false);
                        }
                    }
                    if(page==1){
                        setData();
                    }
                    adapter.updata(entityArrayList);
                    animation.stopAnim();
                    concealRelativeLayout.setVisibility(View.VISIBLE);
                    listView.onFinish(isRefreshOrLoading);
                    break;
                /**没有更多数据*/
                case Constants.FAILURE:
                    listView.onFinish(isRefreshOrLoading);
                    if(page==1){
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Temporarily_no_data), include);
                        concealRelativeLayout.setVisibility(View.GONE);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        /**根据返回的数据数量判断是否开始上滑加载*/
                        if((entityArrayList.size())!=page*Constants.Page){
                            listView.loading(false);
                        }else{
                            listView.loading(true);
                        }
                        new ToastShow(TravelRouteActivity.this,getResources().getString(R.string.No_more),1000);
                    }
                    break;
                /**获取数据失败*/
                case Constants.FAILURE_TWO:
                    listView.onFinish(isRefreshOrLoading);
                    if(page==1){
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                        concealRelativeLayout.setVisibility(View.GONE);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        --page;
                    }
                    break;

                /**回复*/
                case COMMENTS:
                    onImport();
                    position= (int) msg.obj;
                    commentsEditText.setText("回复" + entityArrayList.get(position).getPname()+":");
                    number = commentsEditText.length();
                    commentsEditText.setSelection(number);
                    break;
                /**评论成功*/
                case Constants.ON_SUCEED_TWO:
                    commentsEditText.setText("");
                    new ToastShow(TravelRouteActivity.this,getResources().getString(R.string.Comment_on_success),1000);
                    entityArrayList.add(0, (CommentsEntity) msg.obj);
//                    entityArrayList.get(0).setCount_comments(Integer.parseInt(entityArrayList.get(0).getCount_comments()) + 1 + "");
                    adapter.updata(entityArrayList);
                    break;
                /**评论失败*/
                case Constants.FAILURE_THREE:
                    new ToastShow(TravelRouteActivity.this,getResources().getString(R.string.Comment_on_failure),1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(TravelRouteActivity.this,false);
                    Intent intent = new Intent(TravelRouteActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_travel_route);
        InitView();
        setOnClick();
    }
    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Route_details), true);
        listView = (MyRefreshListView) findViewById(R.id.listView);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        commentsEditText = (EditText) findViewById(R.id.commentsEditText);
        ResendButton=(Button)findViewById(R.id.ResendButton);
        concealRelativeLayout=(RelativeLayout)findViewById(R.id.concealRelativeLayout);
        include=(View)findViewById(R.id.failure);
        loadingInclude = (View) findViewById(R.id.loadingInclude);
        linearLayoutTop= (LinearLayout) View.inflate(TravelRouteActivity.this, R.layout.item_travel_route_top, null);
        linearLayout=(LinearLayout)linearLayoutTop.findViewById(R.id.linearLayout);

        money=(TextView)linearLayoutTop.findViewById(R.id.money);
        commentNumberTextView=(TextView)linearLayoutTop.findViewById(R.id.commentNumberTextView);
        bean=(TextView)linearLayoutTop.findViewById(R.id.bean);
        title=(TextView)linearLayoutTop.findViewById(R.id.title);
        exchange=(TextView)linearLayoutTop.findViewById(R.id.exchange);
        listView.addHeaderView(linearLayoutTop);
        animation=new LoadingAnimation(this,loadingInclude);
        Reconnection();
    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            include.setVisibility(View.GONE);
            loadData();
        }else{
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            new ToastShow(TravelRouteActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            include.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 加载数据
     */
    private void setData(){
        title.setText(travelEntity.getTitle());
        money.setText("￥"+travelEntity.getCost_price());
        commentNumberTextView.setText("评论（"+travelEntity.getComment_count()+"）");
        bean.setText(travelEntity.getMoney()+"爱心豆");
        new ChangeText(bean.getText().toString(),getResources().getColor(R.color.text_love),bean,0,bean.getText().toString().length()-3);
        for (int i = 0; i < travelEntity.getContent().size(); i++) {
            HashMap<String,String> map=travelEntity.getContent().get(i);
            if(!map.get("text").equals("")){
                LinearLayout.LayoutParams imgvwDimens =
                        new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

                TextView im = new TextView(TravelRouteActivity.this);
                //图片充满界面
                im.setLayoutParams(imgvwDimens);
                im.setText(map.get("text"));
                im.setTextColor(getResources().getColor(R.color.text_black));
                im.setTextSize(15);
                im.setPadding(0, 0, 0, Constants.dip2px(TravelRouteActivity.this, 15));
                linearLayout.addView(im);

            }
            if(!map.get("url").equals("http://static.ndian365.com/")){
                LinearLayout.LayoutParams imgvwDimens =
                        new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
                imgvwDimens.height = (int)(MainActivity.instance.getScreenWidth()/1.5);
                ImageView im = new ImageView(TravelRouteActivity.this);
                //图片充满界面
                im.setLayoutParams(imgvwDimens);
                im.setScaleType(ImageView.ScaleType.CENTER_CROP);
                im.setPadding(0, 0, 0, Constants.dip2px(TravelRouteActivity.this, 15));
                im.setCropToPadding(true);
                ImageLoaderUtil.ImageLoader(map.get("url") + Constants.QINIU1 + (int) MainActivity.instance.getScreenWidth(), im);
                linearLayout.addView(im);
                final ArrayList<Photo> lists=new ArrayList<>();
                Photo photo=new Photo();
                photo.path=map.get("url");
                lists.add(photo);
                im.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Image_look_preview.look(TravelRouteActivity.this, 0, lists);
                    }
                });
            }
        }
    }

    /**
     * 获取数据
     */
    private void loadData() {
        //开始动画
        animation.startAnim();
        entityArrayList = new ArrayList<>();
        travelEntity=new TravelEntity();
        adapter=new TravelRouteAdapter(TravelRouteActivity.this,handler,entityArrayList);
        listView.setAdapter(adapter);
        new TravelRouteBiz(TravelRouteActivity.this,handler,entityArrayList,travelEntity,getDid(),page);
    }

    public String getDid(){
        return getIntent().getStringExtra("did");
    }

    private void setOnClick() {
        commentsEditText.setOnClickListener(this);
        ResendButton.setOnClickListener(this);
        include.setOnClickListener(this);
        exchange.setOnClickListener(this);

        //刷新
        listView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshOrLoading=true;
                page=1;
                travelEntity=new TravelEntity();
                entityArrayList=new ArrayList<>();
                new TravelRouteBiz(TravelRouteActivity.this,handler,entityArrayList,travelEntity,getDid(),page);
            }

            @Override
            public void onLoadMore() {
                isRefreshOrLoading=true;
                ++page;
                new TravelRouteBiz(TravelRouteActivity.this,handler,entityArrayList,travelEntity,getDid(),page);
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                commentsEditText.setFocusable(false);
                new KeyboardManage().CloseKeyboard(commentsEditText, TravelRouteActivity.this);
                return false;
            }
        });
        commentsEditText.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            /**兑换信息*/
            case R.id.exchange:
                if(new Configuration().readaIsLogin(TravelRouteActivity.this)){
                    intent=new Intent(TravelRouteActivity.this,TraveExchangeActivity.class);
                    intent.putExtra("entity",travelEntity);
                    startActivity(intent);
                }else{
                    startActivity(new Intent(TravelRouteActivity.this,LoginActivity.class));
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
            /**评论发送*/
            case R.id.ResendButton:
                new KeyboardManage().CloseKeyboard(ResendButton, TravelRouteActivity.this);
                if(new Configuration().readaIsLogin(getApplicationContext())){
                    /**评论*/
                    if(position==-1){
                        /**内容为空则不能评论*/
                        if(commentsEditText.getText().toString().equals("")){
                            new ToastShow(TravelRouteActivity.this,getResources().getString(R.string.Please_enter_a_comment),1000);
                        }else if(commentsEditText.getText().toString().trim().length()>60){
                            new ToastShow(TravelRouteActivity.this,getResources().getString(R.string.Comment_cannot_be_more_than_sixty_characters),1000);
                        }else {
                            new CommentPostsBiz(TravelRouteActivity.this, handler, commentsEditText.getText().toString(), travelEntity.getDid(), TAG);
                        }
                    }else{
                        /**回复*/
                        /**内容为空则不能评论*/
                        if(commentsEditText.getText().toString().substring(entityArrayList.get(position).getPname().length() + 3, commentsEditText.getText().toString().length()).equals("")){
                            new ToastShow(TravelRouteActivity.this,getResources().getString(R.string.Please_enter_a_comment),1000);
                        }else if(commentsEditText.getText().toString().substring(entityArrayList.get(position).getPname().length() + 3, commentsEditText.getText().toString().length()).trim().length()>60){
                            new ToastShow(TravelRouteActivity.this,getResources().getString(R.string.Comment_cannot_be_more_than_sixty_characters),1000);
                        }else {
                            new CommentPostsBiz(TravelRouteActivity.this, handler, commentsEditText.getText().toString().substring(entityArrayList.get(position).getPname().length() + 3,
                                    commentsEditText.getText().toString().length()), travelEntity.getDid(), entityArrayList.get(position).getPid(), TAG);

                        }
                    }
                }else{
                    //跳转到登录界面
                    intent = new Intent(TravelRouteActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
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
                position=-1;
                commentsEditText.setText("");

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
