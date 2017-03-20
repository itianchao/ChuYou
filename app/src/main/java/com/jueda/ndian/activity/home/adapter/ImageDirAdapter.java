package com.jueda.ndian.activity.home.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.jueda.ndian.R;
import com.jueda.ndian.entity.Dir;
import com.jueda.ndian.utils.ImageLoaderUtil;

import java.util.List;


/**
 * Created by yanglw on 2014/8/17.
 * 图片相册
 */
public class ImageDirAdapter extends BaseAdapter
{
    private Context context;
    private  List<Dir> list;
    public ImageDirAdapter(Context context, List<Dir> list)
    {
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
            convertView = View.inflate(context, R.layout.i_imagedir,null);
            holder = new Holder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.iv);
            holder.mTextView = (TextView) convertView.findViewById(R.id.path);
            holder.mNameTextView = (TextView) convertView.findViewById(R.id.name);

            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        Dir dir = list.get(position);
        holder.mTextView.setText(dir.length+"");
        holder.mNameTextView.setText(Html.fromHtml(dir.name));
        ImageLoaderUtil.ImageLoader("file:///" + dir.path, holder.mImageView);

        return convertView;
    }
    private class Holder
    {
        public TextView mTextView, mNameTextView;
        public ImageView mImageView;
    }

}
