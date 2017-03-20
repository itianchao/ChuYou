package com.jueda.ndian.activity.home.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.R;
import com.jueda.ndian.savedata.Configuration;

public class WelcomeActivity extends AppCompatActivity {
    private String isFist;//判断是否第一次登陆
    private ImageView imageView;//图片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_welcome);
        InitView();
    }
    private void InitView() {
        imageView=(ImageView)findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.qidong);
        imageView.setImageBitmap(bitmap);
        isFist=getVersion();//获取最新的版本号
            new Thread() {
                public void run() {
                    try {
                        String version=new Configuration().readaVersion(getApplicationContext());//获取以前的版本号
                        sleep(3000);
                        if(!isFist.equals(version)){
                            //跳整到主界面商品弹窗那里
                            new Configuration().writeaVersion(getApplicationContext(), isFist);
                            Intent intent =new Intent(WelcomeActivity.this, GuidePageActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
//                            if(!new Configuration().readaIsLogin(getApplicationContext())){
//                                Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
//                                startActivity(intent1);
//                            }else{
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
//                            }
                            finish();
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    /**
     * 返回无效
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyUp(keyCode, event);
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
