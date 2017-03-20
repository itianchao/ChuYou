package com.jueda.ndian.listener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jueda.ndian.MainActivity;
import com.jueda.ndian.R;

import java.util.Random;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * 收到消息的处理。
 *
 * @param message 收到的消息实体。
 * @param left    剩余未拉取消息数目。
 * @return 收到消息是否处理完成，true 表示走自已的处理方式，false 走融云默认处理方式。
 */
public class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener{

    @Override
    public boolean onReceived(Message message, int i) {
//        Log.i("--- ----------", i + "");
//        if(message.getConversationType().equals(Conversation.ConversationType.CHATROOM)){
//                Log.i("------","聊天室聊天");
//        }else if(i==0){
//            /**
//             * 定义通知声音
//             */
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(MainActivity.instance, notification);
//            r.play();
//            /**
//             * 定义通知栏
//             */
//            Random random=new Random();
//            int ss=random.nextInt(100);
//            NotificationManager manager= (NotificationManager) MainActivity.instance.getSystemService(Context.NOTIFICATION_SERVICE);
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.instance);
//            mBuilder.setContentTitle("ss"+ss)//设置通知栏标题
//                    .setContentText("ss"+ss) //设置通知栏显示内容
//                            //  .setNumber(number) //设置通知集合的数量
//                    .setTicker("ss" + ss) //通知首次出现在通知栏，带上升动画效果的
//                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
//                    .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级
//                              .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
//                    .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//                            //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
//                    .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
//
////            Intent intent = new Intent(MainActivity.instance,LoadConversationListFragment2.class);
////            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.instance,ss , intent, 0);
////            mBuilder.setContentIntent(pendingIntent);
//            manager.notify(ss, mBuilder.getNotification());
//        }
        return false;
    }
}
