package com.jueda.ndian.activity.me.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.view.CircleContentActivity;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.circle.view.SearchActivity;
import com.jueda.ndian.activity.fragment.CharityCircleFragment;
import com.jueda.ndian.activity.circle.biz.ApplyAddBiz;
import com.jueda.ndian.entity.CircleEntity;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.RoundCornerImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/9.
 * 推荐圈子和搜索圈子
 */
public class RecommendAdapter extends BaseAdapter{
    private Activity context;
    private ArrayList<CircleEntity> recommendList;
    private String who;//判断是外部推荐圈子(CharityCircleFragment)还是搜索圈子(SearchActivity)

    /**
     * 记录加圈按钮
     */
    private Button copyAddButton;
    /**
     * 记录已加圈文本
     */
    private TextView copyHasJoinedTextView;
    /***
     * 标记点击加圈的id
     */
    private int id;

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent;
            switch (msg.what){
                /**申请成功*/
                case Constants.ON_SUCCEED:
                    if(copyAddButton!=null&&copyHasJoinedTextView!=null){
                        copyAddButton.setVisibility(View.GONE);
                        copyHasJoinedTextView.setVisibility(View.VISIBLE);
                    }
                    if(who.equals(SearchActivity.TAG)&&SearchActivity.instance!=null){
                        SearchActivity.instance.finish();
                    }
                    intent=new Intent(context,CircleContentActivity.class);
                    intent.putExtra("cid", recommendList.get(id).getCid());
                    intent.putExtra("name", recommendList.get(id).getName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    if( CharityCircleFragment.instance!=null){
                        CharityCircleFragment.instance.Reconnection();
                    }
                    new ToastShow(context,context.getResources().getString(R.string.Application_is_successful),1000);
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
                    intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;
            }
        }
    };

    public RecommendAdapter(Activity context, ArrayList<CircleEntity> recommendList,String who) {
        this.context=context;
        this.recommendList=recommendList;
        this.who=who;
    }
    public void updata(ArrayList<CircleEntity> recommendList){
        this.recommendList=recommendList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return recommendList.size();
    }

    @Override
    public Object getItem(int position) {
        return recommendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.recommend_item,null);
            holder=new ViewHolder();
            holder.add=(Button)convertView.findViewById(R.id.add);
            holder.HasJoinedTextView=(TextView)convertView.findViewById(R.id.HasJoinedTextView);
            holder.xian=(ImageView)convertView.findViewById(R.id.xian);
            holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
            holder.HeadImage=(RoundCornerImageView)convertView.findViewById(R.id.HeadImage);
            holder.NameTextView=(TextView)convertView.findViewById(R.id.NameTextView);
            holder.numberTextView=(TextView)convertView.findViewById(R.id.numberTextView);
            holder.contentTextView=(TextView)convertView.findViewById(R.id.contentTextView);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        /**判断是圈外推荐还是搜索圈子。*/
        if(who.equals(CharityCircleFragment.TAG)){
            holder.add.setVisibility(View.VISIBLE);
            holder.HasJoinedTextView.setVisibility(View.GONE);
        }else{
            /**1表示是圈内成员，0表示不是*/
            if(recommendList.get(position).getIs_member().equals("1")){
                holder.add.setVisibility(View.GONE);
                holder.HasJoinedTextView.setVisibility(View.VISIBLE);
            }else{
                holder.add.setVisibility(View.VISIBLE);
                holder.HasJoinedTextView.setVisibility(View.GONE);
            }
        }
        /**设置最后一根线不显示*/
        if(position==(recommendList.size()-1)){
            holder.xian.setVisibility(View.GONE);
        }else{
            holder.xian.setVisibility(View.VISIBLE);
        }
        /**设置数据*/
        ImageLoaderUtil.ImageLoader(recommendList.get(position).getAvatar(), holder.HeadImage,R.drawable.head_circle);
        holder.NameTextView.setText(recommendList.get(position).getName());
        holder.numberTextView.setText(recommendList.get(position).getCount());
        holder.contentTextView.setText(recommendList.get(position).getCdesc());
        setOnClick(position, holder.add, holder.relativeLayout,holder.HasJoinedTextView);
        return convertView;
    }

    /**
     * 点击
     * @param position
     * @param add
     * @param relativeLayout
     * @param hasJoinedTextView
     */
    private void setOnClick(final int position, final Button add, RelativeLayout relativeLayout, final TextView hasJoinedTextView) {
        /**申请加圈*/
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(new Configuration().readaIsLogin(context)){
                    copyAddButton=add;
                    copyHasJoinedTextView=hasJoinedTextView;
                    id=position;
                    new ApplyAddBiz(context,myHandler,recommendList.get(position).getCid());

                }else{
                    //跳转到登录界面
                    intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });
        /**进入圈子*/
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CircleContentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("cid", recommendList.get(position).getCid());
                intent.putExtra("name", recommendList.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    class  ViewHolder{
        Button add;
        TextView HasJoinedTextView;//已加入
        ImageView xian;//线
        RelativeLayout relativeLayout;//进入圈子详情
        RoundCornerImageView HeadImage;//头像
        TextView NameTextView;//名称
        TextView numberTextView;//浏览数
        TextView contentTextView;//内容
    }
}
