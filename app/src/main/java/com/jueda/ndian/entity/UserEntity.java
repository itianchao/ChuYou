package com.jueda.ndian.entity;

import java.io.Serializable;

/**
 * 所有关于用户的信息
 * Created by Administrator on 2016/4/16.
 */
public class UserEntity implements Serializable{
    public int uid;//用户id
    public String phoneNumber;//手机号码
    public String uname;//昵称
    public String avater;//头像
    public String sex;//性别
    public String birth;//生日
    public String job;//工作
    public String education;//学历
    public String signature;//个性签名
    public String devotion;//奉献值
    public String lastTime;//最后登录时间
    public String inviteCode;//个人邀请码
    public String userToken;//Token
    public String ryToken;//融云token
    public String countDonationsDost;//用户总捐赠金额
    public String  love_bean;//爱心豆
    public String count_post;//话题数量
    public String surplus;//账户余额
    public String count_ticket;//票数量
    public String add_location;//收货地址
    public String add_phoneNumber;//收货联系方式
    public String add_uname;//收货人姓名

    public String getAdd_location() {
        return add_location;
    }

    public void setAdd_location(String add_location) {
        this.add_location = add_location;
    }

    public String getAdd_phoneNumber() {
        return add_phoneNumber;
    }

    public void setAdd_phoneNumber(String add_phoneNumber) {
        this.add_phoneNumber = add_phoneNumber;
    }

    public String getAdd_uname() {
        return add_uname;
    }

    public void setAdd_uname(String add_uname) {
        this.add_uname = add_uname;
    }

    public String getCount_ticket() {
        return count_ticket;
    }

    public void setCount_ticket(String count_ticket) {
        this.count_ticket = count_ticket;
    }

    public String getCount_post() {
        return count_post;
    }

    public void setCount_post(String count_post) {
        this.count_post = count_post;
    }

    public String getSurplus() {
        return surplus;
    }

    public void setSurplus(String surplus) {
        this.surplus = surplus;
    }

    public String getLove_bean() {
        return love_bean;
    }

    public void setLove_bean(String love_bean) {
        this.love_bean = love_bean;
    }

    public String getCountDonationsDost() {
        return countDonationsDost;
    }

    public void setCountDonationsDost(String countDonationsDost) {
        this.countDonationsDost = countDonationsDost;
    }

    public String getRyToken() {
        return ryToken;
    }

    public void setRyToken(String ryToken) {
        this.ryToken = ryToken;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getAvater() {
        return avater;
    }

    public void setAvater(String avater) {
        this.avater = avater;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDevotion() {
        return devotion;
    }

    public void setDevotion(String devotion) {
        this.devotion = devotion;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
