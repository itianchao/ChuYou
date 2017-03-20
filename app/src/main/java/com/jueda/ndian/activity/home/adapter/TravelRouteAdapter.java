package com.jueda.ndian.activity.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.view.TopicDetailsActivity;
import com.jueda.ndian.activity.me.view.PersonalDetailsActivity;
import com.jueda.ndian.entity.CommentsEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.getCurrentTime;
import com.jueda.ndian.view.CircleImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/26.
 * 旅游路线适配器
 */
public class TravelRouteAdapter extends BaseAdapter{
    private Activity context;
    private Handler handler;
    private  ArrayList<CommentsEntity> list;
    public TravelRouteAdapter(Activity activity, Handler handler, ArrayList<CommentsEntity> entityArrayList) {
        this.context=activity;
        this.handler=handler;
        this.list=entityArrayList;
    }
    public void updata( ArrayList<CommentsEntity> entityArrayList){
        this.list=entityArrayList;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder2 holder2;
        if(convertView==null) {
            convertView=View.inflate(context, R.layout.topic_details_comments,null);
            holder2=new ViewHolder2();
            holder2.commentsLinearLayout=(LinearLayout)convertView.findViewById(R.id.commentsLinearLayout);
            holder2.headImageView=(CircleImageView)convertView.findViewById(R.id.headImageView);
            holder2.NickNameTextView=(TextView)convertView.findViewById(R.id.NickNameTextView);
            holder2.contentTextView=(TextView)convertView.findViewById(R.id.contentTextView);
            holder2.timeTextView=(TextView)convertView.findViewById(R.id.timeTextView);
            convertView.setTag(holder2);
        }else{
            holder2=(ViewHolder2)convertView.getTag();
        }

        /**设置评论信息*/
        if(list.size()>0){
            /**设置是否是回复*/
            if(list.get(position).getBy_id().equals("0")){
                new ChangeText(list.get(position).getPcontent(),context.getResources().getColor(R.color.text_black),holder2.contentTextView,0,list.get(position).getPcontent().length());
            }else{
                holder2.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
                CharSequence str=  context.getResources().getString(R.string.reply)+list.get(position).getBy_uname() +":"+ list.get(position).getPcontent();
                SpannableString spannableString1 = new SpannableString(str);
                spannableString1.setSpan(new ClickableSpan(){
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false); //设置下划线
                    }
                    @Override
                    public void onClick(View widget) {
                        //**跳转到用户信息
                        if(!list.get(position).getBy_uid().equals(ConstantsUser.userEntity.getUid()+"")) {
                            Intent intent = new Intent(context, PersonalDetailsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("uid", list.get(position).getBy_uid());
                            context.startActivity(intent);
                        }
                    }
                },0,(list.get(position).getBy_uname().length()+3), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString1.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.text_blue)),0,(list.get(position).getBy_uname().length()+3) , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder2.contentTextView.setText(spannableString1);
            }
            ImageLoaderUtil.ImageLoader(list.get(position).getPavatar(), holder2.headImageView, R.drawable.head_portrait);
            holder2.NickNameTextView.setText(list.get(position).getPname());

            holder2.timeTextView.setText(getCurrentTime.getCurrentTime(Long.parseLong(list.get(position).getPcreat_time())));
            setOnClickTYPE2(position, holder2.commentsLinearLayout, holder2.headImageView, holder2.NickNameTextView);
        }
        return convertView;
    }
    /**
     * @param position
     * @param commentsLinearLayout
     * @param headImageView
     * @param nickNameTextView
     */
    public void setOnClickTYPE2(final int position, LinearLayout commentsLinearLayout, CircleImageView headImageView, TextView nickNameTextView) {
        /**评论*/
        commentsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message=new Message();
                message.what= TopicDetailsActivity.COMMENTS;
                message.obj=position;
                handler.sendMessage(message);
            }
        });
        /**进入个人信息*/
        headImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!list.get(position).getPuid().equals(ConstantsUser.userEntity.getUid() + "")) {
                    Intent intent = new Intent(context, PersonalDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("uid", list.get(position).getPuid());
                    context.startActivity(intent);
                }
            }
        });
        nickNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!list.get(position).getPuid().equals(ConstantsUser.userEntity.getUid()+"")) {
                    Intent intent = new Intent(context, PersonalDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("uid", list.get(position).getPuid());
                    context.startActivity(intent);
                }
            }
        });
    }

    class ViewHolder2{
        LinearLayout commentsLinearLayout;//评论
        CircleImageView headImageView;//头像
        TextView NickNameTextView;//昵称
        TextView contentTextView;//内容
        TextView timeTextView;//时间
    }
}
