package com.jueda.ndian.utils;

import android.content.Context;

import com.jueda.ndian.savedata.Configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 获取发布时间
 * Created by Administrator on 2016/4/28.
 */
public class getCurrentTime {
    /**
     * 根据格式显示时间
     * @param beforeTime
     * @param format 如"yy:MM:dd:HH:mm"
     * @return
     */
    public static String getCurrentTime(long beforeTime,String format){
        DateFormat bsdf = new SimpleDateFormat(format);
        bsdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Calendar bcalendar = Calendar.getInstance();
        bcalendar.setTimeInMillis(beforeTime * 1000);
        String bbefore=bsdf.format(bcalendar.getTime());
        return bbefore;
    }
    /**
     * 多少时间发布的
     * @param beforeTime
     * @return
     */
    public static String getCurrentTime(long beforeTime) {
        long nowTime=System.currentTimeMillis()/1000;
        long time=nowTime-beforeTime;
        /**发布时间*/
        long ball;
        DateFormat bsdf = new SimpleDateFormat("yy:MM:dd:HH:mm");
        bsdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Calendar bcalendar = Calendar.getInstance();
        bcalendar.setTimeInMillis(beforeTime * 1000);
        String bbefore=bsdf.format(bcalendar.getTime());
        ball=Long.parseLong(bbefore.substring(0, 2)+""+bbefore.substring(3, 5)+""+bbefore.substring(6, 8)+"0000");

        /**当前时间*/
        //判断时间
        long nall;
        DateFormat nsdf = new SimpleDateFormat("yy:MM:dd:HH:mm");
        nsdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Calendar ncalendar = Calendar.getInstance();
        ncalendar.setTimeInMillis(nowTime * 1000);
        String nbefore=nsdf.format(ncalendar.getTime());

        nall=Long.parseLong(nbefore.substring(0, 2) + "" + nbefore.substring(3, 5) + "" +nbefore.substring(6, 8) + "0000");
        /**判断时间*/
        if(nall-ball>=100000000){
            return bbefore.substring(0, 2)+"-"+bbefore.substring(3, 5)+"-"+bbefore.substring(6, 8);
        }else if(nall-ball>=20000){
            return bbefore.substring(3, 5)+"-"+bbefore.substring(6, 8);
        }else if(nall-ball>=10000){
            return "昨天"+bbefore.substring(9, 11)+":"+bbefore.substring(12, 14);
        }else{
            if(time<60){
                return "刚刚";
            }else if(time>60&&time<3600){
                return time/60+"分钟前";
            }else if(time>3600&&time<=21600){
                return time/3600+"小时前";
            }else{
                return "今天"+bbefore.substring(9, 11)+":"+bbefore.substring(12, 14);
            }
        }
    }

    /***
     * 判断是不是当天首次登录
     */
    public static boolean Period(Context context){
        long nowTime=System.currentTimeMillis();
        DateFormat bsdf = new SimpleDateFormat("MMdd");
        bsdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Calendar bcalendar = Calendar.getInstance();

        bcalendar.setTimeInMillis(nowTime);
        String now=bsdf.format(bcalendar.getTime());

        bcalendar.setTimeInMillis(new Configuration().readaDate(context));
        String before=bsdf.format(bcalendar.getTime());

        if(now.equals(before)){
            //是当天就是返回false
            return false;
        }else{
            //第二天就返回true
            return true;
        }

    }
}
