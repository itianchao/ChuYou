package com.jueda.ndian.activity.me.view;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.adapter.UserTopicAdapter;
import com.jueda.ndian.activity.me.biz.GetInfoBiz;
import com.jueda.ndian.activity.circle.biz.TopicBiz;
import com.jueda.ndian.entity.TopicEntity;
import com.jueda.ndian.entity.UserEntity;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.LvUtil;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.CircleImageView;
import com.jueda.ndian.view.MyRefreshListView;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 个人详情
 */
public class PersonalDetailsActivity extends Activity implements UserTopicAdapter.OnChangeList{
    public static String TAG="PersonalDetailsActivity";
    public static PersonalDetailsActivity instance=null;

    private ArrayList<UserEntity> userList;//用户信息
    private MyRefreshListView refreshListView;
    //bar隐藏
    private View include;
    private RelativeLayout relativeLayout;
    //页数
    private int topicPage=1;
    //判断是否刷新或者加载
    private boolean isRefreshOrLoading=false;
    //用户话题适配器
    private UserTopicAdapter topicAdapter;
    private ArrayList<TopicEntity> topicEntityList;
    //状态栏高度
    private RelativeLayout topRelativeLayout;

    //屏幕宽度
    private int screenWidth;
    /**用户数据*/
    private RelativeLayout UserrelativeLayout;//用户布局

    private RelativeLayout usertopRelativeLayout;
    //性别
    ImageView sexImage;
    //头像
    CircleImageView HeadImage;
    //昵称
    TextView NickNameTextView;
    Button back;//返回
    TextView LveTextView;//等级


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**获取用户信息成功*/
                case Constants.ON_SUCCEED:
                    /**获取用户列表数据*/
                    userList= (ArrayList<UserEntity>) msg.obj;
                    setData();
                    new TopicBiz(PersonalDetailsActivity.this, handler, topicEntityList, topicPage, PersonalDetailsActivity.instance.uid());
                    break;
                /**获取用户信息失败*/
                case Constants.FAILURE:
                    new ToastShow(PersonalDetailsActivity.this,getResources().getString(R.string.Network_status_is_poor_failed_to_get_information),1000);
                    break;

                /**话题列表获取成功*/
                case Constants.ON_SUCEED_TWO:
                    /**关闭动画*/
                    topicEntityList= (ArrayList<TopicEntity>) msg.obj;
                    /**根据返回的数据数量判断是否开始上滑加载*/
                    if(topicEntityList.size()==topicPage*Constants.Page){
                        refreshListView.loading(true);
                    }else{
                        if(topicPage==1){
                            refreshListView.loading(false);
                        }
                    }
                        if (topicPage == 1) {
                            refreshListView.setAdapter(topicAdapter);
                        } else {
                            topicAdapter.updata(topicEntityList);
                        }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**获取话题列表没有数据了*/
                case Constants.FAILURE_TWO:
                    if(topicPage!=1){
                        /**根据返回的数据数量判断是否开始上滑加载*/
                        if(topicEntityList.size()!=topicPage*Constants.Page){
                            refreshListView.loading(false);
                        }else{
                            refreshListView.loading(true);
                        }
                        refreshListView.onFinish(isRefreshOrLoading);
                        refreshListView.loading(false);
                        new ToastShow(getApplicationContext(),getResources().getString(R.string.No_more),1000);
                    }
                    break;
                /**获取话题列表失败*/
                case Constants.FAILURE_THREE:
                    if(topicPage==1){
                        new ToastShow(getApplicationContext(),getResources().getString(R.string.For_failure),1000);
                    }else {
                        --topicPage;
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaldetails);
        instance=this;
        InitView();
        setOnClick();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, "个人中心", true);
        refreshListView=(MyRefreshListView)findViewById(R.id.refreshListView);
        topRelativeLayout=(RelativeLayout)findViewById(R.id.topRelativeLayout);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);


        include=(View)findViewById(R.id.include);
        screenWidth =getWindowManager().getDefaultDisplay().getWidth();
        /**
         * 判断系统设置状态栏高度
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams)topRelativeLayout.getLayoutParams();//获取空间当前布局参数
            layoutParams.height+= MainActivity.instance.getStatusBarHeight();
            topRelativeLayout.setLayoutParams(layoutParams);
        }

        /**用户布局控件*/
        UserrelativeLayout= (RelativeLayout) View.inflate(PersonalDetailsActivity.this,R.layout.item_personal_details,null);
        refreshListView.addHeaderView(UserrelativeLayout);
        usertopRelativeLayout=(RelativeLayout)UserrelativeLayout.findViewById(R.id.topRelativeLayout);
        /**
         * 判断系统设置状态栏高度
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams)usertopRelativeLayout.getLayoutParams();//获取空间当前布局参数
            layoutParams.height+= MainActivity.instance.getStatusBarHeight();
            usertopRelativeLayout.setLayoutParams(layoutParams);
        }
        sexImage=(ImageView)UserrelativeLayout.findViewById(R.id.sexImage);
        HeadImage=(CircleImageView)UserrelativeLayout.findViewById(R.id.HeadImage);
        NickNameTextView=(TextView)UserrelativeLayout.findViewById(R.id.NickNameTextView);
        LveTextView=(TextView)UserrelativeLayout.findViewById(R.id.LveTextView);
        back=(Button)UserrelativeLayout.findViewById(R.id.back);

        topicEntityList=new ArrayList<>();
        topicAdapter=new UserTopicAdapter(PersonalDetailsActivity.this,topicEntityList,screenWidth);
        refreshListView.setAdapter(topicAdapter);
        topicAdapter.setOnChangeList(this);
        Reconnection();
    }

    /***
     * 获取传递过来的uid
     * @return
     */
    public  String uid(){
        return  getIntent().getStringExtra("uid");
    }

    private void setOnClick() {
        //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        refreshListView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userList=new ArrayList<>();
                topicEntityList=new ArrayList<>();
                isRefreshOrLoading = true;
                topicPage = 1;
                new GetInfoBiz(PersonalDetailsActivity.this, handler, uid(), userList);
            }

            @Override
            public void onLoadMore() {
                    //话题加载
                    isRefreshOrLoading = true;
                    ++topicPage;
                    new TopicBiz(PersonalDetailsActivity.this, handler, topicEntityList, topicPage, PersonalDetailsActivity.instance.uid());
            }
        });

        refreshListView.setMyOnScroll(new MyRefreshListView.MyOnScroll() {
            private SparseArray recordSp = new SparseArray(0);
            private int mCurrentfirstVisibleItem = 0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mCurrentfirstVisibleItem = firstVisibleItem;
                View firstView = view.getChildAt(0);
                if (null != firstView) {
                    ItemRecod itemRecord = (ItemRecod) recordSp.get(firstVisibleItem);
                    if (null == itemRecord) {
                        itemRecord = new ItemRecod();
                    }
                    itemRecord.height = firstView.getHeight();
                    itemRecord.top = firstView.getTop();
                    recordSp.append(firstVisibleItem, itemRecord);
                    int h = getScrollY();//滚动距离
                    if(h>(Constants.dip2px(PersonalDetailsActivity.this, 50))){
                        relativeLayout.setVisibility(View.VISIBLE);
                    }else{
                        relativeLayout.setVisibility(View.GONE);
                    }
                }
            }
            private int getScrollY() {
                try {
                    int height = 0;
                    for (int i = 0; i < mCurrentfirstVisibleItem; i++) {
                        ItemRecod itemRecod = (ItemRecod) recordSp.get(i);
                        height += itemRecod.height;
                    }
                    ItemRecod itemRecod = (ItemRecod) recordSp.get(mCurrentfirstVisibleItem);
                    if (null == itemRecod) {
                        itemRecod = new ItemRecod();
                    }
                    return height - itemRecod.top;
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                return 0;
            }


            class ItemRecod {
                int height = 0;
                int top = 0;
            }
        });

    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            userList=new ArrayList<>();
            /**获取用户信息*/
            new GetInfoBiz(PersonalDetailsActivity.this,handler,uid(),userList);
        }else{
            new ToastShow(PersonalDetailsActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);

        }
    }

    /***
     * 填充数据
     */
    private void setData() {
        if(userList.get(0).getSex().equals("0")){
            sexImage.setImageResource(R.drawable.woman);
        }else{
            sexImage.setImageResource(R.drawable.man);
        }
        ImageLoaderUtil.ImageLoader(userList.get(0).getAvater(), HeadImage, R.drawable.head_portrait);
        float lve=Integer.parseInt(userList.get(0).getDevotion());
        HashMap<String,String> hashMap= new LvUtil().LvUtil(lve);
        String lvv=hashMap.get("lv");
        LveTextView.setText(lvv.substring(2, lvv.length()) + "级");
        NickNameTextView.setText(userList.get(0).getUname());

    }



    @Override
    protected void onDestroy() {
        instance=null;
        super.onDestroy();
    }

    @Override
    public void change(ArrayList<TopicEntity> entityLis) {
        this.topicEntityList=entityLis;
    }
}
