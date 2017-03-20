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
import com.jueda.ndian.activity.home.view.TaskShareActivity;
import com.jueda.ndian.activity.home.view.TaskShareDetailsActivity;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ImageLoaderUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/29.
 * 分享商品列表
 */
public class TaskShareAdapter extends BaseAdapter{
    private Activity context;
    private ArrayList<CommodityEntity> entityList;

    private final int TYPE_ONE=1;//类型1官方
    private final int TYPE_TWO=2;//类型2个人
    public TaskShareAdapter(Activity context, ArrayList<CommodityEntity> entityList) {
        this.context=context;
        this.entityList=entityList;
    }
    public void updata(ArrayList<CommodityEntity> entityList){
        this.entityList=entityList;
    }

    @Override
    public int getCount() {
        return entityList.size();
    }

    @Override
    public Object getItem(int position) {
        return entityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if(entityList.get(position).getOfficial_or_personal().equals("1")){
            return TYPE_TWO;
        }else{
            return TYPE_ONE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2=null;
        int type=getItemViewType(position);
        if(convertView==null){
            if(type==TYPE_ONE){
                holder1=new ViewHolder1();
                convertView=View.inflate(context, R.layout.item_task_share,null);
                holder1.title=(TextView)convertView.findViewById(R.id.title);
                holder1.money=(TextView)convertView.findViewById(R.id.money);
                holder1.oldermoney=(TextView)convertView.findViewById(R.id.oldermoney);
                holder1.bean=(TextView)convertView.findViewById(R.id.bean);
                holder1.imageView=(ImageView)convertView.findViewById(R.id.imageView);
                holder1.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
                convertView.setTag(holder1);
            }else{
                holder2=new ViewHolder2();
                convertView=View.inflate(context, R.layout.item_task_share_person,null);
                holder2.title=(TextView)convertView.findViewById(R.id.title);
                holder2.money=(TextView)convertView.findViewById(R.id.money);
                holder2.imageView=(ImageView)convertView.findViewById(R.id.imageView);
                holder2.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
                convertView.setTag(holder2);
            }

        }else{
            if(type==TYPE_ONE){
                holder1= (ViewHolder1) convertView.getTag();
            }else{
                holder2= (ViewHolder2) convertView.getTag();
            }

        }
        if(type==TYPE_ONE){
            holder1.title.setText(entityList.get(position).getTitle()+"【自营】");
            new ChangeText( holder1.title.getText().toString(),context.getResources().getColor(R.color.text_off_per),holder1.title,holder1.title.getText().toString().length()-4,holder1.title.getText().toString().length());
            holder1.money.setText("￥"+entityList.get(position).getPrice());
            holder1.oldermoney.setText("￥"+entityList.get(position).getOld_price());
            holder1.bean.setText("佣金："+entityList.get(position).getCommission()+"爱心豆");
            new ChangeText( holder1.bean.getText().toString(),context.getResources().getColor(R.color.text_orange),holder1.bean,3,holder1.bean.getText().toString().length()-3);
            ImageLoaderUtil.ImageLoader(entityList.get(position).getImg(), holder1.imageView);
            setOnClick(position,holder1.relativeLayout);
        }else{
            holder2.title.setText(entityList.get(position).getTitle()+"【个人】");
            new ChangeText( holder2.title.getText().toString(),context.getResources().getColor(R.color.text_off_per),holder2.title,holder2.title.getText().toString().length()-4,holder2.title.getText().toString().length());
            holder2.money.setText("￥"+entityList.get(position).getPrice());
            ImageLoaderUtil.ImageLoader(entityList.get(position).getImg(), holder2.imageView);
            setOnClick(position,holder2.relativeLayout);
        }

        return convertView;
    }

    /**
     * 进入分享商品详情
     * @param position
     * @param relativeLayout
     */
    private void setOnClick(final int position, RelativeLayout relativeLayout) {
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, TaskShareDetailsActivity.class);
                intent.putExtra("entity",entityList.get(position));
                intent.putExtra("who", TaskShareActivity.TAG);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    class ViewHolder1{
        TextView title;//标题
        TextView money;//现价
        TextView oldermoney;//原价
        TextView bean;//佣金
        ImageView imageView;//图片
        RelativeLayout relativeLayout;//分享详情
    }
    class ViewHolder2{
        TextView title;//标题
        TextView money;//现价
        ImageView imageView;//图片
        RelativeLayout relativeLayout;//分享详情
    }
}
