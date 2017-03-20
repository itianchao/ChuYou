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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.fragment.TaskQuestionnaire_Fragment;
import com.jueda.ndian.activity.me.biz.TaskQuestionnaireDelectBiz;
import com.jueda.ndian.activity.me.view.LoginActivity;
import com.jueda.ndian.activity.me.view.MyTaskActivity;
import com.jueda.ndian.activity.home.adapter.ReleaseTopicAdapter;
import com.jueda.ndian.activity.home.biz.CompleteTaskBiz;
import com.jueda.ndian.activity.home.biz.TaskDetailsQuestionnaireBiz;
import com.jueda.ndian.activity.home.biz.uploadImageBiz;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.entity.TaskCommonEntity;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.savedata.Configuration;
import com.jueda.ndian.utils.ChangeTitle;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageAddress;
import com.jueda.ndian.utils.Image_look_preview;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.LoadingAnimation;
import com.jueda.ndian.utils.Share;
import com.jueda.ndian.utils.ToastShow;

import java.io.File;
import java.util.ArrayList;

/**
 * 问卷详情
 */
public class TaskDetailsQuestionnaireActivity extends AppCompatActivity {
    public static final String TAG=TaskDetailsQuestionnaireActivity.class.getName();
    public static TaskDetailsQuestionnaireActivity instance=null;
    private View loadingInclude;//进入时加载
    private LoadingAnimation animation;//动画
    private View include;//网络断开或连接失败显示
    private ScrollView scrollView;
    private WaitDialog dialog;
    private ArrayList<TaskCommonEntity> entityArrayList;
    private Button submit;//提交截图
    private PopupWindow popSubmit;
    private View layoutSubmit;//提交截图
    private TextView deleteTextView;//删除
    private PopupWindow popDelect;
    private View layoutDelect;//删除

    private TextView content;//内容
    private TextView title;//标题
    private ImageView head;//个人还是官方
    private TextView bean;//爱心豆
    private Button share;//分享

    //选择图片和显示图片
    private RelativeLayout relativeLayout;//底部
    private GridView gridview;
    private int screenWidth;//屏幕宽度
    private PopupWindow popPricture;//照片
    private View layoutPricture;
    public static ArrayList<Photo> mList;
    private ReleaseTopicAdapter mAdapter;
    public static ArrayList<String> imagelist=new ArrayList<>();//多张图片上传
    public static final int CLICK_ADD=1;//点击添加按钮
    public static final int CLICK_LOOK=2;//点击查看
    private String fileName;
    Handler hanlder=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //点击事件
                case CLICK_ADD:
                    if (Constants.checkList.size() >= Constants.MAX_SIZE) {
                        new ToastShow(TaskDetailsQuestionnaireActivity.this, getResources().getString(R.string.Can_only_choose_sheets) + Constants.MAX_SIZE + "张", 1000);
                    } else {
                        add();
                    }
                    break;
                /**查看图片*/
                case CLICK_LOOK:
                    int position = (int) msg.obj;
                    Image_look_preview.look_result(TaskDetailsQuestionnaireActivity.this, position, Constants.checkList);
                    break;
                //获取任务成功
                case Constants.ON_SUCEED_TWO:
                    entityArrayList= (ArrayList<TaskCommonEntity>) msg.obj;
                    animation.stopAnim();
                    scrollView.setVisibility(View.VISIBLE);
                    setData();
                    break;
                //获取失败
                case Constants.FAILURE_THREE:
                    scrollView.setVisibility(View.GONE);
                    animation.stopAnim();
                    NdianApplication.connectionFail(getResources().getString(R.string.Click_to_download), include);
                    include.setVisibility(View.VISIBLE);
                    break;
                /**图片上传成功*/
                case Constants.ON_SUCCEED_IMAGE:
                    String image[]= (String[]) msg.obj;
                    new CompleteTaskBiz(TaskDetailsQuestionnaireActivity.this,hanlder,cid(),getAid(),image);
                    break;
                /**图片上传失败*/
                case Constants.FAILURE_IMAGE:
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    new ToastShow(TaskDetailsQuestionnaireActivity.this,getResources().getString(R.string.Image_upload_failure),1000);
                    break;
                /**上传成功*/
                case Constants.ON_SUCCEED:
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    completeTask();
                    //圈内圈外都刷新界面请求数据
                    if(TaskQuestionnaire_Fragment.instance!=null){
                        TaskQuestionnaire_Fragment.instance.Reconnection();
                    }
                    break;
                /**上传失败*/
                case Constants.FAILURE:
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    new ToastShow(TaskDetailsQuestionnaireActivity.this,getResources().getString(R.string.Image_upload_failure),1000);
                    break;
                /**重复提交*/
                case Constants.FAILURE_TWO:
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    new ToastShow(TaskDetailsQuestionnaireActivity.this,"已提交过任务，请耐心等候...",1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    //跳转到登录界面
                    new Configuration().writeaIsLogin(TaskDetailsQuestionnaireActivity.this,false);
                    Intent intent = new Intent(TaskDetailsQuestionnaireActivity.this, TaskExperienceActivity.class);
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
        setContentView(R.layout.activity_task_details_questionnaire);
        InitView();
        setOnClick();
    }


    private void InitView() {
        NdianApplication.instance.setTitle(this, getResources().getString(R.string.For_the_task), true);
        instance=this;
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        Constants.MAX_SIZE=9;//设置最多选择1张图片
        Constants.checkList.clear();
        gridview = (GridView) findViewById(R.id.gridview);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        submit=(Button)findViewById(R.id.submit);
        deleteTextView=(TextView)findViewById(R.id.deleteTextView);
        content=(TextView)findViewById(R.id.content);
        title=(TextView)findViewById(R.id.titles);
        head=(ImageView)findViewById(R.id.head);
        bean=(TextView)findViewById(R.id.bean);
        share=(Button)findViewById(R.id.share);

        if(getWho().equals(MyTaskActivity.TAG)){
            deleteTextView.setVisibility(View.VISIBLE);
        }
        /**初始化数据时界面显示*/
        scrollView=(ScrollView)findViewById(R.id.scrollView);
        include=(View)findViewById(R.id.failure);
        loadingInclude=(View)findViewById(R.id.loadingInclude);
        animation=new LoadingAnimation(this,loadingInclude);
        entityArrayList=new ArrayList<>();
        Reconnection();
    }

    @Override
    protected void onDestroy() {
        instance=null;
        super.onDestroy();
    }
    public String getWho(){
        String who=getIntent().getStringExtra("who");
        if(who==null) who="";
        return who;
    }
    /***
     * 上个界面传递的cid
     * @return
     */
    public String cid(){
        String cid=getIntent().getStringExtra("cid");
        if(cid==null) return "";
        return cid;
    }
    public String getAid(){
        return getIntent().getStringExtra("aid");
    }
    /**
     * 初始化数据时连接
     */
    public void Reconnection(){
        scrollView.setVisibility(View.GONE);
        /**没有网络时界面隐藏。只显示图片*/
        if(Constants.currentNetworkType!=Constants.TYPE_NONE){
            include.setVisibility(View.GONE);
            animation.startAnim();
            new TaskDetailsQuestionnaireBiz(TaskDetailsQuestionnaireActivity.this,entityArrayList,hanlder,getAid());
        }else{
            new ToastShow(TaskDetailsQuestionnaireActivity.this,getResources().getString(R.string.No_network_please_check_the_network1),1000);
            NdianApplication.connectionFail(getResources().getString(R.string.No_network_please_check_the_network), include);
            include.setVisibility(View.VISIBLE);
        }
    }

    private void setData() {
        content.setText(entityArrayList.get(0).getContent());
        title.setText(entityArrayList.get(0).getName());
        bean.setText(entityArrayList.get(0).getReward());
        if(entityArrayList.get(0).getUid().equals("1")){
            head.setImageResource(R.drawable.guanfang);
        }else{
            head.setImageResource(R.drawable.geren);
        }

        mList = new ArrayList<Photo>();
        //添加点击跳转选择图片的虚拟数据
        Photo p=new Photo();
        mList.add(p);
        /**设置最大显示图片，然后隐藏添加图片按钮*/
        mAdapter = new ReleaseTopicAdapter(TaskDetailsQuestionnaireActivity.this, mList, hanlder, screenWidth,Constants.MAX_SIZE);
        gridview.setAdapter(mAdapter);


    }


    private void setOnClick() {

        //删除
        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new Configuration().readaIsLogin(getApplicationContext())) {
                    delect();
                }else {
                    //跳转到登录界面
                    Intent intent = new Intent(TaskDetailsQuestionnaireActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        //重连
        include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reconnection();
            }
        });
        //提交
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new Configuration().readaIsLogin(getApplicationContext())) {
                    if (Constants.checkList.size() == 0) {
                        new ToastShow(TaskDetailsQuestionnaireActivity.this, getResources().getString(R.string.Upload_a_picture_at_least), 1000);
                    } else {
                        dialog = new WaitDialog(TaskDetailsQuestionnaireActivity.this);
                        new uploadImageBiz(TaskDetailsQuestionnaireActivity.this, hanlder, imagelist, false, dialog);
                    }
                }else {
                    //跳转到登录界面
                    Intent intent = new Intent(TaskDetailsQuestionnaireActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        //分享
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new Configuration().readaIsLogin(getApplicationContext())) {
                    new Share(TaskDetailsQuestionnaireActivity.this, relativeLayout, TAG, entityArrayList.get(0).getDownloadUrl(), entityArrayList.get(0).getName());
                } else {
                    //跳转到登录界面
                    Intent intent = new Intent(TaskDetailsQuestionnaireActivity.this, LoginActivity.class);
                    startActivity(intent);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(Activity.RESULT_CANCELED!=resultCode){
            //获取拍照后的相片
            if (requestCode == Constants.TAKING_PICTURES) {
                Bitmap bitmap= BitmapFactory.decodeFile(ImageAddress.ImageAddress(fileName));
                //把bitmap转化成uri
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(TaskDetailsQuestionnaireActivity.this.getContentResolver(), bitmap, null, null));
                Cursor cursor = TaskDetailsQuestionnaireActivity.this.getContentResolver().query(uri, null,
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

    /**
     * 图片选择
     */
    public void add(){
        new KeyboardManage().CloseKeyboard(gridview, TaskDetailsQuestionnaireActivity.this);
        if (popPricture != null && popPricture.isShowing()) {
            popPricture.dismiss();
        } else {
            layoutPricture=TaskDetailsQuestionnaireActivity.this.getLayoutInflater().inflate(
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
                    Intent intent = new Intent(TaskDetailsQuestionnaireActivity.this, ImageDirActivity.class);
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
            NdianApplication.instance.backgroundAlpha((float) 0.4,TaskDetailsQuestionnaireActivity.this);
            // 设置popupwindow的位置（相对tvLeft的位置）
            popPricture.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popPricture.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1,TaskDetailsQuestionnaireActivity.this);
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
     * 删除
     */

    private void delect(){
        /**弹窗提示*/
        if (popDelect != null && popDelect.isShowing()) {
            popDelect.dismiss();
        } else {
            layoutDelect=getLayoutInflater().inflate(
                    R.layout.pop_ok_cancel_content_general, null);
            TextView know=(TextView)layoutDelect.findViewById(R.id.complete);
            TextView content=(TextView)layoutDelect.findViewById(R.id.content);
            TextView center=(TextView)layoutDelect.findViewById(R.id.center);
            content.setText("确定删除任务？");
            know.setText("确定");
            RelativeLayout cancelRelativeLayout=(RelativeLayout)layoutDelect.findViewById(R.id.cancelRelativeLayout);
            cancelRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popDelect.dismiss();
                }
            });
            center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popDelect.dismiss();
                }
            });
            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TaskQuestionnaireDelectBiz(TaskDetailsQuestionnaireActivity.this,hanlder,getAid());
                    popDelect.dismiss();
                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popDelect = new PopupWindow(layoutDelect, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popDelect.setBackgroundDrawable(cd);
            popDelect.update();
            popDelect.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popDelect.setTouchable(true); // 设置popupwindow可点击
            popDelect.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popDelect.setFocusable(true); // 获取焦点
            NdianApplication.instance.backgroundAlpha((float) 0.4, TaskDetailsQuestionnaireActivity.this);
            // 设置popupwindow的位置（相对tvLeft的位置）
            popDelect.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popDelect.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1, TaskDetailsQuestionnaireActivity.this);
                }
            });
            popDelect.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popDelect.dismiss();
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
            NdianApplication.instance.backgroundAlpha((float) 0.4, TaskDetailsQuestionnaireActivity.this);
            // 设置popupwindow的位置（相对tvLeft的位置）
            popSubmit.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popSubmit.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1, TaskDetailsQuestionnaireActivity.this);
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
