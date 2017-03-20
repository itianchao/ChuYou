package com.jueda.ndian.utils;

import android.content.Context;

import com.jueda.ndian.R;
import com.jueda.ndian.entity.UserEntity;
import com.jueda.ndian.savedata.DBManager;

import java.util.ArrayList;

/**
 * 用于数据库存储数据
 * Created by Administrator on 2016/4/25.
 */
public class ConstantsUser {
    public static UserEntity userEntity=new UserEntity();
    public static final String uid="uid";//用户id
    public static final String phoneNumber="phoneNumber";//用户名
    public static final String uname="uname";//昵称
    public static final String avater="avater";//头像
    public static final String sex="sex";//性别
    public static final String birth="birth";//生日
    public static final String job="job";//工作
    public static final String education="education";//学历
    public static final String signature="signature";//个性签名
    public static final String devotion="devotion";//奉献值
    public static final String lastTime="lastTime";//最后登录时间
    public static final String inviteCode="inviteCode";//个人邀请码
    public static final String userToken="userToken";//Token
    public static final String RyToken="RyToken";//融云token
    public static final String countDonationsDost="countDonationsDost";//用户总捐赠金额
    public static final String love_bean="love_bean";//爱心豆
    public static final String count_post="count_post";//话题数量
    public static final String surplus="surplus";//账户余额
    public static final String count_ticket="count_ticket";//旅游票数量
    public static final String add_location="location";//收货地址
    public static final String add_phoneNumber="add_phoneNumber";//收货填写的手机号
    public static final String add_uname="add_uname";//收货人姓名
    /**
     * 获取用户数据
     */
    public UserEntity getUserEntity(Context context){
        DBManager manager=new DBManager(context);
        userEntity=  manager.query();
        return  userEntity;
    }

    /**
     * 设置用户信息
     * @param uid
     * @param phoneNumber
     * @param uname
     * @param avater
     * @param sex
     * @param birth
     * @param job
     * @param education
     * @param signature
     * @param devotion
     * @param lastTime
     * @param inviteCode
     * @param userToken
     * @param ryToken
     */
    public static void setUserEntity(Context context,int uid,String phoneNumber,String uname,String avater,String sex,String birth
            ,String job,String education,String signature,String devotion,String lastTime,String inviteCode,String userToken,String ryToken
            ,String countDonationsDost,String love_bean,String count_post,String surplus,String count_ticket,String add_location,String add_phoneNumber,String add_uname){
        userEntity.setUid(uid);
        userEntity.setPhoneNumber(phoneNumber);
        userEntity.setUname(uname);
        userEntity.setAvater(avater);
        if(sex.equals("0")){
            userEntity.setSex(context.getResources().getString(R.string.woman));
        }else if(sex.equals("1")){
            userEntity.setSex(context.getResources().getString(R.string.man));
        }else{
            userEntity.setSex(sex);
        }
        userEntity.setBirth(birth);
        userEntity.setJob(job);
        userEntity.setEducation(education);
        userEntity.setSignature(signature);
        userEntity.setDevotion(devotion);
        userEntity.setLastTime(lastTime);
        userEntity.setInviteCode(inviteCode);
        userEntity.setUserToken(userToken);
        userEntity.setRyToken(ryToken);
        userEntity.setCountDonationsDost(countDonationsDost);
        userEntity.setLove_bean(love_bean);
        userEntity.setCount_post(count_post);
        userEntity.setSurplus(surplus);
        userEntity.setCount_ticket(count_ticket);
        userEntity.setAdd_location(add_location);
        userEntity.setAdd_phoneNumber(add_phoneNumber);
        userEntity.setAdd_uname(add_uname);
        /**储存到数据库中*/
        DBManager dbManager=new DBManager(context);
        dbManager.add(userEntity);
    }
}
