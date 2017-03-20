package com.jueda.ndian;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jueda.ndian.activity.home.view.CommodityDetailsActivity;
import com.jueda.ndian.activity.home.view.TaskExperienceActivity;
import com.jueda.ndian.activity.circle.view.TopicDetailsActivity;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.fragment.HomeFragment;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.home.adapter.GuidepageAdapter;
import com.jueda.ndian.activity.home.biz.CommodityPopBiz;
import com.jueda.ndian.activity.home.biz.VersionsBiz;
import com.jueda.ndian.activity.home.view.TravelRouteActivity;
import com.jueda.ndian.download.DownloadManager;
import com.jueda.ndian.download.DownloadService;
import com.jueda.ndian.entity.AppEntity;
import com.jueda.ndian.entity.serverAppEntity;
import com.jueda.ndian.listener.MyConnectionStatusListener;
import com.jueda.ndian.listener.MyConversationBehaviorListener;
import com.jueda.ndian.listener.MyReceiveMessageListener;
import com.jueda.ndian.savedata.rong.DBManager;
import com.jueda.ndian.savedata.rong.Friend;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.GetDeviceId;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.view.IndexViewPager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import de.greenrobot.event.util.AsyncExecutor;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
/**主界面*/
public class MainActivity extends FragmentActivity implements View.OnClickListener ,RongIM.UserInfoProvider{
    public static final String TAG="MainActivity";
    public static MainActivity instance=null;
    private long firstTime=0;//记录按键退出时间
    public IndexViewPager MainViewPager;
    private ArrayList<Fragment> fragmentList;//界面集合
    //切换界面
    private RelativeLayout HomeRelativeLayout;
    public RadioButton HomeRadioButton;
    public RelativeLayout CharityRelativeLayout;
    public RadioButton CharityRadioButton;
    private RelativeLayout PersonalRelativeLayout;
    private RadioButton PersonalRadioButton;
    public ImageView Message;//消息
    //融云数据
    private DBManager manager;
    //下载所需
    public DownloadManager downloadManager;
    public ArrayList<AppEntity> applist=new ArrayList<>();//存储首页软件列表
    public ArrayList<AppEntity> circleAppList=new ArrayList<>();//存储圈内软件列表
    public ArrayList<serverAppEntity> apkList=new ArrayList<>();//存储或者删除当前下载包名
    public ArrayList<serverAppEntity> addList=new ArrayList<>();//存储所有下载的包名
    public ArrayList<Integer> idList=new ArrayList<>();//存储下载的顺序号
    public String vals = "";//接受卸载安装广播时做判断
    public int keys = 1024;//接受卸载安装广播时做判断 记录第几个item
    public String who="";//接受卸载安装广播是做判断
    public String  name="";//应用名称

    public final String HOME="HOME";//首页添加下载任务
    public final String CIRCLE="CIRCLE";//圈子中添加下载任务

    public HashMap<String,String> versions=null;//版本更新信息
    private PopupWindow popUpdate;//更新
    private View layoutUpdate;
    private PopupWindow popCommodity;//商品弹窗
    private View layoutCommodity;
    public int screenWidth;//屏幕宽度


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**延迟3秒弹窗*/
                case Constants.CMT_TIME_MARKSTOP:
                    /**判断是否第一次进入软件。是则弹出商品*/
                    if(!getVersion().equals(new Configuration().readaVersion(getApplicationContext()))){
                        new CommodityPopBiz(MainActivity.this,handler);
                        new Configuration().writeaVersion(getApplicationContext(), getVersion());
                    }
                    /**更新版本*/
                    new VersionsBiz(MainActivity.this,handler);
                    break;
                /**获取应用版本*/
                case Constants.ON_SUCCEED:
                    versions= (HashMap<String, String>) msg.obj;
//                    /**若开启发起筹款则显示我的发起筹款*/
//                    if( PersonalFragment.instance!=null){
//                        if(versions.get("mask").equals("0")){
//                            PersonalFragment.instance.FundraisingRelativeLayout.setVisibility(View.GONE);
//                        }else{
//                            PersonalFragment.instance.FundraisingRelativeLayout.setVisibility(View.VISIBLE);
//                        }
//                    }
                    if(Integer.parseInt(versions.get("code"))>Integer.parseInt(getVersion())){
                        Update();
                    }
                    break;
                /**商品弹窗显示*/
                case Constants.ON_SUCEED_TWO:
                    String data= (String) msg.obj;
                    Commodity(data);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance=this;
        Constants.DeviceId = GetDeviceId.GetDeviceIds(MainActivity.this);
        //获取下载数据
        downloadManager = DownloadService.getDownloadManager(getApplicationContext());

        InitView();
        isComment();
        /**倒计时3秒进入更新提示*/
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    handler.sendEmptyMessage(Constants.CMT_TIME_MARKSTOP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**商品弹窗显示*/
    private void Commodity(String data){
        if (popCommodity != null && popCommodity.isShowing()) {
            popCommodity.dismiss();
        } else {
            layoutCommodity = getLayoutInflater().inflate(
                    R.layout.pop_commodity, null);
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popCommodity = new PopupWindow(layoutCommodity, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            RelativeLayout relativeLayout=(RelativeLayout)layoutCommodity.findViewById(R.id.relativeLayout);
            RelativeLayout.LayoutParams linearParams1 = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams(); // 取控件当前的布局参数
            linearParams1.width=(screenWidth-150);// 当控件的高强制设成图片高度linearParams1.height=i/3;// 当控件的高强制设成图片高度
            linearParams1.height=(screenWidth-150);
            relativeLayout.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件imageview中
            ImageView imageView=(ImageView)layoutCommodity.findViewById(R.id.imageView);
            ImageLoaderUtil.ImageLoader(data, imageView);

            //立即抢购
            layoutCommodity.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CommodityDetailsActivity.class);
                    intent.putExtra("id","1");
                    startActivity(intent);
                    popCommodity.dismiss();
                }
            });
            //取消
            layoutCommodity.findViewById(R.id.no_update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popCommodity.dismiss();
                }
            });

            ColorDrawable cd = new ColorDrawable(-0000);
            popCommodity.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
            popCommodity.update();
            popCommodity.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popCommodity.setTouchable(true); // 设置popupwindow可点击
            popCommodity.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popCommodity.setFocusable(true); // 获取焦点
            // 设置popupwindow的位置（相对tvLeft的位置）
            popCommodity.showAtLocation(HomeRelativeLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            popCommodity.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popCommodity.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    /**检查更新版本*/
    private void Update() {
        if (popUpdate != null && popUpdate.isShowing()) {
            popUpdate.dismiss();
        } else {
            layoutUpdate = getLayoutInflater().inflate(
                    R.layout.update_dialog, null);

            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popUpdate = new PopupWindow(layoutUpdate, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            TextView new_Version = (TextView) layoutUpdate.findViewById(R.id.new_Version);//版本更新号
            TextView APP_Size = (TextView) layoutUpdate.findViewById(R.id.APP_Size);//APP大小
            TextView Content = (TextView) layoutUpdate.findViewById(R.id.Content);//更新内容
            new_Version.setText(getResources().getString(R.string.The_latest_version)+"："+versions.get("version"));
            APP_Size.setText(getResources().getString(R.string.Application_of_size) + "：" + versions.get("size"));
            Content.setText(versions.get("update_log"));
            //更新
            layoutUpdate.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new com.jueda.ndian.server.DownloadService().downNewFile(versions.get("url"), 2, "恩典365");
                    popUpdate.dismiss();
                }
            });
            //不更新
            layoutUpdate.findViewById(R.id.no_update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popUpdate.dismiss();
                    finish();
                    System.exit(0);

                }
            });

            ColorDrawable cd = new ColorDrawable(-0000);
            popUpdate.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
            popUpdate.update();
            popUpdate.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popUpdate.setTouchable(true); // 设置popupwindow可点击
            popUpdate.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popUpdate.setFocusable(false); // 获取焦点
            // 设置popupwindow的位置（相对tvLeft的位置）
            popUpdate.showAtLocation(HomeRelativeLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            popUpdate.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popUpdate.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    /**判断是不是直接跳转到评论界面*/
    private void isComment() {
        /**未运行则跳转到评论界面*/
        if(!new Configuration().readaAppRunning(MainActivity.this)){
            if(getIntent().getStringExtra("who")!=null){
                if(getIntent().getStringExtra("who").equals(MainActivity.TAG)){
                    Intent intent = new Intent(MainActivity.this, TopicDetailsActivity.class);
                    intent.putExtra("who", getIntent().getStringExtra("who"));
                    intent.putExtra("id", getIntent().getStringExtra("id"));
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(MainActivity.this, TravelRouteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("did",getIntent().getStringExtra("id"));
                    startActivity(intent);
                }
            }
        }
        new Configuration().writeaAppRunning(MainActivity.this, true);
    }

    /**
     * 融云数据加载
     */
    public void InitRong() {
        /**
         * IMKit SDK调用第二步
         * 建立与服务器的连接
         *
         */
        RongIM.connect(ConstantsUser.userEntity.getRyToken(), new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

            }

            @Override
            public void onSuccess(String userId) {
                RongIM.setOnReceiveMessageListener(new MyReceiveMessageListener());
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }
        });
        RongIM.setUserInfoProvider(MainActivity.this, true);
        //我需要让他显示的内容的数组  此处图片相机
        InputProvider.ExtendProvider[] ep = {new ImageInputProvider(RongContext.getInstance()),new CameraInputProvider(RongContext.getInstance())};
        //我需要让他在什么会话类型中的 bar 展示
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.CHATROOM, ep);
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, ep);
        //状态监听
        RongIM.getInstance().getRongIMClient().setConnectionStatusListener(new MyConnectionStatusListener());
        //消息点击监听
        RongIM.setConversationBehaviorListener(new MyConversationBehaviorListener());
    }

    private void InitView() {
        //界面按钮
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        HomeRelativeLayout=(RelativeLayout)findViewById(R.id.HomeRelativeLayout);
        HomeRadioButton=(RadioButton)findViewById(R.id.HomeRadioButton);
        CharityRelativeLayout=(RelativeLayout)findViewById(R.id.CharityRelativeLayout);
        CharityRadioButton=(RadioButton)findViewById(R.id.CharityRadioButton);
        PersonalRelativeLayout=(RelativeLayout)findViewById(R.id.PersonalRelativeLayout);
        PersonalRadioButton=(RadioButton)findViewById(R.id.PersonalRadioButton);
        Message=(ImageView)findViewById(R.id.Message);
        HomeRelativeLayout.setOnClickListener(this);
        HomeRadioButton.setOnClickListener(this);
        CharityRelativeLayout.setOnClickListener(this);
        CharityRadioButton.setOnClickListener(this);
        PersonalRelativeLayout.setOnClickListener(this);
        PersonalRadioButton.setOnClickListener(this);
        //界面切换
        fragmentList = new ArrayList<>();
        MainViewPager = (IndexViewPager) findViewById(R.id.MainViewPager);
        MainViewPager.setOffscreenPageLimit(4);
        fragmentList.add(new HomeFragment());
        fragmentList.add(new CharityCircleFragment());
//        fragmentList.add(new HaveHelpFragment());
        fragmentList.add(new PersonalFragment());
        FragmentManager manager = this.getSupportFragmentManager();
        GuidepageAdapter adapter = new GuidepageAdapter(manager, fragmentList);
        MainViewPager.setAdapter(adapter);
        Configuration configuration = new Configuration();
        configuration.writeaDownload(MainActivity.this, false);
        addTag();
    }

    /**添加标签*/
    public void addTag() {
        /**没网的话就不调用*/
        /**登录就调用*/
        new AddTagRun().start();
    }
    class AddTagRun extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            if (new Configuration().readaIsLogin(MainActivity.this)) {
                /**登录融云*/
                MainActivity.instance.InitRong();
                new LogUtil("doInBackground", "融云链接成功");
                /**设置别名*/
                new LogUtil("doInBackground","别名设置成功");
                NdianApplication.instance.mPushAgent.setExclusiveAlias(ConstantsUser.userEntity.getUid() + "", "uid");
                NdianApplication.mPushAgent.setAlias(ConstantsUser.userEntity.getUid() + "", "uid");
            } else {
                try {
                    NdianApplication.instance.mPushAgent.removeAlias(ConstantsUser.userEntity.getUid() + "", "uid");
                    new LogUtil("doInBackground","别名取消成功");
                    /**融云退出*/
                    RongIM.getInstance().logout();
                    new LogUtil("doInBackground", "融云退出成功");
                } catch (Exception e) {
                    new LogUtil("doInBackground",e.toString());
                    e.printStackTrace();
                }
            }
            Looper.loop();
        }
    }

    /***
     * 获取屏幕宽度
     * @return
     */
    public int getScreenWidth(){
        if(screenWidth==0)
            screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        return screenWidth;
    }
    /**
     * 切换界面
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            //任务
            case R.id.HomeRelativeLayout:
                selectButton(HomeRadioButton);
                MainViewPager.setCurrentItem(0,false);
                break;
            case R.id.HomeRadioButton:
                selectButton(HomeRadioButton);
                MainViewPager.setCurrentItem(0,false);
                break;
            //慈善圈
            case R.id.CharityRelativeLayout:
                selectButton(CharityRadioButton);
                MainViewPager.setCurrentItem(1,false);
                break;
            case R.id.CharityRadioButton:
                selectButton(CharityRadioButton);
                MainViewPager.setCurrentItem(1,false);
                break;

            //我
            case R.id.PersonalRelativeLayout:
                    selectButton(PersonalRadioButton);
                    MainViewPager.setCurrentItem(2, false);
                break;
            case R.id.PersonalRadioButton:
                    selectButton(PersonalRadioButton);
                    MainViewPager.setCurrentItem(2, false);
                break;
            default:
                break;
        }
    }
    /**
     * 判断是否和上次选中的一样
     * @param button
     */
    public void selectButton(RadioButton button){
        if(HomeRadioButton.getId()!=button.getId()){
            HomeRadioButton.setChecked(false);
        }else{
            HomeRadioButton.setChecked(true);
        }
        if(CharityRadioButton.getId()!=button.getId()){
            CharityRadioButton.setChecked(false);
        }else{
            CharityRadioButton.setChecked(true);
        }
        if(PersonalRadioButton.getId()!=button.getId()){
            PersonalRadioButton.setChecked(false);
        }else{
            PersonalRadioButton.setChecked(true);
        }

    }


    /**获取用户信息*/
    @Override
    public UserInfo getUserInfo(String s) {
        //获取用户数据
        manager=new DBManager(getApplicationContext());
        ArrayList<Friend> l=(ArrayList) manager.query();
        for (Friend i : l) {
            if (i.getUserId().equals(s)) {
                return new UserInfo(i.getUserId(),i.getUserName(), Uri.parse(i.getPortraitUri()));
            }
        }
        return null;
    }


    @Override
    public void finish() {
        /**下载界面退出退出*/
        Configuration configuration=new Configuration();
        configuration.writeaDownload(getApplicationContext(), true);
        /**记录应用退出*/
        new Configuration().writeaAppRunning(MainActivity.this,false);
        super.finish();
    }

    /**
     * 按两次退出
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {//如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {
                    //融云退出
                    RongIM.getInstance().disconnect(true);
                    //两次按键小于2秒时，退出应用
                    finish();
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
    /**
     * 获取状态栏高度
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    /**
     * 表示已安装
     */
    public void Installation(int key,String who){
        if(who.equals(MainActivity.instance.HOME)){
            applist.get(key).setState(1);

        }else if(who.equals(MainActivity.instance.CIRCLE)){
            circleAppList.get(key).setState(1);
        }
        if( TaskExperienceActivity.instance!=null) {
            TaskExperienceActivity.instance.Installation(key);
        }
    }
    /**
     * 表示卸载
     */
    public void Uninstall(int key,String who){
        try{
            if(who.equals(MainActivity.instance.HOME)){
                if(applist.get(key).getState()==1){
                    applist.get(key).setState(2);
                }else {
                    applist.get(key).setState(2);
                }
            }else if(who.equals(MainActivity.instance.CIRCLE)){
                if(circleAppList.get(key).getState()==1){
                    circleAppList.get(key).setState(2);
                }else {
                    circleAppList.get(key).setState(2);
                }
            }
            if( TaskExperienceActivity.instance!=null){
                TaskExperienceActivity.instance.Uninstall(key);
            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        //融云统计
        MobclickAgent.onResume(getApplicationContext());
    }
    @Override
    public void onPause() {
        super.onPause();
        //融云统计
        MobclickAgent.onPause(getApplicationContext());
    }
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionCode+"";
            return  version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
