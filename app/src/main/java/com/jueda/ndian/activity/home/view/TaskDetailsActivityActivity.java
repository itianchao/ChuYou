package com.jueda.ndian.activity.home.view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.biz.TaskActivityDelectBiz;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.me.view.MyTaskActivity;
import com.jueda.ndian.activity.home.adapter.TaskDetailsActivityAdapter;
import com.jueda.ndian.activity.home.biz.TaskDetailsActivityBiz;
import com.jueda.ndian.activity.home.biz.activitySignBiz;
import com.jueda.ndian.entity.TaskCommonEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.Image_look_preview;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.LoginOutUtil;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;
import java.util.ArrayList;

/**
 * 活动任务详情
 */
public class TaskDetailsActivityActivity extends AppCompatActivity {
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示
    private MyRefreshListView refreshListView;

    private int page=1;//页数
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载

    private PopupWindow popActivity;//
    private View layoutActivity;
    public RelativeLayout relativeLayout;//弹窗相对位置
    //添加头部文件
    private RelativeLayout topRelativeLayout;
    private LinearLayout linearLayout;//展示图片
    private Button participate_activity;//参加活动

    private TextView content;//内容
    private TextView title;//标题
    private ImageView head;//个人还是官方
    private TextView bean;//爱心豆
    private TextView details;//描述
    private RelativeLayout apply;//报名
    private TextView deleteTextView;//删除
    private PopupWindow popDelect;
    private View layoutDelect;//删除

    private ArrayList<TaskCommonEntity> entityList;
    private TaskDetailsActivityAdapter adapter;


    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**请求数据成功*/
                case Constants.ON_SUCCEED:
                    animation.stopAnim();
                    refreshListView.setVisibility(View.VISIBLE);
                    entityList = (ArrayList<TaskCommonEntity>) msg.obj;
                        addData();
                    if (entityList.get(0).getEnroll().size()== page * Constants.Page) {
                        refreshListView.loading(true);
                    } else {
                        if (page == 1) {
                            refreshListView.loading(false);
                        }
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**请求数据失败或者没有数据时*/
                case Constants.FAILURE:
                    if(page==1){
                        refreshListView.setVisibility(View.GONE);
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        --page;
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**没有数据*/
                case Constants.FAILURE_TWO:
                    refreshListView.onFinish(isRefreshOrLoading);
                    refreshListView.loading(false);
                    new ToastShow(TaskDetailsActivityActivity.this,getResources().getString(R.string.No_more),1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new LoginOutUtil(TaskDetailsActivityActivity.this);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_task_details);
        InitView();
        setOnClick();
    }


    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.For_the_task), true);
        refreshListView=(MyRefreshListView)findViewById(R.id.refreshListView);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        deleteTextView=(TextView)findViewById(R.id.deleteTextView);

        if(getWho().equals(MyTaskActivity.TAG)){
            deleteTextView.setVisibility(View.VISIBLE);
        }
        /**初始化数据时界面显示*/
        include=(View) findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);
        /**head布局*/
        topRelativeLayout= (RelativeLayout) View.inflate(TaskDetailsActivityActivity.this, R.layout.item_task_details_activity, null);

        content=(TextView)topRelativeLayout.findViewById(R.id.content);
        title=(TextView)topRelativeLayout.findViewById(R.id.title);
        head=(ImageView)topRelativeLayout.findViewById(R.id.head);
        bean=(TextView)topRelativeLayout.findViewById(R.id.bean);
        details=(TextView)topRelativeLayout.findViewById(R.id.details);
        apply=(RelativeLayout)topRelativeLayout.findViewById(R.id.apply1);
        linearLayout=(LinearLayout)topRelativeLayout.findViewById(R.id.linearLayout);
        participate_activity=(Button)topRelativeLayout.findViewById(R.id.participate_activity);
        refreshListView.addHeaderView(topRelativeLayout);

        entityList=new ArrayList<>();
        Reconnection();

    }

    private void setOnClick() {
        //删除
        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new Configuration().readaIsLogin(getApplicationContext())) {
                    delect();
                }else {
                    //跳转到登录界面
                    Intent intent = new Intent(TaskDetailsActivityActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        /**参加活动*/
        participate_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Configuration().readaIsLogin(TaskDetailsActivityActivity.this)){
                    Sign();
                }else{
                    startActivity(new Intent(TaskDetailsActivityActivity.this, LoginActivity.class));
                }
            }
        });
        /**重连*/
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reconnection();
            }
        });
        refreshListView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                ++page;
                isRefreshOrLoading = true;
                loadData();
            }
        });
    }
    /**
     * 获取传递下来的aid
     */
    public String  getAid(){
        return getIntent().getStringExtra("aid");
    }

    public String getWho(){
        String who=getIntent().getStringExtra("who");
        if(who==null) who="";
        return who;
    }
    /**
     * 获取数据
     */
    public void loadData(){
        new TaskDetailsActivityBiz(TaskDetailsActivityActivity.this,handler,entityList,getAid(),page);
    }
    /**
     * 装载数据
     */
    public void addData(){
        if(page==1){
            addImage();
            if(entityList.get(0).getIs_join().equals("1")){
                participate_activity.setVisibility(View.GONE);
            }
            if(entityList.get(0).getUid().equals("1")){
                head.setImageResource(R.drawable.guanfang);
            }else{
                head.setImageResource(R.drawable.geren);
            }
            content.setText(entityList.get(0).getContent());
            details.setText(entityList.get(0).getDescript());
            title.setText(entityList.get(0).getTitle());
            bean.setText(entityList.get(0).getReward());
            if(entityList.get(0).getUid().equals(ConstantsUser.userEntity.getUid()+"")){
                apply.setVisibility(View.VISIBLE);
            }
            adapter=new TaskDetailsActivityAdapter(TaskDetailsActivityActivity.this,entityList);
            refreshListView.setAdapter(adapter);
        }else {
            adapter.updata(entityList);
        }

    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            animation.startAnim();
            refreshListView.setVisibility(View.GONE);
            include.setVisibility(View.GONE);
            loadData();
        }else{
            new ToastShow(TaskDetailsActivityActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }

    public void addImage(){

        String img[] = entityList.get(0).getImg_url();
        for (int i = 0; i < img.length; i++) {
            LinearLayout.LayoutParams imgvwDimens =
                    new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            imgvwDimens.height = entityList.get(0).getImg_height()[i];
            ImageView im = new ImageView(TaskDetailsActivityActivity.this);
            //图片充满界面
            im.setLayoutParams(imgvwDimens);
            im.setScaleType(ImageView.ScaleType.FIT_XY);
            im.setPadding(0, 0, 0, Constants.dip2px(TaskDetailsActivityActivity.this, 15));
            im.setCropToPadding(true);
            ImageLoaderUtil.ImageLoader(img[i] + Constants.QINIU1 + (int) MainActivity.instance.getScreenWidth(), im);
            linearLayout.addView(im);
            final int finalI = i;
            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Image_look_preview.look(TaskDetailsActivityActivity.this, finalI, entityList.get(0).getPhotoArrayList());
                }
            });
        }
    }
    public void Sign(){
        if (popActivity != null && popActivity.isShowing()) {
            popActivity.dismiss();
        } else {
            layoutActivity=getLayoutInflater().inflate(
                    R.layout.pop_participate_activity, null);

            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popActivity = new PopupWindow(layoutActivity, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            //内容
            TextView content=(TextView)layoutActivity.findViewById(R.id.content);
            content.setText("尊敬的"+ ConstantsUser.userEntity.getPhoneNumber()+"用户，确定报名参加活动？报名成功后活动举办方将电话联系您。");
            //取消
            RelativeLayout cancelRelativeLayout=(RelativeLayout)layoutActivity.findViewById(R.id.cancelRelativeLayout);
            cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popActivity.dismiss();
                }
            });
            //立即报名
            final TextView complete=(TextView)layoutActivity.findViewById(R.id.complete);
            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new activitySignBiz(TaskDetailsActivityActivity.this,handler,getAid(),  participate_activity);
                    popActivity.dismiss();
                }
            });
            //取消
            final TextView center=(TextView)layoutActivity.findViewById(R.id.center);
            center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popActivity.dismiss();

                }
            });
            ColorDrawable cd = new ColorDrawable(-0000);
            popActivity.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
            popActivity.update();
            popActivity.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popActivity.setTouchable(true); // 设置popupwindow可点击
            popActivity.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popActivity.setFocusable(true); // 获取焦点
            // 设置popupwindow的位置（相对tvLeft的位置）
            popActivity.showAtLocation(relativeLayout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            popActivity.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popActivity.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
    /**
     * 删除
     */

    private void delect(){
        /**弹窗提示*/
        if (popDelect != null && popDelect.isShowing()) {
            popDelect.dismiss();
        } else {
            layoutDelect=getLayoutInflater().inflate(
                    R.layout.pop_ok_cancel_content_general, null);
            TextView know=(TextView)layoutDelect.findViewById(R.id.complete);
            TextView content=(TextView)layoutDelect.findViewById(R.id.content);
            TextView center=(TextView)layoutDelect.findViewById(R.id.center);
            content.setText("确定删除任务？");
            know.setText("确定");
            RelativeLayout cancelRelativeLayout=(RelativeLayout)layoutDelect.findViewById(R.id.cancelRelativeLayout);
            cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popDelect.dismiss();
                }
            });
            center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popDelect.dismiss();
                }
            });
            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TaskActivityDelectBiz(TaskDetailsActivityActivity.this,handler,getAid());
                    popDelect.dismiss();
                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popDelect = new PopupWindow(layoutDelect, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popDelect.setBackgroundDrawable(cd);
            popDelect.update();
            popDelect.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popDelect.setTouchable(true); // 设置popupwindow可点击
            popDelect.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popDelect.setFocusable(true); // 获取焦点
            NdianApplication.instance.backgroundAlpha((float) 0.4, TaskDetailsActivityActivity.this);
            // 设置popupwindow的位置（相对tvLeft的位置）
            popDelect.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popDelect.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1, TaskDetailsActivityActivity.this);
                }
            });
            popDelect.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popDelect.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
