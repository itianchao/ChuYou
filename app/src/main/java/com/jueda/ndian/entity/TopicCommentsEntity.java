package com.jueda.ndian.entity;

import java.util.ArrayList;

/**
 * 评论+详情
 * Created by Administrator on 2016/4/8.
 */
public class TopicCommentsEntity {
    private String name;
    private String avatar;//头像
    private String did;//捐赠id
    private String title;//标题
    private String content;//内容
    private String money;//募集捐款
    private String strech_goal;//目标金额
    private String count;//浏览慈善
    private String creat_time;//创建时间
    private String uid;//发起人id
    private String is_host;//是否为圈主
    private String img[];//图片
    private int img_height[];//图片的高度
    private String ppid;//话题id
    private String cname;//圈名
    private String cid;//圈id
    private String status;//判断是否是官方发布
    private ArrayList<Photo> photoArrayList;//图片查看需要\
    private String is_like;//判断是否点赞
    private String like_count;//点赞数量

    /**评论*/
    private String devotion;//评论奖励
    private String count_comments;//评论总数
    private String pid;//评论id
    private String puid;//评论者id
    private String pname;//评论人昵称
    private String pavatar;//评论者头像
    private String pcontent;//评论内容
    private String pcreat_time;//评论时间
    private String by_id;//0:一级评论 非0：回复评论id
    private String by_uname;//回复的用户昵称
    private String by_uid;//回复的用户id

    public String getDevotion() {
        return devotion;
    }

    public void setDevotion(String devotion) {
        this.devotion = devotion;
    }

    public String getIs_like() {
        return is_like;
    }

    public void setIs_like(String is_like) {
        this.is_like = is_like;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public String getStrech_goal() {
        return strech_goal;
    }

    public void setStrech_goal(String strech_goal) {
        this.strech_goal = strech_goal;
    }

    public ArrayList<Photo> getPhotoArrayList() {
        return photoArrayList;
    }

    public void setPhotoArrayList(ArrayList<Photo> photoArrayList) {
        this.photoArrayList = photoArrayList;
    }

    public String getBy_uid() {
        return by_uid;
    }

    public void setBy_uid(String by_uid) {
        this.by_uid = by_uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int[] getImg_height() {
        return img_height;
    }

    public void setImg_height(int[] img_height) {
        this.img_height = img_height;
    }

    public String getCount_comments() {
        return count_comments;
    }

    public void setCount_comments(String count_comments) {
        this.count_comments = count_comments;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPpid() {
        return ppid;
    }

    public void setPpid(String ppid) {
        this.ppid = ppid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIs_host() {
        return is_host;
    }

    public void setIs_host(String is_host) {
        this.is_host = is_host;
    }

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        this.img = img;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPavatar() {
        return pavatar;
    }

    public void setPavatar(String pavatar) {
        this.pavatar = pavatar;
    }

    public String getPcontent() {
        return pcontent;
    }

    public void setPcontent(String pcontent) {
        this.pcontent = pcontent;
    }

    public String getPcreat_time() {
        return pcreat_time;
    }

    public void setPcreat_time(String pcreat_time) {
        this.pcreat_time = pcreat_time;
    }

    public String getBy_id() {
        return by_id;
    }

    public void setBy_id(String by_id) {
        this.by_id = by_id;
    }

    public String getBy_uname() {
        return by_uname;
    }

    public void setBy_uname(String by_uname) {
        this.by_uname = by_uname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
