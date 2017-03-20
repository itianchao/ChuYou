package com.jueda.ndian.activity.me.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jueda.ndian.R;
import com.jueda.ndian.entity.PaymentDetailsEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Administrator on 2016/4/9.
 * 收支明细
 */
public class PaymentDetailsAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<PaymentDetailsEntity> entityList;
    public PaymentDetailsAdapter(Context context, ArrayList<PaymentDetailsEntity> entityList) {
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
            convertView=View.inflate(context, R.layout.payment_details_item,null);
            holder=new ViewHolder();
            holder.moneyTextView=(TextView)convertView.findViewById(R.id.moneyTextView);
            holder.typeTextView=(TextView)convertView.findViewById(R.id.typeTextView);
            holder.timeTextView=(TextView)convertView.findViewById(R.id.timeTextView);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
            holder.typeTextView.setText(entityList.get(position).getRemark());
        /**根据类型添加+-号*/
        if(entityList.get(position).getType().equals("0")){
            holder.moneyTextView.setText("+"+entityList.get(position).getMoney());
        }else{
            holder.moneyTextView.setText(entityList.get(position).getMoney());
        }
        /**设置时间*/
        DateFormat nsdf = new SimpleDateFormat("yyyy-MM-dd");
        nsdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Calendar ncalendar = Calendar.getInstance();
        ncalendar.setTimeInMillis(Long.parseLong(entityList.get(position).getCreat_time()) * 1000);
        holder.timeTextView.setText(nsdf.format(ncalendar.getTime()));
        return convertView;
    }
    class ViewHolder{
        TextView typeTextView;//类型
        TextView timeTextView;//时间
        TextView moneyTextView;//金额
    }
}
