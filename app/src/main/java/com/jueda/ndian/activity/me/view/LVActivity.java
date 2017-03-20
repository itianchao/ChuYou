package com.jueda.ndian.activity.me.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageLoaderUtil;
import com.jueda.ndian.utils.LvUtil;
import com.jueda.ndian.view.CircleImageView;

import java.util.HashMap;

/***
 * 等级
 */
public class LVActivity extends AppCompatActivity {
    private RelativeLayout UserrelativeLayout;
    private CircleImageView HeadImage;//头像
    private TextView lvtext;//等级
    private TextView experience;//经验
    private ListView listView;
    private String []lv={"Lv1","Lv2","Lv3","Lv4","Lv5","Lv6","Lv7","Lv8","Lv9","Lv10","Lv11","Lv12","Lv13","Lv14","Lv15","Lv16","Lv32","Lv48","Lv64","Lv128","Lv192","Lv255"};
    private String []values={"5","10","15","20","30","40","50","60","75","90","160","250","360","490","640","810","6250","16810","32490","146410","342250","615040"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_lv);
        InitView();
    }

    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Level_introduction), true);

        UserrelativeLayout= (RelativeLayout) View.inflate(LVActivity.this, R.layout.item_lv_top, null);
        HeadImage=(CircleImageView)UserrelativeLayout.findViewById(R.id.HeadImage);
        lvtext=(TextView)UserrelativeLayout.findViewById(R.id.lv);
        experience=(TextView)UserrelativeLayout.findViewById(R.id.experience);

        listView=(ListView)findViewById(R.id.listView);
        listView.addHeaderView(UserrelativeLayout);
        listView.setAdapter(new ListAdapte());

        ImageLoaderUtil.ImageLoader(ConstantsUser.userEntity.getAvater(), HeadImage, R.drawable.head_portrait);
        HashMap<String,String> hashMap= new LvUtil().LvUtil(Integer.parseInt(ConstantsUser.userEntity.getDevotion()));
        String lvv=hashMap.get("lv");

        lvtext.setText(lvv.substring(2,lvv.length())+"级");
        experience.setText("经验："+ConstantsUser.userEntity.getDevotion());

    }
    class ListAdapte extends BaseAdapter{
        @Override
        public int getCount() {
            return lv.length;
        }

        @Override
        public Object getItem(int position) {
            return lv[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                convertView=View.inflate(LVActivity.this,R.layout.item_lv_botton,null);
                holder=new ViewHolder();
                holder.lv=(TextView)convertView.findViewById(R.id.lv);
                holder.value=(TextView)convertView.findViewById(R.id.value);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.lv.setText(lv[position]);
            holder.value.setText(values[position]);
            return convertView;
        }
        class ViewHolder{
            TextView lv;
            TextView value;
        }
    }

}
