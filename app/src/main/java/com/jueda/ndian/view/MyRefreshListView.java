package com.jueda.ndian.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.LogUtil;

import java.text.SimpleDateFormat;

/**
 * Created by guolinyao on 15/11/10.
 */
public class MyRefreshListView extends ListView implements AbsListView.OnScrollListener {

    private static final int PULL_DOWN = 1;
    private static final int REREESHING = 2;
    private static final int RELEASE_REFRESH = 3;
    private ImageView iv_header;
    private ProgressBar pb_header;
    private TextView tv_state;
    private TextView tv_time;
    private int headerViewHright;
//    private RotateAnimation upRa;
    private RotateAnimation downRa;
    private View headerView;
    public int downY;//按下的y坐标
    private float moveY;//移动之后的y坐标
    private int currentState = PULL_DOWN;//下拉的状态
    private boolean isLoadingMore = false;
    private View footerView;
    private int footerViewHeight;
    private OnRefreshListener mOnRefreshListener;
    private MyOnScroll myOnScroll;
    private Context context;
    public int downNumberY;//记录是否第一次按下屏幕是则重置按下位置。以防中中间下拉刷新时出现错位
    private boolean isLoading=true;//是否显示上滑加载
    private boolean isRefresh=true;//是否显示下滑刷新
    private boolean isShowTime=false;//是否显示刷新时间

    public MyRefreshListView(Context context) {
        this(context, null);//引用二个参数的构造方法
    }
    //布局文件调用
    public MyRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MyRefreshListView);
        isLoading=a.getBoolean(R.styleable.MyRefreshListView_isLoading,true);
        isRefresh=a.getBoolean(R.styleable.MyRefreshListView_isRefresh, true);
        isShowTime=a.getBoolean(R.styleable.MyRefreshListView_isShowTime, false);

        initHeaderView();
        initAnim();
        initFooterView();
        setOnScrollListener(this);//设置滑动监听
    }
    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.listview_footer, null);
        footerView.measure(0, 0);//让系统测量脚布局的高度
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);//隐藏脚布局
        //添加脚布局
            this.addFooterView(footerView);
    }
    /**
     * 初始化动画
     */
    private void initAnim() {
        //向上拉的动画
//        upRa = new RotateAnimation(0, 360,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        upRa.setDuration(800);
//        upRa.setFillAfter(true);
//        向下拉的动画
        downRa = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        downRa.setDuration(800);
        downRa.setFillAfter(true);
    }
    //初始化头布局
    private void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.listview_header, null);
        iv_header = (ImageView) headerView.findViewById(R.id.iv_header);
        pb_header = (ProgressBar) headerView.findViewById(R.id.pb_header);
        tv_state = (TextView) headerView.findViewById(R.id.tv_state);
        tv_time = (TextView) headerView.findViewById(R.id.tv_time);
        if(!isShowTime){
            tv_time.setVisibility(View.GONE);
        }

        //让系统自己去测量自己的宽高
        headerView.measure(0, 0);
        // headerView.getHeight();//这里获得的值永远为0 因为没经过测量
        //获得headerView的height
        headerViewHright = headerView.getMeasuredHeight();
        headerView.setPadding(0, -headerViewHright, 0, 0);//给headerview设置padding
        //给listview添加headerView 头布局
        this.addHeaderView(headerView);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downY = (int) ev.getY();
            downNumberY=0;
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                downNumberY=0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRefresh) {
                    if (currentState == REREESHING) {//如果当前是正在刷新状态 那就不处理移动事件
                        break;
                    }
                    moveY = ev.getY();
                    int dy = (int) (moveY - downY);
                    int paddingTop = -headerViewHright + (int)(dy/2.4);
                    int firstVisiblePosition = getFirstVisiblePosition();//获得当前列表显示的第一个条目的索引
                    //只有当paddingTop大于头部负数时并且可见第一个条目的是listview的第一个条目时才进行处理
                    if (firstVisiblePosition == 0 && paddingTop > -headerViewHright) {
                        if(downNumberY==0){
                            downY= (int) moveY;
                            paddingTop=-headerViewHright;
                            downNumberY=1;
                        }
                        if(isShowTime){
                            tv_time.setText("最近更新时间：" + getCurrentTime());
                        }
                        if (paddingTop > 0 && currentState == PULL_DOWN) {//当头布局完全显示 并且是下拉状态时，显示松开刷新
                            currentState = RELEASE_REFRESH;
                            switchViewOnStateChange();
                        } else if (paddingTop < 0 && currentState == RELEASE_REFRESH) {
                            //当头布局不完全显示，并且为松开刷新状态，松开刷新变成下拉刷新的时候

                            currentState = PULL_DOWN;
                            switchViewOnStateChange();
                        }
                        headerView.setPadding(0, paddingTop, 0, 0);//给headerview设置padding
                        return true;//自己处理触摸事件
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(isRefresh) {
                    if (currentState == PULL_DOWN) {
                        headerView.setPadding(0, -headerViewHright, 0, 0);
                    } else if (currentState == RELEASE_REFRESH) {
                        currentState = REREESHING;
                        switchViewOnStateChange();
                        if (mOnRefreshListener != null) {
                            //回调方法
                            mOnRefreshListener.onRefresh();
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);   //listview自己处理事件
    }
    //根据状态改变头布局的内容
    private void switchViewOnStateChange() {
        switch (currentState) {
            case PULL_DOWN:
//                iv_header.startAnimation(upRa);
//                tv_state.setText("下拉刷新");
                break;
            case RELEASE_REFRESH:
                iv_header.startAnimation(downRa);
//                tv_state.setText("松开刷新");
                break;
            case REREESHING:
                iv_header.clearAnimation();
                iv_header.setVisibility(View.INVISIBLE);
                pb_header.setVisibility(View.VISIBLE);
//                tv_state.setText("正在刷新...");
                headerView.setPadding(0, 0, 0, 0);
                if(isShowTime){
                    new Configuration().writeaDate(context, System.currentTimeMillis());
                }
                break;
        }
    }
    private String getCurrentTime() {
        long nowTime=System.currentTimeMillis()/1000;
        long beforeTime=new Configuration().readaDate(context)/1000;
        long time=nowTime-beforeTime;
        //判断时间
        if(time<60){
            return "刚刚";
        }else if(time>60&&time<3600){
            return time/60+"分钟前";
        }else if(time>3600&&time<86400){
            return time/3600+"小时前";
        }else if(time>86400&&time<864000){
            return time/86400+"天前";
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");
            return sdf.format(beforeTime);
        }
    }
    //滑动状态改变的时候调用
//当滚动发生改变时，调用该方法
//	OnScrollListener.SCROLL_STATE_FLING;2     手指用力滑动一下，离开屏幕，listview有一个惯性的滑动状态
//	OnScrollListener.SCROLL_STATE_IDLE;0    listview列表处于停滞状态，手指没有触摸屏幕
//	OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;1	手指触摸着屏幕，上下滑动的状态
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(myOnScroll!=null){
            myOnScroll.onScrollStateChanged(view,scrollState);
        }
        //当手指离开屏幕，并且列表到达最后一个条目的时候
        int lastVisiblePosition = getLastVisiblePosition();
        if (lastVisiblePosition == (getCount() - 1) && scrollState != SCROLL_STATE_TOUCH_SCROLL &&
                !isLoadingMore) {
            if(isLoading) {
                isLoadingMore = true;
                footerView.setPadding(0, 0, 0, 0);
                setSelection(getCount());//设置最后一条数据显示 设置当前选中的条目
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onLoadMore();
                }
            }
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        if(myOnScroll!=null){
            myOnScroll.onScroll( view, firstVisibleItem,  visibleItemCount, totalItemCount);
        }
    }
    //设置刷新监听器
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mOnRefreshListener = listener;
    }
    //设置滚动监听器
    public void setMyOnScroll(MyOnScroll myOnScroll){
        this.myOnScroll=myOnScroll;
    }
    /**
     * 刷新或加载完成的调用的回调方法
     */
    public void onFinish(boolean isRefreshOrLoading) {
        if(isRefreshOrLoading) {
            if (isLoadingMore) { //加载完成
                footerView.setPadding(0, -footerViewHeight, 0, 0);
                isLoadingMore = false;
            } else {//刷新完成
                iv_header.setVisibility(View.VISIBLE);//箭头显示
                pb_header.setVisibility(INVISIBLE);//进度圈隐藏
                deleteCell(headerView);
//                new thread().start();
            }
        }
    }

    private void deleteCell(final View v) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                headerView.setPadding(0, -headerViewHright, 0, 0);
                currentState = PULL_DOWN;
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationStart(Animation animation) {}
        };
        collapse(v, al);
    }
    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    headerView.setPadding(0, -headerViewHright, 0, 0);
                    currentState = PULL_DOWN;
                }
                else {
                    int height =(int)(initialHeight * interpolatedTime);
                    headerView.setPadding(0, -height, 0, 0);
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al!=null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(300);
        v.startAnimation(anim);
    }


//    class thread extends Thread {
//        @Override
//        public void run() {
//            try {
//                int dd = (int) (headerViewHright / 10);
//                for (int i = 1; i < 11; i++) {
//                    Message message = new Message();
//                    message.obj = dd * i;
//                    message.what = 1;
//                    handler.sendMessage(message);
//                    sleep(20);
//                }
//                handler.sendEmptyMessage(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            if(msg.what==1){
//                int dd= (int) msg.obj;
//                headerView.setPadding(0, -dd, 0, 0);
//            }else{
//                headerView.setPadding(0, -headerViewHright, 0, 0);
//                currentState = PULL_DOWN;
//            }
//        }
//    };

    //刷新的回调接口
    public interface OnRefreshListener {
        //下拉刷新的回调方法
        void onRefresh();
        //加载更多地回调方法
        void onLoadMore();
    }
    //滚动回调接口
    public interface MyOnScroll{
        void onScrollStateChanged(AbsListView view,int scrollState);
        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }
    /**
     * 是否开启上滑加载
     * @param isLoading
     */
    public void loading(boolean isLoading){
        this.isLoading=isLoading;
    }
    /**
     * 是否开启刷新
     * @param Refresh
     */
    public void Refresh(boolean Refresh){
        this.isRefresh=Refresh;
    }
}
