package com.jueda.ndian;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jueda.ndian.activity.circle.view.TopicDetailsActivity;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.fragment.TaskEx_Fragment;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.home.view.TravelRouteActivity;
import com.jueda.ndian.entity.MessageEntity;
import com.jueda.ndian.listener.MyLocationListener;
import com.jueda.ndian.listener.MyReceivePushMessageListener;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.savedata.MessageData;
import com.jueda.ndian.server.DownloadService;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.utils.ToastShow;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Administrator on 2016/3/3.
 */
public class NdianApplication extends Application{
    public static NdianApplication instance=null;
    public static PushAgent mPushAgent;//友盟推送
    public boolean isFist=true;//判断是否是第一次注册信息

    //百度地图
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    public IWXAPI api;

    @Override
    public void onCreate() {
        super.onCreate();
        /***
         * 百度地图
         */
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        /**
         * 初始化融云
         */
        RongIM.init(this);
        //网络状态
        if(!isNetworkAvailable()){
            isFist=true;
            Constants.currentNetworkType=Constants.TYPE_NONE;
            new ToastShow(getApplicationContext(),getResources().getString(R.string.No_network_please_check_the_network1),1000);
        }else{
            isFist=false;
            /***
             * 友盟推送
             */
            OnStartUmeng();
        }
        //微信 appid appsecret
        api = WXAPIFactory.createWXAPI(getApplicationContext(), Constants.WX_APP_ID, false);
        api.registerApp(Constants.WX_APP_ID);
        PlatformConfig.setWeixin(Constants.WX_APP_ID, Constants.WX_APP_SECRET);
        //新浪微博 appkey appsecret
        PlatformConfig.setSinaWeibo("3240312102", "f45fdb35be0d5e62fdf6646cbc15c5d7");
        // QQ和Qzone appid appkey
        PlatformConfig.setQQZone(Constants.APP_ID, "ffsi2yWb5Ivyf06N");

        instance=this;

        /**
         * 初始化图片加载器
         */
         ImageLoaderUtil.Init(getApplicationContext());

        /**
         * 初始化网络请求框架
         */
        NoHttp.init(this);
        Logger.setDebug(true);// 开始NoHttp的调试模式, 这样就能看到请求过程和日志

        /***
         * 获取本地用户信息
         */
        com.jueda.ndian.savedata.DBManager dbManager=new com.jueda.ndian.savedata.DBManager(getApplicationContext());
        ConstantsUser.userEntity=dbManager.query();

        /***
         * 启动下载更新APP服务
         */
        Intent intent = new Intent(this,DownloadService.class);
        startService(intent);
    }

    /**
     * 初始化
     */
    public void OnStartUmeng() {
        if(!isFist){
            /***
             * 友盟推送
             */
            mPushAgent = PushAgent.getInstance(getApplicationContext());
            mPushAgent.setDebugMode(false);
            mPushAgent.enable();

            /**
             * 融云设置接收 push 消息的监听器。
             */
            RongIM.getInstance().getRongIMClient().setOnReceivePushMessageListener(new MyReceivePushMessageListener());
            //监听消息发送过来后的信息
            UmengMessageHandler handler = new UmengMessageHandler() {
                @Override
                public void dealWithCustomMessage(Context context, UMessage msg) {
                    try {
                        NotificationCompat.Builder goApp_builder = new NotificationCompat.Builder(context);
                        new LogUtil("推送",msg.custom);
                        final JSONObject object = new JSONObject(msg.custom);
                        String type = object.getString("type");
                        /**用户登录的情况下才能推送消息*/
                        if (new Configuration().readaIsLogin(context)) {
                            /**进入话题评论/点赞/旅游路线评论*/
                            if (type.equals("1") ||type.equals("15")||type.equals("2")) {
                                /**把评论推送消息添加到消息提醒中*/
                                ArrayList<MessageEntity> messageArrayList=new MessageData().readaMessageData(context);
                                MessageEntity entity=new MessageEntity();
                                entity.setUserId(ConstantsUser.userEntity.getUid() + "");
                                //点赞消息
                                if(type.equals("15")){
                                    goApp_builder .setContentText(object.getString("desc"));
                                    entity.setOtherMessage("1");
                                }else{
                                    goApp_builder .setContentText("评论：" + object.getString("content"));
                                    entity.setCommentMessage("1");
                                }
                                messageArrayList.add(entity);
                                new MessageData().writeaMessageData(context,messageArrayList);
                                if(PersonalFragment.instance!=null){
                                    PersonalFragment.instance.setUser();
                                }

                                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                goApp_builder.setContentTitle(object.getString("title"))//设置通知栏标题
                                        .setTicker(object.getString("desc")) //通知首次出现在通知栏，带上升动画效果的
//                                        .setNumber(1) //设置通知集合的数量
                                        .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                                        .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                                        .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                                        .setSmallIcon(R.mipmap.n365small)
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.n365))
                                        .setOngoing(false);//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)

                                if (type.equals("1")||type.equals("15")) {
                                        if (new Configuration().readaAppRunning(getApplicationContext())) {
                                            Intent intent = new Intent(context, TopicDetailsActivity.class);
                                            intent.putExtra("who", MainActivity.TAG);
                                            intent.putExtra("id", object.getString("pid"));
                                            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            goApp_builder.setContentIntent(pendingIntent);
                                        } else {
                                            Intent intent = new Intent(context,  com.jueda.ndian.MainActivity.class);
                                            intent.putExtra("who", MainActivity.TAG);
                                            intent.putExtra("id", object.getString("pid"));
                                            intent.setAction(Intent.ACTION_MAIN);
                                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                            goApp_builder.setContentIntent(pendingIntent);
                                        }
                                }else{
                                    if (new Configuration().readaAppRunning(getApplicationContext())) {
                                        Intent intent = new Intent(context, TravelRouteActivity.class);
                                        intent.putExtra("did", object.getString("pid"));
                                        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        goApp_builder.setContentIntent(pendingIntent);
                                    } else {
                                        Intent intent = new Intent(context,  com.jueda.ndian.MainActivity.class);
                                        intent.putExtra("who", MainActivity.TAG);
                                        intent.putExtra("id", object.getString("pid"));
                                        intent.setAction(Intent.ACTION_MAIN);
                                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                        goApp_builder.setContentIntent(pendingIntent);
                                    }
                                }

                                Notification mNotification = goApp_builder.build();
                                //设定Notification出现时的声音
                                if (new Configuration().readaSound(context)) {
                                    //声音提醒打开
                                    mNotification.defaults |= Notification.DEFAULT_SOUND;
                                }
                                if (new Configuration().readaVibration(context)) {
                                    //震动打开
                                    //设定如何振动
                                    mNotification.defaults |= Notification.DEFAULT_VIBRATE;
                                }
                                //d)LED 灯闪烁
                                mNotification.defaults |= Notification.DEFAULT_LIGHTS;
                                /**打开评论提醒就显示*/
                                if (new Configuration().readaComment(context)) {
                                    if(type.equals("15")){
                                        manager.notify(2, mNotification);
                                    }else{
                                        manager.notify(1, mNotification);
                                    }
                                }


                            }else if (type.equals("4")) {
                                /**把评论推送消息添加到消息提醒中*/
                                /**移除圈*/
                                ArrayList<MessageEntity> messageArrayList=new MessageData().readaMessageData(context);
                                MessageEntity entity=new MessageEntity();
                                entity.setUserId(ConstantsUser.userEntity.getUid() + "");
                                entity.setOtherMessage("1");
                                messageArrayList.add(entity);
                                new MessageData().writeaMessageData(context, messageArrayList);
                                if(PersonalFragment.instance!=null){
                                    PersonalFragment.instance.setUser();
                                }
                                /**被T出圈*/
                                if (CharityCircleFragment.instance != null) {
                                    CharityCircleFragment.instance.Reconnection();
                                }
                            }else if (type.equals("7")) {
//                                /**用户被迫下线*/
//                                if (new Configuration().readaIsLogin(context)) {
//                                    new Configuration().writeaIsLogin(context, false);
//                                    Constants.isOut = true;
//                                    CharityCircleFragment1.instance.Reconnection();
//                                    Intent intent = new Intent(context, LoginActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    context.startActivity(intent);
//                                }
                            } else if (type.equals("8")) {
//                                /**同意加圈,刷新数据*/
//                                if (CharityCircleFragment.instance != null) {
//                                    CharityCircleFragment.instance.Reconnection();
//                                }
                            } else if(type.equals("9")){
                                /**通过邀请码邀请人进入增加奉献值*/
                                if(PersonalFragment.instance!=null){
                                    PersonalFragment.instance.updata();
                                }
                            }else if(type.equals("10")){
                                    /**用户新加入圈子*/
                               if(new Configuration().readaAppRunning(getApplicationContext())) {
                                  RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                                     @Override
                                     public UserInfo getUserInfo(String userId) {
                                          try {
                                             return new UserInfo(object.getString("uid"), object.getString("uname"), Uri.parse(object.getString("avatar")));//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
                                           } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                         return null;
                                      }
                                  }, true);
                               }
                            }else if(type.equals("12")){
                                /**审核中任务*/
                                ArrayList<MessageEntity> messageArrayList=new MessageData().readaMessageData(context);
                                MessageEntity entity=new MessageEntity();
                                entity.setUserId(ConstantsUser.userEntity.getUid() + "");
                                entity.setTaskAudit("1");
                                messageArrayList.add(entity);
                                new MessageData().writeaMessageData(context, messageArrayList);
                                if(PersonalFragment.instance!=null){
                                    PersonalFragment.instance.messagePoint();
                                }
                            }else if(type.equals("13")){
                                /**已完成任务*/
                                ArrayList<MessageEntity> messageArrayList=new MessageData().readaMessageData(context);
                                MessageEntity entity=new MessageEntity();
                                entity.setUserId(ConstantsUser.userEntity.getUid() + "");
                                entity.setTaskCompleted("1");
                                messageArrayList.add(entity);
                                new MessageData().writeaMessageData(context, messageArrayList);
                                if(PersonalFragment.instance!=null){
                                    PersonalFragment.instance.messagePoint();
                                    PersonalFragment.instance.updata();
                                }
                                if( TaskEx_Fragment.instance!=null){
                                    TaskEx_Fragment.instance.Reconnection();
                                }
                            }else if(type.equals("14")){
                                /**未完成任务*/
                                ArrayList<MessageEntity> messageArrayList=new MessageData().readaMessageData(context);
                                MessageEntity entity=new MessageEntity();
                                entity.setUserId(ConstantsUser.userEntity.getUid() + "");
                                entity.setTaskUnfinished("1");
                                messageArrayList.add(entity);
                                new MessageData().writeaMessageData(context, messageArrayList);
                                if(PersonalFragment.instance!=null){
                                    PersonalFragment.instance.messagePoint();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    super.dealWithCustomMessage(context, msg);
                }

                @Override
                public void handleMessage(Context context, UMessage uMessage) {
                    new LogUtil("UmengMessageHandler", uMessage.custom);
                    super.handleMessage(context, uMessage);
                }
            };
            NdianApplication.mPushAgent.setMessageHandler(handler);
            isFist=false;
        }
    }
    /**
     * 设置标题栏
     * @param context
     * @param title 标题
     * @param isShowBack 是否显示返回键
     */
    public void setTitle(final Activity activity,String title,boolean isShowBack){
        View include=(View)activity.findViewById(R.id.include);
        TextView Title=(TextView)include.findViewById(R.id.title);
        Button back=(Button)include.findViewById(R.id.back);
        Title.setText(title);
        if(isShowBack){
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        }else{
            back.setVisibility(View.GONE);
        }
    }
    /**
     * 设置标题栏
     * @param context
     * @param title 标题
     * @param isShowBack 是否显示返回键
     */
    public void setTitle(final View activity,String title,boolean isShowBack){
        View include=(View)activity.findViewById(R.id.include);
        TextView Title=(TextView)include.findViewById(R.id.title);
        Button back=(Button)include.findViewById(R.id.back);
        Title.setText(title);
        back.setVisibility(View.GONE);
    }

    /**
     * 连接失败或者没有网络时显示
     * @param title
     */
    public static void connectionFail(String title,View include){
        TextView Title=(TextView)include.findViewById(R.id.again);
        Title.setText(title);
    }
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha,Activity context)
    {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }
    /**
     * 检查当前网络是否可用
     *
     * @return
     */
    public boolean isNetworkAvailable()
    {
        Context context = getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        }else{
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    /***
     * 百度地图
     */
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }
}
