package com.jueda.ndian.activity.home.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.entity.TaskCommonEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/25.
 * 活动任务详情适配器
 */
public class TaskDetailsActivityAdapter extends BaseAdapter{
    private Activity context;
    private ArrayList<TaskCommonEntity> entityList;
    public TaskDetailsActivityAdapter(Activity context, ArrayList<TaskCommonEntity> entityList) {
        this.context=context;
        this.entityList=entityList;
    }
    public void updata(ArrayList<TaskCommonEntity> entityList){

        this.entityList=entityList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return entityList.get(0).getEnroll().size();
    }

    @Override
    public Object getItem(int position) {
        return entityList.get(0).getEnroll().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.item_task_details_activity_two,null);
            holder=new ViewHolder();
            holder.phoneNumber=(TextView)convertView.findViewById(R.id.phoneNumber);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.phoneNumber.setText(entityList.get(0).getEnroll().get(position)+"");
        return convertView;
    }
    class ViewHolder{
        TextView phoneNumber;//手机号码
    }
}
