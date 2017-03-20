package com.jueda.ndian.activity.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.view.TaskDetailsEverydayActivity;
import com.jueda.ndian.activity.home.view.TaskExperienceActivity;
import com.jueda.ndian.entity.AppEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/8.
 * 下载任务舒培强
 */
public class TaskExperienceListAdapter extends BaseAdapter{
    private Activity context;
    public ArrayList<AppEntity> applist;
    private PopupWindow popPrompt;//任务数量达到了
    private View layoutPrompt;
    private PopupWindow popAudit;//审核中的
    private View layoutAudit;
    private String who;//判断是圈外还是圈内

    public TaskExperienceListAdapter(Activity context, ArrayList<AppEntity> applist, String who) {
        this.context=context;
        this.applist= applist;
        this.who=who;
    }
    public void updata(ArrayList<AppEntity> applist){
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
            convertView=View.inflate(context,R.layout.item_task_list_experience,null);
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

        holder.title.setText(applist.get(position).getName());
        holder.bean.setText(applist.get(position).getReward());
        holder.head.setImageResource(R.drawable.guanfang);
        if(applist.get(position).getAudit().equals("0")){
            holder.state.setVisibility(View.GONE);
        }else if(applist.get(position).getAudit().equals("1")){
            holder.state.setVisibility(View.VISIBLE);
            holder.state.setImageResource(R.drawable.status02);
        }
        OneClick(position, holder.relativeLayout);
        return convertView;
    }

    //点击事件
    private void OneClick(final int position, final RelativeLayout relativeLayout) {
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    Intent intent=new Intent(context, TaskDetailsEverydayActivity.class);
                    intent.putExtra("entity", applist.get(position));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    if (applist.get(position).getAudit().equals("1")) {
                        AuditPrompt(relativeLayout);
                    } else if (applist.get(position).getIs_fulfil().equals("1")) {
                        Intent intent = new Intent(context, TaskExperienceActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("entity", applist.get(position));
                        intent.putExtra("who", who);
                        context.startActivity(intent);
                    } else {
                        Prompt(relativeLayout, applist.get(position).getIs_fulfil_tip());
                    }
                }
            }
        });
    }

    /***体验任务*/
    class ViewHolder{
        TextView title;//标题
        TextView bean;//爱心豆
        ImageView state;//状态
        RelativeLayout relativeLayout;//跳转界面
        ImageView head;//个人还是官方
    }
    /***
     * 审核中
     */
    private void AuditPrompt(RelativeLayout button){
        if (popAudit != null && popAudit.isShowing()) {
            popAudit.dismiss();
        } else {
            layoutAudit =context.getLayoutInflater().inflate(
                    R.layout.pop_ok_content_title_general, null);
            TextView know=(TextView)layoutAudit.findViewById(R.id.know);
            TextView title=(TextView)layoutAudit.findViewById(R.id.title);
            TextView Content=(TextView)layoutAudit.findViewById(R.id.Content);
            title.setText(context.getResources().getString(R.string.Warm_prompt));
            Content.setText(context.getResources().getString(R.string.Pro_the_auditor_in_the_audit_with_the_strength_of_his_conceptions_of_tasks));

            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popAudit.dismiss();
                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popAudit = new PopupWindow(layoutAudit, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);


            ColorDrawable cd = new ColorDrawable(-0000);
            popAudit.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
            popAudit.update();
            popAudit.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popAudit.setTouchable(true); // 设置popupwindow可点击
            popAudit.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popAudit.setFocusable(true); // 获取焦点
            // 设置popupwindow的位置（相对tvLeft的位置）
            popAudit.showAtLocation(button, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            popAudit.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popAudit.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
    /***
     * 任务完成数
     */
    private void Prompt(RelativeLayout button,String is_fulfil_tip){
        if (popPrompt != null && popPrompt.isShowing()) {
            popPrompt.dismiss();
        } else {
            layoutPrompt =context.getLayoutInflater().inflate(
                    R.layout.prompt_task, null);
            TextView Content=(TextView)layoutPrompt.findViewById(R.id.Content);
            Content.setText(is_fulfil_tip+"");
            TextView know=(TextView)layoutPrompt.findViewById(R.id.know);
            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPrompt.dismiss();
                }
            });

            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popPrompt = new PopupWindow(layoutPrompt, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);


            ColorDrawable cd = new ColorDrawable(-0000);
            popPrompt.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
            popPrompt.update();
            popPrompt.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popPrompt.setTouchable(true); // 设置popupwindow可点击
            popPrompt.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popPrompt.setFocusable(true); // 获取焦点
            // 设置popupwindow的位置（相对tvLeft的位置）
            popPrompt.showAtLocation(button, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            popPrompt.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popPrompt.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
