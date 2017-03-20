package com.jueda.ndian.activity.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.view.CircleContentActivity;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.home.view.selectCircleActivity;
import com.jueda.ndian.entity.CircleEntity;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.view.RoundCornerImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/31.
 * 我的圈子
 */
public class MyCircleAdapter extends BaseAdapter{
    private Activity context;
    private ArrayList<CircleEntity> myCircleList;
    private String TAG;
    public MyCircleAdapter(Activity context, ArrayList<CircleEntity> myCircleList, String tag) {
        this.context=context;
        this.TAG=tag;
        this.myCircleList=myCircleList;
    }
    public void updata(ArrayList<CircleEntity> myCircleList){
        this.myCircleList=myCircleList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return myCircleList.size();
    }

    @Override
    public Object getItem(int position) {
        return myCircleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=View.inflate(context, R.layout.mycircle_item,null);
            holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
            holder.NameTextView=(TextView)convertView.findViewById(R.id.NameTextView);
            holder.xian=(ImageView)convertView.findViewById(R.id.xian);
            holder.HeadImage=(RoundCornerImageView)convertView.findViewById(R.id.HeadImage);
            holder.contentTextView=(TextView)convertView.findViewById(R.id.contentTextView);
            holder.numberTextView=(TextView)convertView.findViewById(R.id.numberTextView);
            holder.my_circle=(ImageView)convertView.findViewById(R.id.my_circle);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        /**我创建标识*/
        if(myCircleList.get(position).getIs_creater().equals("1")){
            holder.my_circle.setVisibility(View.VISIBLE);
        }else{
            holder.my_circle.setVisibility(View.GONE);
        }
        /**设置最后一根线不显示*/
            if(position==(myCircleList.size()-1)){
                holder.xian.setVisibility(View.GONE);
            }else{
                holder.xian.setVisibility(View.VISIBLE);
            }
        /**设置数据*/
        ImageLoaderUtil.ImageLoader(myCircleList.get(position).getAvatar(),holder.HeadImage,R.drawable.head_circle);
        holder.NameTextView.setText(myCircleList.get(position).getName());
        holder.contentTextView.setText(myCircleList.get(position).getCdesc());
        holder.numberTextView.setText(myCircleList.get(position).getCount());

        setOnClick(holder.relativeLayout,position);
        return convertView;
    }

    /**
     * 点击事件
     * @param relativeLayout
     * @param position
     */
    private void setOnClick(RelativeLayout relativeLayout, final int position) {

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TAG.equals(CharityCircleFragment.TAG)){
                    Intent intent=new Intent(context, CircleContentActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("name", myCircleList.get(position).getName());
                    intent.putExtra("cid",myCircleList.get(position).getCid());
                    context.startActivity(intent);
                }else if(TAG.equals(selectCircleActivity.TAG)){
                    Intent intent=new Intent(context, CircleContentActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("name", myCircleList.get(position).getName());
                    intent.putExtra("cid", myCircleList.get(position).getCid());
                    context.setResult(context.RESULT_OK,intent);
                    context.finish();
                }

            }
        });
    }

    class ViewHolder{
        ImageView xian;//线
        RoundCornerImageView HeadImage;//头像
        TextView NameTextView;//名称
        TextView contentTextView;//内容
        TextView numberTextView;//浏览数
        RelativeLayout relativeLayout;//点击进入圈内
        ImageView my_circle;//我的圈子
    }
}
