package com.jueda.ndian.activity.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.view.TaskExchangeDetailsActivity;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.view.RoundCornerImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/25.
 * 兑换商品适配器
 */
public class TaskExchangeAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<CommodityEntity> entityList;
    public TaskExchangeAdapter(Context context, ArrayList<CommodityEntity> entityList) {
        this.entityList=entityList;
        this.context=context;
    }
    public void updata(ArrayList<CommodityEntity> entityList){
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
            convertView=View.inflate(context, R.layout.item_task_exchange,null);
            holder=new ViewHolder();
            holder.imageView=(RoundCornerImageView)convertView.findViewById(R.id.imageView);
            holder.title=(TextView)convertView.findViewById(R.id.title);
            holder.bean=(TextView)convertView.findViewById(R.id.bean);
            holder.money=(TextView)convertView.findViewById(R.id.money);
            holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.money.setText("￥"+entityList.get(position).getOld_price());
        holder.title.setText(entityList.get(position).getTitle());
        holder.bean.setText(entityList.get(position).getBead()+context.getResources().getString(R.string.dedication));
        new ChangeText(holder.bean.getText().toString(),context.getResources().getColor(R.color.text_orange), holder.bean,0, holder.bean.length()-3);
        ImageLoaderUtil.ImageLoader(entityList.get(position).getImg() + Constants.QINIU1 + Constants.dip2px(context, 100), holder.imageView);
        setOnClick(position,holder.relativeLayout);
        return convertView;

    }

    /**
     * 进入兑换详情
     * @param position
     * @param relativeLayout
     */
    private void setOnClick(final int position, RelativeLayout relativeLayout) {
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskExchangeDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", entityList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    class ViewHolder{
        RoundCornerImageView imageView;
        TextView title;//标题
        TextView bean;//爱心豆数量
        TextView money;//原价
        RelativeLayout relativeLayout;//旅游票详情
    }
}
