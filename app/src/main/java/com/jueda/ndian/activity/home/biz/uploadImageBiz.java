package com.jueda.ndian.activity.home.biz;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.jueda.ndian.entity.Photo;
import com.jueda.ndian.nohttp.CallServer;
import com.jueda.ndian.nohttp.HttpListener;
import com.jueda.ndian.nohttp.WaitDialog;
import com.jueda.ndian.utils.Constants;
import com.jueda.ndian.utils.ImageCompress;
import com.jueda.ndian.utils.LogUtil;
import com.jueda.ndian.utils.ToastShow;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 *
 *上传图片到七牛
 * Created by Administrator on 2016/4/15.
 */
public class uploadImageBiz implements HttpListener<String> {
    private Request mRequest;
    private Context context;
    private File image;
    private Handler handler;
    private boolean isloading;
    private WaitDialog dialog;
    private ArrayList<String> imagelist;
    private  int number=0;//记录上传多少个
    private String  name;//文件名
    private boolean isCancelled=true;
    /**单张图片*/
    public uploadImageBiz(Context context, Handler handler, File image, boolean isloading, WaitDialog dialog){
        this.dialog=dialog;
        this.isloading=isloading;
        this.image=image;
        this.handler=handler;
        this.context=context;
        RequestMethod method = RequestMethod.GET;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.HEAD_IMAGE_TOKEN, method);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, isloading);
    }
    /**多张图*/
    public uploadImageBiz(Context context, Handler handler, ArrayList<String> imagelist, boolean isloading, WaitDialog dialog){
        this.dialog=dialog;
        this.imagelist=imagelist;
        this.isloading=isloading;
        this.handler=handler;
        this.context=context;
        RequestMethod method = RequestMethod.GET;// 默认get请求
        mRequest=  NoHttp.createStringRequest(Constants.HEAD_IMAGE_TOKEN, method);
        // 添加到请求队列
        CallServer.getRequestInstance().add(context, 0, mRequest, this, true, isloading);
    }
    @Override
    public void onSucceed(int what, final Response<String> response) {
        dialog.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isCancelled=false;
            }
        });
        if(response!=null) {
            name = response.get().toString().substring(41, 69) + System.currentTimeMillis() + "1";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (imagelist != null) {
                        number = 0;
                        final String images[] = new String[imagelist.size()];
                        //图片名称为当前日期+随机数生成
                        for (int i = 0; i < imagelist.size(); i++) {
                            name = response.get().toString().substring(41, 69) + System.currentTimeMillis() + i;
                            UploadManager uploadManager = new UploadManager();
                            final int finalI = i;
                            File file = ImageCompress.ImageCompress(imagelist.get(i), name);
                            if (file == null) {
                                handler.sendEmptyMessage(Constants.FAILURE_IMAGE);
                                dialog.dismiss();
                            } else {
                                uploadManager.put(file, name, response.get(),
                                        new UpCompletionHandler() {
                                            @Override
                                            public void complete(String arg0, ResponseInfo info, JSONObject response) {
                                                //取消
                                                if (isCancelled) {
                                                    // TODO Auto-generated method stub
                                                    new LogUtil("uploadImageBiz", "我：" + info + ",\r\n " + "我：" + response + "\r\n" + "我" + info.isOK());
                                                    if (info != null) {
                                                        if (info.isOK()) {
                                                            /**成功传出图片key*/
                                                            ++number;
                                                            images[finalI] = arg0;
                                                            /**上传完毕就跳转*/
                                                            if (number == imagelist.size() && number != 0) {
                                                                Message message = new Message();
                                                                message.obj = images;
                                                                message.what = Constants.ON_SUCCEED_IMAGE;
                                                                handler.sendMessage(message);
                                                            }
                                                        } else if (info.error.equals("file exists")) {
                                                            ++number;
                                                            images[finalI] = arg0;
                                                            if (number == imagelist.size() && number != 0) {
                                                                Message message = new Message();
                                                                message.obj = images;
                                                                message.what = Constants.ON_SUCCEED_IMAGE;
                                                                handler.sendMessage(message);
                                                            }
                                                        } else {
                                                            handler.sendEmptyMessage(Constants.FAILURE_IMAGE);
                                                            if (!isloading) {
                                                                dialog.dismiss();
                                                            }
                                                        }
                                                    } else {
                                                        handler.sendEmptyMessage(Constants.FAILURE_IMAGE);
                                                        if (!isloading) {
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                }
                                            }
                                        }, null);
                            }
                        }
                    } else {
                        //图片名称为当前日期+随机数生成
                        UploadManager uploadManager = new UploadManager();
                        uploadManager.put(image, name, response.get(),
                                new UpCompletionHandler() {
                                    @Override
                                    public void complete(String arg0, ResponseInfo info, JSONObject response) {
                                        // TODO Auto-generated method stub
                                        new LogUtil("uploadImageBiz", "我：" + info + ",\r\n " + "我：" + response + "\r\n" + "我" + info.isOK());
                                        //取消
                                        if (isCancelled) {
                                            if (info != null) {
                                                if (info.isOK()) {
                                                    /**成功传出图片key*/
                                                    Message message = new Message();
                                                    message.obj = arg0;
                                                    message.what = Constants.ON_SUCCEED_IMAGE;
                                                    handler.sendMessage(message);
                                                } else if (info.error != null) {
                                                    if (info.error.equals("file exists")) {
                                                        /**成功传出图片key*/
                                                        Message message = new Message();
                                                        message.obj = arg0;
                                                        message.what = Constants.ON_SUCCEED_IMAGE;
                                                        handler.sendMessage(message);
                                                    } else {
                                                        handler.sendEmptyMessage(Constants.FAILURE_IMAGE);
                                                        if (!isloading) {
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                } else {
                                                    handler.sendEmptyMessage(Constants.FAILURE_IMAGE);
                                                    if (!isloading) {
                                                        dialog.dismiss();
                                                    }
                                                }
                                            } else {
                                                handler.sendEmptyMessage(Constants.FAILURE_IMAGE);
                                                if (!isloading) {
                                                    dialog.dismiss();
                                                }
                                            }
                                        }
                                    }
                                }, null);
                    }
                }
            }).start();
        }else{
            handler.sendEmptyMessage(Constants.FAILURE_IMAGE);
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        handler.sendEmptyMessage(Constants.FAILURE_IMAGE);
        if(!isloading){
            dialog.dismiss();
        }
    }
}
