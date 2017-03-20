package com.jueda.ndian.activity.circle.adapter;


import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.circle.view.CircleContentActivity;
import com.jueda.ndian.activity.circle.view.CircleParticularsActivity;
import com.jueda.ndian.activity.circle.view.CircleAllTaskActivity;
import com.jueda.ndian.activity.circle.view.TopicDetailsActivity;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.circle.biz.ApplyAddBiz;
import com.jueda.ndian.activity.circle.biz.ThumbBiz;
import com.jueda.ndian.entity.TopicEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.CircleImageView;
import com.jueda.ndian.view.RoundCornerImageView;

import java.util.ArrayList;

/**
 *
 * Created by Administrator on 2016/3/31.
 * q圈子适配器
 */
public class CircleContentAdapter extends BaseAdapter{
    private Activity context;
    private ArrayList<TopicEntity> entityList;
    private final int DETAILS=1;
    private final int NAVIGATION=2;
    private final int TOPIC=3;
    private int screenWidth;
    private Handler handler;
    private OnChangeList onChangeList;
    /**
     * 记录加圈按钮
     */
    private Button copyAddButton;

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                /**申请成功*/
                case Constants.ON_SUCCEED:
                    if(copyAddButton!=null){
                        copyAddButton.setVisibility(View.GONE);
                    }
                    new ToastShow(context,context.getResources().getString(R.string.Application_is_successful),1000);
                    CircleContentActivity.entityList.get(0).setIs_member("1");
                    if( CharityCircleFragment.instance!=null){
                        CharityCircleFragment.instance.setData();
                    }
                    break;
                /**申请失败，拒绝外人加入*/
                case Constants.FAILURE:
                    new ToastShow(context,context.getResources().getString(R.string.The_circle_has_set_forbid_anyone_to_join_try_to_other_circle),1000);
                    break;
                /**失败*/
                case Constants.FAILURE_THREE:
                    new ToastShow(context,context.getResources().getString(R.string.Add_failure),1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(context,false);
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;
            }
        }
    };


    public CircleContentAdapter(Activity context, ArrayList<TopicEntity> entityList, Handler handler, int screenWidth) {
        this.context=context;
        this.entityList=entityList;
        this.handler=handler;
        this.screenWidth=screenWidth;
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
    public int getItemViewType(int position) {
        if(position==0){
            return DETAILS;
        }else if(position==1){
            return NAVIGATION;
        }
        return TOPIC;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder1=null;
        ViewHolder2 holder2=null;
        ViewHolder3 holder3=null;
        int type=getItemViewType(position);
        if(convertView==null) {
            switch (type) {
                //查看圈子详情
                case DETAILS:
                    convertView=View.inflate(context, R.layout.circle_content_details_item,null);
                    holder1=new ViewHolder1();
                    holder1.detailsRelativeLayout=(RelativeLayout)convertView.findViewById(R.id.detailsRelativeLayout);
                    holder1.HeadImage=(RoundCornerImageView)convertView.findViewById(R.id.HeadImage);
                    holder1.beanTextView=(TextView)convertView.findViewById(R.id.beanTextView);
                    holder1.numberTextView=(TextView)convertView.findViewById(R.id.numberTextView);
                    holder1.addButton=(Button)convertView.findViewById(R.id.addButton);
                    convertView.setTag(holder1);
                    break;
                //导航
                case NAVIGATION:
                    convertView=View.inflate(context, R.layout.circle_content_navigation,null);
                    holder2=new ViewHolder2();
                    holder2.chatRelativeLayout=(RelativeLayout)convertView.findViewById(R.id.chatRelativeLayout);
                    holder2.taskRelativeLayout=(RelativeLayout)convertView.findViewById(R.id.taskRelativeLayout);
                    convertView.setTag(holder2);
                    break;
                //话题内容
                case TOPIC:
                    convertView=View.inflate(context, R.layout.item_topic_list,null);
                    holder3=new ViewHolder3();
                    holder3.ContentImageView1=(ImageView)convertView.findViewById(R.id.ContentImageView1);
                    holder3.ContentImageView2=(ImageView)convertView.findViewById(R.id.ContentImageView2);
                    holder3.ContentImageView3=(ImageView)convertView.findViewById(R.id.ContentImageView3);
                    holder3.lookParticulars=(LinearLayout)convertView.findViewById(R.id.lookParticulars);
                    holder3.HeadImage=(CircleImageView)convertView.findViewById(R.id.HeadImage);
                    holder3.NameTextView=(TextView)convertView.findViewById(R.id.NameTextView);
                    holder3.timeTextView=(TextView)convertView.findViewById(R.id.timeTextView);
                    holder3.contentTextView=(TextView)convertView.findViewById(R.id.contentTextView);
                    holder3.numberTextView=(TextView)convertView.findViewById(R.id.numberTextView);
                    holder3.lookNumberTextView=(TextView)convertView.findViewById(R.id.lookNumberTextView);
                    holder3.imageRelativeLayout=(RelativeLayout)convertView.findViewById(R.id.imageRelativeLayout);
                    holder3.thumbNumber=(RadioButton)convertView.findViewById(R.id.thumbNumber);
                    //设置图片宽高
                    int i=screenWidth- Constants.dip2px(context, 20);
                    LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) holder3.ContentImageView1.getLayoutParams(); // 取控件当前的布局参数
                    linearParams1.height=i/3;// 当控件的高强制设成图片高度
                    holder3.ContentImageView1.setLayoutParams(linearParams1); // 使设置好的布局参数应用到控件imageview中
                    LinearLayout.LayoutParams linearParams2 = (LinearLayout.LayoutParams) holder3.ContentImageView2.getLayoutParams(); // 取控件当前的布局参数
                    linearParams2.height=i/3;// 当控件的高强制设成图片高度
                    holder3.ContentImageView2.setLayoutParams(linearParams2); // 使设置好的布局参数应用到控件imageview中
                    LinearLayout.LayoutParams linearParams3 = (LinearLayout.LayoutParams) holder3.ContentImageView3.getLayoutParams(); // 取控件当前的布局参数
                    linearParams3.height=i/3;// 当控件的高强制设成图片高度
                    holder3.ContentImageView3.setLayoutParams(linearParams3); // 使设置好的布局参数应用到控件imageview中
                    convertView.setTag(holder3);
                    break;
            }
        }else{
            switch (type){
                case DETAILS:
                    holder1= (ViewHolder1) convertView.getTag();
                    break;
                case NAVIGATION:
                    holder2= (ViewHolder2) convertView.getTag();
                    break;
                case TOPIC:
                    holder3= (ViewHolder3) convertView.getTag();
                    break;
            }
        }

        switch (type){
            case DETAILS:
                ImageLoaderUtil.ImageLoader(entityList.get(position).getCircleList().get(0).getAvatar(), holder1.HeadImage,R.drawable.head_circle);//圈子头像
                holder1.beanTextView.setText("圈主：" + entityList.get(position).getCircleList().get(0).getOwner_name());//圈子爱心豆
                holder1.numberTextView.setText(entityList.get(position).getCircleList().get(0).getCdesc());

                /**判断是否为该圈成员*/
                if(entityList.get(position).getCircleList().get(0).getIs_member().equals("1")){
                    holder1.addButton.setVisibility(View.GONE);
                }else{
                    holder1.addButton.setVisibility(View.VISIBLE);
                }
                setOnClickDETAILS(position,holder1.detailsRelativeLayout,holder1.addButton);
                break;
            case NAVIGATION:

                setOnClickNAVIGATION(position, holder2.taskRelativeLayout, holder2.chatRelativeLayout);
                break;
            case TOPIC:
                ImageLoaderUtil.ImageLoader(entityList.get(position).getAvatar(),holder3.HeadImage,R.drawable.head_portrait);
                holder3.imageRelativeLayout.setVisibility(View.VISIBLE);
                /**判断图片有多少张，少于3张要另外处理*/
                if(entityList.get(position).getImg().length>=3){
                    if(entityList.get(position).getImg().length==3){
                        holder3.numberTextView.setVisibility(View.GONE);
                    }else {
                        holder3.numberTextView.setVisibility(View.VISIBLE);
                    }
                    holder3.ContentImageView1.setVisibility(View.VISIBLE);
                    holder3.ContentImageView2.setVisibility(View.VISIBLE);
                    holder3.ContentImageView3.setVisibility(View.VISIBLE);
                    ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[0]+Constants.QINIU1+(screenWidth/3), holder3.ContentImageView1);
                    ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[1]+Constants.QINIU1+(screenWidth/3), holder3.ContentImageView2);
                    ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[2]+Constants.QINIU1+(screenWidth/3), holder3.ContentImageView3);

                }else{
                    holder3.numberTextView.setVisibility(View.GONE);
                    if(entityList.get(position).getImg().length==1){
                        ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[0]+Constants.QINIU1+(screenWidth/3),holder3.ContentImageView1);
                        holder3.ContentImageView2.setVisibility(View.INVISIBLE);
                        holder3.ContentImageView3.setVisibility(View.INVISIBLE);
                    }else if(entityList.get(position).getImg().length==2){
                        ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[0]+Constants.QINIU1+(screenWidth/3),holder3.ContentImageView1);
                        ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()[1]+Constants.QINIU1+(screenWidth/3),holder3.ContentImageView2);
                        holder3.ContentImageView2.setVisibility(View.VISIBLE);
                        holder3.ContentImageView3.setVisibility(View.INVISIBLE);
                    }else{
                        holder3.imageRelativeLayout.setVisibility(View.GONE);
                    }
                }
                holder3.NameTextView.setText(entityList.get(position).getName());
                holder3.timeTextView.setText(entityList.get(position).getCreat_time());
                holder3.contentTextView.setText(entityList.get(position).getContent());
                holder3.numberTextView.setText(entityList.get(position).getImg().length+"");
                holder3.lookNumberTextView.setText(entityList.get(position).getCount());
                if(entityList.get(position).getIs_like().equals("1")){
                    holder3.thumbNumber.setEnabled(false);
                    holder3.thumbNumber.setChecked(true);
                }else{
                    holder3.thumbNumber.setEnabled(true);
                    holder3.thumbNumber.setChecked(false);
                }
                holder3.thumbNumber.setText(entityList.get(position).getLike_count());

                setOnClickTOPIC( holder3.thumbNumber, holder3.lookParticulars, position);
                break;
        }

        return convertView;
    }



    /**
     * @param thumbNumber
     * @param lookParticulars
     * @param position
     */
    private void setOnClickTOPIC(final RadioButton thumbNumber, LinearLayout lookParticulars, final int position) {
        /**查看话题详情*/
        lookParticulars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,TopicDetailsActivity.class);
                intent.putExtra("who", "CircleContentActivity");
                intent.putExtra("id",entityList.get(position).getPid());
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
                new ThumbBiz(context,handler,entityList.get(position).getPid(),thumbNumber);
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

    /**
     * 查看慈善圈详情
     * @param position
     * @param detailsRelativeLayout
     * @param auditButton
     * @param addButton
     */
    private void setOnClickDETAILS(final int position, RelativeLayout detailsRelativeLayout,final Button addButton) {
        /**查看圈子详情*/
        detailsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CircleParticularsActivity.class);
                intent.putExtra("entity", entityList.get(position).getCircleList().get(0));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        /**加入该圈*/
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyAddButton=addButton;
                new ApplyAddBiz(context,myHandler,entityList.get(position).getCircleList().get(0).getCid());

            }
        });
    }

    /**
     * 导航点击事件
     * @param position
     * @param taskRelativeLayout
     * @param chatRelativeLayout
     */
    private void setOnClickNAVIGATION(final int position, RelativeLayout taskRelativeLayout, RelativeLayout chatRelativeLayout) {
        //跳转到任务界面
        taskRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**判断是否是该圈成员*/
                if(entityList.get(position).getCircleList().get(0).getIs_member().equals("1")){
                    Intent intent=new Intent(context, CircleAllTaskActivity.class);
                    intent.putExtra("cid",entityList.get(position).getCircleList().get(0).getCid());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    new ToastShow(context,context.getResources().getString(R.string.To_join_the_circle_to_view_the_task),1500);
                }

            }
        });
        //跳转到聊天界面
        chatRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**判断是否是该圈成员*/
                if(entityList.get(position).getCircleList().get(0).getIs_member().equals("1")){
                    handler.sendEmptyMessage(CircleContentActivity.CHAT);
                }else{
                    new ToastShow(context,context.getResources().getString(R.string.To_join_the_circle_to_enter_the_chat_room),1500);
                }
            }
        });
    }

    class ViewHolder1{
        RelativeLayout detailsRelativeLayout;
        Button addButton;//加圈
        RoundCornerImageView HeadImage;//圈子头像
        TextView beanTextView;//圈主昵称
        TextView numberTextView;//圈子人数
    }
    class ViewHolder2{
        RelativeLayout taskRelativeLayout;//任务
        RelativeLayout chatRelativeLayout;//聊天
    }
    class ViewHolder3{
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
        RelativeLayout imageRelativeLayout;//没有图片时隐藏
        RadioButton thumbNumber;//点赞数量
    }


}
