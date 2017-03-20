package com.jueda.ndian.activity.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.jueda.ndian.MainActivity;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.view.AllTaskActivity;
import com.jueda.ndian.activity.home.view.CommodityDetailsActivity;
import com.jueda.ndian.activity.home.view.TaskExchangeActivity;
import com.jueda.ndian.activity.home.view.TaskShareActivity;
import com.jueda.ndian.activity.home.view.TravelRouteActivity;
import com.jueda.ndian.activity.circle.view.TopicDetailsActivity;
import com.jueda.ndian.activity.home.view.TopicHotActivity;
import com.jueda.ndian.activity.home.view.VideoActivity;
import com.jueda.ndian.activity.home.view.WebActivity;
import com.jueda.ndian.banner.NetworkImageHolderView;
import com.jueda.ndian.entity.ADInfo;
import com.jueda.ndian.entity.TravelEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.view.VerticalSwitchTextView;

import java.util.ArrayList;

/**
 * 首页适配器
 * Created by Administrator on 2016/7/2.
 */
public class HomeAdapter extends BaseAdapter implements OnItemClickListener{
    private Activity context;
    private ArrayList<TravelEntity> topicList;
    public  final int TAGE1=1;//banner图
    public final int TAGE2=2;//跳转任务和签到
    public final int TAGE3=3;//话题列表

    private double screenWidth;//屏幕宽度
    private ArrayList<ADInfo> infos;//banner数据

    private RelativeLayout relativeLayout;
    ViewHolder1 holder1=null;
    ViewHolder2 holder2=null;
    ViewHolder3 holder3=null;
    //跑马灯
    private  ArrayList<String> list;


    public HomeAdapter(Activity context, ArrayList<TravelEntity> topicList, double screenWidth, ArrayList<ADInfo> infos, RelativeLayout relativeLayout, ArrayList<String> list) {
        this.context=context;
        this.topicList=topicList;
        this.screenWidth=screenWidth;
        this.infos=infos;
        this.relativeLayout=relativeLayout;//相对位置
        this.list=list;
    }

    public void updata(ArrayList<TravelEntity> topicList,ArrayList<ADInfo> infos,ArrayList<String> list){
        this.topicList=topicList;
        this.infos=infos;
        this.list=list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return topicList.size();
    }

    @Override
    public Object getItem(int position) {
        return topicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TAGE1;
        }else if(position==1){
            return TAGE2;
        }else{
            return TAGE3;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type=getItemViewType(position);

        if(convertView==null) {
            switch (type){
                case TAGE1:
                    holder1=new ViewHolder1();
                    convertView=View.inflate(context, R.layout.home_banner_item,null);
                    holder1.ad_view=(ConvenientBanner)convertView.findViewById(R.id.convenientBanner);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder1.ad_view.getLayoutParams();
                    layoutParams.height= (int) (screenWidth/2.4);
                    holder1.ad_view.setLayoutParams(layoutParams);
                    if(infos.size()>0){
                        addBanner(holder1.ad_view);
                    }
                    convertView.setTag(holder1);
                    break;
                case TAGE2:
                    holder2=new ViewHolder2();
                    convertView=View.inflate(context, R.layout.home_task_item,null);
                    holder2.task=(LinearLayout)convertView.findViewById(R.id.task);
                    holder2.vertical_switch_textview=(VerticalSwitchTextView)convertView.findViewById(R.id.vertical_switch_textview);
                    holder2.topic=(LinearLayout)convertView.findViewById(R.id.topic);
                    holder2.task_image=(ImageView)convertView.findViewById(R.id.task_image);
                    holder2.exchange=(LinearLayout)convertView.findViewById(R.id.exchange);
                    holder2.share=(LinearLayout)convertView.findViewById(R.id.share);
                    convertView.setTag(holder2);
                    break;
                case TAGE3:
                    holder3=new ViewHolder3();
                    convertView=View.inflate(context, R.layout.item_home_travel,null);
                    holder3.image=(ImageView)convertView.findViewById(R.id.image);
                    holder3.TitleTextView=(TextView)convertView.findViewById(R.id.TitleTextView);
                    holder3.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
                    holder3.lookCount=(TextView)convertView.findViewById(R.id.lookCount);
                    holder3.commentCount=(TextView)convertView.findViewById(R.id.commentCount);
                    holder3.money=(TextView)convertView.findViewById(R.id.money);
                    convertView.setTag(holder3);
                    break;
            }
        }else{
            switch (type){
                case TAGE1:
                    holder1= (ViewHolder1) convertView.getTag();
                    break;
                case TAGE2:
                    holder2= (ViewHolder2) convertView.getTag();
                    break;
                case TAGE3:
                    holder3= (ViewHolder3) convertView.getTag();
                    break;
            }
        }
        switch (type){
            case TAGE1:

                break;
            case TAGE2:

                if(list.size()<=0||list==null){
                    holder2.vertical_switch_textview.setVisibility(View.GONE);
                }else{
                    holder2.vertical_switch_textview.setVisibility(View.VISIBLE);
                    holder2.vertical_switch_textview.setTextContent(list);
                }

                setOnClickTage2(holder2.task,holder2.topic,holder2.exchange,holder2.share);
                break;
            case TAGE3:
                ImageLoaderUtil.ImageLoader(topicList.get(position).getBig_img()+Constants.QINIU1+Constants.dip2px(context,90), holder3.image);
                holder3.TitleTextView.setText(topicList.get(position).getTitle());
                holder3.commentCount.setText(topicList.get(position).getComment_count());
                holder3.lookCount.setText(topicList.get(position).getCount());
                holder3.money.setText(topicList.get(position).getMoney() + context.getResources().getString(R.string.dedication));
                new ChangeText(holder3.money.getText().toString(),context.getResources().getColor(R.color.text_love),holder3.money,0,holder3.money.getText().toString().length()-3);
                setOnClickTage3(position, holder3.relativeLayout);
                break;
        }

        return convertView;
    }

    /**
     * 添加
     * @param ad_view
     */
    private void addBanner(ConvenientBanner convenientBanner) {
        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        },infos) //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.kuang_corners_white_five, R.drawable.kuang_corners_red_dian_five})
                        //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
//                .setOnPageChangeListener(this)//监听翻页事件
                .setOnItemClickListener(this);
        convenientBanner.getViewPager().setPageTransformer(true, new AccordionTransformer());
        convenientBanner.setScrollDuration(1500);
        convenientBanner.startTurning(4000);
    }

    /**
     * 打开banner图
     */
    public void startBanner(){
        if(holder1!=null&&infos.size()>0){
            holder1.ad_view.startTurning(4000);
        }
    }

    /***
     * 关闭banner图
     */
    public void stopBanner(){
        if(holder1!=null&&infos.size()>0){
            holder1.ad_view.stopTurning();
        }
    }
    /***
     * 进入旅游详情
     * @param position
     * @param relativeLayout
     */
    private void setOnClickTage3(final int position, RelativeLayout relativeLayout) {
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, TravelRouteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("did",topicList.get(position).getDid());
                context.startActivity(intent);
            }
        });
    }

    /****
     * 点击进入做任务，签到
     * @param sign
     * @param task
     * @param topic
     * @param exchange
     * @param share
     */
    private void setOnClickTage2(LinearLayout task, LinearLayout topic, LinearLayout exchange, LinearLayout share) {
        /**进入任务*/
        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, AllTaskActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        /**话题列表*/
        topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, TopicHotActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        /**兑换商品*/
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, TaskExchangeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        /**分享赚豆*/
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, TaskShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }




    //banner图
    class ViewHolder1{
        ConvenientBanner ad_view;//banner图
    }
    //任务签到
    class ViewHolder2{
        LinearLayout task;//做任务
        VerticalSwitchTextView vertical_switch_textview;//广播轮播
        LinearLayout topic;//热门话题
        ImageView task_image;//任务图片
        LinearLayout exchange;//兑换商品
        LinearLayout share;//分享商品
    }
    //出游
    class ViewHolder3{

        ImageView image;//图片
        TextView TitleTextView;//标题
        RelativeLayout relativeLayout;//跳转
        TextView lookCount;//浏览数
        TextView commentCount;//评论数
        TextView money;//爱心豆数量

    }



    /***
     * banner监听
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        if(infos.get(position).getType().equals("1")){
            //视频
            Intent intent=new Intent(context, VideoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url",infos.get(position).getOpen_url());
            context.startActivity(intent);
        }else if(infos.get(position).getType().equals("2")){
            //网页
            Intent intent=new Intent(context, WebActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url",infos.get(position).getOpen_url());
            intent.putExtra("title", infos.get(position).getContent());
            context.startActivity(intent);
        }else if(infos.get(position).getType().equals("3")){
            //商品详情
            Intent intent=new Intent(context, CommodityDetailsActivity.class);
            intent.putExtra("id",infos.get(position).getOpen_url());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else if(infos.get(position).getType().equals("4")){
            //话题详情
            Intent intent = new Intent(context, TopicDetailsActivity.class);
            intent.putExtra("who", MainActivity.TAG);
            intent.putExtra("id", infos.get(position).getOpen_url());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else if(infos.get(position).getType().equals("5")){
            //旅游路线
            Intent intent=new Intent(context, TravelRouteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("did",infos.get(position).getOpen_url());
            context.startActivity(intent);
        }else if(infos.get(position).getType().equals("6")){
            //旅游路线
            Intent intent=new Intent(context, AllTaskActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
