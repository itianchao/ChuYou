package com.jueda.ndian.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/16.
 */
public class AppEntity implements Serializable {
    private String aid;//任务id
    private String name;//名称
    private String downloadUrl;//下载地址
    private String content;//内容
    private String time;//时间
    private String packageName;//包名
    private String userUrl;//图片地址
    private String rule;//任务要求
    private String who;//判断是首页任务还是圈内任务
    private int state=1;//1表示安装 2表示未安装
    private String cid;//圈子id
    private String audit;//1审核中，0可以进
    private String is_fulfil;//判断是否今天还能做任务1表示能，0表示不能
    private String is_fulfil_tip;//任务完成后的提示内容
    private String ex_or_sh;//1是体验，2是问卷
    private String sample_image[];//示例图片
    private String lastcount;//剩余次数

    public String getLastcount() {
        return lastcount;
    }

    public void setLastcount(String lastcount) {
        this.lastcount = lastcount;
    }

    //我的任务
    private String reward;//奉献值奖励
    private String type;//我的任务类型
    private String reason;//驳回原因

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String[] getSample_image() {
        return sample_image;
    }

    public void setSample_image(String[] sample_image) {
        this.sample_image = sample_image;
    }



    public String getEx_or_sh() {
        return ex_or_sh;
    }

    public void setEx_or_sh(String ex_or_sh) {
        this.ex_or_sh = ex_or_sh;
    }

    public String getIs_fulfil_tip() {
        return is_fulfil_tip;
    }

    public void setIs_fulfil_tip(String is_fulfil_tip) {
        this.is_fulfil_tip = is_fulfil_tip;
    }

    public String getIs_fulfil() {
        return is_fulfil;
    }

    public void setIs_fulfil(String is_fulfil) {
        this.is_fulfil = is_fulfil;
    }

    public String getAudit() {
        return audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    private int Key;

    public int getKey() {
        return Key;
    }

    public void setKey(int key) {
        Key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

}
