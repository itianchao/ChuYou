package com.jueda.ndian.entity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/24.
 * 赚豆任务模板实体类
 */
public class TaskCommonEntity {
    private String aid;//任务id
    private String uid;//发布者uid
    private String name;//名称
    private String cid;//圈子id
    private String reward;//爱心豆奖励
    private String title;//标题
    private String descript;//描述
    private String content;//说明
    private String creat_time;//时间
    private String status;//状态
    //问卷
    private String DownloadUrl;//分享连接
    //活动
    private String is_join;//活动1是已报名，0是未报名
    private String img_url[];//活动图片展示
    private int img_height[];//图片的高度
    private ArrayList <String> enroll;//报名
    private ArrayList<Photo> photoArrayList;//图片查看需要\

    public String getIs_join() {
        return is_join;
    }

    public void setIs_join(String is_join) {
        this.is_join = is_join;
    }

    public String getDownloadUrl() {
        return DownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        DownloadUrl = downloadUrl;
    }

    public ArrayList<Photo> getPhotoArrayList() {
        return photoArrayList;
    }

    public void setPhotoArrayList(ArrayList<Photo> photoArrayList) {
        this.photoArrayList = photoArrayList;
    }

    public int[] getImg_height() {
        return img_height;
    }

    public void setImg_height(int[] img_height) {
        this.img_height = img_height;
    }

    public ArrayList<String> getEnroll() {
        return enroll;
    }

    public void setEnroll(ArrayList<String> enroll) {
        this.enroll = enroll;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getImg_url() {
        return img_url;
    }

    public void setImg_url(String[] img_url) {
        this.img_url = img_url;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }
}
