package com.jueda.ndian.activity.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.jueda.ndian.R;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.utils.ImageLoaderUtil;

import java.io.File;
import java.util.List;


/**
 * <br/>
 * <br/>
 * Created by yanglw on 2014/8/17.
 * 相册
 */
public class ImagesAdapter extends BaseAdapter
{
    private List<Photo> mCheckList;
    private Context context;
    private List<Photo> list;

    public ImagesAdapter(Context context, List<Photo> list, List<Photo> checkList)
    {
        mCheckList = checkList;
        this.context=context;
        this.list=list;
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Holder holder;
        if (convertView == null)
        {
            convertView = View.inflate(context, R.layout.i_images,null);
            holder = new Holder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.iv);
            holder.mCheckImgaeView = (ImageView) convertView.findViewById(R.id.check);

            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        Photo photo = list.get(position);

        if (mCheckList != null && mCheckList.contains(photo))
        {
            holder.mCheckImgaeView.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.mCheckImgaeView.setVisibility(View.INVISIBLE);
        }
        ImageLoaderUtil.ImageLoader("file:///" + photo.path, holder.mImageView);
        return convertView;
    }

    public void setCheck(int postion, View view)
    {
        Photo photo = list.get(postion);

        boolean checked = mCheckList.contains(photo);

        Holder holder = (Holder) view.getTag();

        if (checked)
        {
            mCheckList.remove(photo);
            holder.mCheckImgaeView.setVisibility(View.INVISIBLE);
        }
        else
        {
            mCheckList.add(photo);
            holder.mCheckImgaeView.setVisibility(View.VISIBLE);
        }
    }


    private class Holder
    {
        public ImageView mImageView, mCheckImgaeView;
    }

}
