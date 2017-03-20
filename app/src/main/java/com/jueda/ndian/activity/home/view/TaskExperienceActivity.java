package com.jueda.ndian.activity.home.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.view.PreviewActivity;
import com.jueda.ndian.activity.fragment.TaskEx_Fragment;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.home.adapter.ReleaseTopicAdapter;
import com.jueda.ndian.activity.home.biz.CompleteTaskBiz;

import com.jueda.ndian.activity.home.biz.TaskExperienceBiz;
import com.jueda.ndian.activity.home.biz.uploadImageBiz;
import com.jueda.ndian.download.BaseDownloadHolder;
import com.jueda.ndian.download.DownloadInfo;
import com.jueda.ndian.download.DownloadManager;
import com.jueda.ndian.download.DownloadRequestCallBack;
import com.jueda.ndian.entity.AppEntity;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.entity.serverAppEntity;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageAddress;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.Image_look_preview;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.ToastShow;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * 任务下载界面
 */
public class TaskExperienceActivity extends Activity {
    public static TaskExperienceActivity instance=null;

    private ProgressBar progressBar;//进度条
    private Button button;//点击下载
    private TextView baifenbi;//显示百分比.
    private String apkName="";
    private String who="";//判断是首页还是圈子任务
    private String OlderApkName="";//之前下载过的包名、为空则没有下载
    private File olderFile;
    private boolean isInstall=false;//是否安装
    private holdler holders = null;
    private AppEntity appentity=new AppEntity();//任务信息
    private int id=1024;//当前下载队列id
    private TextView titles;//标题
    private TextView rule;//任务
    /**加载请求数据**/
    private ScrollView scrollView;
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示
    //示例图
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout menuLinerLayout;//头部导航

    //*********图片选择上传***********//
    private RelativeLayout relativeLayout;//底部
    private int screenWidth;//屏幕宽度
    private PopupWindow popPricture;//照片
    private View layoutPricture;
    private WaitDialog dialog;//弹窗
    public static ArrayList<String> imagelist=new ArrayList<>();//多张图片上传
    public static ArrayList<Photo> mList;
    private GridView gridview;
    private ReleaseTopicAdapter mAdapter;
    public static final int CLICK_ADD=1;//点击添加按钮
    public static final int CLICK_LOOK=2;//点击查看
    private TextView NickNameTextView;//爱心豆
    private TextView submit;//提交
    private PopupWindow popSubmit;
    private View layoutSubmit;//提交成功
    private PopupWindow popCancle;
    private View layoutCancle;//提交成功
    private boolean isComplete=false;//是否完成
    private String fileName;

    Handler hanlder=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //获取任务成功
                case Constants.ON_SUCEED_TWO:
                    animation.stopAnim();
                    scrollView.setVisibility(View.VISIBLE);
                    InitData();
                    break;
                //获取失败
                case Constants.FAILURE_THREE:
                    scrollView.setVisibility(View.GONE);
                    animation.stopAnim();
                    NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    include.setVisibility(View.VISIBLE);
                    break;
                //点击事件
                case CLICK_ADD:
                    if(Constants.checkList.size()>=Constants.MAX_SIZE){
                        new ToastShow(TaskExperienceActivity.this,getResources().getString(R.string.Can_only_choose_sheets)+Constants.MAX_SIZE+"张",1000);
                    }else{
                        add();
                    }
                    break;
                /**查看图片*/
                case CLICK_LOOK:
                    startActivityForResult(new Intent(TaskExperienceActivity.this, PreviewActivity.class), 10);
                    break;
                /**图片上传成功*/
                case Constants.ON_SUCCEED_IMAGE:
                    String image[]= (String[]) msg.obj;
                    new CompleteTaskBiz(TaskExperienceActivity.this,hanlder,appentity.getCid(),appentity.getAid(),image);
                    break;
                /**图片上传失败*/
                case Constants.FAILURE_IMAGE:
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    new ToastShow(TaskExperienceActivity.this,getResources().getString(R.string.Image_upload_failure),1000);
                    break;
                /**上传成功*/
                case Constants.ON_SUCCEED:
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    isComplete=true;
                    completeTask();
                    //圈内圈外都刷新界面请求数据
                    if(TaskEx_Fragment.instance!=null){
                        TaskEx_Fragment.instance.Reconnection();
                    }
                    break;
                /**上传失败*/
                case Constants.FAILURE:
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    new ToastShow(TaskExperienceActivity.this,getResources().getString(R.string.Image_upload_failure),1000);
                    break;
                /**重复提交*/
                case Constants.FAILURE_TWO:
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    isComplete=true;
                    new ToastShow(TaskExperienceActivity.this,"已提交过任务，请耐心等候...",1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(TaskExperienceActivity.this,false);
                    Intent intent = new Intent(TaskExperienceActivity.this, TaskExperienceActivity.class);
                    startActivity(intent);
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
        new ChangeTitle(this);
        setContentView(R.layout.activity_task);
        InitView();
        setDataPhoto();
        setOnClick();
    }
    private void InitView() {
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        appentity= (AppEntity) getIntent().getSerializableExtra("entity");
        who=getIntent().getStringExtra("who");
        NdianApplication.instance.setTitle(this, "进行任务", true);
        Constants.checkList.clear();
        Constants.MAX_SIZE=6;//设置最多选择6张图片
        gridview = (GridView) findViewById(R.id.gridview);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        submit=(TextView)findViewById(R.id.submit);
        horizontalScrollView=(HorizontalScrollView)findViewById(R.id.horizontalScrollView);
        menuLinerLayout = (LinearLayout) findViewById(R.id.linearLayoutMenu);
        //任务下载
        NickNameTextView=(TextView)findViewById(R.id.NickNameTextView);
        rule=(TextView)findViewById(R.id.rule);
        titles=(TextView)findViewById(R.id.titles);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        button=(Button)findViewById(R.id.button);
        baifenbi=(TextView) findViewById(R.id.baifenbi);
        /**初始化数据时界面显示*/
        scrollView=(ScrollView)findViewById(R.id.scrollView);
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);
        Reconnection();

    }

    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        if(who.equals(MainActivity.instance.HOME)){
            MainActivity.instance.applist.clear();
        }else{
            MainActivity.instance.circleAppList.clear();
        }

        scrollView.setVisibility(View.GONE);
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            include.setVisibility(View.GONE);
            animation.startAnim();
            new TaskExperienceBiz(TaskExperienceActivity.this,who,hanlder,appentity.getAid(),appentity.getCid());
        }else{
            new ToastShow(TaskExperienceActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }
    /**
     * 初始化数据
     */
    public void InitData(){
        appentity=MainActivity.instance.applist.get(0);
        //添加示例图
        // 参数设置
        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                Constants.dip2px(getApplicationContext(), 100),
                Constants.dip2px(getApplicationContext(), 150)
        );
        final ArrayList<Photo> photoArrayList=new ArrayList<>();
        for(int i=0;i<appentity.getSample_image().length;i++){
            Photo photo=new Photo();
            photo.path=appentity.getSample_image()[i];
            photoArrayList.add(photo);
        }
        // 添加imageview控件
        for(int i = 0;i < photoArrayList.size();i++) {
            final ImageView tvMenu = new ImageView(TaskExperienceActivity.this);
            ImageLoaderUtil.ImageLoader(photoArrayList.get(i).path, tvMenu);
            tvMenu.setScaleType(ImageView.ScaleType.CENTER_CROP);
            menuLinerLayoutParames.setMargins(0, 0, Constants.dip2px(TaskExperienceActivity.this, 10), 0);
            menuLinerLayout.addView(tvMenu,menuLinerLayoutParames);
            final int finalI = i;
            tvMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Image_look_preview.look(TaskExperienceActivity.this, finalI, photoArrayList);
                }
            });
        }
        //设置任务
        NickNameTextView.setText( appentity.getReward());
        rule.setText(appentity.getRule());
        titles.setText(appentity.getName());
        apkName=appentity.getPackageName();
        if(appentity.getDownloadUrl().equals("")){
            button.setVisibility(View.GONE);
        }else{
            B:if(appentity.getState()==1){
                button.setText("打开");
            }else {
                for(int i=0;i<MainActivity.instance.addList.size();i++){
                    if(MainActivity.instance.addList.get(i).getName().equals(apkName)){
                        OlderApkName=apkName;
                        olderFile=MainActivity.instance.addList.get(i).getFile();
                        if (isAvilible(getApplicationContext(), apkName)) {
                            button.setText("打开");
                            appentity.setState(1);
                        }else{
                            button.setText("安装");
                        }
                        break B;
                    }else{
                        button.setText("点击下载");
                    }
                }
            }
        }
    }
    @Override
    public void finish() {
        if(isComplete){
            super.finish();
        }else{
            finishs();
        }
    }

    /**
     * 图片设置setdata
     */
    private void setDataPhoto() {
        mList = new ArrayList<Photo>();
        //添加点击跳转选择图片的虚拟数据
        Photo p=new Photo();
        mList.add(p);
        /**设置最大显示图片，然后隐藏添加图片按钮*/
        mAdapter = new ReleaseTopicAdapter(this, mList, hanlder, screenWidth,6);
        gridview.setAdapter(mAdapter);

        /***
         * 提交任务
         */
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTask();

            }
        });

    }
    public void submitTask(){
        if(new Configuration().readaIsLogin(getApplicationContext())){
           if (appentity.getState() == 1||appentity.getDownloadUrl().equals("")) {
                if (Constants.checkList.size() == 0) {
                    new ToastShow(TaskExperienceActivity.this, getResources().getString(R.string.Upload_a_picture_at_least), 1000);
                } else {
                    dialog = new WaitDialog(TaskExperienceActivity.this);
                    new uploadImageBiz(TaskExperienceActivity.this, hanlder, imagelist, false, dialog);
                }
            } else {
                new ToastShow(TaskExperienceActivity.this, getResources().getString(R.string.Please_download_and_complete_the_task_as_required), 1000);
            }
        }else{
            startActivity(new Intent(TaskExperienceActivity.this,LoginActivity.class));
        }
    }
    /**
     * 返回时图片获取
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(Activity.RESULT_CANCELED!=resultCode){
            //获取拍照后的相片
            if (requestCode == Constants.TAKING_PICTURES) {
                Bitmap bitmap= BitmapFactory.decodeFile(ImageAddress.ImageAddress(fileName));
                //把bitmap转化成uri
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                Cursor cursor = this.getContentResolver().query(uri, null,
                        null, null, null);
                if (cursor.moveToFirst()) {
                    String videoPath = cursor.getString(cursor
                            .getColumnIndex("_data"));// 获取绝对路径
                    Photo photo = new Photo();
                    photo.path = videoPath;
                    mList.add(0, photo);
                    //添加到全局中
                    Constants.checkList.add(0, photo);
                }
                imagelist.clear();
                mAdapter.notifyDataSetChanged();
            }
            if(data!=null){
                //相册选择的图片
                if (requestCode == 10) {
                    if(Constants.IS_CANCEL==1){
                        ArrayList<Photo> list = data.getParcelableArrayListExtra(Constants.RES_PHOTO_LIST);
                        mList.clear();
                        if (list != null) {
                            mList.addAll(list);
                            Photo photo1 = new Photo();
                            mList.add(photo1);

                        }
                    }else if(Constants.IS_CANCEL==2){
                        Constants.checkList.clear();
                        Constants.checkList.addAll(mList);
                        Constants.checkList.remove(Constants.checkList.size() - 1);
                    }
                    imagelist.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //添加图片
    public void add(){
        new KeyboardManage().CloseKeyboard(gridview, TaskExperienceActivity.this);
        if (popPricture != null && popPricture.isShowing()) {
            popPricture.dismiss();
        } else {
            layoutPricture=getLayoutInflater().inflate(
                    R.layout.register_user_dialog, null);

            //拍照
            Button button1 = (Button) layoutPricture.findViewById(R.id.photograph);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //调用相机拍照
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileName=System.currentTimeMillis()+"";
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(ImageAddress.ImageAddress(fileName))));
                    startActivityForResult(intent, Constants.TAKING_PICTURES);
                    popPricture.dismiss();

                }
            });
            //相册
            Button button2 = (Button) layoutPricture.findViewById(R.id.album);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TaskExperienceActivity.this, ImageDirActivity.class);
//                                      intent.putExtra(Constan.ARG_PHOTO_LIST, mList);
                    startActivityForResult(intent, 10);
                    popPricture.dismiss();

                }
            });
            //取消
            Button button3 = (Button) layoutPricture.findViewById(R.id.cancel);
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPricture.dismiss();
                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popPricture = new PopupWindow(layoutPricture, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popPricture.setBackgroundDrawable(cd);
//            popPricture.setAnimationStyle(R.style.PopupAnimationSex);
            popPricture.update();
            popPricture.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popPricture.setTouchable(true); // 设置popupwindow可点击
            popPricture.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popPricture.setFocusable(true); // 获取焦点
            NdianApplication.instance.backgroundAlpha((float) 0.4,TaskExperienceActivity.this);
            // 设置popupwindow的位置（相对tvLeft的位置）
            popPricture.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popPricture.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1,TaskExperienceActivity.this);
                }
            });
            popPricture.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popPricture.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    /**下载任务按钮**/
    private void setOnClick() {
        //重连
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reconnection();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否登录
                if (new Configuration().readaIsLogin(getApplicationContext())) {
                    if (isAvilible(getApplicationContext(), apkName)) {
                        appentity.setState(1);
                        button.setText("打开");
                        //打开软件
                        PackageManager packageManager = getApplicationContext().getPackageManager();
                        Intent intent = new Intent();
                        intent = packageManager.getLaunchIntentForPackage(apkName);
                        startActivity(intent);
                    } else {
                        down();
                    }
                } else {
                    //跳转到登录界面
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void down(){
        if (appentity.getState() == 1) {
            if (isAvilible(getApplicationContext(), apkName)) {
                //打开软件
                PackageManager packageManager = getApplicationContext().getPackageManager();
                Intent intent = new Intent();
                intent = packageManager.getLaunchIntentForPackage(apkName);
                startActivity(intent);
            } else {
                IsInstall();
            }
        } else {
            IsInstall();
        }
    }

    /**
     * 有包则直接安装，没有则下载
     */
    public void IsInstall(){
        if (OlderApkName.equals(apkName)) {
            //安装
            Instanll(olderFile, getApplicationContext());
        } else {
            //统计点击下载事件次数
            HashMap<String, String> map = new HashMap<>();
            map.put("type", "download");
            map.put("quantity", appentity.getName());
            MobclickAgent.onEvent(getApplicationContext(), "download", map);
            try {
                button.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                MainActivity.instance.downloadManager.addNewDownload(appentity.getDownloadUrl(), appentity.getName(), "/ndian/apk/" + System.currentTimeMillis() +apkName+ ".apk", true, false, new DownloadRequestCallBack());
                baifenbi.setText(getResources().getString(R.string.In_the_download));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.To_download), Toast.LENGTH_SHORT).show();

                progressBar.setClickable(false);
                if (id == 1024) {
                    id = MainActivity.instance.idList.size();
                    MainActivity.instance.idList.add(id);
                }
                setData();
            } catch (com.lidroid.xutils.exception.DbException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 设置下载内容
     */
    private void setData() {
        try {
            DownloadInfo downloadInfo = MainActivity.instance.downloadManager.getDownloadInfo(id);
            holders=new holdler(downloadInfo);
            ViewUtils.inject(holders, TaskExperienceActivity.this);
            HttpHandler<File> handler = downloadInfo.getHandler();
            if(handler != null){
                DownloadManager.ManagerCallBack requestCallBack = (DownloadManager.ManagerCallBack) handler.getRequestCallBack();
                if(requestCallBack.getBaseCallBack() == null){
                    requestCallBack.setBaseCallBack(new DownloadRequestCallBack());
                }
                requestCallBack.setUserTag(new WeakReference<holdler>(holders));
            }
            holders.refresh();
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

    }
    /**
     * 记录当前fragment,便于管理
     */
    public class holdler extends BaseDownloadHolder {
        @ViewInject(R.id.progressBar)
        ProgressBar progressBar;

        public holdler(DownloadInfo downloadInfo) {
            super(downloadInfo);

        }
        /**
         * 刷新按钮显示效果
         */
        @Override
        public void refresh() {
            if (downloadInfo.getFileLength() > 0) {
                Log.i("----", downloadInfo.getProgress() * 100 / downloadInfo.getFileLength() + "");
                baifenbi.setText(downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()+"%");
                progressBar.setProgress((int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
            } else {
                progressBar.setProgress(0);
            }
            HttpHandler.State state = downloadInfo.getState();
            switch (state) {
                case WAITING:
//                    stopBtn.setText("暂停");
                    break;
                case STARTED:
//                    stopBtn.setText("暂停");
                    break;
                case LOADING:
//                    stopBtn.setText("暂停");
                    break;
                case CANCELLED:
//                    stopBtn.setText("继续");
                    break;
                case SUCCESS:
                    progressBar.setClickable(true);
                    progressBar.setProgress(100);
//                    stopBtn.setVisibility(View.INVISIBLE);
                    baifenbi.setText(getResources().getString(R.string.The_installation));
                    isInstall=false;
                    //安装软件
                    serverAppEntity entity =new serverAppEntity();
                    File file=new File(downloadInfo.getFileSavePath());
                    entity.setFile(file);
                    entity.setName(apkName);
                    if(who.equals(MainActivity.instance.HOME)){
                        entity.setWho(MainActivity.instance.HOME);
                    }else if(who.equals(MainActivity.instance.CIRCLE)){
                        entity.setWho(MainActivity.instance.CIRCLE);
                    }
                    entity.setKey(appentity.getKey());
                    MainActivity.instance.apkList.add(entity);
                    MainActivity.instance.addList.add(entity);
                    Instanll(file, getApplicationContext());
                    break;
                case FAILURE:
//                    stopBtn.setText("重试");
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.Download_failed_to_download),Toast.LENGTH_SHORT).show();
                    progressBar.setClickable(true);
                    baifenbi.setText(getResources().getString(R.string.Continue_to_download));
                    break;
                default:
                    break;
            }
        }

        /**
         * 下载进度条点击事件
         * @param view
         * @throws DbException
         */
        @OnClick(R.id.progressBar)
        public void stop(View view) throws DbException {
            HttpHandler.State state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                case STARTED:
                case LOADING:
//                    try {
//                        MainActivity.instance.downloadManager.stopDownload(downloadInfo);
//                        Toast.makeText(getActivity(),"暂停",Toast.LENGTH_SHORT).show();
//                    } catch (DbException e) {
//                        LogUtils.e(e.getMessage(), e);
//                    }
                    break;
                case CANCELLED:
                    break;
                case FAILURE:
                    try {
                        MainActivity.instance.downloadManager.resumeDownload(downloadInfo, new DownloadRequestCallBack());
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.start),Toast.LENGTH_SHORT).show();
                        progressBar.setClickable(false);
                    } catch (DbException e) {
                        LogUtils.e(e.getMessage(), e);
                    }
                    HttpHandler<File> handler = downloadInfo.getHandler();
                    if(handler != null){
                        DownloadManager.ManagerCallBack requestCallBack = (DownloadManager.ManagerCallBack) handler.getRequestCallBack();
                        if(requestCallBack.getBaseCallBack() == null){
                            requestCallBack.setBaseCallBack(new DownloadRequestCallBack());
                        }
                        requestCallBack.setUserTag(new WeakReference<holdler>(holders));
                    }
                    break;
                case SUCCESS:
                    if(isInstall){
                        //打开软件
                        PackageManager packageManager = getApplicationContext().getPackageManager();
                        Intent intent=new Intent();
                        intent = packageManager.getLaunchIntentForPackage(apkName);
                        startActivity(intent);
                    }else{
                        //安装
                        //判断apkList中有没有现在的安装包信息，有则跳过，没有则添加进去
                        A:if(MainActivity.instance.apkList.size()>0){
                            for(int i=0;i<MainActivity.instance.apkList.size();i++){
                                serverAppEntity entity;
                                entity=MainActivity.instance.apkList.get(i);

                                Object val = entity.getName();
                                if (apkName.equals((String) val)) {
                                    break A;
                                }
                            }
                            serverAppEntity entity =new serverAppEntity();
                            File file=new File(downloadInfo.getFileSavePath());
                            entity.setFile(file);
                            entity.setName(apkName);
                            if(who.equals(MainActivity.instance.HOME)){
                                entity.setWho(MainActivity.instance.HOME);
                            }else if(who.equals(MainActivity.instance.CIRCLE)){
                                entity.setWho(MainActivity.instance.CIRCLE);
                            }
                            entity.setKey(appentity.getKey());
                            MainActivity.instance.apkList.add(entity);
                        }else{
                            serverAppEntity entity =new serverAppEntity();
                            File file=new File(downloadInfo.getFileSavePath());
                            entity.setFile(file);
                            entity.setName(apkName);
                            entity.setKey(appentity.getKey());
                            MainActivity.instance.apkList.add(entity);
                        }
                        File file=new File(downloadInfo.getFileSavePath());
                        Instanll(file, getApplicationContext());
                    }
                    break;
                default:
                    break;
            }
        }
    }
    /**
     * 判断是否安装
     * @param context
     * @param packageName
     * @return
     */
    private boolean isAvilible(Context context, String packageName){
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if(pinfo != null){
            for(int i = 0; i < pinfo.size(); i++){
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }
    // 安装下载后的apk文件
    public void Instanll(File file, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        context.startActivity(intent);
    }
    /**
     * 返回界面时检查有没有完成任务
     */
    public void finishs() {
        new KeyboardManage().CloseKeyboard(gridview, TaskExperienceActivity.this);
        if (popCancle != null && popCancle.isShowing()) {
            popCancle.dismiss();
        } else {
            layoutCancle=getLayoutInflater().inflate(
                    R.layout.pop_ok_cancel_content_general, null);
            TextView content=(TextView)layoutCancle.findViewById(R.id.content);
            content.setText("你还没有提交截图，确定取消任务吗？");
            TextView center=(TextView)layoutCancle.findViewById(R.id.center);
            center.setText("确定");
            TextView complete=(TextView)layoutCancle.findViewById(R.id.complete);
            complete.setText("继续任务");
            //确定
            center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isComplete=true;
                    clearDownload();
                    instance=null;
                    finish();
                    popCancle.dismiss();
                }
            });
            //继续任务
            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popCancle.dismiss();
                }
            });

            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popCancle = new PopupWindow(layoutCancle, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popCancle.setBackgroundDrawable(cd);
            popCancle.update();
            popCancle.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popCancle.setTouchable(true); // 设置popupwindow可点击
            popCancle.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popCancle.setFocusable(true); // 获取焦点
            NdianApplication.instance.backgroundAlpha((float) 0.4,TaskExperienceActivity.this);
            // 设置popupwindow的位置（相对tvLeft的位置）
            popCancle.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popCancle.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1,TaskExperienceActivity.this);
                }
            });
            popCancle.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popCancle.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
    /**
     * 任务截图上传成功显示
     */
    private void completeTask(){


        /**弹窗提示*/
        if (popSubmit != null && popSubmit.isShowing()) {
            popSubmit.dismiss();
        } else {
            layoutSubmit=getLayoutInflater().inflate(
                    R.layout.pop_task_submit, null);
            TextView know=(TextView)layoutSubmit.findViewById(R.id.know);
            know.setText("确定");
            RelativeLayout cancelRelativeLayout=(RelativeLayout)layoutSubmit.findViewById(R.id.cancelRelativeLayout);
            cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popSubmit.dismiss();
                }
            });
            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    popSubmit.dismiss();
                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popSubmit = new PopupWindow(layoutSubmit, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popSubmit.setBackgroundDrawable(cd);
            popSubmit.update();
            popSubmit.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popSubmit.setTouchable(true); // 设置popupwindow可点击
            popSubmit.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popSubmit.setFocusable(true); // 获取焦点
            NdianApplication.instance.backgroundAlpha((float) 0.4, TaskExperienceActivity.this);
            // 设置popupwindow的位置（相对tvLeft的位置）
            popSubmit.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popSubmit.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1, TaskExperienceActivity.this);
                }
            });
            popSubmit.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popSubmit.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }




    /**
     * 表示已安装
     */
    public void Installation(int key){
        isInstall=true;
        if(appentity.getState()==2){
            appentity.setState(1);
        }else{
            appentity.setState(1);
        }
        baifenbi.setText("打开");
    }
    /**
     * 表示卸载
     */
    public void Uninstall(int key){
        isInstall=false;
        if(appentity.getState()==1){
            appentity.setState(2);
            button.setText("点击下载");
            baifenbi.setText("点击下载");
        }else {
            appentity.setState(2);
            baifenbi.setText("安装");
        }
    }

    /**
     * 关闭界面时清除当前页面点击的下载
     */
    public void clearDownload(){
        List<DownloadInfo> list;
        list=MainActivity.instance.downloadManager.getDownloadInfoList();
        if(list!=null){
            for(int i=0;i<list.size();i++){
                try {
                    DownloadInfo downloadInfo = MainActivity.instance.downloadManager.getDownloadInfo(i);
                    MainActivity.instance.downloadManager.removeDownload(downloadInfo);
                    MainActivity.instance.idList.remove(i);
                    --i;
                } catch (com.lidroid.xutils.exception.DbException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

}
