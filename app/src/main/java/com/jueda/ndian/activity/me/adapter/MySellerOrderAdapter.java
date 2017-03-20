package com.jueda.ndian.activity.me.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.MySellerOrderDetailsActivity;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.getCurrentTime;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/30.
 *  卖家订单适配器
 */
public class MySellerOrderAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CommodityEntity> entityList;
    public MySellerOrderAdapter(Activity context, ArrayList<CommodityEntity> entityList) {
        this.context=context;
        this.entityList=entityList;
    }
    public void updata( ArrayList<CommodityEntity> entityList){
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
            convertView=View.inflate(context, R.layout.item_myseller_order,null);
            holder=new ViewHolder();
            holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
            holder.view=(ImageView)convertView.findViewById(R.id.view);
            holder.title=(TextView)convertView.findViewById(R.id.title);
            holder.money=(TextView)convertView.findViewById(R.id.moneys);
            holder.name=(TextView)convertView.findViewById(R.id.name);
            holder.time=(TextView)convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        ImageLoaderUtil.ImageLoader(entityList.get(position).getImg() + Constants.QINIU1 + Constants.dip2px(context, 70), holder.view);
        holder.time.setText(getCurrentTime.getCurrentTime(Long.parseLong(entityList.get(position).getCreat_time()), "yyyy/MM/dd HH:mm"));
        holder.title.setText(entityList.get(position).getTitle());
        holder.name.setText("收货人："+entityList.get(position).getConsignee());
        holder.money.setText("已支付：￥"+entityList.get(position).getPrice());
        new ChangeText(holder.money.getText().toString(),context.getResources().getColor(R.color.text_red),holder.money,4,holder.money.getText().toString().length());
        setOnClick(holder.relativeLayout, position);
        return convertView;
    }

    private void setOnClick(RelativeLayout relativeLayout, final int position) {
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MySellerOrderDetailsActivity.class);
                intent.putExtra("entity",entityList.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    class ViewHolder{
        RelativeLayout relativeLayout;//点击跳转到买家订单详情
        ImageView view;//图片
        TextView title;//标题
        TextView name;//购买者
        TextView money;//金额
        TextView time;//时间
    }
}
