package com.jueda.ndian.activity.home.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.home.adapter.ReleaseTopicAdapter;
import com.jueda.ndian.activity.home.biz.ReleaseGoodsBiz;
import com.jueda.ndian.activity.home.biz.uploadImageBiz;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageAddress;
import com.jueda.ndian.utils.Image_look_preview;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.ToastShow;
import com.jueda.ndian.view.MyGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 发布商品
 */
public class ReleaseGoodsActivity extends AppCompatActivity {
    public static ReleaseGoodsActivity instance=null;
    private EditText title;//标题
    private EditText price;//价格
    private EditText content;//描述
    private EditText freight;//运费
    private EditText phone;//手机号码
    private TextView sendTextView;//提交
    private PopupWindow popSubmit;
    private View layoutSubmit;//提交成功

    //图片
    private MyGridView gridview;
    private RelativeLayout relativeLayout;//底部
    private ReleaseTopicAdapter mAdapter;
    private PopupWindow popPricture;//照片
    private View layoutPricture;
    public static ArrayList<Photo> mList;
    public static ArrayList<String> imagelist=new ArrayList<>();//多张图片上传
    public static final int CLICK_ADD=1;//点击添加按钮
    public static final int CLICK_LOOK=2;//点击查看
    private WaitDialog dialog;
    private String fileName;
    Handler hanlder=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            Intent intent;
            switch (msg.what){
                //点击事件
                case CLICK_ADD:
                    if(Constants.checkList.size()>=Constants.MAX_SIZE){
                        new ToastShow(ReleaseGoodsActivity.this,getResources().getString(R.string.Can_only_choose_sheets)+Constants.MAX_SIZE+"张",1000);
                    }else{
                        add();
                    }
                    break;
                /**查看图片*/
                case CLICK_LOOK:
                    int position= (int) msg.obj;
                    Image_look_preview.look_result(ReleaseGoodsActivity.this, position, Constants.checkList);
                    break;

                /**图片上传成功*/
                case Constants.ON_SUCCEED_IMAGE:
                    String image[]= (String[]) msg.obj;
                    new ReleaseGoodsBiz(ReleaseGoodsActivity.this,hanlder,image,title.getText().toString(),price.getText().toString(),content.getText().toString(),freight.getText().toString(),phone.getText().toString(),false);
                    break;
                /**图片上传失败*/
                case Constants.FAILURE_IMAGE:
                    new ToastShow(ReleaseGoodsActivity.this,getResources().getString(R.string.Image_upload_failure),1000);
                    break;
                /**创建成功*/
                case Constants.ON_SUCCEED:
                    if(dialog!=null&&dialog.isShowing()){
                        dialog.dismiss();
                    }
                    completeTask();
                    break;
                /**创建失败*/
                case Constants.FAILURE:
                    new ToastShow(ReleaseGoodsActivity.this,getResources().getString(R.string.Post_failure),1000);
                    if(dialog!=null&&dialog.isShowing()){
                        dialog.dismiss();
                    }
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(ReleaseGoodsActivity.this,false);
                    intent = new Intent(ReleaseGoodsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    if(dialog!=null&&dialog.isShowing()){
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
        setContentView(R.layout.activity_release_goods);
        instance=this;
        InitView();
        setData();
        setOnClick();
    }



    @Override
    protected void onDestroy() {
        instance=null;
        super.onDestroy();
    }

    private void InitView() {
        Constants.checkList.clear();
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.Release_goods), true);
        sendTextView=(TextView)findViewById(R.id.sendTextView);
        title=(EditText)findViewById(R.id.titles);
        price=(EditText)findViewById(R.id.price);

        content=(EditText)findViewById(R.id.content);
        freight=(EditText)findViewById(R.id.freight);
        phone=(EditText)findViewById(R.id.phone);
        gridview=(MyGridView)findViewById(R.id.gridview);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
    }
    private void setData() {
        mList = new ArrayList<Photo>();
        //添加点击跳转选择图片的虚拟数据
        Photo p=new Photo();
        mList.add(p);
        /**设置最大显示图片，然后隐藏添加图片按钮*/
        mAdapter = new ReleaseTopicAdapter(this, mList, hanlder, MainActivity.instance.getScreenWidth(),Constants.MAX_SIZE);
        gridview.setAdapter(mAdapter);
    }
    private void setOnClick() {
        sendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().equals("") || price.getText().toString().equals("") || content.getText().toString().equals("") ||
                        freight.getText().toString().equals("") || phone.getText().toString().equals("")) {
                    new ToastShow(ReleaseGoodsActivity.this, getResources().getString(R.string.Incomplete_information), 1000);
                } else {
                    if (Constants.checkList.size() > 0) {
                        //输入正确金额
                        Pattern pattern = Pattern.compile("^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$");
                        Matcher matcher = pattern.matcher(price.getText().toString().trim());
                        Matcher matcher1 = pattern.matcher(freight.getText().toString().trim());
                        if (matcher.matches() && matcher1.matches()) {
                            dialog = new WaitDialog(ReleaseGoodsActivity.this);
                            new uploadImageBiz(ReleaseGoodsActivity.this, hanlder, imagelist, false, dialog);
                        } else {
                            new ToastShow(ReleaseGoodsActivity.this, getResources().getString(R.string.Please_input_the_correct_amount), 1000);
                        }

                    } else {
                        new ToastShow(ReleaseGoodsActivity.this, getResources().getString(R.string.Upload_a_picture_at_least), 1000);
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
            if(data!=null){
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void add(){
        new KeyboardManage().CloseKeyboard(gridview, ReleaseGoodsActivity.this);
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
                    Intent intent = new Intent(ReleaseGoodsActivity.this, ImageDirActivity.class);
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
            NdianApplication.instance.backgroundAlpha((float) 0.4,ReleaseGoodsActivity.this);
            // 设置popupwindow的位置（相对tvLeft的位置）
            popPricture.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popPricture.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1,ReleaseGoodsActivity.this);
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
    /**
     * 任务截图上传成功显示
     */
    private void completeTask(){


        /**弹窗提示*/
        if (popSubmit != null && popSubmit.isShowing()) {
            popSubmit.dismiss();
        } else {
            layoutSubmit=getLayoutInflater().inflate(
                    R.layout.pop_task_submit, null);
            TextView know=(TextView)layoutSubmit.findViewById(R.id.know);
            know.setText("确定");
            RelativeLayout cancelRelativeLayout=(RelativeLayout)layoutSubmit.findViewById(R.id.cancelRelativeLayout);
            cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popSubmit.dismiss();
                }
            });
            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    popSubmit.dismiss();
                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popSubmit = new PopupWindow(layoutSubmit, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popSubmit.setBackgroundDrawable(cd);
            popSubmit.update();
            popSubmit.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popSubmit.setTouchable(true); // 设置popupwindow可点击
            popSubmit.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popSubmit.setFocusable(true); // 获取焦点
            NdianApplication.instance.backgroundAlpha((float) 0.4, ReleaseGoodsActivity.this);
            // 设置popupwindow的位置（相对tvLeft的位置）
            popSubmit.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popSubmit.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1, ReleaseGoodsActivity.this);
                }
            });
            popSubmit.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popSubmit.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
