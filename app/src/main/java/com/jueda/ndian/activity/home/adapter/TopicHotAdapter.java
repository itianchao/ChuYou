package com.jueda.ndian.activity.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.view.TopicDetailsActivity;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.me.view.MyTopicActivity;
import com.jueda.ndian.activity.circle.biz.ThumbBiz;
import com.jueda.ndian.entity.TopicEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.view.CircleImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/15.
 * 热门话题列表
 */
public class TopicHotAdapter extends BaseAdapter{
    private Activity context;
    private ArrayList<TopicEntity> entityList;
    private int initialHeight;//控件高度
    private OnChangeList onChangeList;

    public TopicHotAdapter(Activity activity, ArrayList<TopicEntity> entityList) {
        this.context=activity;
        this.entityList=entityList;
    }
    public void updata(ArrayList<TopicEntity> entityList){
        this.entityList=entityList;
        notifyDataSetChanged();
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
            initialHeight = holder.lookParticulars.getMeasuredHeight();
            //设置图片宽高
            int i=MainActivity.instance.screenWidth- Constants.dip2px(context, 20);
            LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) holder.ContentImageView1.getLayoutParams(); // 取控件当前的布局参数
            linearParams1.height=i/3;// 当控件的高强制设成图片高度
            holder.ContentImageView1.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件imageview中
            LinearLayout.LayoutParams linearParams2 = (LinearLayout.LayoutParams) holder.ContentImageView2.getLayoutParams(); // 取控件当前的布局参数
            linearParams2.height=i/3;// 当控件的高强制设成图片高度
            holder.ContentImageView2.setLayoutParams(linearParams2); // 使设置好的布局参数应用到控件imageview中
            LinearLayout.LayoutParams linearParams3 = (LinearLayout.LayoutParams) holder.ContentImageView3.getLayoutParams(); // 取控件当前的布局参数
            linearParams3.height=i/3;// 当控件的高强制设成图片高度
            holder.ContentImageView3.setLayoutParams(linearParams3); // 使设置好的布局参数应用到控件imageview中

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
            holder.lookParticulars.getLayoutParams().height=initialHeight;
        }

        holder.imageRelativeLayout.setVisibility(View.VISIBLE);
        /**判断图片有多少张，少于3张要另外处理*/
        if(entityList.get(position).getImg().length>=3){
            holder.ContentImageView1.setVisibility(View.VISIBLE);
            holder.ContentImageView2.setVisibility(View.VISIBLE);
            holder.ContentImageView3.setVisibility(View.VISIBLE);
            holder.numberTextView.setVisibility(View.VISIBLE);
            ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[0]+Constants.QINIU1+(MainActivity.instance.screenWidth/3),holder.ContentImageView1);
            ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[1]+Constants.QINIU1+(MainActivity.instance.screenWidth/3),holder.ContentImageView2);
            ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[2]+Constants.QINIU1+(MainActivity.instance.screenWidth/3), holder.ContentImageView3);
        }else{
            holder.numberTextView.setVisibility(View.GONE);
            if(entityList.get(position).getImg().length==1){
                ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[0]+Constants.QINIU1+(MainActivity.instance.screenWidth/3),holder.ContentImageView1);
                holder.ContentImageView2.setVisibility(View.INVISIBLE);
                holder.ContentImageView3.setVisibility(View.INVISIBLE);
            }else if(entityList.get(position).getImg().length==2){
                ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[0]+Constants.QINIU1+(MainActivity.instance.screenWidth/3),holder.ContentImageView1);
                ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[1]+Constants.QINIU1+(MainActivity.instance.screenWidth/3),holder.ContentImageView2);
                holder.ContentImageView2.setVisibility(View.VISIBLE);
                holder.ContentImageView3.setVisibility(View.INVISIBLE);
            }else{
                holder.imageRelativeLayout.setVisibility(View.GONE);
            }
        }
        ImageLoaderUtil.ImageLoader(entityList.get(position).getAvatar(), holder.HeadImage);
        holder.NameTextView.setText(entityList.get(position).getName());
        holder.timeTextView.setText(entityList.get(position).getCreat_time());
        holder.contentTextView.setText(entityList.get(position).getContent());
        holder.numberTextView.setText(entityList.get(position).getImg().length + "");
        holder.lookNumberTextView.setText(entityList.get(position).getCount());
        if(entityList.get(position).getIs_like().equals("1")){
            holder.thumbNumber.setEnabled(false);
            holder.thumbNumber.setChecked(true);
        }else{
            holder.thumbNumber.setEnabled(true);
            holder.thumbNumber.setChecked(false);
        }
        holder.thumbNumber.setText(entityList.get(position).getLike_count());
        setOnClickTOPIC(holder.thumbNumber, holder.lookParticulars, holder.HeadImage, position);
        return convertView;
    }
    /**
     * @param thumbNumber
     * @param lookParticulars
     * @param headImage
     * @param position
     */
    private void setOnClickTOPIC(final RadioButton thumbNumber, final LinearLayout lookParticulars, CircleImageView headImage, final int position) {
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
        /**点赞*/
        thumbNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entityList.get(position).setIs_like("1");
                entityList.get(position).setLike_count((Integer.parseInt(entityList.get(position).getLike_count()) + 1) + "");
                thumbNumber.setText(entityList.get(position).getLike_count());
                thumbNumber.setChecked(true);
                thumbNumber.setEnabled(false);
                new ThumbBiz(context, handler, entityList.get(position).getPid(),thumbNumber);
                if(onChangeList!=null){
                    onChangeList.change(entityList);
                }
            }
        });

    }
    /***
     * 点赞后通知改变集合内容
     * @param onChangeList
     */
    public void setOnChangeList(OnChangeList onChangeList){
        this.onChangeList=onChangeList;
    }
    public interface OnChangeList{
        void change(ArrayList<TopicEntity> entityLis);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==Constants.USER_FAILURE){
                /**用户失效*/
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(context,false);
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
            }
        }
    };
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
        RadioButton thumbNumber;//点赞数量
    }
}
