package com.jueda.ndian.activity.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.MyBuyersOrderActivity;
import com.jueda.ndian.activity.me.view.OrderDetailsActivity;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;

import java.util.ArrayList;

/**
 * 买家订单适配器
 * Created by Administrator on 2016/6/25.
 */
public class MyBuyersOrderAdapter extends BaseAdapter{
    private Context context;
    private  ArrayList<CommodityEntity> entityList;
    public MyBuyersOrderAdapter(Context context, ArrayList<CommodityEntity> entityList) {
        this.context=context;
        this.entityList=entityList;
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
            convertView=View.inflate(context, R.layout.adapter_myorder_item,null);
            holder=new ViewHolder();
            holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
            holder.view=(ImageView)convertView.findViewById(R.id.view);
            holder.TitleTextView=(TextView)convertView.findViewById(R.id.TitleTextView);
            holder.MoneyTextView=(TextView)convertView.findViewById(R.id.MoneyTextView);
            holder.state=(TextView)convertView.findViewById(R.id.state);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        ImageLoaderUtil.ImageLoader(entityList.get(position).getImg()+ Constants.QINIU1+Constants.dip2px(context,70), holder.view);
        if(entityList.get(position).getGoodsState().equals("2")){
            holder.state.setText("待收货");
            new ChangeText(holder.state.getText().toString(),context.getResources().getColor(R.color.text_red), holder.state,0, holder.state.length());
        }else{
            holder.state.setText("已完成");
            new ChangeText(holder.state.getText().toString(),context.getResources().getColor(R.color.text_gray), holder.state,0, holder.state.length());

        }
        holder.TitleTextView.setText(entityList.get(position).getTitle());
        holder.MoneyTextView.setText("￥"+entityList.get(position).getTotal_fee());
        setOnClick(holder.relativeLayout, position);
        return convertView;
    }

    private void setOnClick(RelativeLayout relativeLayout, final int position) {
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("entity",entityList.get(position));
                intent.putExtra("who", MyBuyersOrderActivity.TAG);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    class ViewHolder{
        RelativeLayout relativeLayout;//点击跳转到买家订单详情
        ImageView view;//图片
        TextView TitleTextView;//标题
        TextView MoneyTextView;//金额
        TextView state;//状态
    }
}
