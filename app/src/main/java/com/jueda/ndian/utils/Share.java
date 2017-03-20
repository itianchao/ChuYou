package com.jueda.ndian.utils;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.circle.view.CircleParticularsActivity;
import com.jueda.ndian.activity.circle.view.TopicDetailsActivity;
import com.jueda.ndian.activity.home.view.TaskDetailsQuestionnaireActivity;
import com.jueda.ndian.activity.home.view.TaskShareDetailsActivity;
import com.jueda.ndian.activity.me.view.InviteFriendsActivity;
import com.jueda.ndian.nohttp.WaitDialog;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/17.
 * 弹出分享框
 *
 */
public class Share {
    // 弹出窗口
    private PopupWindow popPricture;
    //弹出布局
    private View layoutPricture;
    private GridView gridView;
    //数据集合
    private ArrayList<Entity> entityList;
    private UMShareAPI mShareAPI;

    private Activity context;
    private View view;
    private String who;
    private String code;
    private String id;
    private String imageurl;
    private String title;
    private String text;
    /**
     *
     * @param context
     * @param view
     * @param who判断是那个 邀请好友（InviteFriendsActivity） 圈子分享（CircleParticularsActivity）分享商品（TaskShareDetailsActivity）分享话题（TopicDetailsActivity）
     *                 分享问卷（TaskDetailsQuestionnaireActivity）
     * @param code
     * @param id 商品话题 id
     */
    public Share(Activity context,View view,String who,String code,String id){
        this.context=context;
        this.view=view;
        this.who=who;
        this.code=code;
        this.id=id;
        new LogUtil("share",who+"\n code:"+code+"\n id:"+id);
        Share();
    }

    /**
     * 分享商品和话题
     * @param context
     * @param view
     * @param who
     * @param id
     * @param imageurl
     * @param title
     * @param text
     */
    public Share(Activity context,View view,String who,String id,String imageurl,String title,String text){
        this.context=context;
        this.view=view;
        this.who=who;
        this.id=id;
        this.imageurl=imageurl;
        this.title=title;
        this.text=text;
        Share();
    }
    public void  Share() {
        mShareAPI = UMShareAPI.get(context);
        addData();
        if (popPricture != null && popPricture.isShowing()) {
            popPricture.dismiss();
        } else {
            layoutPricture =context.getLayoutInflater().inflate(
                    R.layout.share, null);
            TextView cancel=(TextView)layoutPricture.findViewById(R.id.cancel);//取消
            gridView=(GridView)layoutPricture.findViewById(R.id.gridView);
            RelativeLayout disappear=(RelativeLayout)layoutPricture.findViewById(R.id.disappear);
            ShareAdapter adapter=new ShareAdapter(context);

            gridView.setAdapter(adapter);

            //触摸关闭
            disappear.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popPricture.dismiss();
                    return false;
                }
            });
            //取消
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPricture.dismiss();
                }
            });
            //点击item分享
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long iid) {
                    String url = "";
                    String SinText="";//新浪的text
                    UMImage image = null;
                    //个人邀请码
                    if(who.equals(InviteFriendsActivity.TAG)){
                        image=new UMImage(context,R.mipmap.n365);
                        text="邀请码："+code;
                        title="亲，给你推荐一个不花钱去出游的神器";
                        url="http://www.ndian365.com/home/index/share_u?code=" + code+"&nickName="+ConstantsUser.userEntity.getUname()+"&Avatar="+ConstantsUser.userEntity.getAvater();
                        try {
                            SinText=text + url + code+"&nickName="+java.net.URLEncoder.encode(ConstantsUser.userEntity.getUname(), "utf-8")+"&Avatar="+ConstantsUser.userEntity.getAvater();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                     //圈邀请码
                    }else if(who.equals(CircleParticularsActivity.TAG)){
                        image=new UMImage(context,R.mipmap.n365);
                        text="邀请码："+code;
                        title="亲，给你推荐一个不花钱去出游的神器";
                        url="http://www.ndian365.com/home/index/share_c?code=" + code+"&nickName="+ConstantsUser.userEntity.getUname()+"&Avatar="+ConstantsUser.userEntity.getAvater();
                        try {
                            SinText=text + url + code+"&nickName="+java.net.URLEncoder.encode(ConstantsUser.userEntity.getUname(), "utf-8")+"&Avatar="+ConstantsUser.userEntity.getAvater();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //分享商品
                    }else if(who.equals(TaskShareDetailsActivity.TAG)){
                        title="快看，这东西真不错";
                        if(text.length()>30){
                            text=text.substring(0,28);
                        }
                        image=new UMImage(context,imageurl);
                        url="http://www.ndian365.com/index.php/home/index/share_product?id="+id+"&uid="+ConstantsUser.userEntity.getUid();
//                            "&nickName="+java.net.URLEncoder.encode(ConstantsUser.userEntity.getUname(), "utf-8")
                        SinText=text + url;

                    }else if(who.equals(TopicDetailsActivity.TAG)){
                        //分享话题
                        if(imageurl!=""){
                            image=new UMImage(context,imageurl);
                        }else{
                            image=new UMImage(context,R.mipmap.n365);
                        }

                        title="热门话题";
                        if(text.length()>30){
                            text=text.substring(0,28);
                        }
                        url="http://www.ndian365.com/index.php/home/index/post?pid="+id;
//                            "&nickName="+java.net.URLEncoder.encode(ConstantsUser.userEntity.getUname(), "utf-8")
                        SinText=text + url;
                    }else if(who.equals(TaskDetailsQuestionnaireActivity.TAG)){
                     //分享问卷
                        image=new UMImage(context,R.mipmap.n365);
                        title="问卷";
                        if(id.length()>30){
                            text=id.substring(0,28);
                        }else{
                            text=id;
                        }
                        url=code;
//                            "&nickName="+java.net.URLEncoder.encode(ConstantsUser.userEntity.getUname(), "utf-8")
                        SinText=text + url;
                    }
                    //修改登录弹窗界面
                    WaitDialog waitDialog=new WaitDialog(context);
                    Config.dialog = waitDialog.dialog;
                    switch ((int) iid){
                        //QQ
                        case 2:
                            new ShareAction(context)
                                    .setPlatform(SHARE_MEDIA.QQ)
                                    .setCallback(umShareListener)
                                    .withText(text)
                                    .withTitle(title)
                                    .withTargetUrl(url)
                                    .withMedia(image)
                                    .share();
                            popPricture.dismiss();
                            break;
                        //QQ空间
                        case 3:
                            new ShareAction(context)
                                    .setPlatform(SHARE_MEDIA.QZONE)
                                    .setCallback(umShareListener)
                                    .withText(text)
                                    .withTitle(title)
                                    .withTargetUrl(url)
                                    .withMedia(image)
                                    .share();
                            popPricture.dismiss();

                            break;
                        //微信
                        case 0:
                            new ShareAction(context)
                                    .setPlatform(SHARE_MEDIA.WEIXIN)
                                    .setCallback(umShareListener)
                                    .withText(text)
                                    .withTitle(title)
                                    .withTargetUrl(url)
                                    .withMedia(image)
                                    .share();
                            popPricture.dismiss();
                            break;
                        //微信朋友圈
                        case 1:
                            new ShareAction(context)
                                    .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                    .setCallback(umShareListener)
                                    .withText(text)
                                    .withTitle(title)
                                    .withTargetUrl(url)
                                    .withMedia(image)
                                    .share();
                            popPricture.dismiss();
                            break;
                        //新浪
                        case 4:
                            new ShareAction(context)
                                    .setPlatform(SHARE_MEDIA.SINA)
                                    .setCallback(umShareListener)
                                    .withTitle(title)
                                    .withText(SinText)
                                    .withMedia(image)
                                    .share();
                            popPricture.dismiss();
                            break;
                    }
                }
            });

            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popPricture = new PopupWindow(layoutPricture, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);

            ColorDrawable cd = new ColorDrawable(-0000);
            popPricture.setBackgroundDrawable(cd);
            popPricture.update();
            popPricture.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popPricture.setTouchable(true); // 设置popupwindow可点击
            popPricture.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popPricture.setFocusable(true); // 获取焦点
            NdianApplication.instance.backgroundAlpha((float) 0.4,context);
            // 设置popupwindow的位置（相对tvLeft的位置）
            popPricture.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            popPricture.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1, context);
                }
            });

        }
    }
    class ShareAdapter extends BaseAdapter{
        private Context context;
        public ShareAdapter(Activity context) {
            this.context=context;
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
            ImageView image = null;
            TextView name = null;
                convertView=View.inflate(context,R.layout.share_item,null);
                image=(ImageView)convertView.findViewById(R.id.umeng_socialize_shareboard_image);
                name=(TextView)convertView.findViewById(R.id.umeng_socialize_shareboard_pltform_name);
            image.setImageResource(entityList.get(position).getImage());
            name.setText(entityList.get(position).getName());
            return convertView;
        }
    }
    /**
     * 添加数据
     */
    public void addData(){
        entityList=new ArrayList<>();
        //微信
        Entity entity3=new Entity();
        entity3.setName("微信");
        entity3.setImage(R.drawable.umeng_socialize_wechat);
        entityList.add(entity3);
        //朋友圈
        Entity entity5=new Entity();
        entity5.setName("朋友圈");
        entity5.setImage(R.drawable.umeng_socialize_wechat_friend);
        entityList.add(entity5);
        //QQ
        Entity entity1=new Entity();
        entity1.setName("QQ");
        entity1.setImage(R.drawable.umeng_socialize_qq_on);
        entityList.add(entity1);

        //Qzone
        Entity entity2=new Entity();
        entity2.setName("QQ空间");
        entity2.setImage(R.drawable.umeng_socialize_qzone_on);
        entityList.add(entity2);
        //新浪
        Entity entity4=new Entity();
        entity4.setName("新浪");
        entity4.setImage(R.drawable.umeng_socialize_sina_on);
        entityList.add(entity4);
    }

    private UMShareListener umShareListener= new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(context," 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            new LogUtil("Share",t.toString());
            Toast.makeText(context," 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(context," 分享取消啦", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 实体类
     */
    class Entity{
        private String name;
        private int image;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }
    }

}
