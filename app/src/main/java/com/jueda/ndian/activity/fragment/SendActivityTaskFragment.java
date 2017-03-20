package com.jueda.ndian.activity.fragment;

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
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jueda.ndian.NdianApplication;
import com.jueda.ndian.R;
import com.jueda.ndian.activity.home.view.ImageDirActivity;
import com.jueda.ndian.activity.home.view.TaskReleaseActivity;
import com.jueda.ndian.activity.home.adapter.ReleaseTopicAdapter;
import com.jueda.ndian.activity.home.biz.TaskSendActivityBiz;
import com.jueda.ndian.activity.home.biz.uploadImageBiz;
import com.jueda.ndian.activity.me.view.RechargeActivity;
import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ConstantsUser;
import com.jueda.ndian.utils.ImageAddress;
import com.jueda.ndian.utils.Image_look_preview;
import com.jueda.ndian.utils.KeyboardManage;
import com.jueda.ndian.utils.LoginOutUtil;
import com.jueda.ndian.utils.ToastShow;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/24.
 * 发布活动任务
 */
public class SendActivityTaskFragment extends LazyFragment {
    public static SendActivityTaskFragment instance=null;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private View view;

    private EditText title;//标题
    private EditText bean;//佣金
    private EditText rule;//任务说明
    private EditText describe;//描述
    private EditText number;//任务数量
    private PopupWindow popSubmit;
    private View layoutSubmit;//提交成功
    private PopupWindow popPrompt;//爱心豆不足
    private View layoutPrompt;
    private WaitDialog waitDialog;
    private String cid;//圈id

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
                        new ToastShow(getActivity(), getResources().getString(R.string.Can_only_choose_sheets) + Constants.MAX_SIZE + "张", 1000);
                    } else {
                        add();
                    }
                    break;
                /**查看图片*/
                case CLICK_LOOK:
                    int position = (int) msg.obj;
                    Image_look_preview.look_result(getActivity(),SendActivityTaskFragment.this, position, Constants.checkList);
                    break;
                /**图片上传成功*/
                case Constants.ON_SUCCEED_IMAGE:
                    String image[]= (String[]) msg.obj;
                    new TaskSendActivityBiz(getActivity(),hanlder,cid,bean.getText().toString(),describe.getText().toString(),rule.getText().toString(),title.getText().toString(),number.getText().toString(),image);
                    break;
                /**图片上传失败*/
                case Constants.FAILURE_IMAGE:
                    new ToastShow(getActivity(),getResources().getString(R.string.Image_upload_failure),1000);
                    break;
                /**发布成功*/
                case Constants.ON_SUCCEED:
                    if (waitDialog!=null&&waitDialog.isShowing())
                        waitDialog.dismiss();
                    PersonalFragment.userObservable.bean();
                    completeTask();


                    break;
                /**爱心豆不足*/
                case Constants.FAILURE_TWO:
                    PersonalFragment.userObservable.bean();
                    deficiency();
                    break;
                /**发布失败*/
                case Constants.FAILURE:
                    if (waitDialog!=null&&waitDialog.isShowing())
                        waitDialog.dismiss();
                    new ToastShow(getActivity(),getResources().getString(R.string.Post_failure),1000);
                    break;
                /**用户失效*/
                case Constants.USER_FAILURE:
                    if (waitDialog!=null&&waitDialog.isShowing())
                        waitDialog.dismiss();
                    new LoginOutUtil(getActivity());
                    break;
            }
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=View.inflate(getActivity(), R.layout.fragment_send_activity,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        isPrepared = true;
        lazyLoad();
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
        instance=this;
        InitView();
        setData();
    }

    @Override
    public void onDestroy() {
        instance=null;
        super.onDestroy();
    }

    private void InitView() {
        screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        Constants.MAX_SIZE=9;//设置最多选择1张图片
        Constants.checkList.clear();
        gridview = (GridView) view.findViewById(R.id.gridview);
        relativeLayout=(RelativeLayout)view.findViewById(R.id.relativeLayout);
        bean=(EditText)view.findViewById(R.id.bean);
        rule=(EditText)view.findViewById(R.id.rule);
        describe=(EditText)view.findViewById(R.id.describe);
        title=(EditText)view.findViewById(R.id.title);
        number=(EditText)view.findViewById(R.id.number);
    }
    private void setData() {
        mList = new ArrayList<Photo>();
        //添加点击跳转选择图片的虚拟数据
        Photo p=new Photo();
        mList.add(p);
        /**设置最大显示图片，然后隐藏添加图片按钮*/
        mAdapter = new ReleaseTopicAdapter(getActivity(), mList, hanlder, screenWidth,Constants.MAX_SIZE);
        gridview.setAdapter(mAdapter);
    }

    /**
     * 提交数据
     * @param cid
     */
    public void updata(String cid){
        this.cid=cid;
        if(title.getText().toString().equals("")||bean.getText().toString().equals("")||bean.getText().toString().equals("0")||number.getText().toString().equals("")||number.getText().toString().equals("0")||rule.getText().toString().equals("")||describe.getText().toString().equals("")||Constants.checkList.size()==0){
            new ToastShow(getActivity(),getResources().getString(R.string.Incomplete_information),1000);
        }else{
            int money=Integer.parseInt(bean.getText().toString())*Integer.parseInt(number.getText().toString());
            if(Integer.parseInt(ConstantsUser.userEntity.getLove_bean())>=money){
                waitDialog = new WaitDialog(getActivity());
                new uploadImageBiz(getActivity(), hanlder, imagelist, false, waitDialog);
            }else{
                deficiency();
            }
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser&&mList!=null&&mAdapter!=null){
            mList.clear();
            Photo photo = new Photo();
            mList.add( photo);
            Constants.checkList.clear();
            mAdapter.notifyDataSetChanged();
        }
        super.setUserVisibleHint(isVisibleToUser);
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
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, null, null));
                Cursor cursor = getActivity().getContentResolver().query(uri, null,
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
                        Constants.checkList.clear();
                        mList.clear();
                        if (list != null) {
                            Constants.checkList.addAll(list);
                            mList.addAll(list);
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
        new KeyboardManage().CloseKeyboard(gridview, getActivity());
        if (popPricture != null && popPricture.isShowing()) {
            popPricture.dismiss();
        } else {
            layoutPricture=getActivity().getLayoutInflater().inflate(
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
                    SendActivityTaskFragment.this.startActivityForResult(intent, Constants.TAKING_PICTURES);
                    popPricture.dismiss();
                }
            });
            //相册
            Button button2 = (Button) layoutPricture.findViewById(R.id.album);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ImageDirActivity.class);
//                intent.putExtra(Constan.ARG_PHOTO_LIST, mList);
                    SendActivityTaskFragment.this.startActivityForResult(intent, 10);
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
            NdianApplication.instance.backgroundAlpha((float) 0.4,getActivity());
            // 设置popupwindow的位置（相对tvLeft的位置）
            popPricture.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popPricture.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1,getActivity());
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
            layoutSubmit=getActivity().getLayoutInflater().inflate(
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
                    TaskReleaseActivity.instance.finish();
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
            NdianApplication.instance.backgroundAlpha((float) 0.4, getActivity());
            // 设置popupwindow的位置（相对tvLeft的位置）
            popSubmit.showAtLocation(relativeLayout, Gravity.BOTTOM, 0, 0);

            popSubmit.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    NdianApplication.instance.backgroundAlpha(1,getActivity());
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
    /**
     * 爱心豆不足
     */
    private void deficiency(){
        if (popPrompt != null && popPrompt.isShowing()) {
            popPrompt.dismiss();
        } else {
            layoutPrompt =getActivity().getLayoutInflater().inflate(
                    R.layout.pop_ok_cancel_content_general, null);
            TextView know=(TextView)layoutPrompt.findViewById(R.id.complete);
            TextView contents=(TextView)layoutPrompt.findViewById(R.id.content);
            TextView center=(TextView)layoutPrompt.findViewById(R.id.center);
            contents.setText("亲，您的爱心豆余额不足");
            know.setText("去充值");

            center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPrompt.dismiss();
                }
            });
            know.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popPrompt.dismiss();
                    startActivity(new Intent(getActivity(), RechargeActivity.class));

                }
            });
            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            popPrompt = new PopupWindow(layoutPrompt, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            ColorDrawable cd = new ColorDrawable(-0000);
            popPrompt.setBackgroundDrawable(cd);
//                    popSex.setAnimationStyle(R.style.PopupAnimationSex);
            popPrompt.update();
            popPrompt.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popPrompt.setTouchable(true); // 设置popupwindow可点击
            popPrompt.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popPrompt.setFocusable(true); // 获取焦点
            // 设置popupwindow的位置（相对tvLeft的位置）
            popPrompt.showAtLocation(relativeLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            popPrompt.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popPrompt.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
