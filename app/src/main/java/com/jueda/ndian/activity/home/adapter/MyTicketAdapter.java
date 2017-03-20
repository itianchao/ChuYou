package com.jueda.ndian.activity.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.MyTicketDetailsActivity;
import com.jueda.ndian.entity.TravelEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/27.
 * 我的旅游票
 */
public class MyTicketAdapter extends BaseAdapter{
    private Activity context;
    private ArrayList<TravelEntity> entityList;
    public MyTicketAdapter(Activity context, ArrayList<TravelEntity> entityList) {
        this.context=context;
        this.entityList=entityList;
    }
    public void  updata(ArrayList<TravelEntity> entityList){
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
            convertView=View.inflate(context, R.layout.item_myticket,null);
            holder=new ViewHolder();
            holder.imageView=(ImageView)convertView.findViewById(R.id.imageView);
            holder.title=(TextView)convertView.findViewById(R.id.title);
            holder.bean=(TextView)convertView.findViewById(R.id.bean);
            holder.money=(TextView)convertView.findViewById(R.id.money);
            holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.money.setText("￥"+entityList.get(position).getCost_price());
        holder.title.setText(entityList.get(position).getTitle());
        holder.bean.setText(entityList.get(position).getMoney()+context.getResources().getString(R.string.dedication));
        new ChangeText(holder.bean.getText().toString(),context.getResources().getColor(R.color.text_love), holder.bean,0, holder.bean.length()-3);
        ImageLoaderUtil.ImageLoader(entityList.get(position).getBig_img()+ Constants.QINIU1+Constants.dip2px(context,100),holder.imageView);
        setOnClick(position,holder.relativeLayout);
        return convertView;
    }

    /**
     * 旅游票详情
     * @param position
     * @param relativeLayout
     */
    private void setOnClick(final int position, RelativeLayout relativeLayout) {
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MyTicketDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("entity",entityList.get(position));
                context.startActivity(intent);
            }
        });
    }

    class ViewHolder{
        ImageView imageView;
        TextView title;//标题
        TextView bean;//爱心豆数量
        TextView money;//原价
        RelativeLayout relativeLayout;//旅游票详情
    }
}
