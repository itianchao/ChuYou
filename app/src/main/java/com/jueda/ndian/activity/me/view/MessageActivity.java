package com.jueda.ndian.activity.me.view;

import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.adapter.MessageAdapter;
import com.jueda.ndian.activity.me.biz.ClearMessageBiz;
import com.jueda.ndian.activity.me.biz.MessageListBiz;
import com.jueda.ndian.entity.MessageEntity;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.LoginOutUtil;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyRefreshListView;

import java.util.ArrayList;


/**
 * 消息
 */
public class MessageActivity extends AppCompatActivity {
    public static MessageActivity instance=null;
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示

    private ArrayList<MessageEntity> entityList;
    private MessageAdapter adapter;
    private MyRefreshListView refreshListView;
    private TextView ClearTextView;//清空
    private int page=1;//页数
    private boolean isRefreshOrLoading=false;//判断是否刷新或者加载
    private PopupWindow popClear;//清空
    private View layoutClear;
    public RelativeLayout relativeLayout;//弹窗相对位置



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**请求数据成功*/
                case Constants.ON_SUCCEED:
                    animation.stopAnim();
                    ClearTextView.setVisibility(View.VISIBLE);
                    refreshListView.setVisibility(View.VISIBLE);
                    entityList= (ArrayList<MessageEntity>) msg.obj;
                    adapter.notifyDataSetChanged();
                    if(entityList.size()-1==page*Constants.Page){
                        refreshListView.loading(true);
                    }else{
                        if(page==1){
                            refreshListView.loading(false);
                        }
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**请求数据失败或者没有数据时*/
                case Constants.FAILURE:
                    if(page==1){
                        ClearTextView.setVisibility(View.GONE);
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Temporarily_no_message), include);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        if(entityList.size()-1!=page*Constants.Page){
                            refreshListView.loading(false);
                        }else{
                            refreshListView.loading(true);
                        }
                        refreshListView.onFinish(isRefreshOrLoading);
                        refreshListView.loading(false);
                        new ToastShow(MessageActivity.this,getResources().getString(R.string.No_more),1000);
                    }
                    break;
                /**请求失败*/
                case Constants.FAILURE_TWO:
                    if(page==1){
                        ClearTextView.setVisibility(View.GONE);
                        animation.stopAnim();
                        NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                        include.setVisibility(View.VISIBLE);
                    }else{
                        --page;
                    }
                    refreshListView.onFinish(isRefreshOrLoading);
                    break;
                /**数据清空成功*/
                case Constants.ON_SUCEED_TWO:
                    Reconnection();
                    page=1;
                    break;
                /**数据清空失败*/
                case Constants.FAILURE_THREE:
                    new ToastShow(MessageActivity.this,getResources().getString(R.string.Empty_the_failure),1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new LoginOutUtil(MessageActivity.this);
                    finish();
                    break;
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        instance=this;
        setContentView(R.layout.activity_audit);
        InitView();
        setOnClick();
    }
    private void InitView() {
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        refreshListView=(MyRefreshListView)findViewById(R.id.refreshListView);
        ClearTextView=(TextView)findViewById(R.id.ClearTextView);
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.message), true);
        /**初始化数据时界面显示*/
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);
        Reconnection();
    }
    private void setData() {
        entityList=new ArrayList<>();
        animation.startAnim();
        adapter=new MessageAdapter(MessageActivity.this,entityList,relativeLayout);
        refreshListView.setAdapter(adapter);
        new MessageListBiz(MessageActivity.this,handler,entityList,page);
    }


    private void setOnClick() {
        refreshListView.setOnRefreshListener(new MyRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                ++page;
                isRefreshOrLoading=true;
                new MessageListBiz(MessageActivity.this, handler, entityList, page);
            }
        });

        /**重连*/
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reconnection();
            }
        });
        /**清空*/
        ClearTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popClear != null && popClear.isShowing()) {
                    popClear.dismiss();
                } else {
                    layoutClear=getLayoutInflater().inflate(
                            R.layout.pop_ok_cancel_content_general, null);
                    TextView content=(TextView)layoutClear.findViewById(R.id.content);//设置内容
                    content.setText(getResources().getString(R.string.Sure_you_want_to_delete_all_the_messages));
                    // 创建弹出窗口
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
                    popClear = new PopupWindow(layoutClear, ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT);
                    //取消
                    RelativeLayout cancelRelativeLayout=(RelativeLayout)layoutClear.findViewById(R.id.cancelRelativeLayout);
                    cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popClear.dismiss();
                        }
                    });
                    //确定
                    final TextView complete=(TextView)layoutClear.findViewById(R.id.complete);
                    complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new ClearMessageBiz(MessageActivity.this,handler);
                            popClear.dismiss();
                        }
                    });
                    //取消
                    final TextView center=(TextView)layoutClear.findViewById(R.id.center);
                    center.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popClear.dismiss();

                        }
                    });
                    ColorDrawable cd = new ColorDrawable(-0000);
                    popClear.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
                    popClear.update();
                    popClear.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popClear.setTouchable(true); // 设置popupwindow可点击
                    popClear.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    popClear.setFocusable(true); // 获取焦点
                    // 设置popupwindow的位置（相对tvLeft的位置）
                    popClear.showAtLocation(layoutClear, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                    popClear.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // 如果点击了popupwindow的外部，popupwindow也会消失
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popClear.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
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
            refreshListView.setVisibility(View.GONE);
            include.setVisibility(View.GONE);
            setData();
        }else{
            new ToastShow(MessageActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }
}
