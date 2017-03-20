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
import com.jueda.ndian.activity.fragment.TaskActivity_Fragment;
import com.jueda.ndian.activity.fragment.TaskQuestionnaire_Fragment;
import com.jueda.ndian.activity.home.view.TaskDetailsActivityActivity;
import com.jueda.ndian.activity.home.view.TaskDetailsQuestionnaireActivity;
import com.jueda.ndian.entity.TaskCommonEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/24.
 * 任务通用模板
 */
public class TaskCommonAdapter extends BaseAdapter {
    private Activity context;
    public ArrayList<TaskCommonEntity> applist;
    private String who;//判断是圈外还是圈内
    private String TAG;//活动任务TaskActivity_Fragment

    public TaskCommonAdapter(Activity context, ArrayList<TaskCommonEntity> applist, String who, String TAG) {
        this.context=context;
        this.applist= applist;
        this.who=who;
        this.TAG=TAG;
    }
    public void updata(ArrayList<TaskCommonEntity> applist){
        this.applist=applist;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return applist.size();
    }

    @Override
    public Object getItem(int position) {
        return applist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.item_task_list_experience,null);
            holder=new ViewHolder();
            holder.title=(TextView)convertView.findViewById(R.id.title);
            holder.bean=(TextView)convertView.findViewById(R.id.bean);
            holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
            holder.state=(ImageView)convertView.findViewById(R.id.state);
            holder.head=(ImageView)convertView.findViewById(R.id.head);
            convertView.setTag(holder);

        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        if(applist.get(position).getUid().equals("1")){
            holder.head.setImageResource(R.drawable.guanfang);
        }else{
            holder.head.setImageResource(R.drawable.geren);
        }
        holder.title.setText(applist.get(position).getName());
        holder.bean.setText(applist.get(position).getReward());
        OneClick(position, holder.relativeLayout);
        return convertView;
    }

    //点击事件
    private void OneClick(final int position, final RelativeLayout relativeLayout) {
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TAG.equals(TaskActivity_Fragment.TAG)){
                    //活动详情
                    Intent intent=new Intent(context, TaskDetailsActivityActivity.class);
                    intent.putExtra("aid",applist.get(position).getAid());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else if(TAG.equals(TaskQuestionnaire_Fragment.TAG)){
                    //问卷详情
                    Intent intent=new Intent(context, TaskDetailsQuestionnaireActivity.class);
                    intent.putExtra("aid",applist.get(position).getAid());
                    intent.putExtra("cid",TaskQuestionnaire_Fragment.instance.cid());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    /***活动任务*/
    class ViewHolder{
        TextView title;//标题
        TextView bean;//爱心豆
        ImageView state;//状态
        RelativeLayout relativeLayout;//跳转界面
        ImageView head;//个人还是官方
    }

}
