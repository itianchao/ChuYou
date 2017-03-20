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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.MyTopicActivity;
import com.jueda.ndian.activity.me.view.PersonalDetailsActivity;
import com.jueda.ndian.activity.circle.view.TopicDetailsActivity;
import com.jueda.ndian.activity.me.biz.DeleteTopicBiz;
import com.jueda.ndian.entity.TopicEntity;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.view.CircleImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/7.
 * 我的话题
 */
public class MyTopicAdapter extends BaseAdapter{
    private Activity context;
    private ArrayList<TopicEntity> entityList;
    private int screenWidth;
    private RelativeLayout mRelativeLayout;
    private PopupWindow popMoer;
    private View layoutMoer;
    private int initialHeight;//控件高度

    public MyTopicAdapter(Activity context, ArrayList<TopicEntity> entityList, int screenWidth, RelativeLayout mRelativeLayout) {
        this.context=context;
        this.entityList=entityList;
        this.screenWidth=screenWidth;
        this.mRelativeLayout=mRelativeLayout;
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
        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=View.inflate(context, R.layout.item_topic_list,null);
            holder.ContentImageView1=(ImageView)convertView.findViewById(R.id.ContentImageView1);
            holder.ContentImageView2=(ImageView)convertView.findViewById(R.id.ContentImageView2);
            holder.ContentImageView3=(ImageView)convertView.findViewById(R.id.ContentImageView3);
            holder.lookParticulars=(LinearLayout)convertView.findViewById(R.id.lookParticulars);
            holder.HeadImage=(CircleImageView)convertView.findViewById(R.id.HeadImage);
            holder.NameTextView=(TextView)convertView.findViewById(R.id.NameTextView);
            holder.timeTextView=(TextView)convertView.findViewById(R.id.timeTextView);
            holder.contentTextView=(TextView)convertView.findViewById(R.id.contentTextView);
            holder.numberTextView=(TextView)convertView.findViewById(R.id.numberTextView);
            holder.lookNumberTextView=(TextView)convertView.findViewById(R.id.lookNumberTextView);
            holder.imageRelativeLayout=(RelativeLayout)convertView.findViewById(R.id.imageRelativeLayout);
            holder.thumbNumber=(RadioButton)convertView.findViewById(R.id.thumbNumber);
            holder.thumbNumber.setVisibility(View.GONE);
            //设置图片宽高
            int i=screenWidth- Constants.dip2px(context, 20);
            LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) holder.ContentImageView1.getLayoutParams(); // 取控件当前的布局参数
            linearParams1.height=i/3;// 当控件的高强制设成图片高度
            holder.ContentImageView1.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件imageview中
            LinearLayout.LayoutParams linearParams2 = (LinearLayout.LayoutParams) holder.ContentImageView2.getLayoutParams(); // 取控件当前的布局参数
            linearParams2.height=i/3;// 当控件的高强制设成图片高度
            holder.ContentImageView2.setLayoutParams(linearParams2); // 使设置好的布局参数应用到控件imageview中
            LinearLayout.LayoutParams linearParams3 = (LinearLayout.LayoutParams) holder.ContentImageView3.getLayoutParams(); // 取控件当前的布局参数
            linearParams3.height=i/3;// 当控件的高强制设成图片高度
            holder.ContentImageView3.setLayoutParams(linearParams3); // 使设置好的布局参数应用到控件imageview中
            initialHeight = holder.lookParticulars.getMeasuredHeight();
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
            AbsListView.LayoutParams layoutParams=new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
            holder.lookParticulars.setLayoutParams(layoutParams);
        }
        ImageLoaderUtil.ImageLoader(entityList.get(position).getAvatar(), holder.HeadImage);
        holder.imageRelativeLayout.setVisibility(View.VISIBLE);
        /**判断图片有多少张，少于3张要另外处理*/
        if(entityList.get(position).getImg().length>=3){
            holder.ContentImageView1.setVisibility(View.VISIBLE);
            holder.ContentImageView2.setVisibility(View.VISIBLE);
            holder.ContentImageView3.setVisibility(View.VISIBLE);
            holder.numberTextView.setVisibility(View.VISIBLE);
            ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[0]+Constants.QINIU1+(screenWidth/3),holder.ContentImageView1);
            ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[1]+Constants.QINIU1+(screenWidth/3),holder.ContentImageView2);
            ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[2]+Constants.QINIU1+(screenWidth/3), holder.ContentImageView3);
        }else{
            holder.numberTextView.setVisibility(View.GONE);
            if(entityList.get(position).getImg().length==1){
                ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[0]+Constants.QINIU1+(screenWidth/3),holder.ContentImageView1);
                holder.ContentImageView2.setVisibility(View.INVISIBLE);
                holder.ContentImageView3.setVisibility(View.INVISIBLE);
            }else if(entityList.get(position).getImg().length==2){
                ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[0]+Constants.QINIU1+(screenWidth/3),holder.ContentImageView1);
                ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[1]+Constants.QINIU1+(screenWidth/3),holder.ContentImageView2);
                holder.ContentImageView2.setVisibility(View.VISIBLE);
                holder.ContentImageView3.setVisibility(View.INVISIBLE);
            }else{
                holder.imageRelativeLayout.setVisibility(View.GONE);
            }
        }
        holder.NameTextView.setText(entityList.get(position).getName());
        holder.timeTextView.setText(entityList.get(position).getCreat_time());
        holder.contentTextView.setText(entityList.get(position).getContent());
        holder.numberTextView.setText(entityList.get(position).getImg().length + "");
        holder.lookNumberTextView.setText(entityList.get(position).getCount());

        setOnClickTOPIC(holder.lookParticulars, holder.HeadImage, position);
        return convertView;
    }
    /**
     *  @param lookParticulars
     * @param headImage
     * @param position
     */
    private void setOnClickTOPIC(final LinearLayout lookParticulars, CircleImageView headImage, final int position) {
        /**查看话题详情*/
        lookParticulars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TopicDetailsActivity.class);
                intent.putExtra("who", MyTopicActivity.TAG);
                intent.putExtra("id", entityList.get(position).getPid());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        /**长按删除*/
        lookParticulars.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator= (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                if (popMoer != null && popMoer.isShowing()) {
                    popMoer.dismiss();
                } else {
                    layoutMoer =context.getLayoutInflater().inflate(
                            R.layout.pop_delete, null);
                    TextView deleteTextView = (TextView) layoutMoer.findViewById(R.id.deleteTextView);
                    RelativeLayout cancelRelativeLayout = (RelativeLayout) layoutMoer.findViewById(R.id.cancelRelativeLayout);
                    cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popMoer.dismiss();
                        }
                    });
                    //删除单行
                    deleteTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DeleteTopicBiz(context,entityList.get(position).getPid());
                            deleteCell(lookParticulars,position);
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
        /**查看个人详情*/
        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonalDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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

    private void collapse(final View v, Animation.AnimationListener al) {
       initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.getLayoutParams().height=0;
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
        ImageView ContentImageView1;
        ImageView ContentImageView2;
        ImageView ContentImageView3;
        LinearLayout lookParticulars;//查看详情
        CircleImageView HeadImage;//头像
        TextView NameTextView;//昵称
        TextView timeTextView;//时间
        TextView contentTextView;//内容
        TextView numberTextView;//图片数量
        TextView lookNumberTextView;//浏览次数
        RelativeLayout imageRelativeLayout;//没有图片就隐藏
        RadioButton thumbNumber;//我的隐藏
    }
}
