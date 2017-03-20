package com.jueda.ndian.activity.me.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.jueda.ndian.activity.home.biz.VersionsBiz;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ToastShow;

import java.util.HashMap;


/**
 * 关于
 */
public class AboutActivity extends AppCompatActivity {
    private TextView version;//版本
    private RelativeLayout PhoneRelativeLayout;//拨打客服电话
    private RelativeLayout updateRelativeLayout;//版本更新
    private WaitDialog waitDialog;
    public HashMap<String,String> versions;//版本更新信息
    private PopupWindow popUpdate;//更新
    private View layoutUpdate;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**获取应用版本*/
                case Constants.ON_SUCCEED:
                    waitDialog.dismiss();
                    versions= (HashMap<String, String>) msg.obj;
                    if(Integer.parseInt(versions.get("code"))>Integer.parseInt(getVersion())){
                        Update();
                    }else{
                        new ToastShow(AboutActivity.this,getResources().getString(R.string.No_update),1000);
                    }
                    break;
                /**获取失败*/
                case Constants.FAILURE:
                    waitDialog.dismiss();
                    new ToastShow(AboutActivity.this,getResources().getString(R.string.For_failure),1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_about);
        InitView();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.about), true);
        updateRelativeLayout=(RelativeLayout)findViewById(R.id.updateRelativeLayout);
        PhoneRelativeLayout=(RelativeLayout)findViewById(R.id.PhoneRelativeLayout);
        version=(TextView)findViewById(R.id.version);
        //赋值
        version.setText("V" + getName());

        //拨打客服电话
        PhoneRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + "0755-23026684");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });
        //更新
        updateRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitDialog = new WaitDialog(AboutActivity.this);
                new VersionsBiz(AboutActivity.this, handler);
            }
        });
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
            APP_Size.setText(getResources().getString(R.string.Application_of_size)+"："+versions.get("size"));
            Content.setText(versions.get("update_log"));
            //更新
            layoutUpdate.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new com.jueda.ndian.server.DownloadService().downNewFile(versions.get("url"),2, "恩典365");
                    popUpdate.dismiss();
                }
            });
            //不更新
            layoutUpdate.findViewById(R.id.no_update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popUpdate.dismiss();
                }
            });

            ColorDrawable cd = new ColorDrawable(-0000);
            popUpdate.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
            popUpdate.update();
            popUpdate.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popUpdate.setTouchable(true); // 设置popupwindow可点击
            popUpdate.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popUpdate.setFocusable(true); // 获取焦点
            // 设置popupwindow的位置（相对tvLeft的位置）
            popUpdate.showAtLocation(updateRelativeLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getName() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName+"";
            return  version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
