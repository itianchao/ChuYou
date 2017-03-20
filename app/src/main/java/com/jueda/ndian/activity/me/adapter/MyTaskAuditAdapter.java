package com.jueda.ndian.activity.me.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.entity.AppEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/14.
 * 我的审核任务
 */
public class MyTaskAuditAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AppEntity> entityList;

    public MyTaskAuditAdapter(Context context, ArrayList<AppEntity> entityList) {
        this.context=context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder1;
        if(convertView==null){
                    convertView=View.inflate(context, R.layout.item_my_task_audit_list,null);
                    holder1=new ViewHolder1();
                    holder1.NameTextView=(TextView)convertView.findViewById(R.id.NameTextView);
                    holder1.rewardTextView=(TextView)convertView.findViewById(R.id.rewardTextView);
                    holder1.formTextView=(TextView)convertView.findViewById(R.id.formTextView);
                    holder1.timeTextView=(TextView)convertView.findViewById(R.id.timeTextView);
                    holder1.head=(ImageView)convertView.findViewById(R.id.head);
                    convertView.setTag(holder1);
        }else{
            holder1= (ViewHolder1) convertView.getTag();
        }
        if(entityList.get(position).getEx_or_sh().equals("1")){
            holder1.head.setImageResource(R.drawable.experience);
        }else{
            holder1.head.setImageResource(R.drawable.wenjuan);
        }

        holder1.NameTextView.setText(entityList.get(position).getName());
        holder1.rewardTextView.setText(entityList.get(position).getReward());
        if(entityList.get(position).getCid().equals("0")){
            holder1.formTextView.setText("来至:广场任务");
        }else{
            holder1.formTextView.setText("来至:圈内任务");
        }
        holder1.timeTextView.setText("提交时间："+entityList.get(position).getTime());

        return convertView;
    }


    class ViewHolder1{
        TextView NameTextView;//软件名称
        TextView rewardTextView;//奖励爱心豆
        TextView formTextView;//来自哪里
        TextView timeTextView;//时间
        ImageView head;//任务类型
    }

}
