package com.jueda.ndian.activity.circle.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.view.ImageDirActivity;
import com.jueda.ndian.activity.fragment.PersonalFragment;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.home.adapter.ReleaseTopicAdapter;
import com.jueda.ndian.activity.circle.biz.CreatPostBiz;
import com.jueda.ndian.activity.home.biz.uploadImageBiz;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageAddress;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.utils.Image_look_preview;

import java.io.File;
import java.util.ArrayList;

/**
 * 发布信息
 */
public class ReleaseActivity extends Activity{
    public static ReleaseActivity instance=null;

    public static ArrayList<Photo> mList;
    private GridView gridview;
    private ReleaseTopicAdapter mAdapter;
    private String who;//还是发布话题(CircleContentActivity)
    private RelativeLayout relativeLayout;//底部
    private EditText TitleEditText;//标题   ---新增活动4-40字  其余2-30字
    private EditText ContentEditText;//内容  ---新增活动15-500字  其余10-2000字
    private TextView sendTextView;//发送
    private int screenWidth;//屏幕宽度
    private PopupWindow popPricture;//照片
    private View layoutPricture;
    public static ArrayList<String> imagelist=new ArrayList<>();//多张图片上传
    private String Cid;//圈子id;
    private WaitDialog dialog;//弹窗
    private String fileName;


    public static final int CLICK_ADD=1;//点击添加按钮
    public static final int CLICK_LOOK=2;//点击查看
    Handler hanlder=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent;
            switch (msg.what){
                //点击事件
                case CLICK_ADD:
                    if(Constants.checkList.size()>=Constants.MAX_SIZE){
                       new ToastShow(ReleaseActivity.this,getResources().getString(R.string.Can_only_choose_sheets)+Constants.MAX_SIZE+"张",1000);
                    }else{
                        add();
                    }
                    break;
                /**查看图片*/
                case CLICK_LOOK:
                    int position= (int) msg.obj;
                    Image_look_preview.look_result(ReleaseActivity.this, position, Constants.checkList);
                    break;
                /**图片上传成功*/
                case Constants.ON_SUCCEED_IMAGE:
                    String image[]= (String[]) msg.obj;
                    if (who.equals(CircleContentActivity.TAG)) {
                    //发布圈内话题
                        new CreatPostBiz(ReleaseActivity.this,hanlder,Cid,TitleEditText.getText().toString(),ContentEditText.getText().toString(),image);
                    }
                    break;
                /**图片上传失败*/
                case Constants.FAILURE_IMAGE:
                    new ToastShow(ReleaseActivity.this,getResources().getString(R.string.Image_upload_failure),1000);
                    break;
                /**创建成功*/
                case Constants.ON_SUCCEED:
                    //发布圈内话题
                    if (who.equals(CircleContentActivity.TAG)) {
                        CircleContentActivity.instance.refresh();
                        ConstantsUser.userEntity.setCount_post((Integer.parseInt(ConstantsUser.userEntity.getCount_post()) + 1) + "");
                        if(PersonalFragment.instance!=null){
                            PersonalFragment.instance.setUser();
                        }
                    }
                    new ToastShow(ReleaseActivity.this, getResources().getString(R.string.New_success), 1000);
                    finish();
                    dialog.dismiss();
                    break;
                /**创建失败*/
                case Constants.FAILURE:
                    new ToastShow(ReleaseActivity.this,getResources().getString(R.string.New_failure),1000);
                    dialog.dismiss();
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(ReleaseActivity.this,false);
                    intent = new Intent(ReleaseActivity.this, LoginActivity.class);
                    startActivity(intent);
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ChangeTitle(this);
        setContentView(R.layout.activity_release_topic);
        instance=this;
        who=getIntent().getStringExtra("who");
        InitView();
        setData();
        setOnClick();
    }

    private void InitView() {
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        Constants.checkList.clear();
        TitleEditText=(EditText)findViewById(R.id.TitleEditText);
        ContentEditText=(EditText) findViewById(R.id.ContentEditText);
        gridview = (GridView) findViewById(R.id.gridview);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        sendTextView=(TextView)findViewById(R.id.sendTextView);
        if(who.equals(CircleContentActivity.TAG)){
            Cid=getIntent().getStringExtra("cid");
            Constants.MAX_SIZE=9;//设置最多选择1张图片
            NdianApplication.instance.setTitle(this, getResources().getString(R.string.Release_topic), true);
            //话题界面修改
            TitleEditText.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) ContentEditText.getLayoutParams();
            layoutParams.height=Constants.dip2px(getApplicationContext(),60)+layoutParams.height;
            ContentEditText.setLayoutParams(layoutParams);
            ContentEditText.setHint(getResources().getString(R.string.Please_enter_the_topic_content));

        }
    }

    @Override
    public void finish() {
        instance=null;
        super.finish();
    }

    private void setData() {
        mList = new ArrayList<Photo>();
        //添加点击跳转选择图片的虚拟数据
        Photo p=new Photo();
        mList.add(p);
        /**设置最大显示图片，然后隐藏添加图片按钮*/
        mAdapter = new ReleaseTopicAdapter(this, mList, hanlder, screenWidth,Constants.MAX_SIZE);
        gridview.setAdapter(mAdapter);
    }

    private void setOnClick() {
        /**发布*/
        sendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KeyboardManage().CloseKeyboard(sendTextView,ReleaseActivity.this);
              if (who.equals(CircleContentActivity.TAG)) {
                    /**发布话题*/
                        if (ContentEditText.getText().toString().trim().length() >= 1 && ContentEditText.getText().toString().trim().length() <= 2000) {
                            if (Constants.checkList.size() == 0) {
                                String image[] = new String[0];
                                dialog = new WaitDialog(ReleaseActivity.this);
                                new CreatPostBiz(ReleaseActivity.this, hanlder, Cid, TitleEditText.getText().toString(), ContentEditText.getText().toString(), image);
                            } else {
                                dialog = new WaitDialog(ReleaseActivity.this);
                                new uploadImageBiz(ReleaseActivity.this, hanlder, imagelist, false, dialog);
                            }
                        } else {
                            new ToastShow(ReleaseActivity.this, getResources().getString(R.string.Content_of_at_least_10_2000_words), 1000);
                        }
                }
            }
        });
    }

    /**
     * 返回时图片获取
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(Activity.RESULT_CANCELED!=resultCode){
            //获取拍照后的相片
            if (requestCode == Constants.TAKING_PICTURES) {
                Bitmap bitmap= BitmapFactory.decodeFile(ImageAddress.ImageAddress(fileName));
                //把bitmap转化成uri
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                Cursor cursor = this.getContentResolver().query(uri, null,
                        null, null, null);
                if (cursor.moveToFirst()) {
                    String videoPath = cursor.getString(cursor
                            .getColumnIndex("_data"));// 获取绝对路径
                    Photo photo = new Photo();
                    photo.path = videoPath;
                    mList.add(mList.size()-1, photo);
                    if(mList.size()==Constants.MAX_SIZE+1) {
                        mList.remove(Constants.MAX_SIZE);
                    }
                    //添加到全局中
                    Constants.checkList.add(0,photo);
                }
                imagelist.clear();
                mAdapter.notifyDataSetChanged();
            }
        }
        if(Activity.RESULT_CANCELED!=resultCode&&data!=null){
            //相册选择的图片
            if (requestCode == 10) {
                if(Constants.IS_CANCEL==1){
                    ArrayList<Photo> list = data.getParcelableArrayListExtra(Constants.RES_PHOTO_LIST);
                    mList.clear();
                    Constants.checkList.clear();
                    if (list != null) {
                        mList.addAll(list);
                        Constants.checkList.addAll(list);
                        if(mList.size()!=Constants.MAX_SIZE) {
                            Photo photo1 = new Photo();
                            mList.add(photo1);
                        }
                    }
                }else if(Constants.IS_CANCEL==2){
                    Constants.checkList.clear();
                    Constants.checkList.addAll(mList);
                    if(mList.size()!=Constants.MAX_SIZE) {
                        Constants.checkList.remove(Constants.checkList.size() - 1);
                    }
                }
                imagelist.clear();
                mAdapter.notifyDataSetChanged();
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void add(){
        new KeyboardManage().CloseKeyboard(gridview,ReleaseActivity.this);
        if (popPricture != null && popPricture.isShowing()) {
            popPricture.dismiss();
        } else {
            layoutPricture=getLayoutInflater().inflate(
                    R.layout.register_user_dialog, null);

            //拍照
            Button button1 = (Button) layoutPricture.findViewById(R.id.photograph);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //调用相机拍照
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileName=System.currentTimeMillis()+"";
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(ImageAddress.ImageAddress(fileName))));
                    startActivityForResult(intent, Constants.TAKING_PICTURES);
                    popPricture.dismiss();
                }
            });
            //相册
            Button button2 = (Button) layoutPricture.findViewById(R.id.album);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ReleaseActivity.this, ImageDirActivity.class);
//                intent.putExtra(Constan.ARG_PHOTO_LIST, mList);
                    startActivityForResult(intent, 10);
                    popPricture.dismiss();
                }
            });
            //取消
            Button button3 = (Button) layoutPricture.findViewById(R.id.cancel);
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPricture.dismiss();
                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popPricture = new PopupWindow(layoutPricture, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popPricture.setBackgroundDrawable(cd);
//            popPricture.setAnimationStyle(R.style.PopupAnimationSex);
            popPricture.update();
            popPricture.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popPricture.setTouchable(true); // 设置popupwindow可点击
            popPricture.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popPricture.setFocusable(true); // 获取焦点
            NdianApplication.instance.backgroundAlpha((float) 0.4,ReleaseActivity.this);
            // 设置popupwindow的位置（相对tvLeft的位置）
            popPricture.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popPricture.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1,ReleaseActivity.this);
                }
            });
            popPricture.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popPricture.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
