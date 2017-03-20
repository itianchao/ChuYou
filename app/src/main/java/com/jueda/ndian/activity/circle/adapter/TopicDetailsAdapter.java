package com.jueda.ndian.activity.circle.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.view.CircleContentActivity;
import com.jueda.ndian.activity.me.view.MyTopicActivity;
import com.jueda.ndian.activity.me.view.PersonalDetailsActivity;
import com.jueda.ndian.activity.circle.view.TopicDetailsActivity;
import com.jueda.ndian.activity.fragment.HomeFragment;
import com.jueda.ndian.entity.TopicCommentsEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.Image_look_preview;
import com.jueda.ndian.utils.getCurrentTime;
import com.jueda.ndian.view.CircleImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/8.
 * 话题详情
 */
public class TopicDetailsAdapter extends BaseAdapter{
    private Activity context;
    private ArrayList<TopicCommentsEntity> list;
    private final int TYPE1=1;//头部消息图片
    private final int TYPE2=2;//评论

    private PopupWindow popMoer;
    private View layoutMoer;
    private float screenWidth;
    private String who;//判断是圈内的话题（CircleContentActivity）还是我的话题（MyTopicActivity）或圈外话题（MainActivity）
    private Handler handler;

    public TopicDetailsAdapter(Activity context, ArrayList<TopicCommentsEntity> list, String who, Handler handler, float screenWidth) {
        this.context=context;
        this.list=list;
        this.who=who;
        this.handler=handler;
        this.screenWidth=screenWidth;
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
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
       if(position==0){
           return TYPE1;
       }else{
           return TYPE2;
       }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type=getItemViewType(position);
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        if(convertView==null) {
            switch (type) {
                case TYPE1:
                    convertView=View.inflate(context, R.layout.topic_details_top_item,null);
                    holder1=new ViewHolder1();
                    holder1.linearLayout= (LinearLayout) convertView.findViewById(R.id.linearLayout);
                    holder1.fromRelativeLayout=(RelativeLayout)convertView.findViewById(R.id.fromRelativeLayout);
                    holder1.headImageView=(CircleImageView)convertView.findViewById(R.id.headImageView);
                    holder1.NickNameTextView=(TextView)convertView.findViewById(R.id.NickNameTextView);
                    holder1.ishostTextView=(TextView)convertView.findViewById(R.id.ishostTextView);
                    holder1.lookNumberTextView=(TextView)convertView.findViewById(R.id.lookNumberTextView);
                    holder1.contentTextView=(TextView)convertView.findViewById(R.id.contentTextView);
                    holder1.commentNumberTextView=(TextView)convertView.findViewById(R.id.commentNumberTextView);
                    holder1.sofa=(RelativeLayout)convertView.findViewById(R.id.sofa);
                    holder1.CircleName=(TextView)convertView.findViewById(R.id.CircleName);
                    holder1.timeTextView=(TextView)convertView.findViewById(R.id.timeTextView);
                    holder1.gridLayout=(GridLayout)convertView.findViewById(R.id.gridLayout);
                    /**设置图片*/
//                    if(who.equals(CircleContentActivity.TAG)||who.equals(MyTopicActivity.TAG)||who.equals(MainActivity.TAG)) {
                        String img[] = list.get(position).getImg();
                        for (int i = 0; i < img.length; i++) {
                            LinearLayout.LayoutParams imgvwDimens =
                                    new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
                            imgvwDimens.height = list.get(0).getImg_height()[i];
                            ImageView im = new ImageView(context);
                            //图片充满界面
                            im.setLayoutParams(imgvwDimens);
                            im.setScaleType(ImageView.ScaleType.FIT_XY);
                            im.setPadding(0, 0, 0, Constants.dip2px(context, 15));
                            im.setCropToPadding(true);
                            ImageLoaderUtil.ImageLoader(img[i] + Constants.QINIU1 + (int) screenWidth, im);
                            holder1.linearLayout.addView(im);
                            setLookPicture(position, i, im);
                        }
//                    }else{
//                        String img[] = list.get(position).getImg();
//                        for (int i = 0; i < img.length; i++) {
//                            LinearLayout.LayoutParams imgvwDimens =
//                                    new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
//                            int s = (int) ((screenWidth-Constants.dip2px(context, 15))/3);
//                            imgvwDimens.width = s;
//                            imgvwDimens.height=s;
//                            ImageView im = new ImageView(context);
//                            //图片充满界面
//                            im.setLayoutParams(imgvwDimens);
//                            im.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                            im.setPadding(0, 0, Constants.dip2px(context, 15), Constants.dip2px(context, 15));
//                            im.setCropToPadding(true);
//                            ImageLoaderUtil.ImageLoader(img[i] + Constants.QINIU1 + (int) (screenWidth/3), im);
//                            holder1.gridLayout.addView(im);
//                            setLookPicture(position, i, im);
//                        }
//                    }
                    convertView.setTag(holder1);
                    break;
                case TYPE2:
                    convertView=View.inflate(context, R.layout.topic_details_comments,null);
                    holder2=new ViewHolder2();
                    holder2.commentsLinearLayout=(LinearLayout)convertView.findViewById(R.id.commentsLinearLayout);
                    holder2.headImageView=(CircleImageView)convertView.findViewById(R.id.headImageView);
                    holder2.NickNameTextView=(TextView)convertView.findViewById(R.id.NickNameTextView);
                    holder2.contentTextView=(TextView)convertView.findViewById(R.id.contentTextView);
                    holder2.timeTextView=(TextView)convertView.findViewById(R.id.timeTextView);
                    convertView.setTag(holder2);
                    break;
            }
        }else{
            switch (type) {
                case TYPE1:
                    holder1= (ViewHolder1) convertView.getTag();
                    break;
                case TYPE2:
                    holder2=(ViewHolder2)convertView.getTag();
                    break;
            }
        }
        switch (type) {
            case TYPE1:
                /**设置基本数据*/
                ImageLoaderUtil.ImageLoader(list.get(position).getAvatar(), holder1.headImageView, R.drawable.head_portrait);
                holder1.NickNameTextView.setText(list.get(position).getName());
                holder1.lookNumberTextView.setText(list.get(position).getCount());
                holder1.contentTextView.setText(list.get(position).getContent());

                holder1.CircleName.setText(context.getResources().getString(R.string.from)+"："+list.get(position).getCname());
                holder1.timeTextView.setText(getCurrentTime.getCurrentTime(Long.parseLong(list.get(position).getCreat_time())));
                /**来自某个圈*/
                if(who.equals(CircleContentActivity.TAG)){
                    holder1.fromRelativeLayout.setVisibility(View.GONE);
                }
                /**评论数为0就显示抢沙发*/
                if(list.size()<2){
                    holder1.sofa.setVisibility(View.VISIBLE);
                }else {
                    holder1.sofa.setVisibility(View.GONE);
                }
                holder1.commentNumberTextView.setText(context.getResources().getString(R.string.Comment)+"("+(list.get(position).getCount_comments())+")");

                /**捐赠是否为官方捐赠*/
                if(who.equals(HomeFragment.TAG)||who.equals(MainActivity.TAG)){
                    if (list.get(0).getUid().equals("1")) {
                        holder1.ishostTextView.setVisibility(View.VISIBLE);
                        holder1.ishostTextView.setText(context.getResources().getString(R.string.The_official));
                        holder1.fromRelativeLayout.setVisibility(View.GONE);
                    }else if (list.get(position).getIs_host().equals("0")) {
                        holder1.ishostTextView.setVisibility(View.GONE);
                    } else {
                        holder1.ishostTextView.setVisibility(View.VISIBLE);
                        holder1.ishostTextView.setText(context.getResources().getString(R.string.Circle_the_main));
                    }
                    if(!list.get(0).getUid().equals("1")) {
                        setOnClickTYPE1(holder1.fromRelativeLayout, position, holder1.headImageView, holder1.NickNameTextView);
                    }
                }else{
                    /**是否为圈主*/
                    if (list.get(position).getIs_host().equals("0")) {
                        holder1.ishostTextView.setVisibility(View.GONE);
                    } else {
                        holder1.ishostTextView.setVisibility(View.VISIBLE);
                        holder1.ishostTextView.setText(context.getResources().getString(R.string.Circle_the_main));
                    }
                    setOnClickTYPE1(holder1.fromRelativeLayout, position, holder1.headImageView, holder1.NickNameTextView);
                }
                    setCopyContent(holder1.contentTextView,list.get(position).getContent());
                break;
            case TYPE2:
                /**设置评论信息*/
                if(list.size()>=2){
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
                    ImageLoaderUtil.ImageLoader(list.get(position).getPavatar(), holder2.headImageView,R.drawable.head_portrait);
                    holder2.NickNameTextView.setText(list.get(position).getPname());

                    holder2.timeTextView.setText(getCurrentTime.getCurrentTime(Long.parseLong(list.get(position).getPcreat_time())));
                    setOnClickTYPE2(position, holder2.commentsLinearLayout, holder2.headImageView, holder2.NickNameTextView);
                }
                break;
        }
        return convertView;
    }

    /***
     * 复制内容
     * @param contentTextView
     * @param content
     */
    private void setCopyContent(final TextView contentTextView, final String content) {
        contentTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator= (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                copy(contentTextView, content);
                return false;
            }
        });
    }
    /**
     * 浏览图片
     * @param position
     * @param i 第几张图片
     * @param im 控件
     */
    private void setLookPicture(final int position, final int i, ImageView im) {
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image_look_preview.look(context,i,list.get(position).getPhotoArrayList());
            }
        });
    }

    /**
     * 类型一
     * @param fromRelativeLayout
     * @param position
     * @param headImageView
     * @param nickNameTextView
     */
    private void setOnClickTYPE1(RelativeLayout fromRelativeLayout, final int position, CircleImageView headImageView, TextView nickNameTextView) {
        /**进入慈善圈详情*/
        fromRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CircleContentActivity.class);
                intent.putExtra("cid", list.get(position).getCid());
                intent.putExtra("name", list.get(position).getCname());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        /**进入个人信息*/
        headImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**从个人详情或者我的话题中进入则不能查看我的信息。*/
                if(!list.get(position).getUid().equals(ConstantsUser.userEntity.getUid()+"")) {
                        Intent intent = new Intent(context, PersonalDetailsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("uid", list.get(position).getUid());
                        context.startActivity(intent);
                }
            }
        });
        nickNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!list.get(position).getUid().equals(ConstantsUser.userEntity.getUid()+"")) {
                    Intent intent = new Intent(context, PersonalDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("uid", list.get(position).getUid());
                    context.startActivity(intent);
                }
            }
        });
    }

    /**
     * 类型二
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

    /***
     * 复制内容
     * @param view
     * @param content
     */
    public void copy(View view, final String content){
        if (popMoer != null && popMoer.isShowing()) {
            popMoer.dismiss();
        } else {
            layoutMoer =context.getLayoutInflater().inflate(
                    R.layout.pop_delete, null);
            TextView deleteTextView = (TextView) layoutMoer.findViewById(R.id.deleteTextView);
            deleteTextView.setText("复制");
            RelativeLayout cancelRelativeLayout = (RelativeLayout) layoutMoer.findViewById(R.id.cancelRelativeLayout);
            cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popMoer.dismiss();
                }
            });
            //复制
            deleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取剪贴板管理服务
                    ClipboardManager cm =(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    //将文本数据复制到剪贴板
                    cm.setText(content);
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
            popMoer.showAtLocation(view, Gravity.BOTTOM, 0, 0);

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
    }

    class ViewHolder1{
        LinearLayout linearLayout;//图片
        GridLayout gridLayout;//图片
        RelativeLayout fromRelativeLayout;//来自某某圈
        CircleImageView headImageView;//发表人头像
        TextView NickNameTextView;//发表人名称
        TextView ishostTextView;//是否是圈主
        TextView lookNumberTextView;//浏览数
        TextView contentTextView;//发表的内容
        TextView commentNumberTextView;//评论数量
        RelativeLayout sofa;//抢沙发
        TextView CircleName;//圈名
        TextView timeTextView;//时间

    }
    class ViewHolder2{
        LinearLayout commentsLinearLayout;//评论
        CircleImageView headImageView;//头像
        TextView NickNameTextView;//昵称
        TextView contentTextView;//内容
        TextView timeTextView;//时间
    }
}
