package com.jueda.ndian.activity.home.view;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.CountdownUtil;
import com.jueda.ndian.utils.LogUtil;

import java.util.Timer;

/***
 * 视频播放
 */
public class VideoActivity extends Activity{
    private VideoView video;
    private int old_duration;//记录播放的时间
    private ProgressBar progressBar;//加载动画
    private TextView back;//返回
    private String url;//视频连接
    final Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            int duration = video.getCurrentPosition();
            new LogUtil("VideoActivity","duration----"+duration+"    'old_duration"+ old_duration);
            if (old_duration == duration && video.isPlaying()) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                old_duration = duration;
            }
            handler.postDelayed(runnable, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video);
        InitView();
        }
    private void InitView() {
        url=getIntent().getStringExtra("url");
        video = (VideoView) findViewById(R.id.video);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        back=(TextView)findViewById(R.id.back);


          /* 获取MediaController对象，控制媒体播放 */
        MediaController mc = new MediaController(this);
        video.setMediaController(mc);

        /* 设置URI ， 指定数据 */
//        video.setVideoURI(Uri.parse("/mnt/sdcard/aa.mp4"));
        video.setVideoPath(url);
        /* 开始播放视频 */
        video.start();
        /*  请求获取焦点 */
        video.requestFocus();
        setOnListening();
    }

    private void setOnListening() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                new LogUtil("VideoActivity", "Error: " + what + "," + extra);// Error: 1,-1004
                video.start();
                return true;
            }
        });
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progressBar.setVisibility(View.GONE);
                runnable.run();
            }
        });
    }

    @Override
    public void finish() {
        handler.removeCallbacks(runnable);
        super.finish();
    }




}
