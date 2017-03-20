package com.jueda.ndian.activity.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.MyTask_ActivityFragment;
import com.jueda.ndian.activity.home.view.TaskDetailsActivityActivity;
import com.jueda.ndian.activity.home.view.TaskDetailsQuestionnaireActivity;
import com.jueda.ndian.activity.me.view.MyTaskActivity;
import com.jueda.ndian.entity.AppEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/31.
 * 我的发布任务
 */
public class MyTask_ReleaseAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AppEntity> entityList;
    private String who;

    public MyTask_ReleaseAdapter(Context context, ArrayList<AppEntity> entityList,String who) {
        this.context=context;
        this.entityList=entityList;
        this.who=who;
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
            convertView=View.inflate(context, R.layout.item_my_task_release_list,null);
            holder1=new ViewHolder1();
            holder1.NameTextView=(TextView)convertView.findViewById(R.id.NameTextView);
            holder1.rewardTextView=(TextView)convertView.findViewById(R.id.rewardTextView);
            holder1.formTextView=(TextView)convertView.findViewById(R.id.formTextView);
            holder1.timeTextView=(TextView)convertView.findViewById(R.id.timeTextView);
            holder1.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
            convertView.setTag(holder1);
        }else{
            holder1= (ViewHolder1) convertView.getTag();
        }

        holder1.NameTextView.setText(entityList.get(position).getName());
        holder1.rewardTextView.setText(entityList.get(position).getReward());
        if(entityList.get(position).getCid().equals("0")){
            holder1.formTextView.setText("来至:广场任务");
        }else{
            holder1.formTextView.setText("来至:圈内任务");
        }
        holder1.timeTextView.setText("提交时间："+entityList.get(position).getTime());
        setOnClick(position,holder1.relativeLayout);
        return convertView;
    }

    private void setOnClick(final int position, RelativeLayout relativeLayout) {
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(who.equals(MyTask_ActivityFragment.TAG)){
                    Intent intent=new Intent(context, TaskDetailsActivityActivity.class);
                    intent.putExtra("aid",entityList.get(position).getAid());
                    intent.putExtra("who", MyTaskActivity.TAG);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    //问卷详情
                    Intent intent=new Intent(context, TaskDetailsQuestionnaireActivity.class);
                    intent.putExtra("aid", entityList.get(position).getAid());
                    intent.putExtra("cid", entityList.get(position).getCid());
                    intent.putExtra("who",MyTaskActivity.TAG);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });
    }


    class ViewHolder1{
        TextView NameTextView;//软件名称
        TextView rewardTextView;//奖励爱心豆
        TextView formTextView;//来自哪里
        TextView timeTextView;//时间
        RelativeLayout relativeLayout;//跳转
    }

}
