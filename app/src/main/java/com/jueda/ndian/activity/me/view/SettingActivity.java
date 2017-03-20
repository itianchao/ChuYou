package com.jueda.ndian.activity.me.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.biz.LoginOutBiz;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.LoginOutUtil;
import com.jueda.ndian.utils.ToastShow;
import com.nostra13.universalimageloader.utils.StorageUtils;


import java.io.File;
import java.text.DecimalFormat;

/***
 * 设置
 */
public class SettingActivity extends Activity implements View.OnClickListener{
    public static final String TAG="SettingActivity";
    private RelativeLayout aboutRelativeLayout;//关于
    private RelativeLayout logout;//退出
    private RelativeLayout scoreRelativeLayout;//评分
    private RelativeLayout MessageNotificationRelativeLayout;//消息通知
    private RelativeLayout CacheRelativeLayout;//清除缓存大小
    private TextView CacheTextView;//缓存大小
    private double Cache=0;//缓存大小；

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**退出成功*/
                case Constants.ON_SUCCEED:
                    new LoginOutUtil(SettingActivity.this);
                    finish();
                    new ToastShow(SettingActivity.this,getResources().getString(R.string.Exit_the_success),1000);
                    break;
                /**退出失败*/
                case Constants.FAILURE:
                    new ToastShow(SettingActivity.this,getResources().getString(R.string.Exit_the_failure),1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_setting);
        InitView();
        setOnClick();
    }
    private void InitView() {
        NdianApplication.instance.setTitle(this,getResources().getString(R.string.setting),true);
        scoreRelativeLayout=(RelativeLayout)findViewById(R.id.scoreRelativeLayout);
        logout=(RelativeLayout)findViewById(R.id.logoutRelativeLayout);
        CacheRelativeLayout=(RelativeLayout)findViewById(R.id.CacheRelativeLayout);
        CacheTextView=(TextView)findViewById(R.id.CacheTextView);
        aboutRelativeLayout=(RelativeLayout)findViewById(R.id.aboutRelativeLayout);
        MessageNotificationRelativeLayout=(RelativeLayout)findViewById(R.id.MessageNotificationRelativeLayout);


        //缓存获取
        File ImageLoaderCacheDir= StorageUtils.getCacheDirectory(getApplicationContext());//imageloader缓存地址
        File MyCacheDir=new File(Environment.getExternalStorageDirectory()+"/ndian/ndianImages/");//自己定义缓存地址
        File APKCacheDir= new File(Environment.getExternalStorageDirectory()+"/ndian/apk/");//APK下载地址缓存地址

        if(ImageLoaderCacheDir.exists()){
            DecimalFormat df = new DecimalFormat("###.##");
            Cache=Double.parseDouble(df.format(getFolderSize(ImageLoaderCacheDir)/1024/1024));
        }
        if(MyCacheDir.exists()){
            DecimalFormat df = new DecimalFormat("###.##");
            Cache+=Double.parseDouble(df.format(getFolderSize(MyCacheDir)/1024/1024));
            CacheTextView.setText(df.format(Cache)+"MB");
        }
        if(APKCacheDir.exists()){
            DecimalFormat df = new DecimalFormat("###.##");
            Cache+=Double.parseDouble(df.format(getFolderSize(APKCacheDir)/1024/1024));
                CacheTextView.setText(df.format(Cache)+"MB");
        }
        if(Cache==0) {
            CacheTextView.setText("0.00MB");
        }
    }

    private void setOnClick() {
        logout.setOnClickListener(this);
        aboutRelativeLayout.setOnClickListener(this);
        scoreRelativeLayout.setOnClickListener(this);
        MessageNotificationRelativeLayout.setOnClickListener(this);
        CacheRelativeLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            //关于
            case R.id.aboutRelativeLayout:
                intent=new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;

            //退出
            case R.id.logoutRelativeLayout:
                new LoginOutBiz(SettingActivity.this,handler, true);
                break;
            //评分
            case R.id.scoreRelativeLayout:
                Uri uri = Uri.parse("market://details?id="+getPackageName());
                intent = new Intent(Intent.ACTION_VIEW,uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            //消息通知
            case R.id.MessageNotificationRelativeLayout:
                startActivity(new Intent(SettingActivity.this,MessageNotificationActivity.class));
                break;
            //清除缓存
            case R.id.CacheRelativeLayout:
                WaitDialog waitDialog=new WaitDialog(SettingActivity.this);
                deleteFolderFile(Environment.getExternalStorageDirectory()+"/ndian/ndianImages/",true);
                deleteFolderFile(StorageUtils.getCacheDirectory(getApplicationContext()).toString(),true);
                deleteFolderFile(Environment.getExternalStorageDirectory() + "/ndian/apk/", true);
                new ToastShow(SettingActivity.this, getResources().getString(R.string.clean_up_the_complete),1000);
                CacheTextView.setText("0.00MB");
                waitDialog.dismiss();
                break;
        }
    }
    /**
     * 获取文件夹大小
     * @param file File实例
     * @return long
     */
    public static double getFolderSize(java.io.File file){

        double size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
            {
                if (fileList[i].isDirectory())
                {
                    size = size + getFolderSize(fileList[i]);

                }else{
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //return size/1048576;
        return size;
    }
    /**
     * 删除指定目录下文件及目录
     * @param deleteThisPath
     * @param filepath
     * @return
     */
    public void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除

                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
