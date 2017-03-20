package com.jueda.ndian.utils;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

import com.jueda.ndian.R;

/**
 * 加载动画
 * Created by Administrator on 2016/4/21.
 */
public class LoadingAnimation {
    private View loadingImageView;//加载动画;
    private AnimationDrawable animationDrawable;//动画
    private View loadingInclude;
    public  LoadingAnimation(Activity activity,View loadingInclude){
         this.loadingInclude=loadingInclude;
         loadingImageView = (ImageView) loadingInclude.findViewById(R.id.loadingImageView);
    }
    public  LoadingAnimation(View view,View loadingInclude){
        this.loadingInclude=loadingInclude;
        loadingImageView = (ImageView) loadingInclude.findViewById(R.id.loadingImageView);
    }
    /**
     * 开始动画
     */
    public void startAnim(){
             loadingInclude.setVisibility(View.VISIBLE);
            animationDrawable = (AnimationDrawable) loadingImageView.getBackground();
            animationDrawable.start();
    }
    /**
     * 停止动画
     */
    public void stopAnim(){
             loadingInclude.setVisibility(View.GONE);
            animationDrawable= (AnimationDrawable) loadingImageView.getBackground();
            animationDrawable.stop();
    }
}
