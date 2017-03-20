package com.jueda.ndian.activity.me.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.MyExchangeGoodsActivity;
import com.jueda.ndian.activity.me.view.OrderDetailsActivity;
import com.jueda.ndian.entity.CommodityEntity;
import com.jueda.ndian.utils.ChangeText;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageLoaderUtil;
import java.util.ArrayList;
/**
 * Created by Administrator on 2016/8/29.
 * 我的兑换商品
 */
public class MyExchangeGoodsAdapter extends BaseAdapter{
    private Activity context;
    private ArrayList<CommodityEntity> entityList;
    public MyExchangeGoodsAdapter(Activity context, ArrayList<CommodityEntity> entityList) {
        this.context=context;
        this.entityList=entityList;
    }
    public void  updata(ArrayList<CommodityEntity> entityList){
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
            convertView=View.inflate(context, R.layout.item_myexchange_goods,null);
            holder=new ViewHolder();
            holder.imageView=(ImageView)convertView.findViewById(R.id.image);
            holder.title=(TextView)convertView.findViewById(R.id.title);
            holder.bean=(TextView)convertView.findViewById(R.id.bean);
            holder.state=(TextView)convertView.findViewById(R.id.state);
            holder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.relativeLayout);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.title.setText(entityList.get(position).getTitle());
        holder.bean.setText("已使用："+entityList.get(position).getBead()+context.getResources().getString(R.string.dedication));
        new ChangeText(holder.bean.getText().toString(),context.getResources().getColor(R.color.text_love), holder.bean,4, holder.bean.length()-3);
        if(entityList.get(position).getGoodsState().equals("2")){
            holder.state.setText("待收货");
            new ChangeText(holder.state.getText().toString(),context.getResources().getColor(R.color.text_red), holder.state,0, holder.state.length());
        }else{
            holder.state.setText("已完成");
            new ChangeText(holder.state.getText().toString(),context.getResources().getColor(R.color.text_gray), holder.state,0, holder.state.length());
        }
        ImageLoaderUtil.ImageLoader(entityList.get(position).getImg() + Constants.QINIU1 + Constants.dip2px(context, 70), holder.imageView);
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
                Intent intent=new Intent(context, OrderDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("entity", entityList.get(position));
                intent.putExtra("who", MyExchangeGoodsActivity.TAG);
                context.startActivity(intent);
            }
        });
    }

    class ViewHolder{
        ImageView imageView;
        TextView title;//标题
        TextView bean;//爱心豆数量
        TextView state;//状态
        RelativeLayout relativeLayout;//兑换详情
    }
}
