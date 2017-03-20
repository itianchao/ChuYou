package com.jueda.ndian.activity.me.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.view.CircleContentActivity;
import com.jueda.ndian.activity.circle.view.TopicDetailsActivity;
import com.jueda.ndian.activity.home.view.TravelRouteActivity;
import com.jueda.ndian.activity.me.biz.DeleteMessageBiz;
import com.jueda.ndian.entity.MessageEntity;
import com.jueda.ndian.savedata.MessageData;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.view.CircleImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/20.
 * 消息
 */
public class MessageAdapter extends BaseAdapter{
    private Activity context;
    private ArrayList<MessageEntity> entityList;
    private ArrayList<MessageEntity> messageArrayList;//消息信息
    private PopupWindow popMoer;
    private View layoutMoer;
    private RelativeLayout mRelativeLayout;
    private int initialHeight;//控件高度



    public MessageAdapter(Activity context, ArrayList<MessageEntity> entityList, RelativeLayout mRelativeLayout) {
        this.context=context;
        this.entityList=entityList;
        this.mRelativeLayout=mRelativeLayout;
        messageArrayList=new MessageData().readaMessageData(context);//消息信息
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
        ViewHolder holder = null;
        if(convertView==null){
                    holder=new ViewHolder();
                    convertView=View.inflate(context, R.layout.item_message, null);
                    holder.linearLayout=(LinearLayout)convertView.findViewById(R.id.linearLayout);
                    holder.HeadImage= (CircleImageView) convertView.findViewById(R.id.HeadImage);
                    holder.NickNameTextView=(TextView)convertView.findViewById(R.id.NickNameTextView);
                    holder.contentTextView=(TextView)convertView.findViewById(R.id.contentTextView);
                    holder.disposeTextView=(TextView)convertView.findViewById(R.id.disposeTextView);
                    holder.timeTextView=(TextView)convertView.findViewById(R.id.timeTextView);
                    holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
                    holder.timeRelativeLayout=(RelativeLayout)convertView.findViewById(R.id.timeRelativeLayout);
                    holder.consentButton=(Button)convertView.findViewById(R.id.consentButton);
                    initialHeight=holder.relativeLayout.getMeasuredHeight();
                    convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
            AbsListView.LayoutParams layoutParams=new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
            holder.linearLayout.setLayoutParams(layoutParams);


        }

                /**话题评论*/
                if(entityList.get(position).getType().equals("1")||entityList.get(position).getType().equals("2")){
                    holder.disposeTextView.setVisibility(View.GONE);
                    holder.consentButton.setVisibility(View.GONE);
                    holder.timeRelativeLayout.setVisibility(View.VISIBLE);
                    ImageLoaderUtil.ImageLoader(entityList.get(position).getAvatar(), holder.HeadImage,R.drawable.head_portrait);
                    holder.NickNameTextView.setText(entityList.get(position).getName());
                    holder.timeTextView.setText(entityList.get(position).getCreat_time());
                    holder.contentTextView.setText(context.getResources().getString(R.string.Comment)+"："+entityList.get(position).getContent());
//                    holder.typeTextView.setText(context.getResources().getString(R.string.Comment));
//                    holder.typeTextView.setTextColor(context.getResources().getColor(R.color.text_blue));
                }else if(entityList.get(position).getType().equals("4")){
                    /**移除圈*/
                    holder.disposeTextView.setVisibility(View.VISIBLE);
                    holder.timeRelativeLayout.setVisibility(View.GONE);
                    holder.consentButton.setVisibility(View.GONE);
                    ImageLoaderUtil.ImageLoader(entityList.get(position).getAvatar(), holder.HeadImage,R.drawable.head_portrait);
                    holder.NickNameTextView.setText(entityList.get(position).getCname());
                    holder.disposeTextView.setText(context.getResources().getString(R.string.The_applicant)+"："+entityList.get(position).getName());
                    holder.contentTextView.setText(entityList.get(position).getContent());
//                    holder.typeTextView.setText(context.getResources().getString(R.string.Remove_the_ring));
//                    holder.typeTextView.setTextColor(context.getResources().getColor(R.color.text_move));
                }else if(entityList.get(position).getType().equals("15")){
                    /**话题点赞*/
                    holder.disposeTextView.setVisibility(View.GONE);
                    holder.consentButton.setVisibility(View.GONE);
                    holder.timeRelativeLayout.setVisibility(View.VISIBLE);
                    ImageLoaderUtil.ImageLoader(entityList.get(position).getAvatar(), holder.HeadImage,R.drawable.head_portrait);
                    holder.NickNameTextView.setText(entityList.get(position).getName());
                    holder.timeTextView.setText(entityList.get(position).getCreat_time());
                    holder.contentTextView.setText(entityList.get(position).getContent());
                }
                setOnClick(position, holder.relativeLayout, holder.linearLayout);

        return convertView;
    }

    /**消息跳转*/
    private void setOnClick(final int position, final RelativeLayout relativeLayout, final LinearLayout linearLayout) {
        /**跳转*/
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entityList.get(position).getType().equals("1")||entityList.get(position).getType().equals("15")) {
                    /**话题评论/话题点赞*/
                    Intent intent = new Intent(context, TopicDetailsActivity.class);
                    intent.putExtra("who", CircleContentActivity.TAG);
                    intent.putExtra("id", entityList.get(position).getPid());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else if(entityList.get(position).getType().equals("2")){
                    /**旅游线路*/
                    Intent intent=new Intent(context, TravelRouteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("did",entityList.get(position).getDid());
                    context.startActivity(intent);
                }
            }
        });
        //长安删除
        relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator= (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                if (popMoer != null && popMoer.isShowing()) {
                    popMoer.dismiss();
                } else {
                    layoutMoer = context.getLayoutInflater().inflate(
                            R.layout.pop_delete, null);
                    TextView deleteTextView = (TextView) layoutMoer.findViewById(R.id.deleteTextView);
                    RelativeLayout cancelRelativeLayout = (RelativeLayout) layoutMoer.findViewById(R.id.cancelRelativeLayout);
                    cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popMoer.dismiss();
                        }
                    });
                    deleteTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DeleteMessageBiz(context,entityList.get(position).getId());
                            deleteCell(linearLayout,position);
                            popMoer.dismiss();
                        }
                    });
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
                    popMoer = new PopupWindow(layoutMoer, ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    ColorDrawable cd = new ColorDrawable(-0000);
                    popMoer.setBackgroundDrawable(cd);
                    popMoer.update();
                    popMoer.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popMoer.setTouchable(true); // 设置popupwindow可点击
                    popMoer.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    popMoer.setFocusable(true); // 获取焦点
                    // 设置popupwindow的位置（相对tvLeft的位置）
                    popMoer.showAtLocation(mRelativeLayout, Gravity.BOTTOM, 0, 0);

                    popMoer.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // 如果点击了popupwindow的外部，popupwindow也会消失
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popMoer.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
                }
                return false;
            }
        });

    }

    private void deleteCell(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                entityList.remove(index);
                notifyDataSetChanged();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        };
        collapse(v, al);
    }
    Animation anim;
    private void collapse(final View v, Animation.AnimationListener al) {
        initialHeight = v.getMeasuredHeight();
        anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.getLayoutParams().height=0;
                    anim.cancel();
                }
                else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al!=null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(300);
        v.startAnimation(anim);
    }


    class ViewHolder{
        CircleImageView HeadImage;//头像
        TextView NickNameTextView;//昵称
        TextView contentTextView;//消息内容
        TextView disposeTextView;//处理人或申请人
        TextView timeTextView;//时间
        RelativeLayout relativeLayout;//跳转
        RelativeLayout timeRelativeLayout;//时间隐藏
        Button consentButton;//确定
        LinearLayout linearLayout;//消息外层
    }

}
