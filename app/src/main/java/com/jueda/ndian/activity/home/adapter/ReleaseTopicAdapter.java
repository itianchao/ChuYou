package com.jueda.ndian.activity.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.view.ReleaseActivity;
import com.jueda.ndian.activity.fragment.SendActivityTaskFragment;
import com.jueda.ndian.activity.fragment.SendQuestionnaireTaskFragment;
import com.jueda.ndian.activity.home.view.CutImageViewActivity;
import com.jueda.ndian.activity.home.view.ReleaseGoodsActivity;
import com.jueda.ndian.activity.home.view.TaskDetailsQuestionnaireActivity;
import com.jueda.ndian.activity.home.view.TaskExperienceActivity;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.view.RoundCornerImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/5.
 * 相册选中图片显示
 */
public class ReleaseTopicAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Photo> list;
    private Handler hanlder;
    private int screenWidth;
    private int max;
    public ReleaseTopicAdapter(Context context, ArrayList<Photo> mList, Handler hanlder, int screenWidth,int max) {
        this.context=context;
        this.list=mList;
        this.max=max;
        this.hanlder=hanlder;
        this.screenWidth=screenWidth;
    }

    public void updata(ArrayList<Photo> mList){
        list=mList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

            convertView=View.inflate(context, R.layout.i_main,null);
            RoundCornerImageView imageView = (RoundCornerImageView) convertView.findViewById(R.id.image);
            int i=screenWidth- Constants.dip2px(context,40);

            //最后一张图就显示添加按钮
            if( list.get(position).path==null){
                imageView.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams linearParams1 = (RelativeLayout.LayoutParams) imageView.getLayoutParams(); // 取控件当前的布局参数
                linearParams1.height = i / 3;// 当控件的高强制设成图片高度
                imageView.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件imageview中
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件imageview中
                imageView.setImageResource(R.drawable.press_image_photo);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hanlder.sendEmptyMessage(ReleaseActivity.CLICK_ADD);
                    }
                });
            }else {
                RelativeLayout.LayoutParams linearParams1 = (RelativeLayout.LayoutParams) imageView.getLayoutParams(); // 取控件当前的布局参数
                linearParams1.height = i / 3;// 当控件的高强制设成图片高度
                imageView.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件imageview中
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件imageview中
                String path = "file:///" + list.get(position).path;
                ImageLoaderUtil.ImageLoader(path, imageView);
                String string = ImageLoader.getInstance().getDiscCache().get(path).getPath();
                if (ReleaseActivity.instance != null) {
                    //发布话题
                    if (!ReleaseActivity.imagelist.contains(string)) {
                        ReleaseActivity.imagelist.add(string);
                    }
                }
                if (TaskExperienceActivity.instance != null) {
                    if (!TaskExperienceActivity.imagelist.contains(string)) {
                        TaskExperienceActivity.imagelist.add(string);
                    }
                }
                if (SendActivityTaskFragment.instance != null) {
                    if (!SendActivityTaskFragment.imagelist.contains(string)) {
                        SendActivityTaskFragment.imagelist.add(string);
                    }
                }
                if (SendQuestionnaireTaskFragment.instance != null) {
                    if (!SendQuestionnaireTaskFragment.imagelist.contains(string)) {
                        SendQuestionnaireTaskFragment.imagelist.add(string);
                    }
                }
                if (TaskDetailsQuestionnaireActivity.instance != null) {
                    if (!TaskDetailsQuestionnaireActivity.imagelist.contains(string)) {
                        TaskDetailsQuestionnaireActivity.imagelist.add(string);
                    }
                }
                if (ReleaseGoodsActivity.instance != null) {
                    if (!ReleaseGoodsActivity.imagelist.contains(string)) {
                        ReleaseGoodsActivity.imagelist.add(string);
                    }
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message message=new Message();
                        message.obj=position;
                        message.what=2;
                        hanlder.sendMessage(message);
                    }
                });
            }
        return convertView;
    }

}
