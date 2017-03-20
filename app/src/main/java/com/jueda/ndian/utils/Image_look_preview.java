package com.jueda.ndian.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.jueda.ndian.activity.home.view.PreviewActivity;
import com.jueda.ndian.entity.Photo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/8.
 * 查看图片需要传递的数据
 */
public class Image_look_preview {
    /**
     * 需要返回值
     * 查看本地图片
     * @param activity
     * @param position  选择第几个默认显示
     * @param list  图片集合
     * @param is_delect 是否有删除按钮
     * @param Type 是网络图片还是本地图片
     */
    public static void look_result(Activity activity,int position,ArrayList<Photo> list){
        Intent intent=new Intent(activity, PreviewActivity.class);
        intent.putExtra(PreviewActivity.LIST,list);
        intent.putExtra(PreviewActivity.POSITION,position);
        activity.startActivityForResult(intent, 10);
    }
    public static void look_result(Activity activity,Fragment fragment,int position,ArrayList<Photo> list){
        Intent intent=new Intent(activity, PreviewActivity.class);
        intent.putExtra(PreviewActivity.LIST,list);
        intent.putExtra(PreviewActivity.POSITION,position);
        fragment.startActivityForResult(intent, 10);
    }

    /**
     *
     * @param activity
     * @param position 选择第几个默认显示
     * @param list 图片集合
     */
    public static void look(Activity activity,int position,ArrayList<Photo> list){
        Intent intent=new Intent(activity, com.jueda.ndian.activity.circle.view.PreviewActivity.class);
        intent.putExtra(PreviewActivity.LIST,list);
        intent.putExtra(PreviewActivity.POSITION,position);
        activity.startActivity(intent);
    }

}
