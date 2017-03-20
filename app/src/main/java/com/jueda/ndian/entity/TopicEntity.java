package com.jueda.ndian.entity;

import java.util.ArrayList;

/**
 * 话题
 * Created by Administrator on 2016/4/7.
 */
public class TopicEntity {
    private ArrayList<CircleEntity> CircleList;//圈子详情信息
    private String name;//发起人名称
    private String pid;//话题id
    private String title;//标题
    private String content;//内容
    private String count;//浏览次数
    private String uid;//发起人id
    private String avatar;//发起人头像
    private String [] img;//图片
    private String creat_time;//发布时间
    private UserEntity entity;//用户信息
    private String is_like;//是否点赞 0未点赞，1点赞
    private String like_count;//点赞数
    //首页话题
    private String image;//图片
    private String open_title;
    private String type;//类型1为话题，2为推广

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

    public String getOpen_title() {
        return open_title;
    }

    public void setOpen_title(String open_title) {
        this.open_title = open_title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserEntity getEntity() {
        return entity;
    }

    public void setEntity(UserEntity entity) {
        this.entity = entity;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }

    public ArrayList<CircleEntity> getCircleList() {
        return CircleList;
    }

    public void setCircleList(ArrayList<CircleEntity> circleList) {
        CircleList = circleList;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
